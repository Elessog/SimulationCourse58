package AirportView;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import AirportServ.SimuParam;


@SuppressWarnings("serial")
public class StartIhmView extends JPanel {
	
	private JLabel mNbGates;
	private JLabel mFrequence;
	private JLabel mDebut;
	private JLabel mFin;
	private JTextField mInbGates;
	private JTextField mIFrequence;
	private JTextField mIDebut;
	private JLabel mOuverture;
	private JLabel mFermeture;
	private AbstractButton mValider;
	private JTextField mIrandom;
	private JTextField mIouverture;
	private JTextField mIFin;
	private JTextField mnbrSimu;
	private JButton mCancel;
	private JTextField mIfermeture;
	private JLabel mDSeed;
	
	
	public StartIhmView(){
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		this.getLayout();
		
		mNbGates =     new JLabel("Nombre de portes : ");
		c.gridx = 0;
		c.gridy = 0;		
		add(mNbGates,c);
		
		mFrequence =    new JLabel("Fréquence avions (en minutes entre chaque avion) : ");
		c.gridx = 0;
		c.gridy = 1;
		add(mFrequence,c);
		
		mDebut =      new JLabel("Date début : ");
		c.gridx = 0;
		c.gridy = 2;
		add(mDebut,c);
		
		mFin = new JLabel("Date de fin : ");
		c.gridx = 0;
		c.gridy = 3;	
		add(mFin,c);
		
		mOuverture =  new JLabel("Heure d'ouverture : ");
		c.gridx = 0;
		c.gridy = 4;	
		add(mOuverture,c);
		
		mFermeture =  new JLabel("Heure de fermerture : ");
		c.gridx = 0;
		c.gridy = 5;	
		add(mFermeture,c);
		
		mInbGates = new JTextField("6");
		c.gridx = 1;
		c.gridy = 0;		
		add(mInbGates,c);
		
		mIFrequence = new JTextField("20");
		c.gridx = 1;
		c.gridy = 1;
		add(mIFrequence,c);
		
		mIDebut = new JTextField("20/12/1991 04:45:00.5000");
		c.gridx = 1;
		c.gridy = 2;
		add(mIDebut,c);
		
		mIFin = new JTextField("20/03/1992 04:45:00.5000");
		c.gridx = 1;
		c.gridy = 3;	
		add(mIFin,c);
		
		
		mIouverture = new JTextField("7");
		c.gridx = 1;
		c.gridy = 4;	
		add(mIouverture,c);
		
		mIfermeture = new JTextField("22");
		c.gridx = 1;
		c.gridy = 5;	
		add(mIfermeture,c);
		
		mDSeed =  new JLabel("Utiliser propre graine random : ");
		c.gridx = 0;
		c.gridy = 6;	
		add(mDSeed,c);
		
		mDSeed =  new JLabel("   ");
		c.gridx = 0;
		c.gridy = 7;	
		add(mDSeed,c);
		
		mDSeed =  new JLabel("     ");
		c.gridx = 0;
		c.gridy = 8;	
		add(mDSeed,c);
		
		mIrandom = new JTextField("12345");
		
		final JCheckBox mMapBox = new JCheckBox();
		c.gridx = 1;		
		c.gridy = 6;	
		mMapBox.setSelected(false);
		mMapBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (mMapBox.isSelected()){
					c.gridx = 1;
					c.gridy = 7;
					add(mIrandom,c);
					StartIhmView.this.updateUI();
				}
				else{
					remove(mIrandom);
					StartIhmView.this.updateUI();
				}
			}
		});
		add(mMapBox,c);		
		
		
			
		
		mValider = new JButton("Valider");
		c.gridx = 1;
		c.gridy = 9;
		mValider.addActionListener(new ActionListener() 
		{			
			

			@Override
			public void actionPerformed(ActionEvent e) 
			{	
				SimuParam param = new SimuParam();
				String seed = null;
				if (mMapBox.isSelected())
					seed = mIrandom.getText();
				int nbGates = Integer.parseInt(mInbGates.getText());
				int freq = Integer.parseInt(mIFrequence.getText());
				int ouv = Integer.parseInt(mIouverture.getText());
				int ferm = Integer.parseInt(mIfermeture.getText());
				param.processIhm(nbGates, freq, seed,
						mIDebut.getText().trim(),
						mIFin.getText().trim(), 
						ouv, ferm);
				MainView.getMainView().startSimu(param);
			}
		});;
		add(mValider,c);
		

		mCancel = new JButton("Cancel");
		c.gridx = 0;
		c.gridy = 9;
		mCancel.addActionListener(new ActionListener() 
		{			
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				MainView.getMainView().remove(StartIhmView.this);
				MainView.getMainView().add(new InitView());
				MainView.getMainView().setVisible(true);
			}
		});
		add(mCancel,c);
	}


}
