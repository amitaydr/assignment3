package protocol;

import java.io.IOException;

import app.GameManager;
import app.GameRoom;
import tokenizer.TBGPCommand;
import tokenizer.TBGPMessage;

public  class TBGP implements AsyncServerProtocol<TBGPMessage> {
	private GameRoom gameRoom;
	private String nickname;
	private boolean shouldclose;

	@Override
	public void processMessage(TBGPMessage msg, ProtocolCallback<TBGPMessage> callback) {
		TBGPCommand command = msg.getCommand();
		switch (command){
			case NICK :
				TBGPMessage result;
				if (nickname != null){
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
					if (!gameRoom.quit(nickname)) { //if quit returns false it means the room is in a middle of a game
						callback.sendMessage(new TBGPMessage("JOIN REJECTED cannot leave the room "+ gameRoom +" in the middle of a game!",TBGPCommand.SYSMSG));
						break;
					}
				}
				boolean ans = GameManager.getInstance().joinGameRoom(msg.getMessage(), nickname);
				if(ans){
					callback.sendMessage(new TBGPMessage("JOIN ACCEPTED joined the room "+ msg.getMessage(),TBGPCommand.SYSMSG));
					gameroom = GameManager.getInstance().getGameRoom(msg.getMessage());
				}else{ 
					callback.sendMessage(new TBGPMessage("JOIN REJECTED room"+ msg.getMessage() +"is in the middle of a game!",TBGPCommand.SYSMSG));
				}
				break;
			case LISTGAMES:
				GameManager.getInstance().listGames();
				break;
			case MSG:
				gameRoom.broadcast(nickname + ": "+ msg.getMessage());
				break;
			case QUIT:
				boolean ansQuit = gameRoom.quit(nickname);
				
				
				break;
			case SELECTRESP:
				break;
			case STARTGAME:
				break;
			case TXTRESP:
				break;
			default:
				break;
			}
			
	}

	@Override
	public boolean isEnd(TBGPMessage msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldClose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connectionTerminated() {
		// TODO Auto-generated method stub
		
	}



}
