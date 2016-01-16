package reactor;

import java.nio.charset.CharacterCodingException;

import protocol.TBGPProtocolCallback;
import tokenizer.TBGPMessage;

public class TBGPReactorCallback implements TBGPProtocolCallback  {
	private ConnectionHandler<TBGPMessage> handler;

	public TBGPReactorCallback(ConnectionHandler<TBGPMessage> connectionHandler) {
		handler = connectionHandler;
	}


	public void sendMessage(TBGPMessage msg)  {
		try {
			handler.addOutMessage(msg);
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		
	}
	
}
