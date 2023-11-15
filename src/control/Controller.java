package control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Comunicazione;
import model.Giocatore;
import view.*;

public class Controller extends Thread implements ActionListener, WindowListener {

	private FinestraPrincipale finestraPrincipale;
	private FinestraGioco finestraGioco;
	private Giocatore giocatore;
	
	public Controller(FinestraPrincipale finestraPrincipale) {
		this.finestraPrincipale = finestraPrincipale;
		finestraPrincipale.registraEventi(this);
	}

	@Override
	public void run() {
		
		while(true) {
			
			if(!giocatore.isInPartita()) {
				esci();
				break;
			}
			
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==finestraPrincipale.getBtnConnettiti())
			connetti();
		
		if(finestraGioco!=null) {
			
			if(e.getSource()==finestraGioco.getBtnMatrice(0,0))
				giocatore.inviaScelta(Comunicazione.A1);
			
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
			
			//-- USCITA --
			if(e.getSource()==finestraGioco.getBtnEsci()) {
				giocatore.inviaScelta(Comunicazione.EXIT);
				esci();
			}
			
		}
		
	}

	private void connetti() {
		
		String indirizzo = finestraPrincipale.getIndirizzo();
		
		if(validaIndirizzo(indirizzo)) {
			
			try {
				
				giocatore = new Giocatore(indirizzo);
				
				finestraGioco = new FinestraGioco();
				finestraGioco.registraEventi(this);
				
				giocatore.setFinestra(finestraGioco);
				
				finestraPrincipale.setVisible(false);
				
				this.start();
				
			}
			catch(IOException e) {
				finestraPrincipale.mostraErrore("Errore durante la connessione al server. Inserisci l'indirizzo corretto o riprova più tardi.");
			}
			
		}
		else
			finestraPrincipale.mostraErrore("Errore: Formato dell'indirizzo non corretto.");
		
		finestraPrincipale.getTextFieldIndirizzo().setText("");
		
	}
	
	private void esci() {
		
		giocatore.chiudiConnessione();
		giocatore = null;
		
		finestraGioco.dispose();
		finestraGioco = null;
		
		finestraPrincipale.setVisible(true);
		
	}
	
	private boolean validaIndirizzo(String indirizzo) {
	    
		String condition = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";

	    Pattern p = Pattern.compile(condition);
	    Matcher m = p.matcher(indirizzo);
	    
	    return m.matches();

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		esci();
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
