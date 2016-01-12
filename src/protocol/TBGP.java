package protocol;

import tokenizer.StringMessage;

public abstract class TBGP implements AsyncServerProtocol<StringMessage> {

	@Override
	public void processMessage(StringMessage msg, ProtocolCallback<StringMessage> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEnd(StringMessage msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldClose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connectionTerminated() {
		// TODO Auto-generated method stub
		
	}


}
