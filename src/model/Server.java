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
	private int[][] matriceTris;
	private boolean inPartita;
	
	public Server() {
		
		try {
			
			server = new ServerSocket(8081,2);
			
			System.out.println("SERVER ATTIVO");
			
			primo = new Semaphore(1);
			secondo = new Semaphore(0);
			
			inizializzaMat();
			
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
				
				new ConnessionePrimo(richiestaClient,primo,secondo,matriceTris,inPartita);
				
				richiestaClient = server.accept();
				
				new ConnessioneSecondo(richiestaClient,primo,secondo,matriceTris,inPartita);
				
			}
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}

	private void inizializzaMat() {
		
		matriceTris = new int[3][3];
		
		for(int i=0;i<matriceTris.length;i++) {
			for(int j=0;j<matriceTris[0].length;j++)
				matriceTris[i][j] = 0;
		}
		
	}
	
}
