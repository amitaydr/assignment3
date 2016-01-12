package protocol;

import java.io.IOException;

import app.GameManager;
import app.GameRoom;
import tokenizer.TBGPCommand;
import tokenizer.TBGPMessage;

public  class TBGP implements AsyncServerProtocol<TBGPMessage> {
	private GameRoom gameRoom;
	private String nickname;

	@Override
	public void processMessage(TBGPMessage msg, ProtocolCallback<TBGPMessage> callback) {
		TBGPCommand command = msg.getCommand();
		switch (command){
			case NICK :
				if (nickname != null){
					boolean ans = GameManager.getInstance().acquireNickname(msg.getMessage(), (TBGPProtocolCallback) callback);
					TBGPMessage result = new TBGPMessage ("NICK " +(ans? "ACCEPTED":"REJECTED"), TBGPCommand.SYSMSG);
					if (ans) nickname = msg.getMessage();
					try {callback.sendMessage(result);
					} catch (IOException e) {e.printStackTrace();}
				}else{
					TBGPMessage result = new TBGPMessage ("NICK REJECTED This player alrady has a nickname- " + nickname, TBGPCommand.SYSMSG);
				}
			case JOIN:
				break;
			case LISTGAMES:
				break;
			case MSG:
				break;
			case QUIT:
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
