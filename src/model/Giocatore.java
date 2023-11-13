package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

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
		
		Protocollo com = null;
		
		while(inPartita) {

			try {
				
				lettura.acquire();

				try {
					
					Object o = input.readObject();
					
					if(o instanceof Protocollo) {

						com = (Protocollo)o;
						
						switch(com.getComunicazione()) {
					
							case OP_ACK:

								if(com.getMatriceTris()!=null)
									mostraMatrice(com.getMatriceTris());

								if(!primaLettura) {
									primaLettura = true;
									scrittura.release();
									inviaScelta(Comunicazione.OP_ACK);
								}
								else {
									primaLettura = false;
									scrittura.release();
								}
								
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
								
								if(!primaLettura) {
									primaLettura = true;
									scrittura.release();
									inviaScelta(Comunicazione.OP_ACK);
								}
								else {
									primaLettura = false;
									scrittura.release();
								}
								
								break;
								
							case SCONFITTA:
								
								if(com.getMatriceTris()!=null)
									mostraMatrice(com.getMatriceTris());
								
								finestra.mostraMessaggio("HAI PERSO!");
								inPartita = false;
								
								if(!primaLettura) {
									primaLettura = true;
									scrittura.release();
									inviaScelta(Comunicazione.OP_ACK);
								}
								else {
									primaLettura = false;
									scrittura.release();
								}
								
								break;
								
							case EXIT:
								
								finestra.mostraMessaggio("L'AVVERSARIO HA ABBANDONATO!");
								inPartita = false;
								
								break;
								
							default:
								lettura.release();
								break;
								
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
		
		try {
			
			scrittura.acquire();
			
			Protocollo com = new Protocollo(scelta);
			
			try {
				output.writeObject(com);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		
			lettura.release();
			
		}
		catch(InterruptedException e) {
			finestra.mostraMessaggio("Aspetta il tuo turno.");
		}
		
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
				
				if(matrice[i][j]==1)
					finestra.getBtnMatrice(i,j).setText("O");
				else if(matrice[i][j]==2)
					finestra.getBtnMatrice(i,j).setText("X");
				
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
