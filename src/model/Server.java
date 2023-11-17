package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server extends Thread {

	private ServerSocket server;	//socket del server
	private Socket richiestaPrimo;	//connessione con il primo giocatore
	private Socket richiestaSecondo;	//connessione con il secondo giocatore
	private ObjectOutputStream outputPrimo;	//buffer di output del primo giocatore
	private ObjectInputStream inputPrimo;	//buffer di input del primo giocatore
	private ObjectOutputStream outputSecondo;	//buffer di output del secondo giocatore
	private ObjectInputStream inputSecondo;	//buffer di input del secondo giocatore
	private int[][] matriceTris;	//matrice di interi che rappresenta la griglia del tris (0 = nessuna scelta, 1 = cerchio, 2 = x)
	
	public Server() {
		
		try {
			
			server = new ServerSocket(8081,2);	//apriamo la connessione del server
			//server.setSoTimeout(10000);	//impostiamo un tempo massimo entro il quale i giocatori possono connettersi
			
			System.out.println("SERVER ATTIVO");
			
			this.start();	//facciamo partire il thread
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		
		while(true) {	//per sempre
			
			try {
				
				richiestaPrimo = server.accept();	//si connette col primo giocatore
				
				System.out.println("Prima connessione richiesta da: "+richiestaPrimo.getInetAddress().toString()+":"+richiestaPrimo.getPort());
				
				outputPrimo = new ObjectOutputStream(richiestaPrimo.getOutputStream());	//apre i buffer
				inputPrimo = new ObjectInputStream(richiestaPrimo.getInputStream());
				
				richiestaSecondo = server.accept();	//si connette col secondo giocatore
				
				System.out.println("Seconda connessione richiesta da: "+richiestaSecondo.getInetAddress().toString()+":"+richiestaSecondo.getPort());
				
				outputSecondo = new ObjectOutputStream(richiestaSecondo.getOutputStream());
				inputSecondo = new ObjectInputStream(richiestaSecondo.getInputStream());
				
				scrivi(outputPrimo,new Protocollo(Comunicazione.START));	//da il via libera ad entrambi i giocatori
				scrivi(outputSecondo,new Protocollo(Comunicazione.START));
				
				gioco();	//parte il gioco, quando termina si aspettano due nuovi giocatori
				
			}
			catch(IOException e) {
				chiudi();	//la chiusura della connessione è triggerata dalle eccezioni
			}
			
		}
		
	}
	
	private void gioco() throws IOException {	//le IOException di questo metodo e di quelli chiamati in questo vengono lanciate al run(), che le catturerà terminando la partita in modo sicuro (le eccezioni vengono lanciate principalmente quando i giocatori abbandonano)
		
		inizializzaMat();	//inizializza la matrice di interi tutta a 0
		
		Protocollo com = new Protocollo(Comunicazione.OP_ACK,matriceTris);
		int i = 0;
		
		scrivi(outputPrimo,com);	//scriviamo una comunicazione al primo giocatore per sincronizzare il meccanismo lettura/scrittura/lettura

		while(true) {		

			if(i%2==0)	//per alternare i giocatori
				com = leggi(inputPrimo);	//legge la scelta e genera una risposta
			else 
				com = leggi(inputSecondo);
			
			if(i%2!=0 && com.getComunicazione().equals(Comunicazione.VITTORIA))	//nel turno del secondo giocatore, se questo vince si deve comunicare al primo che ha perso
				com.setComunicazione(Comunicazione.SCONFITTA);
			
			scrivi(outputPrimo,com);	//scriviamo al primo giocatore
			
			if(i%2==0 && com.getComunicazione().equals(Comunicazione.VITTORIA))	//nel turno del primo giocatore, se questo vince si deve comunicare al secondo che ha perso
				com.setComunicazione(Comunicazione.SCONFITTA);
			else if(i%2!=0 && com.getComunicazione().equals(Comunicazione.SCONFITTA))	//se si ha comunicato al primo che ha perso bisogna ri-cambiare in vittoria per dirlo al secondo
				com.setComunicazione(Comunicazione.VITTORIA);
				
			scrivi(outputSecondo,com);	//scriviamo al secondo giocatore
			
			if(i%2==0)
				leggi(inputPrimo);	//alternandosi, i giocatori risponderanno con messaggi di ACK, senza una scelta, quindi non serve generare una risposta
			else
				leggi(inputSecondo);
			
			i++;

		}
		
	}
	
	private void scrivi(ObjectOutputStream output, Protocollo com) throws IOException {	//scrittura dinamica in base al buffer di output
		output.reset();	//per ripulire il buffer
		output.writeObject(com);	//scrittura al giocatore
	}
	
	private Protocollo leggi(ObjectInputStream input) throws IOException {	//lettura dinamica in base al buffer di input passato
		
		Protocollo com = null;
		int g = 0;
			
		try {
			
			Object o = input.readObject();	//leggiamo l'oggetto dal client
			
			if(o instanceof Protocollo) {
				
				com = (Protocollo)o;
				
				if(!com.getComunicazione().equals(Comunicazione.OP_ACK)) {	//se non è un semplice ACK, ma una scelta
					
					if(input.equals(inputPrimo))	//capiamo da quale giocatore abbiamo letto la scelta
						g = 1;
					else
						g = 2;
						
					aggiornaMat(com.getComunicazione(),g);	//aggiorniamo la matrice con la nuova scelta
					
					switch(controllo(g)) {	//controllo sulla matrice
					
						case -1:	//nessun risultato
							com = new Protocollo(Comunicazione.OP_ACK,matriceTris);
							break;
						
						case 0:	//nessun risultato + griglia piena
							com = new Protocollo(Comunicazione.PAREGGIO,matriceTris);
							break;
							
						case 1:	//vittoria del giocatore
							com = new Protocollo(Comunicazione.VITTORIA,matriceTris);
							break;
						
						default:
							break;
					
					}
					
				}
				
			}
			else
				com = new Protocollo(Comunicazione.OP_NACK,"Classe corrotta ricevuta dal server.");	//comunichiamo errore
			
		}
		catch(ClassNotFoundException e){}
		
		return com;	//ritorniamo la risposta generata
		
	}
	
	private void chiudi() {
		
		try {
			
			try {
				scrivi(outputPrimo,new Protocollo(Comunicazione.EXIT));	//proviamo a scrivere ai due giocatori che il gioco è terminato, se non rispondono vuol dire che se ne sono già andati e catturiamo l'eccezione
			}
			catch(IOException e) {}
			
			try {
				scrivi(outputSecondo,new Protocollo(Comunicazione.EXIT));
			}
			catch(IOException e) {}
			
			outputPrimo.close();	//chiudiamo i buffer
			inputPrimo.close();
			
			outputSecondo.close();
			inputSecondo.close();
			
			richiestaPrimo.close();	//chiudiamo le connessioni
			richiestaSecondo.close();
			
		}
		catch(IOException e) {}
		
	}

	private void inizializzaMat() {
		
		matriceTris = new int[3][3];	//matrice 3x3 da inizializzare tutta a 0
		
		for(int i=0;i<matriceTris.length;i++) {
			for(int j=0;j<matriceTris[0].length;j++)
				matriceTris[i][j] = 0;
		}
		
	}
	
	private void aggiornaMat(Comunicazione scelta, int val) {
		
		switch(scelta) {	//per capire su che cella l'utente vuole scrivere
		
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
	
	private int controllo(int giocatore) {    //-1 nessun risultato, 0 griglia piena, 1 vittoria
       
        int ris = -1;	//valore di default
        boolean pieno = true;

        if(matriceTris[0][0] == giocatore) {	//controllo diagonale destra-sinistra
        	
            if(matriceTris[0][0] == matriceTris[1][1] && matriceTris[1][1] == matriceTris[2][2])
                ris = 1;
            
        }
        
        if(matriceTris[0][2] == giocatore) {	//controllo diagonale sinistra-destra
        	
            if(matriceTris[0][2] == matriceTris[1][1] && matriceTris[1][1] == matriceTris[2][0])
                ris = 1;
        
        }
        
        for(int i=0;i<matriceTris.length;i++) {
            
        	if (matriceTris[i][0] == giocatore) {	//controllo su righe 
                
        		if(matriceTris[i][0] == matriceTris[i][1] && matriceTris[i][1] == matriceTris[i][2])
                    ris = 1;

            }

            if(matriceTris[0][i] == giocatore) {	//controllo su colonne
                
            	if(matriceTris[0][i] == matriceTris[1][i] && matriceTris[1][i] == matriceTris[2][i])
                    ris = 1;

            }
            
        }
       
        for(int i=0;i<matriceTris.length;i++) {
            
        	for(int j=0;j<matriceTris[0].length;j++) {
            	
            	if(matriceTris[i][j]==0) {
            		pieno = false;	//controlliamo se c'è ancora spazio sulla griglia
            		break;
            	}
            		
            }
        	
        }
        
        if(pieno && ris!=1)	//se la griglia è piena e il giocatore non ha vinto
        	ris = 0;

        return ris;

    }
	
}