package AirportView;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;


public class InitView extends JPanel {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton mTextFile;
	private JButton mBoxes;
	
	
	public InitView(){
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		mTextFile = new JButton("Init simu via text file");
		c.gridx = 0;
		c.gridy = 0;
		mTextFile.addActionListener(new ActionListener() 
		{			
			

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				GetTextView.getDragDrop();
				//MainView.getMainView().getmTabbedView().add();
			}
		});;
		add(mTextFile,c);
		
		mBoxes = new JButton("Init simu via interfaces");
		c.gridx = 0;
		c.gridy = 1;
		mBoxes.addActionListener(new ActionListener() 
		{			
			

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				MainView.getMainView().remove(InitView.this);
				MainView.getMainView().add(new StartIhmView());
				MainView.getMainView().setVisible(true);
			}
		});;
		add(mBoxes,c);
		
		
	}
	
	
}
