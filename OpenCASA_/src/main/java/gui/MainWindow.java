package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow extends JFrame {

	public MainWindow() throws HeadlessException {createGUI();}
	
	public MainWindow(GraphicsConfiguration gc) {
		super(gc);
		createGUI();
	}

	public MainWindow(String title) throws HeadlessException {
		super(title);
		createGUI();
	}

	public MainWindow(String title, GraphicsConfiguration gc) {
		super(title, gc);
		createGUI();
	}
	
	
	public void createGUI() {

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//natural height, maximum width
		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady=40;
		
		JButton motilityBtn = new JButton("Motility");
		motilityBtn.setBackground(new Color(229,255,204));
		//Add action listener
		motilityBtn.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				
			}

		} );
		c.gridx = 0;
		c.gridy = 0;
		
		panel.add(motilityBtn, c);

		JButton chemotaxisBtn = new JButton("Chemotaxis");
		chemotaxisBtn.setBackground(new Color(204,229,255));
		//Add action listener
		chemotaxisBtn.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				
			}

		} );
		c.gridx = 1;
		c.gridy = 0;
		
		panel.add(chemotaxisBtn, c);
		
		JButton viabilityBtn = new JButton("Viability");
		viabilityBtn.setBackground(new Color(255,153,153));
		//Add action listener
		viabilityBtn.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				
			}

		} );
		c.gridx = 0;
		c.gridy = 1;

		try{
			Image img = ImageIO.read(getClass().getResource("/viability.png"));
			viabilityBtn.setIcon(new ImageIcon(img));
		} catch (Exception ex) {
		    System.out.println(ex);
		}
		
		panel.add(viabilityBtn, c);
		
		JButton morphometryBtn = new JButton("Morphometry");
		morphometryBtn.setBackground(new Color(255,204,153));
		//Add action listener
		morphometryBtn.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				
			}

		} );
		c.gridx = 1;
		c.gridy = 1;
	
		panel.add(morphometryBtn, c);
		
		
		this.setPreferredSize(new Dimension(400, 400));
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(panel);
		this.pack();
		this.setVisible(true);
	}

}
