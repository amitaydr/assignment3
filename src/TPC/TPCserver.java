package TPC;

import java.io.*;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import app.GameManager;
import protocol.AsyncServerProtocol;
import protocol.ProtocolCallback;
import protocol.ServerProtocolFactory;
import protocol.TBGP;
import protocol.TPCCallbackFactory;
import tokenizer.MessageTokenizer;
import tokenizer.TBGPMessage;
import tokenizer.TBGPMessageTokenizer;
import tokenizer.TokenizerFactory;

public class TPCserver<T> implements Runnable {

	private ServerSocketChannel serverSocket;
	private int listenPort;
	private ServerProtocolFactory<T> factory;
	private TokenizerFactory<T> tokenizerFactory;
	private TPCCallbackFactory<T> callbackFactory;
	private static final Logger logger = Logger.getLogger("edu.spl.TPC");

	
	/**
	 *  the TPCserver constructor
	 *  
	 * @param port the port number for the server socket
	 * @param p server protocol factory needed to create new protocols for new connections
	 * @param t tokenizer factory needed to create new tokenizers for new connections
	 * @param c callback factory in order to create a new callback fo every new connection
	 */
	public TPCserver(int port, ServerProtocolFactory<T> p, TokenizerFactory<T> t, TPCCallbackFactory<T> c)
	{
		serverSocket = null;
		listenPort = port;
		factory = p;
		tokenizerFactory =  t;
		callbackFactory = c;
	}
	
	/**
	 * creates a server socket channel and binds it to the given port in a *blocking* way (unlike reactor)
	 * @param port port to bind the server socket to
	 * @return the new server socket 
	 * @throws IOException if the port is busy
	 */
	private ServerSocketChannel createServerSocket(int port)
            throws IOException {
        try {
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.socket().bind(new InetSocketAddress(port));
            return ssChannel;
        } catch (IOException e) {
            logger.info("Port " + port + " is busy");
            throw e;
        }
    }
	 
	/**
	 * first- creating the server socket channel. 
	 * then, waiting for connections in the serverSocket.accept() method (it is a blocking method)
	 * for every new TPCconnection create a new connectionHandler and run it in a new thread
	 */
	public void run()
	{
		try {
			serverSocket = createServerSocket(listenPort);
			System.out.println("Listening on port " + listenPort );
		}
		catch (IOException e) {
			System.out.println("Cannot listen on port " + listenPort);
		}
		
		while (true)
		{
			try {
				TPCConnectionHandler<T> newConnection = new TPCConnectionHandler<T>(serverSocket.accept(), factory.create(), tokenizerFactory.create(),callbackFactory);
            new Thread(newConnection).start();
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept on port " + listenPort);
			}
		}
	}
	

	/**
	 * closes the connection
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		serverSocket.close();
	}
	
	/**
	 * main function- creates and runs a TBGP TPC server 
	 * @param args  first argument- port number. second argument- json paths for the games
	 * @throws IOException
	 */
	
	public static void main(String[] args) throws IOException
	{
		 if (args.length != 2) {
	            System.err.println("Usage: java TPCserver <port> <json_paths>");
	            System.exit(1);
		 }
		 
		int port = Integer.decode(args[0]).intValue();
		String[] jsonPaths = {args[1] + ".json"};
        GameManager.getInstance().initialize(jsonPaths);
		
		TPCserver<TBGPMessage> server = new TPCserver<TBGPMessage>(port, new ServerProtocolFactory<TBGPMessage>() {
            public AsyncServerProtocol<TBGPMessage> create() {
                return new TBGP();
            }
        }, new TokenizerFactory<TBGPMessage>(){
			public MessageTokenizer<TBGPMessage> create() {
		        final Charset charset = Charset.forName("UTF-8");
				return new TBGPMessageTokenizer(charset);
			}
        }, new TPCCallbackFactory<TBGPMessage>(){

			@Override
			public ProtocolCallback<TBGPMessage> create(TPCConnectionHandler<TBGPMessage> handler) {
				return new TBGPTCPCallback(handler);
			}
        	
        });
		
		Thread serverThread = new Thread(server);
      serverThread.start();
		try {
			serverThread.join();
		}
		catch (InterruptedException e)
		{
			System.out.println("Server stopped");
		}
		
		
				
	}
}
