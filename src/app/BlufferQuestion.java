package app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class BlufferQuestion {

	private String questionText;
	
	private String realAnswer;
	
	private HashMap<String,String> bluffs = new HashMap<String,String>();
	
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
	
	public void addPlayerBluff(String nickname, String bluff) {
		bluffs.put(bluff, nickname);
	}
	
	public String[] printAnswers() {
		choices = new String[bluffs.size() + 1];
		int role = (int)(choices.length*Math.random());
		choices[role] = realAnswer;
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
