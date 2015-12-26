package AirportView;

import javax.swing.JFrame;

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

}