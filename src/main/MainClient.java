package main;

import control.*;
import view.*;

public class MainClient {

	public static void main(String[] args) {
		FinestraPrincipale fp = new FinestraPrincipale();	//avviamo la finestra di connessione ed il controller
		Controller ctr = new Controller(fp);
	}

}