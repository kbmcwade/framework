package tests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import core.MethLib;

public class ApiExportConnectorsResponseTime extends MethLib{

	String testName = "ApiExportConnectorsResponseTime";
	
	@BeforeTest
	public void runTest(){
		
		if(! MethLib.runTest(testName)){
			
			appLogs.debug("Run Test set to no for: "+testName+". Skipping test.");
			throw new SkipException("Run Test set to no for: "+testName+". Skipping test.");
			
		}else{
			
			appLogs.debug("Executing test: "+testName+".");
			
		}
		
	}
	
	@Test//(dataProvider="getData")
	public void testApiExportConnectorsResponseTime () throws FailingHttpStatusCodeException, IOException, SQLException, AddressException, ClassNotFoundException, MessagingException{
	
		//local variables
		String url = null;
		String mySqlQuery = null;
		
		//declare new soft assertion so test will complete before setting to fail
		SoftAssert softAssert = new SoftAssert();
		
		//get exports data from db
		mySqlQuery = "select id from exporters;";
		List<String> exportId = null;
		exportId = mySql.getMysqlQuery(mySqlQuery);
	
		for(String ex : exportId){
			
			//get export name
			mySqlQuery = "select name from exporters where id ="+ex+";";
			List<String> exportNameList = null;
			exportNameList = mySql.getMysqlQuery(mySqlQuery);
			String exportName = exportNameList.get(0);
			
			//get export url
			mySqlQuery = "select url from exporters where id ="+ex+";";
			List<String> exportUrlList = null;
			exportUrlList = mySql.getMysqlQuery(mySqlQuery);
			String exportUrl = exportUrlList.get(0);
			
			//get export token
			mySqlQuery = "select value from  exporter_config_values where exporter_id ="+ex+" and name = \'Token\';";
			List<String> exportTokenList = null;
			exportTokenList = mySql.getMysqlQuery(mySqlQuery);
			String exportToken = exportTokenList.get(0);
			
			//log export test start
			appLogs.debug("Starting: "+exportName+".");
			
			//construct request url
			url = apiUrl+"export/"+exportUrl+"?token="+exportToken;
			
			//make api request for export - get response time and status code
			Object apiResponse [] = MethLib.apiGetJson(url);
		
			// get status code, load time, and response as string from the request
			int statusCode = (int) apiResponse[0];
			long responseTime = (long) apiResponse[1];
			String response = (String) apiResponse[2];
            
			 //log response time
	        appLogs.debug("Response time: "+MethLib.formatMilliseconds(responseTime)+".");
	       
			if (statusCode != 200){
			
				appLogs.debug("FAILED - "+url+" returned a status code of "+statusCode+".");
				softAssert.fail();
			
			
			}else{
			
				appLogs.debug("Passed - "+url+" returned a status code of "+statusCode+".");
			
			}
		
			if (response.contentEquals("{}")){
			
				appLogs.debug("FAILED - Message response is blank.");	
				softAssert.fail();
		
			}else if (response.contains("Error in exception handler.") || response.contains("There was an error retrieving records.")){
			
				appLogs.debug("FAILED - Error found in message response: \""+response+"\"");	
				softAssert.fail();
			
			}else{
		
				appLogs.debug("Passed - No errors found in message response.");
		
			}

			appLogs.debug("Checking for unexpected indicators in message response...");
			
			//get all indicators that do not have an active status from db
/*			mySqlQuery = "select value from indicators where status_id <> 1;";
*			List<String> nonActiveIndicators = null;
*			nonActiveIndicators = mySql.getMysqlQuery(mySqlQuery);
*		
*			//assert the response doesn't contain any non-active indicators
*			for(String na : nonActiveIndicators){
*		
*				if (na.length() > 9 && response.contains(na)){
*			
*					appLogs.debug("FAILED - Found non-active indicator in response: \""+na+"\".");	
*					softAssert.fail();
*			
*				}
*			
*			}
*/			
		}
		
		//if any soft asserts failed, fail the test
		softAssert.assertAll();
	}
	
	/*@DataProvider
	*public Object[][] getData(){
	*	
	*	return TestReference.getData(testName);
	*	
	*}
	*/
}

