package tokenizer;

public class TBGPMessage implements Message<TBGPMessage> {
	private TBGPCommand command;
	private String Message;
	
	
	public TBGPMessage(TBGPCommand command, String message) {
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
