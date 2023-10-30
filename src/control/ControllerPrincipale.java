package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Giocatore;
import view.FinestraGioco;
import view.FinestraPrincipale;

public class ControllerPrincipale implements ActionListener {

	private FinestraPrincipale finestra;
	
	public ControllerPrincipale(FinestraPrincipale finestra) {
		this.finestra = finestra;
		finestra.registraEventi(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==finestra.getBtnConnettiti())
			connetti();
		
	}
	
	private void connetti() {
		
		String indirizzo = finestra.getIndirizzo();
		
		if(validaIndirizzo(indirizzo)) {
			
			try {
				
				Giocatore g = new Giocatore(indirizzo);
				
				FinestraGioco fg = new FinestraGioco();
				ControllerGioco cg = new ControllerGioco(g,fg);
				
				g.setFinestra(fg);
				
				finestra.setVisible(false);
				
			}
			catch(IOException e) {
				finestra.mostraErrore("Errore durante la connessione al server. Inserisci l'indirizzo corretto o riprova più tardi.");
			}
			
		}
		else
			finestra.mostraErrore("Errore: Formato dell'indirizzo non corretto.");
		
	}
	
	private boolean validaIndirizzo(String indirizzo) {
	    
		String condition = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";

	    Pattern p = Pattern.compile(condition);
	    Matcher m = p.matcher(indirizzo);
	    
	    return m.matches();

	}
	
}
