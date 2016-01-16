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

	
	
	public TPCserver(int port, ServerProtocolFactory<T> p, TokenizerFactory<T> t, TPCCallbackFactory<T> c)
	{
		serverSocket = null;
		listenPort = port;
		factory = p;
		tokenizerFactory =  t;
		callbackFactory = c;
	}
	
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
	
	public void run()
	{
		try {
			serverSocket = createServerSocket(listenPort);
			System.out.println("Listening...");
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
	

	// Closes the connection
	public void close() throws IOException
	{
		serverSocket.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		 if (args.length != 2) {
	            System.err.println("Usage: java TPCserver <port> <json_paths>");
	            System.exit(1);
		 }
		 
		int port = Integer.decode(args[0]).intValue();
		String[] jsonPaths = {"jsonExample/" + args[1] + ".json"};
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
