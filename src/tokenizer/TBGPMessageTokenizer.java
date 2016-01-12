package tokenizer;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

public class TBGPMessageTokenizer implements MessageTokenizer<TBGPMessage> {

	@Override
	public void addBytes(ByteBuffer bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasMessage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TBGPMessage nextMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuffer getBytesForMessage(TBGPMessage msg) throws CharacterCodingException {
		// TODO Auto-generated method stub
		return null;
	}


}
