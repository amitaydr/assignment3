package app;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import protocol.TBGPProtocolCallback;
import tokenizer.TBGPCommand;
import tokenizer.TBGPMessage;
/**
 * The GameRoom class is responsible for handling a single game and the players playing it.
 */
public class GameRoom {
	
	private ConcurrentHashMap<TBGPProtocolCallback, String> players;
	
	private String name;
	
	private GameProtocol currentGame = null;
	
	private static final Logger logger = Logger.getLogger("edu.spl.reactor");
	/**
	 * The GameRoom constructor.
	 * @param name	the name of the game room.
	 */
	public GameRoom(String name) {
		players = new ConcurrentHashMap<TBGPProtocolCallback, String>();
		this.name = name;
	}
	/**
	 * Starts a game within the game room with all the players in it at the time.
	 * @param gameName	the name of the game to start
	 */
	public synchronized void startGame(String gameName) {
		if(!inSession()) {
			GameProtocol game = GameManager.getInstance().searchGame(gameName).create(this);
			//to take care of game is null
			this.currentGame = game;
			logger.info(gameName + " game starting");
		} else {
			logger.info("A different game is already in session");
		}
	}
	/**
	 * @return	true if there is already a game in session, false otherwise.
	 */
	public boolean inSession() {
		return currentGame != null;
	}
	/**
	 * Adds a player to the game room.
	 * @param nickname	the player's nickname
	 * @param callback	the player's {@link TBGPProtocolCallback}
	 */
	public void addPlayer(String nickname, TBGPProtocolCallback callback) {
		broadcast(nickname + " joined the room", TBGPCommand.SYSMSG);
		players.put(callback, nickname);
	}
	/**
	 * Sends a message to all players in the game room.
	 * @param msg	the message text.
	 * @param command	the type of {@link TBGPCommand} to send.
	 */
	public void broadcast(String msg, TBGPCommand command) {
		players.forEach((k,v) -> k.sendMessage(new TBGPMessage(msg,command)));
	}
	/**
	 * If the game room is not in the middle of a game, removes the player from the game room. Otherwise, does nothing.
	 * @param callback	the {@link TBGPProtocolCallback} of the player to remove.
	 * @return	true if the player was removed, false otherwise.
	 */
	public boolean quit(TBGPProtocolCallback callback) {
		if(!inSession()) {
			String removedPlayer = players.remove(callback);
			broadcast(removedPlayer + " left the room", TBGPCommand.SYSMSG);
			logger.info(removedPlayer + " left the room");
			return true;
		} else {
			logger.info("Unable to leave room - game in session");
			return false;
		}
	}
	/**
	 * @return the name of the game room.
	 */
	public String toString() {
		return name;
	}
	/**
	 * @return	The {@link GameProtocol} of the game currently played. Null if no game is currently being played.
	 */
	public GameProtocol getGameProtocol() {
		return currentGame;
	}
	/**
	 * @return	the number of players in the game room.
	 */
	public int numOfPlayers() {
		return players.size();
	}
	/**
	 * @return a list of the players' {@link TBGPProtocolCallback}
	 */
	public Set<TBGPProtocolCallback> getPlayerList() {
			return players.keySet();
	}
	/**
	 * @param callback		a {@link TBGPProtocolCallback} of a player.
	 * @return		the nickname of the player.
	 */
	public String playerNickname(TBGPProtocolCallback callback) {
		return players.get(callback);
	}
	/**
	 * Sets the current game to null.
	 */
	public void endGame() {
		currentGame = null;
	}

}
