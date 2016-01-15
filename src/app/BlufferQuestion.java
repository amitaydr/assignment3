package app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import protocol.TBGPProtocolCallback;

public class BlufferQuestion {

	private String questionText;
	
	private String realAnswer;
	
	private HashMap<String,LinkedList<TBGPProtocolCallback>> bluffs = new HashMap<String,LinkedList<TBGPProtocolCallback>>();
	
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
		if(bluffs.containsKey(bluff)) {
			bluffs.get(bluff).add(callback);
		} else {
			LinkedList<TBGPProtocolCallback> bluffers = new LinkedList<TBGPProtocolCallback>();
			bluffers.add(callback);
			bluffs.put(bluff, bluffers);
		}
	}
	
	public String[] printAnswers() {
		choices = new String[bluffs.size() + 1];
		int roll = (int)(choices.length*Math.random());
		choices[roll] = realAnswer;
		Iterator<Entry<String, LinkedList<TBGPProtocolCallback>>> it = bluffs.entrySet().iterator();
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
	
	public int getNumOfChoices() {
		return choices.length + 1;
	}

	public String getChoice(int choiceNum) {
		if(choices != null) {
			if(choiceNum < choices.length && choiceNum >=0) return choices[choiceNum];
			else return null;
		} else return null;
	}

	public LinkedList<TBGPProtocolCallback> getCallbackByBluff(String choice) {
		return bluffs.get(choice);
	}
}
