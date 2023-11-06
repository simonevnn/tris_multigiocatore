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
	private boolean inPartita;
	
	public ConnessionePrimo(Socket richiestaClient, Semaphore primo, Semaphore secondo, int[][] matriceTris, boolean inPartita) {
		
		try {
			
			connessione = richiestaClient;
			
			System.out.println("Connessione richiesta da: "+connessione.getInetAddress().toString()+":"+connessione.getPort());
			
			input = new ObjectInputStream(connessione.getInputStream());
			output = new ObjectOutputStream(connessione.getOutputStream());
			
			this.primo = primo;
			this.secondo = secondo;
			
			this.matriceTris = matriceTris;
			this.inPartita = inPartita;
			
			this.start();
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		
		boolean bloccoSemaforo = false;
		
		while(true) {
			
			try {
				
				if(!bloccoSemaforo)
					primo.acquire();

				try {
					
					Object o = input.readObject();
					
					if(o instanceof Protocollo) {
						
						Protocollo scelta = (Protocollo)o;
						
						if(scelta.getComunicazione().equals(Comunicazione.EXIT)) {
							
							
							
						}
						
						if(controllaMatrice(scelta.getComunicazione())) {
							
							//controllo vincita/sconfitta/uscita
							
						}
						
					}
					
					Protocollo com = new Protocollo(Comunicazione.OP_NACK,"Attendi il tuo turno.");
					
				}
				catch(IOException | ClassNotFoundException e){
					e.printStackTrace();
				}
				
			}
			catch(InterruptedException e) {
				
				bloccoSemaforo = true;
			
				Protocollo com = new Protocollo(Comunicazione.OP_NACK,"Attendi il tuo turno.");
				
				try {
					output.writeObject(com);
				}
				catch(IOException e1){
					e.printStackTrace();
				}
			
			}
			
			if(!bloccoSemaforo)
				secondo.release();
		
		}
		
		try {
			input.close();
			output.close();
			connessione.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean controllaMatrice(Comunicazione scelta) {
		
		int i=0,j=0;
		
		switch(scelta) {
		
			case A1:
				i = 0;
				j = 0;
				break;
			
			case B1:
				i = 0;
				j = 1;
				break;
				
			case C1:
				i = 0;
				j = 2;
				break;
				
			case A2:
				i = 1;
				j = 0;
				break;
				
			case B2:
				i = 1;
				j = 1;
				break;
				
			case C2:
				i = 1;
				j = 2;
				break;
				
			case A3:
				i = 2;
				j = 0;
				break;
				
			case B3:
				i = 2;
				j = 1;
				break;
				
			case C3:
				i = 2;
				j = 2;
				break;
				
			default:
				break;
		
		}
		
		if(matriceTris[i][j]!=0)
			return false;
		
		return true;
		
	}
	
	
	
}
