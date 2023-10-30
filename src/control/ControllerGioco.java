package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import model.Comunicazione;
import model.Giocatore;
import view.FinestraGioco;

public class ControllerGioco implements ActionListener {

	private Giocatore giocatore;
	private FinestraGioco finestra;
	
	public ControllerGioco(Giocatore giocatore, FinestraGioco finestra) {
	
		this.giocatore = giocatore;
		
		this.finestra = finestra;
		finestra.registraEventi(this);
	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==finestra.getBtnMatrice(0,0))
			giocatore.inviaScelta(Comunicazione.A1);
		
		if(e.getSource()==finestra.getBtnMatrice(0,1))
			giocatore.inviaScelta(Comunicazione.B1);
		
		if(e.getSource()==finestra.getBtnMatrice(0,2))
			giocatore.inviaScelta(Comunicazione.C1);
		
		if(e.getSource()==finestra.getBtnMatrice(1,0))
			giocatore.inviaScelta(Comunicazione.A2);
		
		if(e.getSource()==finestra.getBtnMatrice(1,1))
			giocatore.inviaScelta(Comunicazione.B2);
		
		if(e.getSource()==finestra.getBtnMatrice(1,2))
			giocatore.inviaScelta(Comunicazione.C2);
		
		if(e.getSource()==finestra.getBtnMatrice(2,0))
			giocatore.inviaScelta(Comunicazione.A3);
		
		if(e.getSource()==finestra.getBtnMatrice(2,1))
			giocatore.inviaScelta(Comunicazione.B3);
		
		if(e.getSource()==finestra.getBtnMatrice(2,2))
			giocatore.inviaScelta(Comunicazione.C3);
		
	}
	
}
