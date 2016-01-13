package app;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import protocol.TBGPProtocolCallback;
import tokenizer.TBGPMessage;
import com.google.gson.Gson;

public class BlufferProtocol implements GameProtocol {

	private final String[] questions;
	
	public BlufferProtocol(String jsonPath) {
		initialize(jsonPath);
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
	public void initialize(String jsonPath) {
		Gson gson = new Gson();
		try {
			FileReader f = new FileReader(jsonPath);
			JsonAdapter ja = gson.fromJson(f, JsonAdapter.class);
			//TODO
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
