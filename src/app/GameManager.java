package app;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import protocol.TBGPProtocolCallback;

public class GameManager {
	
	private final ConcurrentHashMap<String,TBGPProtocolCallback> players;
	
	private final ConcurrentHashMap<String,GameRoom> gamerooms;
	
	private final HashMap<String,GameProtocolFactory> games;
	
	private static final Logger logger = Logger.getLogger("edu.spl.reactor");

	private static class GameManagerInstance {
		private static GameManager instance = new GameManager();
	}
	
	private GameManager() {
		players = new ConcurrentHashMap<String,TBGPProtocolCallback>();
		gamerooms = new ConcurrentHashMap<String,GameRoom>();
		games = new HashMap<String,GameProtocolFactory>();
		games.put("BLUFFER",new GameProtocolFactory() {

			@Override
			public GameProtocol create() {
				return new BlufferProtocol();
			}
			
		});
	}
	
	public static GameManager getInstance() {
		return GameManagerInstance.instance;
	}
	
	public boolean acquireNickname(String nickname, TBGPProtocolCallback callback) {
		if(players.containsKey(nickname)) {
			logger.info("Nickname unavailable");
			return false;
		} else {
			logger.info("Nickname accepted");
			players.put(nickname, callback);
			return true;
		}
	}
	
	public GameProtocolFactory searchGame(String gameName) {
		if(games.containsKey(gameName)) return games.get(gameName);
		else {
			logger.info("The Game " + gameName + " is unavailable");
			return null;
		}
	}
}
