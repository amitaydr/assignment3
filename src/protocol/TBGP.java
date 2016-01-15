package protocol;

import app.GameManager;
import app.GameRoom;
import tokenizer.TBGPCommand;
import tokenizer.TBGPMessage;

public  class TBGP implements AsyncServerProtocol<TBGPMessage> {
	private GameRoom gameRoom;
	private String nickname;
	private boolean shouldClose = false;
	private boolean connectionTerminated = false;

	@Override
	public void processMessage(TBGPMessage msg, ProtocolCallback<TBGPMessage> callback) {
		if (connectionTerminated) return;
		TBGPCommand command = msg.getCommand();
		if (command != null){
			switch (command){
				case NICK :
					TBGPMessage result;
					if (nickname == null){
						boolean ans = GameManager.getInstance().acquireNickname(msg.getMessage(), (TBGPProtocolCallback) callback);
						 result = new TBGPMessage ("NICK " +(ans? "ACCEPTED":"REJECTED"), TBGPCommand.SYSMSG);
						if (ans) nickname = msg.getMessage();
					}else{
						 result = new TBGPMessage ("NICK REJECTED This player alrady has a nickname- " + nickname, TBGPCommand.SYSMSG);
					}
					callback.sendMessage(result);
					break;
				case JOIN:
					if (gameRoom != null) { //User is already in some room
						if (!gameRoom.quit((TBGPProtocolCallback) callback)) { //if quit returns false it means the room is in a middle of a game
							callback.sendMessage(new TBGPMessage("JOIN REJECTED cannot leave the room "+ gameRoom +" in the middle of a game!",TBGPCommand.SYSMSG));
							break;
						}
					}
					if (nickname == null){
						callback.sendMessage(new TBGPMessage("JOIN REJECTED you need a nickname first! use NICK <your_nickname> command",TBGPCommand.SYSMSG));
						break;
					}
					boolean ans = GameManager.getInstance().joinGameRoom(msg.getMessage(), nickname);
					if(ans){
						callback.sendMessage(new TBGPMessage("JOIN ACCEPTED joined the room "+ msg.getMessage(),TBGPCommand.SYSMSG));
						gameRoom = GameManager.getInstance().searchRoom(msg.getMessage());
					}else{ 
						callback.sendMessage(new TBGPMessage("JOIN REJECTED room"+ msg.getMessage() +"is in the middle of a game!",TBGPCommand.SYSMSG));
					}
					break;
				case LISTGAMES:
					callback.sendMessage(new TBGPMessage("ACCEPTED "+GameManager.getInstance().listGames(),TBGPCommand.SYSMSG));
					break;
				case MSG:
					if (gameRoom == null){
						callback.sendMessage(new TBGPMessage("MSG REJECTED you need to join a room first! use JOIN <room_name> command",TBGPCommand.SYSMSG));
						break;
					}
					gameRoom.broadcast(nickname + ": "+ msg.getMessage(), TBGPCommand.USRMSG);
					break;
				case QUIT:
					boolean ansQuit = gameRoom.quit((TBGPProtocolCallback)callback);
					if (ansQuit){
						GameManager.getInstance().exit(nickname);
						shouldClose = true;
						callback.sendMessage(new TBGPMessage("QUIT ACCEPTED bye bye", TBGPCommand.SYSMSG));
					}else{
						callback.sendMessage(new TBGPMessage("QUIT REJECTED cannot leave before game is over!", TBGPCommand.SYSMSG));
					}				
					break;
				case SELECTRESP : case TXTRESP:
					if (gameRoom != null && gameRoom.inSession()){
						gameRoom.getGameProtocol().processMessage(msg, (TBGPProtocolCallback) callback);
					}else{
						callback.sendMessage(new TBGPMessage(msg.getCommand()+" REJECTED not in gameRoom or no game in session" ,TBGPCommand.SYSMSG));
					}
					
					break;
				case STARTGAME:
					if (gameRoom != null && !gameRoom.inSession()){
						boolean ansStart = gameRoom.startGame(msg.getMessage());
						callback.sendMessage(new TBGPMessage("STARTGAME "+(ansStart? "ACCEPTED":"REJECTED") ,TBGPCommand.SYSMSG));
					}else{
						callback.sendMessage(new TBGPMessage("STARTGAME REJECTED not in gameRoom or other game in session" ,TBGPCommand.SYSMSG));
					}
					break;
				default:
					callback.sendMessage(new TBGPMessage( msg.getCommand() +" UNIDENTIFIED this is not a user command!" ,TBGPCommand.SYSMSG));
					break;
				}
		}else{
			callback.sendMessage(new TBGPMessage( msg.getMessage() +" UNIDENTIFIED this is an unknown command! user commands are:\n"
					+ "NICK <nickname>,  JOIN <game_room_name>, LISTGAMES, STARTGAME <game_name>, MSG <chat_message>\n"
					+ "TXTRESP <text_response>, SELECTRESP <number_of_selected_response>, QUIT" ,TBGPCommand.SYSMSG));
		}
			
	}

	@Override
	public boolean isEnd(TBGPMessage msg) {
		return msg.getCommand().equals(TBGPCommand.QUIT);
	}

	@Override
	public boolean shouldClose() {
		return shouldClose;
	}

	@Override
	public void connectionTerminated() {
		this.connectionTerminated  = true;
	}



}
