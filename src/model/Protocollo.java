package model;

public class Protocollo {

	private Comunicazione comunicazione;
	private String messaggio;
	
	public Protocollo(Comunicazione comunicazione, String messaggio) {
		this.comunicazione = comunicazione;
		this.messaggio = messaggio;
	}
	
	public Protocollo(Comunicazione comunicazione) {
		this.comunicazione = comunicazione;
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
	
}
