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
	
	private void attendi() {
		
		try {
			
			Object o = input.readObject();
			
			if(o instanceof Protocollo) {
				
				Protocollo temp = (Protocollo)o;
				
				switch(temp.getComunicazione()) {
				
					case START:
						finestra.sbloccaBottoni();
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
		
		System.out.println("in attesa");
		attendi();
		System.out.println("attesa terminata");
		
		Protocollo com = null;
		
		while(inPartita) {

			try {
				
				lettura.acquire();
				System.out.println("lettura acquisito");
				try {
					
					Object o = input.readObject();
					System.out.println("oggetto letto");
					if(o instanceof Protocollo) {
						System.out.println("oggetto è protocollo");
						com = (Protocollo)o;
						
						switch(com.getComunicazione()) {
					
							case OP_ACK:

								if(com.getMatriceTris()!=null) {
									System.out.println("matrice non nulla");
									mostraMatrice(com.getMatriceTris());
								}

								if(primaLettura) {
									primaLettura = false;
									scrittura.release();
									System.out.println("prima lettura effettuata");
								}
								else {
									primaLettura = true;
									lettura.release();
									System.out.println("seconda lettura effettuata");
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
								
								if(primaLettura) {
									primaLettura = false;
									scrittura.release();
									System.out.println("prima lettura effettuata");
								}
								else {
									primaLettura = true;
									lettura.release();
									System.out.println("seconda lettura effettuata");
								}
								
								break;
								
							case SCONFITTA:
								
								if(com.getMatriceTris()!=null)
									mostraMatrice(com.getMatriceTris());
								
								finestra.mostraMessaggio("HAI PERSO!");
								inPartita = false;
								
								if(primaLettura) {
									primaLettura = false;
									scrittura.release();
									System.out.println("prima lettura effettuata");
								}
								else {
									primaLettura = true;
									lettura.release();
									System.out.println("seconda lettura effettuata");
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
				System.out.println("lettura occupato");
			}
			
		}
		
	}

	public void inviaScelta(Comunicazione scelta) {
		
		try {
			
			scrittura.acquire();
			System.out.println("scrittura acquisito");
			Protocollo com = new Protocollo(scelta);
			
			try {
				output.writeObject(com);
				System.out.println("scritto al server");
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		
			lettura.release();
			
		}
		catch(InterruptedException e) {
			System.out.println("scrittura occupato");
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
				System.out.println("mostra matrice");
				if(matrice[i][j]==1)
					finestra.changeBtnText(i, j, "O");
				else if(matrice[i][j]==2)
					finestra.changeBtnText(i, j, "X");
				
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
