package app;

import java.util.ArrayList;
import protocol.TBGPProtocolCallback;
import tokenizer.TBGPMessage;

public class BlufferProtocol implements GameProtocol {

	private final String[] questions;
	
	public BlufferProtocol(String jsonPath) {
		initialize();
	}

	@Override
	public void processMessage(TBGPMessage msg, TBGPProtocolCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

}
