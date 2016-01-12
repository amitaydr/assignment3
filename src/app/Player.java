package app;

import java.util.logging.Logger;
import protocol.TBGPProtocolCallback;

public class Player {
	
	private final String nickname;
	
	private TBGPProtocolCallback callback;
	
	private GameRoom instance = null;
	
	private static final Logger logger = Logger.getLogger("edu.spl.reactor");
	
	public Player(String nickname, TBGPProtocolCallback callback) {
		this.nickname = nickname;
		this.callback = callback;
	}

	public String getNickname() {
		return nickname;
	}

	public TBGPProtocolCallback getCallback() {
		return callback;
	}

	public void setGameRoom(GameRoom instance) {
		this.instance = instance;
	}
	
	public GameRoom getGameRoom() {
		if(instance == null) {
			logger.info("Player " + nickname + " is not assigned to a game room");
		}
		return instance;
	}
}
