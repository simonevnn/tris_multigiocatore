package view;

import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import control.Controller;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;

public class FinestraPrincipale extends JFrame {

	private JPanel contentPane;
	private JLabel lblTitolo;
	private JLabel lblSottotitolo;
	private JTextField textFieldIndirizzo;
	private JButton btnConnettiti;

	public FinestraPrincipale() {
		
		super("Connettiti al server");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setBounds(100, 100, 450, 300);
		setResizable(false);
		
		setVisible(true);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblTitolo = new JLabel("TRIS");
		lblTitolo.setBounds(178, 37, 78, 39);
		lblTitolo.setFont(new Font("Tahoma", Font.BOLD, 32));
		contentPane.add(lblTitolo);
		
		lblSottotitolo = new JLabel("Connettiti al server per giocare");
		lblSottotitolo.setHorizontalAlignment(SwingConstants.CENTER);
		lblSottotitolo.setBounds(123, 87, 188, 14);
		contentPane.add(lblSottotitolo);
		
		textFieldIndirizzo = new JTextField();
		textFieldIndirizzo.setBounds(103, 112, 135, 23);
		contentPane.add(textFieldIndirizzo);
		textFieldIndirizzo.setColumns(10);
		
		btnConnettiti = new JButton("Connettiti");
		btnConnettiti.setBounds(246, 112, 95, 23);
		contentPane.add(btnConnettiti);
		
		getRootPane().setDefaultButton(btnConnettiti);
		
	}

	public JButton getBtnConnettiti() {
		return btnConnettiti;
	}
	
	public JTextField getTextFieldIndirizzo() {
		return textFieldIndirizzo;
	}

	public void registraEventi(Controller controller) {
		btnConnettiti.addActionListener(controller);
	}
	
	public String getIndirizzo() {
		return textFieldIndirizzo.getText();
	}
	
	public void mostraErrore(String err) {
		JOptionPane.showMessageDialog(contentPane,err);
	}

}
