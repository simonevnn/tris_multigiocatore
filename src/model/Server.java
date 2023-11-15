package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

	private ServerSocket server;
	private Socket richiestaPrimo;
	private Socket richiestaSecondo;
	private ObjectOutputStream outputPrimo;
	private ObjectInputStream inputPrimo;
	private ObjectOutputStream outputSecondo;
	private ObjectInputStream inputSecondo;
	private int[][] matriceTris;
	private boolean inPartita;
	
	public Server() {
		
		try {
			
			server = new ServerSocket(8081,2);
			
			System.out.println("SERVER ATTIVO");
			
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
				
				richiestaPrimo = server.accept();
				
				System.out.println("Connessione richiesta da: "+richiestaPrimo.getInetAddress().toString()+":"+richiestaPrimo.getPort());
				
				outputPrimo = new ObjectOutputStream(richiestaPrimo.getOutputStream());
				inputPrimo = new ObjectInputStream(richiestaPrimo.getInputStream());
				
				richiestaSecondo = server.accept();
				
				System.out.println("Connessione richiesta da: "+richiestaSecondo.getInetAddress().toString()+":"+richiestaSecondo.getPort());
				
				outputSecondo = new ObjectOutputStream(richiestaSecondo.getOutputStream());
				inputSecondo = new ObjectInputStream(richiestaSecondo.getInputStream());
				
				inPartita = true;
				
				scrivi(outputPrimo,new Protocollo(Comunicazione.START));
				scrivi(outputSecondo,new Protocollo(Comunicazione.START));
				
				gioco();
				
			}
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void gioco() {
		System.out.println("gioco avviato");
		
		Protocollo com = new Protocollo(Comunicazione.OP_ACK,matriceTris);
		int i = 0;
		
		scrivi(outputPrimo,com);
		System.out.println("scritto al primo (fuori dal while)");
		while(inPartita) {		

			if(i%2==0) {
				com = leggi(inputPrimo);
				System.out.println("scelta dal primo");
			}
			else {
				com = leggi(inputSecondo);
				System.out.println("scelta dal secondo");
			}
			
			scrivi(outputPrimo,com);
			scrivi(outputSecondo,com);
			System.out.println("broadcast effettuatop");
			
			if(i%2==0)
				leggi(inputPrimo);
			else
				leggi(inputSecondo);
			
			i++;

		}
		
		try {
			
			richiestaPrimo.close();
			richiestaSecondo.close();
			
			outputPrimo.close();
			inputPrimo.close();
			
			outputSecondo.close();
			inputSecondo.close();
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void scrivi(ObjectOutputStream output, Protocollo com) {
		
		try {
			
			if(com.getMatriceTris()!=null) {
				for(int i=0;i<matriceTris.length;i++) {
					for(int j=0;j<matriceTris[0].length;j++) 
						System.out.println(matriceTris[i][j]);						
				}
			}
			
			output.reset();
			output.writeObject(com);
			
		}
		catch(IOException e) {
		}
		
	}
	
	private Protocollo leggi(ObjectInputStream input) {
		
		Protocollo com = null;
		int g = 0;
			
		try {
			
			Object o = input.readObject();
			
			if(o instanceof Protocollo) {
				
				com = (Protocollo)o;
				
				switch(com.getComunicazione()) {
				
					case OP_ACK:
						break;
					
					case EXIT:
						com = new Protocollo(Comunicazione.EXIT);
						inPartita = false;
						break;
					
					default:
						
						if(input.equals(inputPrimo))
							g = 1;
						else
							g = 2;
							
						aggiornaMat(com.getComunicazione(),g);
						
						switch(controllo(matriceTris,g)) {
						
							case -1:
								com = new Protocollo(Comunicazione.OP_ACK,matriceTris);
								break;
							
							case 0:
								com = new Protocollo(Comunicazione.PAREGGIO,matriceTris);
								break;
								
							case 1:
								com = new Protocollo(Comunicazione.VITTORIA,matriceTris);
								break;
							
							case 2:
								com = new Protocollo(Comunicazione.SCONFITTA,matriceTris);
								break;
							
							default:
								break;
						
						}
						
						break;
					
				}
				
			}
			else
				com = new Protocollo(Comunicazione.OP_NACK,"Classe corrotta ricevuta dal server.");
			
		}
		catch(IOException | ClassNotFoundException e){
		}
		
		return com;
		
	}
	
	private void inizializzaMat() {
		
		matriceTris = new int[3][3];
		
		for(int i=0;i<matriceTris.length;i++) {
			for(int j=0;j<matriceTris[0].length;j++)
				matriceTris[i][j] = 0;
		}
		
	}
	
	private void aggiornaMat(Comunicazione scelta, int val) {
		
		switch(scelta) {
		
			case A1:
				matriceTris[0][0] = val;
				break;
	
			case B1:
				matriceTris[0][1] = val;
				break;
				
			case C1:
				matriceTris[0][2] = val;
				break;
				
			case A2:
				matriceTris[1][0] = val;
				break;
				
			case B2:
				matriceTris[1][1] = val;
				break;
				
			case C2:
				matriceTris[1][2] = val;
				break;
				
			case A3:
				matriceTris[2][0] = val;
				break;
				
			case B3:
				matriceTris[2][1] = val;
				break;
				
			case C3:
				matriceTris[2][2] = val;
				break;
		
			default:
				break;
			
		}
		
	}
	
	private int controllo(int[][] matrice, int giocatore) {	//-1 nessun risultato, 0 griglia piena, 1 vittoria, 2 sconfitta
		
		
		
		return -1;
		
	}

	public static void main(String[] args) {
		Server server = new Server();
	}
	
}
