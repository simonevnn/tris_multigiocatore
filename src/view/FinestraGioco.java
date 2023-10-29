package view;

import java.awt.EventQueue;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

public class FinestraGioco extends JFrame {

	private JPanel contentPane;
	private JPanel panelTris;
	private JButton btnNewButton;
	
	public FinestraGioco() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setVisible(true);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new GridBagLayout());
		setContentPane(contentPane);
		
		panelTris = new JPanel();
		panelTris.setSize(getSize());
		contentPane.add(panelTris);
		
		btnNewButton = new JButton("New button");
		panelTris.add(btnNewButton);
		
	}

}
