package TPC;

import java.io.*;
import java.net.*;

import protocol.AsyncServerProtocol;
import protocol.ServerProtocolFactory;
import protocol.TBGP;
import tokenizer.TBGPMessage;

public class TPCserver<T> implements Runnable {

	private ServerSocket serverSocket;
	private int listenPort;
	private ServerProtocolFactory<T> factory;
	
	
	public TPCserver(int port, ServerProtocolFactory<T> p)
	{
		serverSocket = null;
		listenPort = port;
		factory = p;
	}
	
	public void run()
	{
		try {
			serverSocket = new ServerSocket(listenPort);
			System.out.println("Listening...");
		}
		catch (IOException e) {
			System.out.println("Cannot listen on port " + listenPort);
		}
		
		while (true)
		{
			try {
				TPCConnectionHandler newConnection = new TPCConnectionHandler(serverSocket.accept(), factory.create());
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
		// Get port
		int port = Integer.decode(args[0]).intValue();
		
		TPCserver<TBGPMessage> server = new TPCserver<TBGPMessage>(port, new ServerProtocolFactory<TBGPMessage>() {
            public AsyncServerProtocol<TBGPMessage> create() {
                return new TBGP();
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
