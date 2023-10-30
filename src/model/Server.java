package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Server extends Thread {

	private ServerSocket server;
	private Socket richiestaClient;
	private Semaphore primo;
	private Semaphore secondo;
	
	public Server() {
		
		try {
			
			server = new ServerSocket(8081,2);
			
			System.out.println("SERVER ATTIVO");
			
			primo = new Semaphore(1);
			secondo = new Semaphore(0);
			
			this.start();
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		
		try {
			
			richiestaClient = server.accept();
			
			new ConnessionePrimo(richiestaClient,primo,secondo);
			
			richiestaClient = server.accept();
			
			new ConnessioneSecondo(richiestaClient,primo,secondo);
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
