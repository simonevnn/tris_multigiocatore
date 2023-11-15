package view;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import control.Controller;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class FinestraGioco extends JFrame {

	private JPanel contentPane;
	private JPanel pannelloTitolo;
	private JPanel pannelloTris;
	private JPanel pannelloBottoni;
	private JPanel pannelloEsci;
	private JLabel lblTitolo;
	private JButton[][] matriceBtn;
	private JButton btnEsci;
	
	public FinestraGioco() {
		
		super("Partita");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
        contentPane = new JPanel(new BorderLayout(5,5));
        contentPane.setBorder(new EmptyBorder(10,10,10,10));
        contentPane.setPreferredSize(new Dimension(500,550));
        
        pannelloTitolo = new JPanel(new BorderLayout(5,5));
        contentPane.add(pannelloTitolo, BorderLayout.NORTH);
        
        lblTitolo = new JLabel("ATTESA GIOCATORI...",JLabel.CENTER);
        lblTitolo.setBounds(178, 37, 78, 39);
		lblTitolo.setFont(new Font("Tahoma", Font.BOLD, 32));
        pannelloTitolo.add(lblTitolo);
        
        pannelloTris = new JPanel(new BorderLayout(30,30));
        contentPane.add(pannelloTris, BorderLayout.CENTER);
        
        pannelloBottoni = new JPanel(new GridLayout(3,3,5,5));
        pannelloTris.add(pannelloBottoni, BorderLayout.CENTER);
		
        matriceBtn = new JButton[3][3];
        
		for(int i=0;i<matriceBtn.length;i++) {
			
			for(int j=0;j<matriceBtn[0].length;j++) {
				
				matriceBtn[i][j] = new JButton("");
				matriceBtn[i][j].setPreferredSize(new Dimension(50,50));
				
				matriceBtn[i][j].setBackground(Color.WHITE);
				matriceBtn[i][j].setIcon(new ImageIcon("resources/neutrale.png"));
				
				matriceBtn[i][j].setEnabled(false);
				
				pannelloBottoni.add(matriceBtn[i][j]);
			
			}
			
		}
                   
		pannelloEsci = new JPanel();
		contentPane.add(pannelloEsci, BorderLayout.SOUTH);
		
		btnEsci = new JButton("Esci dalla partita");
		btnEsci.setBounds(90,30,90,30);
		
		btnEsci.setBackground(Color.RED);
		btnEsci.setForeground(Color.WHITE);
		//btnEsci.setBorder(new LineBorder(Color.RED,3));
		
		pannelloEsci.add(btnEsci);
		
        setContentPane(contentPane);
        pack();
        setResizable(false);
        setVisible(true);
		
	}
	
	public JLabel getLblTitolo() {
		return lblTitolo;
	}
	
	public JButton getBtnMatrice(int i, int j) {
		return matriceBtn[i][j];
	}
	
	public JButton getBtnEsci() {
		return btnEsci;
	}

	public void registraEventi(Controller controller) {
		
		for(int i=0;i<matriceBtn.length;i++) {
			
			for(int j=0;j<matriceBtn[0].length;j++)
				matriceBtn[i][j].addActionListener(controller);

		}
		
		btnEsci.addActionListener(controller);
		
	}

	public void mostraMessaggio(String msg) {
		JOptionPane.showMessageDialog(contentPane,msg);
	}
	
	public void cambiaTerminePartita() {
		
		pannelloBottoni.removeAll();
		pannelloBottoni.revalidate();
		pannelloBottoni.repaint();
		
		pannelloEsci.removeAll();
		pannelloEsci.revalidate();
		pannelloEsci.repaint();
			
		pannelloBottoni.setLayout(new GridBagLayout());
		btnEsci.setPreferredSize(new Dimension(120,50));
		btnEsci.setText("Esci");
		
		pannelloBottoni.add(btnEsci);

	}
	
	public void sbloccaBottoni() {
		
		for(int i=0;i<matriceBtn.length;i++) {
			
			for(int j=0;j<matriceBtn[0].length;j++)
				matriceBtn[i][j].setEnabled(true);

		}
		
	}
	
	public void scriviScelta(int i, int j, int val) {
		matriceBtn[i][j].setEnabled(false);
		matriceBtn[i][j].setDisabledIcon(new ImageIcon("resources/giocatore"+val+".png"));
	}
	
}
