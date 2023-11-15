package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;

import view.FinestraGioco;

public class Giocatore extends Thread {

	private Socket connessione;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private FinestraGioco finestra;
	private Semaphore lettura;
	private Semaphore scrittura;
	private boolean primaLettura;
	private boolean inPartita;
	
	public Giocatore(String indirizzo) throws IOException {
		
		connessione = new Socket(indirizzo,8081);
		
		output = new ObjectOutputStream(connessione.getOutputStream());
		input = new ObjectInputStream(connessione.getInputStream());
		
		lettura = new Semaphore(1);
		scrittura = new Semaphore(0);
		
		primaLettura = inPartita = true;
		
		this.start();
		
	}

	public void setFinestra(FinestraGioco finestra) {
		this.finestra = finestra;
	}
	
	private void attendi() {
		
		try {
			
			Object o = input.readObject();
			
			if(o instanceof Protocollo) {
				
				Protocollo temp = (Protocollo)o;
				
				switch(temp.getComunicazione()) {
				
					case START:
						finestra.sbloccaBottoni();
						finestra.getLblTitolo().setText("IN PARTITA");
						break;
						
					default:
						throw new IOException();
				
				}
				
			}
				
		}	
		catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * while(true){
	 * 
	 * 		acquisisci lettura
	 * 		leggi
	 * 		rilascia scrittura
	 * 
	 * }
	 * 
	 */
	
	@Override
	public void run() {
		
		attendi();

		while(inPartita) {

			try {
				
				lettura.acquire();

				try {
					
					
					Object o = input.readObject();
					
					if(o instanceof Protocollo) {
						
						Protocollo com = (Protocollo)o;
						
						switch(com.getComunicazione()) {
					
							case OP_ACK:

								if(com.getMatriceTris()!=null)		
									mostraMatrice(com.getMatriceTris());
								
								break;
							
							case OP_NACK:
								
								if(com.getMessaggio()!=null)
									finestra.mostraMessaggio("Errore: "+com.getMessaggio());
								
								break;
								
							case VITTORIA:
								
								if(com.getMatriceTris()!=null)
									mostraMatrice(com.getMatriceTris());
								
								finestra.mostraMessaggio("HAI VINTO!");
								inPartita = false;
								
								break;
								
							case SCONFITTA:
								
								if(com.getMatriceTris()!=null)
									mostraMatrice(com.getMatriceTris());
								
								finestra.mostraMessaggio("HAI PERSO!");
								inPartita = false;
								
								break;
								
							case EXIT:
								
								finestra.mostraMessaggio("L'AVVERSARIO HA ABBANDONATO!");
								inPartita = false;
								
								break;
								
							default:
								lettura.release();
								break;
								
						}
						
						if(primaLettura) {
							primaLettura = false;
							scrittura.release();
						}
						else {
							primaLettura = true;
							scrittura.release();
							inviaScelta(Comunicazione.OP_ACK);
						}
						
					}
					else
						lettura.release();
					
				}
				catch(IOException | ClassNotFoundException e) {
					e.printStackTrace();
					lettura.release();
				}
				
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

	public void inviaScelta(Comunicazione scelta) {
			
			if(scrittura.tryAcquire()) {
				
				Protocollo com = new Protocollo(scelta);
				
				try {
					output.writeObject(com);
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			
				lettura.release();
				
			}
			else
				finestra.mostraMessaggio("Aspetta il tuo turno.");
			
	}
	
	public void chiudiConnessione() {
		
		try {
			
			inPartita = false;
			
			output.close();
			input.close();
			
			connessione.close();
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void mostraMatrice(int[][] matrice) {
		
		for(int i=0;i<matrice.length;i++) {

			for(int j=0;j<matrice[0].length;j++) {
				
				if(matrice[i][j]!=0)
					finestra.scriviScelta(i,j,matrice[i][j]);
				
			}
			
		}
		
	}
	
	public boolean isInPartita() {
		return inPartita;
	}

	public void setInPartita(boolean inPartita) {
		this.inPartita = inPartita;
	}
	
}
