package TPC;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.util.logging.Logger;
import protocol.AsyncServerProtocol;
import protocol.ProtocolCallback;
import protocol.ServerProtocol;
import protocol.TPCCallbackFactory;
import tokenizer.MessageTokenizer;

public class TPCConnectionHandler<T> implements Runnable {
	SocketChannel clientSocket;
	ServerProtocol<T> protocol;
	MessageTokenizer<T> tokenizer;
	ProtocolCallback<T> callback;
	private TPCCallbackFactory<T> callbackFactory;
	private static final int BUFFER_SIZE = 1024;
	private static final Logger logger = Logger.getLogger("edu.spl.TPC");

	
	public TPCConnectionHandler(SocketChannel socketChannel, ServerProtocol<T> p, MessageTokenizer<T> tok, TPCCallbackFactory<T> callbackFac) {
		clientSocket = socketChannel;
		protocol = p;
		tokenizer = tok;
		callbackFactory = callbackFac;
		System.out.println("Accepted connection from client!");
		System.out.println("The client is from: " + socketChannel.socket().getRemoteSocketAddress());
	}
	
	public void run() {		
			initialize();
		try {
			process();
		} 
		catch (IOException e) {
			System.out.println("Error in I/O");
		} 
		
		System.out.println("Connection closed - bye bye...");
		close();

	}
	
	public void process() throws IOException {
		T msg;
		ByteBuffer buff = ByteBuffer.allocate(BUFFER_SIZE );
		int numBytesRead;
		
		while (true){
			numBytesRead = 0;
			try {
				numBytesRead = clientSocket.read(buff);
			} catch (IOException e) {
				numBytesRead = -1;
			}
			// is the channel closed??
			if (numBytesRead == -1) {
				// No more bytes can be read from the channel
				logger.info("client on " + clientSocket.socket().getRemoteSocketAddress() + " has disconnected");
				close();
				// tell the protocol that the connection terminated.
				if (protocol instanceof AsyncServerProtocol){
					((AsyncServerProtocol<T>)protocol).connectionTerminated();
				}
				return;
			}
			buff.flip();
			tokenizer.addBytes(buff);
			if (tokenizer.hasMessage()) {
			         msg = tokenizer.nextMessage();
			         protocol.processMessage(msg, callback);
			         if (protocol.isEnd(msg))break;
			}
			buff.clear();
		}
	}
	
	public void sendMessage(T msg) {
		
		ByteBuffer buf;
		try {
			buf = tokenizer.getBytesForMessage(msg);
			while (buf.remaining() != 0) {
				clientSocket.write(buf);
			}
		} catch (CharacterCodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	// Starts listening
	public void initialize()  {
		callback = callbackFactory.create(this);
	}
	
	// Closes the connection
	public void close()
	{
		try {			
			clientSocket.close();
		}
		catch (IOException e){
			System.out.println("Exception in closing I/O");
		}
	}
}
