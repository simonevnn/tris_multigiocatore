package control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import model.Comunicazione;
import model.Giocatore;
import view.*;

public class Controller implements ActionListener, WindowListener {

	private FinestraPrincipale finestraPrincipale;
	private FinestraGioco finestraGioco;
	private Giocatore giocatore;
	
	public Controller(FinestraPrincipale finestraPrincipale) {
		this.finestraPrincipale = finestraPrincipale;
		finestraPrincipale.registraEventi(this);	//il controller viene creato all'inizio con la finestra principale
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==finestraPrincipale.getBtnConnettiti())
			connetti();
		
		if(finestraGioco!=null) {	//per evitare che vengano chiamati i metodi get quando la finestra di gioco non esiste ancora
			
			if(e.getSource()==finestraGioco.getBtnMatrice(0,0))
				giocatore.inviaScelta(Comunicazione.A1);	//inviamo la scelta al server
			
			if(e.getSource()==finestraGioco.getBtnMatrice(0,1))
				giocatore.inviaScelta(Comunicazione.B1);

			if(e.getSource()==finestraGioco.getBtnMatrice(0,2))
				giocatore.inviaScelta(Comunicazione.C1);
			
			if(e.getSource()==finestraGioco.getBtnMatrice(1,0))
				giocatore.inviaScelta(Comunicazione.A2);
			
			if(e.getSource()==finestraGioco.getBtnMatrice(1,1))
				giocatore.inviaScelta(Comunicazione.B2);
			
			if(e.getSource()==finestraGioco.getBtnMatrice(1,2))
				giocatore.inviaScelta(Comunicazione.C2);
			
			if(e.getSource()==finestraGioco.getBtnMatrice(2,0))
				giocatore.inviaScelta(Comunicazione.A3);
			
			if(e.getSource()==finestraGioco.getBtnMatrice(2,1))
				giocatore.inviaScelta(Comunicazione.B3);
			
			if(e.getSource()==finestraGioco.getBtnMatrice(2,2))
				giocatore.inviaScelta(Comunicazione.C3);
			
			// -- USCITA --
			if(e.getSource()==finestraGioco.getBtnEsci())
				conferma();	//chiediamo la conferma di uscita
			
		}
		
	}

	private void connetti() {
		
		String indirizzo = finestraPrincipale.getIndirizzo();	//prende l'indirizzo dalla finestra di connessione
		
		if(validaIndirizzo(indirizzo)) {	
			
			try {
				
				giocatore = new Giocatore(indirizzo);	//prova a connettersi al server creando l'oggetto client (Giocatore)
				
				finestraGioco = new FinestraGioco();	//crea la nuova finestra di gioco
				finestraGioco.registraEventi(this);
				
				giocatore.setFinestra(finestraGioco);	//passa la nuova finestra al giocatore
				
				giocatore.start();	//fa partire il thread del client
				
				finestraPrincipale.setVisible(false);	//nasconde la finestra di connessione

			}
			catch(IOException e) {
				finestraPrincipale.mostraErrore("Errore durante la connessione al server. Inserisci l'indirizzo corretto o riprova più tardi.");	//se la connessione col server non riesce
			}
			
		}
		else
			finestraPrincipale.mostraErrore("Errore: Formato dell'indirizzo non corretto.");	//se l'indirizzo non rispetta la regex
		
		finestraPrincipale.getTextFieldIndirizzo().setText("");	//resetta il campo di testo della finestra di connessione
		
	}
	
	private boolean validaIndirizzo(String indirizzo) {
	    
		String condition = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";	//regex che prevede nnn.nnn.nnn.nnn (n = numero)

	    Pattern p = Pattern.compile(condition);
	    Matcher m = p.matcher(indirizzo);
	    
	    return m.matches();	//vero se l'indirizzo inserito rispetta la regex

	}
	
	private void conferma() {
		
		int s = JOptionPane.showConfirmDialog(finestraGioco.getContentPane(),"Abbandonare il gioco?","Conferma uscita",JOptionPane.YES_NO_CANCEL_OPTION);	//chiediamo all'utente se vuole davvero uscire
		
		if(s==0)
			finestraGioco.dispose();	//chiudendo la finestra viene anche avviata la corretta procedura di chiusura
	
	}
	
	private void esci() {
		
		giocatore.chiudiConnessione();	//chiude la connessione ed i buffer
		giocatore = null;	//resetta il client
		
		finestraPrincipale.setVisible(true);	//mostra nuovamente la finestra di connessione
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		
		if(e.getSource().equals(finestraGioco)) 
			conferma();
		
	}
	
	@Override
	public void windowClosed(WindowEvent e) {
		
		if(e.getSource().equals(finestraGioco))
			esci();	//se viene chiusa la finestra di gioco allora bisogna uscire dal gioco con la procedura corretta
	
	}
	
	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
	
}
