package app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import protocol.TBGPProtocolCallback;
/**
 * The BlufferQuestion class represents a single question in the Bluffer game.
 * It holds within it the question itself and the true answer for it, among with player bluffs and selections.
 */
public class BlufferQuestion {

	private String questionText;
	
	private String realAnswer;
	
	private HashMap<String,LinkedList<TBGPProtocolCallback>> bluffs = new HashMap<String,LinkedList<TBGPProtocolCallback>>();
	
	private String[] choices = null;
	/**
	 * The BlufferQuestion constructor.
	 * @param question	The question text.
	 * @param trueAnswer	The true answer of question text.
	 */
	public BlufferQuestion(String question, String trueAnswer) {
		this.questionText = question;
		this.realAnswer = trueAnswer;
	}
	/**
	 * @return	the question text.
	 */
	public String getQuestion() {
		return questionText;
	}
	/**
	 * @return the true answer text.
	 */
	public String getTrueAnswer() {
		return realAnswer;
	}
	/**
	 * Stores a player bluff for this particular question.
	 * @param callback	the player's {@link TBGPProtocolCallback}
	 * @param bluff		the bluff text.
	 */
	public void addPlayerBluff(TBGPProtocolCallback callback, String bluff) {
		if(bluffs.containsKey(bluff)) {
			bluffs.get(bluff).add(callback);
		} else {
			LinkedList<TBGPProtocolCallback> bluffers = new LinkedList<TBGPProtocolCallback>();
			bluffers.add(callback);
			bluffs.put(bluff, bluffers);
		}
	}
	/**
	 * @return 	a list containing the true answer along with the players' bluffs in a randomized order.
	 */
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
	/**
	 * @return	the number of answers to choose from.
	 */
	public int getNumOfChoices() {
		return choices.length;
	}
	/**
	 * Get the answer text of the selection made during the time players guess the correct answer from the bluffs.
	 * @param choiceNum		the index of the choice in list.
	 * @return	the answer text.
	 */
	public String getChoice(int choiceNum) {
		if(choices != null) {
			if(choiceNum < choices.length && choiceNum >=0) return choices[choiceNum];
			else return null;
		} else return null;
	}
	/**
	 * @param choice an answer's text
	 * @return	the {@link TBGPProtocolCallback} of the player who made the bluff
	 */
	public LinkedList<TBGPProtocolCallback> getCallbackByBluff(String choice) {
		return bluffs.get(choice);
	}
}
