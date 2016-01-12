package app;

import protocol.TBGPProtocolCallback;
import tokenizer.TBGPMessage;

public interface GameProtocol {
	
	public void processMessage(TBGPMessage msg, TBGPProtocolCallback callback);
		
	public String getName();
}
