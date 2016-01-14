package app;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import protocol.TBGPProtocolCallback;
import tokenizer.TBGPCommand;
import tokenizer.TBGPMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BlufferProtocol implements GameProtocol {
	
	private BlufferState gameState = BlufferState.INITIALIZING;
	
	private BlufferQuestion[] questions = null;
	
	private int numOfCurrentQuestion = 0;
	
	private GameRoom gameRoom;
	
	private int playerCounter;
	
	public BlufferProtocol(String jsonPath, GameRoom gameroom) {
		this.gameRoom = gameroom;
		playerCounter = gameRoom.numOfPlayers();
		initialize(jsonPath);
	}

	@Override
	public void processMessage(TBGPMessage msg, TBGPProtocolCallback callback) {
		switch(gameState) {
			case INITIALIZING:
				callback.sendMessage(new TBGPMessage(msg.getCommand() + " REJECTED: Game has not yet started", TBGPCommand.SYSMSG));
				break;
			case WAITING_FOR_BLUFFS:
				if(msg.getCommand() != TBGPCommand.TXTRESP) {
					callback.sendMessage(new TBGPMessage(msg.getCommand() + " REJECTED: Expecting a bluff for the question", TBGPCommand.SYSMSG));
				} else {
					playerCounter--;
					questions[numOfCurrentQuestion].addPlayerBluff(nickname, bluff);					
				}
				break;
			case WAITING_FOR_CHOICES:
				break;
			default:
				break;
		
		}

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
			JsonObject jo = gson.fromJson(f, JsonObject.class);
			JsonArray jquestions = jo.getAsJsonArray("questions");
			for(int i=0; i<jquestions.size(); i++){
				JsonObject q = jquestions.get(i).getAsJsonObject();
				questions[i] = new BlufferQuestion(q.getAsJsonObject("questionText").getAsString(),q.getAsJsonObject("realAnswer").getAsString() );
			}
			gameRoom.broadcast(questions[0].getQuestion(), TBGPCommand.ASKTXT);
			gameState = BlufferState.WAITING_FOR_BLUFFS;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printQuestions (){
		for (int i = 0; i<questions.length; i++){
			System.out.println(questions[i].getQuestion());
		}
	}

}
