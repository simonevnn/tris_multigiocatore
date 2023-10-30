package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ConnessionePrimo extends Thread {

	private Socket connessione;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Semaphore primo;
	private Semaphore secondo;
	private int[][] matriceTris;
	
	public ConnessionePrimo(Socket richiestaClient, Semaphore primo, Semaphore secondo, int[][] matriceTris) {
		
		try {
			
			connessione = richiestaClient;
			
			System.out.println("Connessione richiesta da: "+connessione.getInetAddress().toString()+":"+connessione.getPort());
			
			input = new ObjectInputStream(connessione.getInputStream());
			output = new ObjectOutputStream(connessione.getOutputStream());
			
			this.primo = primo;
			this.secondo = secondo;
			
			this.matriceTris = matriceTris;
			
			this.start();
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Connessione con il primo giocatore avviata con successo.");
	}
	
}
