package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import app.BlufferProtocol;
import app.GameProtocol;
import app.GameProtocolFactory;

public class BlufferProtocolTest {
	private GameProtocolFactory factory = new GameProtocolFactory() {
		
		@Override
		public GameProtocol create() {
			return new BlufferProtocol("C:/Users/amitaydr/Desktop/Ass3/assignment3/bluffer[2].json");
		}
	private BlufferProtocol bluf ;


	@Before
	public void setUp() throws Exception {
		bluf = factory.create();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
