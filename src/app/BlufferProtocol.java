package app;

import java.io.FileNotFoundException;
import java.io.FileReader;
import protocol.TBGPProtocolCallback;
import tokenizer.TBGPCommand;
import tokenizer.TBGPMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
					questions[numOfCurrentQuestion].addPlayerBluff(callback, msg.getMessage());
					callback.sendMessage(new TBGPMessage("TXTRSP ACCEPTED", TBGPCommand.SYSMSG));
					if(playerCounter == 0) {
						String[] choices = questions[numOfCurrentQuestion].printAnswers();
						String choiceList = "";
						for(int i = 0; i < choices.length; i++) {
							choiceList = i + ". " + choices[i] + " ";
						}
						gameRoom.broadcast(choiceList, TBGPCommand.ASKCHOICES);
						playerCounter = gameRoom.numOfPlayers();
						gameState = BlufferState.WAITING_FOR_CHOICES;
					}
				}
				break;
			case WAITING_FOR_CHOICES:
				if(msg.getCommand() != TBGPCommand.SELECTRESP) {
					callback.sendMessage(new TBGPMessage(msg.getCommand() + " REJECTED: Please select an answer from list", TBGPCommand.SYSMSG));
				} else {
					Integer choiceNum = -1;
					try {
						choiceNum = Integer.parseInt(msg.getMessage());
					} catch(NumberFormatException e) {
						callback.sendMessage(new TBGPMessage(msg.getCommand() + " REJECTED: Response must be an integer", TBGPCommand.SYSMSG));
						break;
					}
					if(choiceNum.intValue() < questions[numOfCurrentQuestion].getNumOfChoices() && choiceNum.intValue() >= 0) {
						playerCounter--;
						callback.sendMessage(new TBGPMessage(msg.getCommand() + " ACCEPTED", TBGPCommand.SYSMSG));
						callback.sendMessage(new TBGPMessage("The correct answer is: " + questions[numOfCurrentQuestion].getTrueAnswer(), TBGPCommand.GAMEMSG));
						if(questions[numOfCurrentQuestion].getChoiceNum(choiceNum) == questions[numOfCurrentQuestion].getTrueAnswer()) {
							callback.sendMessage(new TBGPMessage("Correct! +10pts", TBGPCommand.GAMEMSG));
							//TODO Rethink the scoring method
						}
					}
				}
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
		JsonParser parser = new JsonParser();
		try {
			FileReader f = new FileReader(jsonPath);
			JsonObject jo = (JsonObject)parser.parse(f);
			JsonArray jquestions = jo.get("questions").getAsJsonArray();
			
			
			questions = new BlufferQuestion[jquestions.size()];
			for(int i=0; i<jquestions.size(); i++){
				JsonObject q = jquestions.get(i).getAsJsonObject();
				String questionText = q.get("questionText:").getAsString();
				String realAnswer = q.get("realAnswer:").getAsString();
				questions[i] = new BlufferQuestion(questionText,realAnswer);
			}
			gameRoom.broadcast(questions[0].getQuestion(), TBGPCommand.ASKTXT);
			gameState = BlufferState.WAITING_FOR_BLUFFS;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void printQuestions (){
		for (int i = 0; i<questions.length; i++){
			System.out.println(questions[i].getQuestion());
		}
	}

}
