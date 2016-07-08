package core;

import org.apache.log4j.Logger;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestListeners extends TestListenerAdapter{

	public static Logger appLogs = Logger.getLogger("devpinoyLogger");
	
	public void onTestFailure(ITestResult tr){
		
		appLogs.debug("FAILED test: "+tr.getName()+".");
		
	}

	
	public void onTestSuccess(ITestResult tr){
		
		appLogs.debug("Passed test: "+tr.getName()+".");
		
	}
	
}
