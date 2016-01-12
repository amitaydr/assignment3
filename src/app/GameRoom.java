package app;

import java.util.logging.Logger;

public class GameRoom {
	
	private GameProtocol currentGame = null;
	
	private static final Logger logger = Logger.getLogger("edu.spl.reactor");

	public GameRoom(GameProtocol currentGame) {
		this.currentGame = currentGame;
	}

	public synchronized boolean startGame(GameProtocol game) {
		if(!inSession()) {
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
}
