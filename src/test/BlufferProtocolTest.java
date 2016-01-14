package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import app.BlufferProtocol;
import app.GameProtocol;
import app.GameProtocolFactory;
import app.GameRoom;

public class BlufferProtocolTest {
	private GameRoom gr;
	private GameProtocolFactory factory = new GameProtocolFactory() {
		
		@Override
		public GameProtocol create(GameRoom g) {
			return new BlufferProtocol("C:/Users/amitaydr/Desktop/Ass3/assignment3/bluffer[2].json", g);
		}
	};
	private BlufferProtocol bluf ;


	@Before
	public void setUp() throws Exception {
		gr = new GameRoom ("aaa");
		bluf = (BlufferProtocol) factory.create(gr);
	}

	@After
	public void tearDown() throws Exception {
	}
	

	@Test
	public void test() {
		bluf.printQuestions();
	}

}
