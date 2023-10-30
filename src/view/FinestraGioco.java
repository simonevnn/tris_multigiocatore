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
import javax.swing.border.EmptyBorder;

import control.ControllerGioco;

import javax.swing.JButton;

public class FinestraGioco extends JFrame {

	private JPanel contentPane;
	private JPanel pannelloTitolo;
	private JPanel pannelloTris;
	private JPanel pannelloBottoni;
	private JLabel lblTitolo;
	private JButton[][] matriceBtn;
	
	public FinestraGioco() {
		
		super("Partita");
        
        contentPane = new JPanel(new BorderLayout(5,5));
        contentPane.setBorder(new EmptyBorder(10,10,10,10));
        contentPane.setPreferredSize(new Dimension(500,550));
        
        pannelloTitolo = new JPanel(new BorderLayout(10,15));
        contentPane.add(pannelloTitolo, BorderLayout.NORTH);
        
        lblTitolo = new JLabel("In partita",JLabel.CENTER);
        lblTitolo.setBounds(178, 37, 78, 39);
		lblTitolo.setFont(new Font("Tahoma", Font.PLAIN, 32));
        pannelloTitolo.add(lblTitolo);
        
        pannelloTris = new JPanel(new BorderLayout(10,15));
        contentPane.add(pannelloTris, BorderLayout.CENTER);
        
        pannelloBottoni = new JPanel(new GridLayout(3,3,5,5));
        pannelloTris.add(pannelloBottoni, BorderLayout.CENTER);
		
		for(int i=0;i<matriceBtn.length;i++) {
			
			for(int j=0;j<matriceBtn[0].length;j++) {
				
				matriceBtn[i][j] = new JButton();
				matriceBtn[i][j].setPreferredSize(new Dimension(50,50));
				pannelloBottoni.add(matriceBtn[i][j]);
			
			}
			
		}
                        
        setContentPane(contentPane);
        pack();
        setVisible(true);
		
	}
	
	public JLabel getLblTitolo() {
		return lblTitolo;
	}

	public JButton getBtnMatrice(int i, int j) {
		return matriceBtn[i][j];
	}
	
	public void registraEventi(ControllerGioco controller) {
		
		for(int i=0;i<matriceBtn.length;i++) {
			
			for(int j=0;j<matriceBtn[0].length;j++)
				matriceBtn[i][j].addActionListener(controller);

		}
		
	}

	public void mostraErrore(String err) {
		JOptionPane.showMessageDialog(contentPane,err);
	}
	
	public static void main(String[] args) {
		FinestraGioco fg = new FinestraGioco();	//TEST ONLY
	}

}
