package model;

import java.io.Serializable;

public class Protocollo implements Serializable {	//classe degli oggetti trasmessi in rete, serializzabili

	private Comunicazione comunicazione;	//tipo di scelta/comunicazione
	private int[][] matriceTris;	//eventuale matrice (comunicata dal server)
	private String messaggio;	//eventuale messaggio (solitamente di errore)
	
	public Protocollo(Comunicazione comunicazione, int[][] matriceTris, String messaggio) {
		this.comunicazione = comunicazione;
		this.matriceTris = matriceTris;
		this.messaggio = messaggio;
	}

	public Protocollo(Comunicazione comunicazione, String messaggio) {
		this.comunicazione = comunicazione;
		this.messaggio = messaggio;
	}
	
	public Protocollo(Comunicazione comunicazione, int[][] matriceTris) {
		this.comunicazione = comunicazione;
		this.matriceTris = matriceTris;
	}
	
	public Protocollo(Comunicazione comunicazione) {
		this.comunicazione = comunicazione;
	}
	
	public Protocollo() {
	}

	public Comunicazione getComunicazione() {
		return comunicazione;
	}

	public void setComunicazione(Comunicazione comunicazione) {
		this.comunicazione = comunicazione;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

	public int[][] getMatriceTris() {
		return matriceTris;
	}

	public void setMatriceTris(int[][] matriceTris) {
		this.matriceTris = matriceTris;
	}
	
}
