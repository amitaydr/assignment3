package protocol;

import java.nio.charset.CharacterCodingException;
import reactor.ConnectionHandler;
import tokenizer.TBGPMessage;

public class TBGPProtocolCallback implements ProtocolCallback<TBGPMessage> {
	private ConnectionHandler<TBGPMessage> handler;

	public TBGPProtocolCallback(ConnectionHandler<TBGPMessage> connectionHandler) {
		handler = connectionHandler;
	}

	@Override
	public void sendMessage(TBGPMessage msg)  {
		try {
			handler.addOutMessage(msg);
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		
	}
	

}
