package AirportServ;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import enstabretagne.simulation.core.ISimulationDateProvider;
import enstabretagne.base.utility.Logger;
import enstabretagne.base.utility.LoggerParamsNames;
import enstabretagne.base.utility.loggerimpl.SysOutLogger;
import enstabretagne.base.utility.loggerimpl.XLSXExcelDataloggerImpl;

public class LoggerUtil extends Logger {
	
	private static LoggerUtil mSingleton;
	
	public static String locFile; 
	
	private LoggerUtil(){
		super();
		
	}
	
	public static LoggerUtil getLogger()
	{
		if(mSingleton == null)
			mSingleton = new LoggerUtil();
		return mSingleton;
	}
	
	public void initialize(ISimulationDateProvider engine){
		HashMap<String,HashMap<String,Object>> loggersNames = new HashMap<String,HashMap<String,Object>>();
		loggersNames.put(SysOutLogger.class.getCanonicalName(), new HashMap<String,Object>());
		
		
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put(LoggerParamsNames.DirectoryName.toString(), System.getProperty("user.dir"));
		
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		params.put(LoggerParamsNames.FileName.toString(),"LoggerAirplaneSimu"+timeStamp+".xlsx");
		loggersNames.put(XLSXExcelDataloggerImpl.class.getCanonicalName(),params);
		locFile = params.toString();
		Logger.Init(engine, loggersNames, true);
	}

}
