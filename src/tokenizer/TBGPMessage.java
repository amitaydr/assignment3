package tokenizer;

public class TBGPMessage implements Message<TBGPMessage> {
	private TBGPCommand command;
	private String Message;
	
	
	public TBGPMessage(String message, TBGPCommand command) {
		this.command = command;
		Message = message;
	}


	public TBGPCommand getCommand() {
		return command;
	}


	public String getMessage() {
		return Message;
	}
	
}
