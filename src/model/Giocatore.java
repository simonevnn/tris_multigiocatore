package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import view.FinestraGioco;

public class Giocatore {

	private Socket connessione;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private FinestraGioco finestra;
	
	public Giocatore(String indirizzo) throws IOException {
		
		connessione = new Socket(indirizzo,8081);
		
		output = new ObjectOutputStream(connessione.getOutputStream());
		input = new ObjectInputStream(connessione.getInputStream());
		
	}
	
	public void setFinestra(FinestraGioco finestra) {
		this.finestra = finestra;
	}

	public void inviaScelta(Comunicazione scelta) {
			
		Protocollo com = new Protocollo(scelta);
		
		try {
			
			output.writeObject(com);
				
			Object o = input.readObject();
			
			if(o instanceof Protocollo) {
				
				com = (Protocollo)o;
				
				if(com.getComunicazione().equals(Comunicazione.OP_ACK)) {
					
					if(com.getMatriceTris()!=null)
						mostraMatrice(com.getMatriceTris());
					
				}
				else if(com.getComunicazione().equals(Comunicazione.OP_NACK)) {
					
					if(com.getMessaggio()!=null)
						finestra.mostraErrore("Errore: "+com.getMessaggio());
				
				}
				else if(com.getComunicazione().equals(Comunicazione.VITTORIA)) {
					//istruzioni vittoria
					
				}
				else if(com.getComunicazione().equals(Comunicazione.SCONFITTA)) {
					//istruzioni sconfitta
					
				}
				
			}
			else
				System.out.println("Errore: Classe corrotta ricevuta dal server.");
			
		}
		catch(IOException | ClassNotFoundException e) {
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
	
}
