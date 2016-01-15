package app;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import protocol.TBGPProtocolCallback;
import tokenizer.TBGPCommand;
import tokenizer.TBGPMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BlufferProtocol implements GameProtocol {
	
	private BlufferState gameState = BlufferState.INITIALIZING;
	
	private BlufferQuestion[] questions = new BlufferQuestion[3];
	
	private int numOfCurrentQuestion = 0;
	
	private GameRoom gameRoom;
	
	private int playerCounter;
	
	String scoreBoard = "";
	
	private HashMap<TBGPProtocolCallback, Integer> scores;
	
	private HashMap<TBGPProtocolCallback, Integer> currentRoundScore;
	
	private HashMap<TBGPProtocolCallback, Boolean> wasCorrect;
	
	public BlufferProtocol(String jsonPath, GameRoom gameroom) {
		this.gameRoom = gameroom;
		scores = new HashMap<TBGPProtocolCallback, Integer>();
		currentRoundScore = new HashMap<TBGPProtocolCallback, Integer>();
		wasCorrect = new HashMap<TBGPProtocolCallback, Boolean>();
		playerCounter = gameRoom.numOfPlayers();
		initialize(jsonPath);
	}

	@Override
	public synchronized void processMessage(TBGPMessage msg, TBGPProtocolCallback callback) {
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
							choiceList = choiceList + i + ". " + choices[i] + " ";
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
						if(questions[numOfCurrentQuestion].getChoice(choiceNum) == questions[numOfCurrentQuestion].getTrueAnswer()) {
							currentRoundScore.put(callback, currentRoundScore.get(callback) + 10); //Update player score for correct answer
							wasCorrect.put(callback, true); //Update that player picked the correct answer
						} else {
							TBGPProtocolCallback blufferCallback = questions[numOfCurrentQuestion].getCallbackByBluff(questions[numOfCurrentQuestion].getChoice(choiceNum));
							currentRoundScore.put(blufferCallback, currentRoundScore.get(blufferCallback) + 5); //Update bluffer score if a player picked his bluff
						}
					} else callback.sendMessage(new TBGPMessage(msg.getCommand() + " REJECTED: pick a number in the correct range", TBGPCommand.SYSMSG));
					if(playerCounter == 0) {
						wasCorrect.forEach((k,v) -> {
							int roundScore = currentRoundScore.get(k);
							k.sendMessage(new TBGPMessage((v? "Correct":"Wrong") + "! +" + roundScore + "pts", TBGPCommand.GAMEMSG));
							scores.put(k, scores.get(k) + roundScore);
							currentRoundScore.put(k, 0);
							wasCorrect.put(k, false);
						});
						if(numOfCurrentQuestion < 2) {
							numOfCurrentQuestion++;
							gameRoom.broadcast(questions[numOfCurrentQuestion].getQuestion(), TBGPCommand.ASKTXT);
							playerCounter = gameRoom.numOfPlayers();
							gameState = BlufferState.WAITING_FOR_BLUFFS;
						} else {
							scores.forEach((k,v) -> {
								scoreBoard = scoreBoard + ", " + gameRoom.playerNickname(k) + ": " + v + "pts";
							});
							scoreBoard = scoreBoard.substring(2); //To not include the first comma
							gameRoom.broadcast("Summary: " + scoreBoard, TBGPCommand.ASKTXT);
							gameRoom.endGame();
						}
					}
				}
				break;
		}

	}

	@Override
	public void initialize(String jsonPath) {
		JsonParser parser = new JsonParser();
		ArrayList<BlufferQuestion> tempQuestions = new ArrayList<BlufferQuestion>();
		try {
			FileReader f = new FileReader(jsonPath);
			JsonObject jo = (JsonObject)parser.parse(f);
			JsonArray jquestions = jo.get("questions").getAsJsonArray();
			
			for(int i=0; i<jquestions.size(); i++){
				JsonObject q = jquestions.get(i).getAsJsonObject();
				String questionText = q.get("questionText:").getAsString();
				String realAnswer = q.get("realAnswer:").getAsString();
				tempQuestions.add(new BlufferQuestion(questionText,realAnswer));
			}
			for (int j=0; j<3; j++){
				questions[j] = tempQuestions.remove((int)(Math.random()*tempQuestions.size()));
			}
			gameRoom.broadcast(questions[0].getQuestion(), TBGPCommand.ASKTXT);
			gameState = BlufferState.WAITING_FOR_BLUFFS;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Set<TBGPProtocolCallback> playerList = gameRoom.getPlayerList();
		playerList.forEach((i) -> {
			scores.put(i, 0);
			currentRoundScore.put(i, 0);
			wasCorrect.put(i, false);
		});
	}
	//TODO For testing - need to erase before submission
	public void printQuestions (){
		for (int i = 0; i<questions.length; i++){
			System.out.println(questions[i].getQuestion());
		}
	}

}
