package TPC;

import protocol.TBGPProtocolCallback;
import tokenizer.TBGPMessage;

public class TBGPTCPCallback implements TBGPProtocolCallback {

	private TPCConnectionHandler<TBGPMessage> m_TPCConnectionHandler;

	public TBGPTCPCallback(TPCConnectionHandler<TBGPMessage> TPCConnectionHandler) {
		this.m_TPCConnectionHandler = TPCConnectionHandler;
	}

	@Override
	public void sendMessage(TBGPMessage msg) {
		m_TPCConnectionHandler.sendMessage(msg);
	
	}

}
