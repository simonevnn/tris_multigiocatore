package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ConnessioneSecondo extends Thread {

	private Socket connessione;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Semaphore primo;
	private Semaphore secondo;
	private int[][] matriceTris;
	private boolean inPartita;
	
	public ConnessioneSecondo(Socket richiestaClient, Semaphore primo, Semaphore secondo, int[][] matriceTrism, boolean inPartita) {
		
		try {
			
			connessione = richiestaClient;
			
			System.out.println("Connessione richiesta da: "+connessione.getInetAddress().toString()+":"+connessione.getPort());
			
			input = new ObjectInputStream(connessione.getInputStream());
			output = new ObjectOutputStream(connessione.getOutputStream());
			
			this.primo = primo;
			this.secondo = secondo;
			
			this.matriceTris = matriceTris;
			this.inPartita = true;
			
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
				
				Object o = input.readObject();
				
				if(o instanceof Protocollo) {
					
					Protocollo com = (Protocollo)o;
					
					if(com.getComunicazione().equals(Comunicazione.EXIT)) {
						
						inPartita = false;
						
						com = new Protocollo(Comunicazione.OP_ACK);
						
						output.writeObject(com);
						
						input.close();
						output.close();
						connessione.close();
						
						break;
						
					}
					else {
						
						try {
							
							if(!bloccoSemaforo)
								secondo.acquire();
							
							aggiorna(com.getComunicazione());
							
							if(controllo()==-1)
								com = new Protocollo(Comunicazione.OP_ACK,matriceTris);
							else if(controllo()==1)
								com = new Protocollo(Comunicazione.VITTORIA);
							else if(controllo()==0)
								com = new Protocollo(Comunicazione.SCONFITTA);
							else if(!inPartita)
								com = new Protocollo(Comunicazione.EXIT);
							
							bloccoSemaforo = false;
							
						}
						catch(InterruptedException e) {
							com = new Protocollo(Comunicazione.OP_NACK,"Attendi il tuo turno.");
							bloccoSemaforo = true;
						}
						
						output.writeObject(com);
						
						if(!bloccoSemaforo)
							primo.release();
						
					}
					
				}
				
			}
			catch(IOException | ClassNotFoundException e){
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void aggiorna(Comunicazione scelta) {
		
		switch(scelta) {
		
			case A1:
				matriceTris[0][0] = 2;
				break;

			case B1:
				matriceTris[0][1] = 2;
				break;
				
			case C1:
				matriceTris[0][2] = 2;
				break;
				
			case A2:
				matriceTris[1][0] = 2;
				break;
				
			case B2:
				matriceTris[1][1] = 2;
				break;
				
			case C2:
				matriceTris[1][2] = 2;
				break;
				
			case A3:
				matriceTris[2][0] = 2;
				break;
				
			case B3:
				matriceTris[2][1] = 2;
				break;
				
			case C3:
				matriceTris[2][2] = 2;
				break;
		
			default:
				break;
				
		}
		
	}
	
	private int controllo() {	//-1 nessun risultato, 1 vittoria, 0 sconfitta
		
		
		
		return -1;
		
	}
	
	
}
