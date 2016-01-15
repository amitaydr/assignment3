package app;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import protocol.TBGPProtocolCallback;
import tokenizer.TBGPCommand;
import tokenizer.TBGPMessage;

public class GameRoom {
	
	private ConcurrentHashMap<String,TBGPProtocolCallback> players;
	
	private String name;
	
	private GameProtocol currentGame = null;
	
	private static final Logger logger = Logger.getLogger("edu.spl.reactor");

	public GameRoom(String name) {
		players = new ConcurrentHashMap<String,TBGPProtocolCallback>();
		this.name = name;
	}
	
	public synchronized boolean startGame(String gameName) {
		if(!inSession()) {
			GameProtocol game = GameManager.getInstance().searchGame(gameName).create(this);
			//to take care of game is null
			this.currentGame = game;
			logger.info(game.getName() + " game starting");
			return true;
		} else {
			logger.info("A different game is already in session");
			return false;
		}
	}
	
	public boolean inSession() {
		return currentGame != null;
	}

	public void addPlayer(String nickname, TBGPProtocolCallback callback) {
		broadcast(nickname + " joined the room", TBGPCommand.USRMSG);
		players.put(nickname, callback);
	}
	
	public void broadcast(String msg, TBGPCommand command) {
		players.forEach((k,v) -> v.sendMessage(new TBGPMessage(msg,command)));
	}
	
	public boolean quit(String nickname) {
		if(!inSession()) {
			players.remove(nickname);
			broadcast(nickname + " left the room", TBGPCommand.SYSMSG);
			logger.info(nickname + " left the room");
			return true;
		} else {
			logger.info("Unable to leave room - game in session");
			return false;
		}
	}
	
	public String toString() {
		return name;
	}
	
	public GameProtocol getGameProtocol() {
		return currentGame;
	}
	
	public int numOfPlayers() {
		return players.size();
	}

}
