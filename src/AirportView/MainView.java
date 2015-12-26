package AirportView;

import java.io.IOException;

import javax.swing.JFrame;

import AirportServ.ParserFile;
import AirportSim.*;

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

	public void simulation()
	{
		//mTabbedView.add(new StatistiquesView());
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
			parser.getParam().startSimu();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}