package app;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import protocol.TBGPProtocolCallback;

// TODO GameManager description

public class GameManager {
	
	private final ConcurrentHashMap<String,TBGPProtocolCallback> players;
	
	private final ConcurrentHashMap<String,GameRoom> gamerooms;
	
	private final HashMap<String,GameProtocolFactory> games;
	
	private final String gameList;
	
	private static final Logger logger = Logger.getLogger("edu.spl.reactor");

	private static class GameManagerInstance {
		private static GameManager instance = new GameManager();
	}
	/**
	 * The GameManager constructor. Available games are added here to the games list.
	 */
	private GameManager() {
		players = new ConcurrentHashMap<String,TBGPProtocolCallback>();
		gamerooms = new ConcurrentHashMap<String,GameRoom>();
		gameList = new String("BLUFFER");
		games = new HashMap<String,GameProtocolFactory>();
		games.put("BLUFFER",new GameProtocolFactory() {

			@Override
			public GameProtocol create() {
				return new BlufferProtocol();
			}
			
		});
	}
	/**
	 * Get the instance of the GameManager.
	 * @return	the GameManager instance
	 */
	public static GameManager getInstance() {
		return GameManagerInstance.instance;
	}
	/**
	 * Check if a nickname is available and connect to the {@link GameManager}.
	 * @param nickname	the nickname to log on with
	 * @param callback	the player {@link TBGPProtocolCallback}
	 * @return	true if nickname was accepted and connection was successful and false if nickname was unavailable and connection failed.
	 */
	public synchronized boolean acquireNickname(String nickname, TBGPProtocolCallback callback) {
		if(players.containsKey(nickname)) {
			logger.info("Nickname unavailable");
			return false;
		} else {
			logger.info("Nickname accepted - " + nickname + " logged on");
			players.put(nickname, callback);
			return true;
		}
	}
	/**
	 * Searches for a game.
	 * @param gameName	the game name to search for
	 * @return	a {@link GameProtocolFactory) of the game protocol searched for if it exists, null otherwise
	 */
	public GameProtocolFactory searchGame(String gameName) {
		if(games.containsKey(gameName)) return games.get(gameName);
		else {
			logger.info("The Game " + gameName + " is unavailable");
			return null;
		}
	}
	/**
	 * Deletes the player from the {@link GameManager} players list.
	 * If no such player exists, does nothing.
	 * @param nickname	the nickname of the player to delete
	 */
	public void exit(String nickname) {
		if(players.containsKey(nickname)) {
			players.remove(nickname);
			logger.info(nickname + " logged off");
		} else logger.info(nickname + " does not exist");
	}
	/**
	 * Join a {@link GameRoom}. If room does not exist, creates a new one and enters the player into it.
	 * If the player wishes to join a game room in session, returns false.
	 * @param roomName	The game room name
	 * @param nickname	The player nickname
	 * @return	true if the player entered the game room, false otherwise
	 */
	public synchronized boolean joinGameRoom(String roomName, String nickname) {
		if(gamerooms.containsKey(roomName)) {
			GameRoom gameRoom = gamerooms.get(roomName);
			synchronized(gameRoom) {
				if(gameRoom.inSession()) {
					logger.info(nickname + " was unable to join game room " + " - already in session");
					return false;
				} else {
					gameRoom.addPlayer(nickname,players.get(nickname));
					logger.info(nickname + " joined " + roomName + " game room");
					return true;
				}
			}
		} else {
			GameRoom gameRoom = new GameRoom(roomName);
				gameRoom.addPlayer(nickname,players.get(nickname));
				gamerooms.put(roomName, gameRoom);
				logger.info(nickname + " joined " + roomName + " game room");
				return true;
		}
	}
	/**
	 * Search for a game room.
	 * @param roomName	the game room name to search for
	 * @return	the instance of the game room if it exists, null otherwise
	 */
	public GameRoom searchRoom(String roomName) {
		if(gamerooms.containsKey(roomName)) return gamerooms.get(roomName);
		else return null;
	}
	
	public String listGames() {
		return gameList;
	}
}
