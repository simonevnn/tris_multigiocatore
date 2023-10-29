package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import control.ControllerGioco;
import view.FinestraGioco;

public class Giocatore {

	private Socket connessione;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public Giocatore(String indirizzo) throws IOException {
		
		connessione = new Socket(indirizzo,8081);
		
		output = new ObjectOutputStream(connessione.getOutputStream());
		input = new ObjectInputStream(connessione.getInputStream());
			
		FinestraGioco fg = new FinestraGioco();
		ControllerGioco cg = new ControllerGioco();
		
	}
	
}
