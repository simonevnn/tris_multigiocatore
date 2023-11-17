package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;

import view.FinestraGioco;

public class Giocatore extends Thread {

	private Socket connessione;	//socket di connessione col server
	private ObjectOutputStream output;	//buffer di output
	private ObjectInputStream input;	//buffer di input
	private FinestraGioco finestra;
	private Semaphore lettura;	//semaforo di lettura
	private Semaphore scrittura;	//semaforo di scrittura
	private boolean primaLettura;
	private boolean inPartita;
	
	public Giocatore(String indirizzo) throws IOException {
		
		connessione = new Socket(indirizzo,8081);	//proviamo a connetterci al server, l'eccezione viene catturata nel controller
		
		output = new ObjectOutputStream(connessione.getOutputStream());	//apriamo il canale di output
		input = new ObjectInputStream(connessione.getInputStream());	//apriamo il canale di input
		
		lettura = new Semaphore(1);	//lettura parte da verde
		scrittura = new Semaphore(0);	//scrittura parte da rosso
		
		primaLettura = inPartita = true;
		
	}

	public void setFinestra(FinestraGioco finestra) {
		this.finestra = finestra;
	}
	
	private void attendi() {
		
		try {
			
			Object o = input.readObject();	//aspetta di ricevere il via libera per cominciare la partita
			
			if(o instanceof Protocollo) {
				
				Protocollo temp = (Protocollo)o;
				
				switch(temp.getComunicazione()) {
				
					case START:	//via libera
						finestra.sbloccaBottoni();	//vengono sbloccati i bottoni della griglia
						finestra.getLblTitolo().setText("IN PARTITA");	//viene cambiata la scritta del titolo
						break;
						
					case EXIT:	//se c'era un primo giocatore in coda che però ha abbandonato
						inPartita = false;	//per non entrare nemmeno nel while del run
						finestra.confermaMessaggio("L'AVVERSARIO HA ABBANDONATO!", "Partita terminata");	//fa tornare alla schermata di connessione
						break;
						
					default:
						break;
				
				}
				
			}
				
		}	
		catch(IOException | ClassNotFoundException e) {}
		
	}
	
	@Override
	public void run() {
		
		attendi();	//bisogna attendere che entrambi i giocatori siano entrati

		while(inPartita) {

			try {
				
				lettura.acquire();	//se è libero viene acquisito, altrimenti si attende

				try {
					
					Object o = input.readObject();	//viene letto l'oggetto dal server
					
					if(o instanceof Protocollo) {	//se è di tipo del nostro protocollo
						
						Protocollo com = (Protocollo)o;	//facciamo il casting
						
						switch(com.getComunicazione()) {	//vediamo che cosa ci comunica il server
					
							case OP_ACK:	//normale OK

								if(com.getMatriceTris()!=null)	//se il server ha allegato una matrice
									mostraMatrice(com.getMatriceTris());	//stampiamo le scelte sulla griglia dei bottoni
								
								break;
							
							case OP_NACK:	//errore
								
								if(com.getMessaggio()!=null)
									finestra.mostraErrore("Errore: "+com.getMessaggio());	//viene mostrato errore
								
								break;
								
							case VITTORIA:
								
								inPartita = false;	//termina partita
								
								if(com.getMatriceTris()!=null)
									mostraMatrice(com.getMatriceTris());	//mostriamo la matrice
								
								finestra.confermaMessaggio("VITTORIA!", "Partita terminata");	//per tornare alla finestra di connessione e chiudere tutto
								
								break;
								
							case SCONFITTA:
								
								inPartita = false;
								
								if(com.getMatriceTris()!=null)
									mostraMatrice(com.getMatriceTris());
								
								finestra.confermaMessaggio("SCONFITTA!", "Partita terminata");
								
								break;
								
							case PAREGGIO:
								
								inPartita = false;
								
								if(com.getMatriceTris()!=null)
									mostraMatrice(com.getMatriceTris());
								
								finestra.confermaMessaggio("PAREGGIO!", "Partita terminata");
								
								break;
								
							case EXIT:
								
								inPartita = false;
								
								finestra.confermaMessaggio("L'AVVERSARIO HA ABBANDONATO!", "Partita terminata");
								
								break;
								
							default:
								break;
								
						}
						
						if(inPartita) {	//se la partita non è terminata
							
							if(primaLettura) {	//se è la prima lettura
								primaLettura = false;
								scrittura.release();	//attendiamo la scelta dell'utente
							}
							else {
								primaLettura = true;
								scrittura.release();
								inviaScelta(Comunicazione.OP_ACK);	//altrimenti inviamo semplice risposta al server che abbiamo ricevuto la nuova comunicazione e aspettiamo il nostro turno
							}
							
						}
						
					}
					else
						lettura.release();	//se riceviamo classe corrotta dobbiamo leggere di nuovo
					
				}
				catch(IOException | ClassNotFoundException e) {
					lettura.release();	//in caso di eccezione leggiamo di nuovo
				}
				
			}
			catch(InterruptedException e) {}
			
		}
		
	}

	public void inviaScelta(Comunicazione scelta) {
			
			if(scrittura.tryAcquire()) {	//proviamo a vedere se il semaforo di scrittura è libero (true)
				
				Protocollo com = new Protocollo(scelta);	//creiamo una comunicazione con la scelta dell'utente
				
				try {
					output.writeObject(com);	//scriviamo al server
				}
				catch(IOException e) {}
			
				lettura.release();	//facciamo continuare la ricezione dal server
				
			}
			else
				finestra.mostraErrore("Aspetta il tuo turno.");	//se il client sta leggendo
			
	}
	
	public void chiudiConnessione() {
		
		try {
			
			inPartita = false;
			
			output.close();	//chiusura dei buffer
			input.close();
			
			connessione.close();	//chiusura della connessione
			
		}
		catch(IOException e) {}
		
	}
	
	private void mostraMatrice(int[][] matrice) {
		
		for(int i=0;i<matrice.length;i++) {

			for(int j=0;j<matrice[0].length;j++) {
				
				if(matrice[i][j]!=0)	//se la cella della matrice ha una scelta scritta (1 o 2, mentre 0 significa vuota)
					finestra.scriviScelta(i,j,matrice[i][j]);
				
			}
			
		}
		
	}
	
}
