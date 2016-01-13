package app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class BlufferQuestion {

	private String question;
	
	private String trueAnswer;
	
	private HashMap<String,String> bluffs = new HashMap<String,String>();
	
	private String[] choices = null;

	public BlufferQuestion(String question, String trueAnswer) {
		this.question = question;
		this.trueAnswer = trueAnswer;
	}

	public String getQuestion() {
		return question;
	}

	public String getTrueAnswer() {
		return trueAnswer;
	}
	
	public void addPlayerBluff(String nickname, String bluff) {
		bluffs.put(bluff, nickname);
	}
	
	public String[] printAnswers() {
		choices = new String[bluffs.size() + 1];
		int role = (int)(choices.length*Math.random());
		choices[role] = trueAnswer;
		Iterator<Entry<String, String>> it = bluffs.entrySet().iterator();
		int i = 0;
		while(it.hasNext()) {
			if(choices[i] == null) choices[i] = it.next().getValue();
			else {
				i++;
				choices[i] = it.next().getKey();
			}
			i++;
		}
		return choices;
	}
	
	public String searchBluff(String bluff) {
		return bluffs.get(bluff);
	}
}
