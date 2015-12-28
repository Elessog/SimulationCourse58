package AirportView;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import AirportServ.ParserFile;
import AirportServ.SimuParam;

@SuppressWarnings("serial")
public class MainView extends JFrame
{	
	/**
	 * 
	 */
	private static MainView mSingleton;
	
	public static MainView getMainView()
	{
		if(mSingleton == null)
			mSingleton = new MainView();
		return mSingleton;
	}
	
	public MainView()
	{
		mSingleton = this;
		setTitle("Simulation de Trafic d'aéroport");
		setSize(800,640);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		add(new InitView());
		setVisible(true);
	}

	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		MainView test = new MainView();
	}

	public void parsefile(String absolutePath) {
		// TODO Auto-generated method stub
		try {
			ParserFile parser = new ParserFile(absolutePath);
			int dialogButton = JOptionPane.YES_NO_OPTION;
            int result = JOptionPane.showConfirmDialog (null, "Do you want to start simulation ?","Warning",dialogButton);
            if(result == JOptionPane.YES_OPTION){
            	String sim = parser.getParam().startSimu();
            	JOptionPane.showMessageDialog(null, "File save :" +sim);
            }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void startSimu(SimuParam param) {
		String sim = param.startSimu();
		JOptionPane.showMessageDialog(null, "File save :" +sim);
	}

}