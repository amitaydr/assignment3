package protocol;

import java.io.IOException;

import reactor.ConnectionHandler;
import tokenizer.TBGPMessage;

public class TBGPProtocolCallback implements ProtocolCallback<TBGPMessage> {
	private ConnectionHandler<TBGPMessage> handler;

	public TBGPProtocolCallback(ConnectionHandler<TBGPMessage> connectionHandler) {
		handler = connectionHandler;
	}

	@Override
	public void sendMessage(TBGPMessage msg) throws IOException {
		handler.addOutMessage(msg);
		
	}
	

}
