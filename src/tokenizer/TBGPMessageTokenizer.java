package tokenizer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Vector;

public class TBGPMessageTokenizer implements MessageTokenizer<TBGPMessage> {
	private final String _messageSeparator = "\n";

	   private final StringBuffer _stringBuf = new StringBuffer();
	   /**
		 * the fifo queue, which holds data coming from the socket. Access to the
		 * queue is serialized, to ensure correct processing order.
		 */
		private final Vector<ByteBuffer> _buffers = new Vector<ByteBuffer>();

	   private final CharsetDecoder _decoder;
	   private final CharsetEncoder _encoder;

	   public TBGPMessageTokenizer(Charset charset) {

	      this._decoder = charset.newDecoder();
	      this._encoder = charset.newEncoder();
	   }

	   /**
	    * Add some bytes to the message.  
	    * Bytes are converted to chars, and appended to the internal StringBuffer.
	    * Complete messages can be retrieved using the nextMessage() method.
	    *
	    * @param bytes an array of bytes to be appended to the message.
	    */
	   public synchronized void addBytes(ByteBuffer bytes) {
		   _buffers.add(bytes);
	      
	   }

	   /**
	    * Is there a complete message ready?.
	    * @return true the next call to nextMessage() will not return null, false otherwise.
	    */
	   public synchronized boolean hasMessage() {
		   while(_buffers.size() > 0) {
	           ByteBuffer bytes = _buffers.remove(0);
	           CharBuffer chars = CharBuffer.allocate(bytes.remaining());
	 	      this._decoder.decode(bytes, chars, false); // false: more bytes may follow. Any unused bytes are kept in the decoder.
	 	      chars.flip();
	 	      this._stringBuf.append(chars);
		   }
		   return this._stringBuf.indexOf(this._messageSeparator) > -1;
	   }

	   /**
	    * Get the next complete message if it exists, advancing the tokenizer to the next message.
	    * @return the next complete message, null if no complete message exist. if the command is not valid returns TBGPMessage with null command field
	    */
	   public synchronized TBGPMessage nextMessage() {
	      String message = null;
	      TBGPCommand command = null;
	      String commandString = null;
	      int messageEnd = this._stringBuf.indexOf(this._messageSeparator);
	      int commandEnd = this._stringBuf.indexOf(" ");
	      if (messageEnd > 0) {
	    	 if (commandEnd >-1 && commandEnd < messageEnd){
		    	 commandString = this._stringBuf.substring(0, commandEnd);
		         message = this._stringBuf.substring(commandEnd+" ".length(), messageEnd);
		         this._stringBuf.delete(0, messageEnd+this._messageSeparator.length());
	    	 }else{
	    		 commandString = this._stringBuf.substring(0, messageEnd);
		         this._stringBuf.delete(0, messageEnd+this._messageSeparator.length());
	    	 }
	    	 try{
	    		 command = TBGPCommand.valueOf(commandString);	    	 
	    	 }catch(IllegalArgumentException e){
	    		 command = null;
	    	 }
	    	 if (command == null) message = commandString;  //if command is unidentified we want to tell the client what he sent
	      }else if(messageEnd == 0) {
	    	  commandString = "EMPTY_MESSAGE";
		      this._stringBuf.delete(0, messageEnd+this._messageSeparator.length());
	      }else {
	    	  return null;
	      }
	      return new TBGPMessage(message, command);
	   }

	   /**
	    * Convert the TBGP message into bytes representation, taking care of encoding and framing.
	    *
	    * @return a ByteBuffer with the message content converted to bytes, after framing information has been added.
	    */
	   public ByteBuffer getBytesForMessage(TBGPMessage msg)  throws CharacterCodingException {
	      StringBuilder sb = new StringBuilder(msg.getCommand() + " " + msg.getMessage());
	      sb.append(this._messageSeparator);
	      ByteBuffer bb = this._encoder.encode(CharBuffer.wrap(sb));
	      return bb;
	   }

}
