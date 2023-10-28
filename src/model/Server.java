package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

	private ServerSocket server;
	private Socket richiestaClient;
	
	public Server() {
		
		try {
			
			server = new ServerSocket(8081,2);
			
			System.out.println("SERVER ATTIVO");
			
			this.start();
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		
		try {
			
			while(true) {
				
				richiestaClient = server.accept();
				
				new Connessione(richiestaClient);
				
			}
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
