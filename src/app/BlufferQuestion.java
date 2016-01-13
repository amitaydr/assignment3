package app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import protocol.TBGPProtocolCallback;

public class BlufferQuestion {

	private String questionText;
	
	private String realAnswer;
	
	private HashMap<String,TBGPProtocolCallback> bluffs = new HashMap<String,TBGPProtocolCallback>();
	
	private String[] choices = null;

	public BlufferQuestion(String question, String trueAnswer) {
		this.questionText = question;
		this.realAnswer = trueAnswer;
	}

	public String getQuestion() {
		return questionText;
	}

	public String getTrueAnswer() {
		return realAnswer;
	}
	
	public void addPlayerBluff(TBGPProtocolCallback callback, String bluff) {
		bluffs.put(bluff, callback);
	}
	
	public String[] printAnswers() {
		choices = new String[bluffs.size() + 1];
		int role = (int)(choices.length*Math.random());
		choices[role] = realAnswer;
		Iterator<Entry<String, TBGPProtocolCallback>> it = bluffs.entrySet().iterator();
		int i = 0;
		while(it.hasNext()) {
			if(choices[i] == null) choices[i] = it.next().getKey();
			else {
				i++;
				choices[i] = it.next().getKey();
			}
			i++;
		}
		return choices;
	}
	
	public TBGPProtocolCallback searchBluff(String bluff) {
		return bluffs.get(bluff);
	}
	
	public int getNumOfChoices() {
		return choices.length + 1;
	}

	public String getChoiceNum(int choiceNum) {
		if(choices != null) {
			if(choiceNum < choices.length && choiceNum >=0) return choices[choiceNum];
			else return null;
		} else return null;
	}
}
