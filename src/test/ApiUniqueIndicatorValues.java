package tests;

import java.io.IOException;
import java.sql.SQLException;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import core.MethLib;

public class ApiUniqueIndicatorValues extends MethLib{

    String testName = "ApiUniqueIndicatorValues";
    
    @BeforeTest
    public void runTest(){
        
        if(! MethLib.runTest(testName)){
            
            appLogs.debug("Run Test set to no for: "+testName+". Skipping test.");
            throw new SkipException("Run Test set to no for: "+testName+". Skipping test.");
            
        }else{
            
            appLogs.debug("Executing test: "+testName+".");
            
        }
        
    }
    
    @Test
    public void testApiUniqueIndicatorValues() throws SQLException, InterruptedException, IOException{

        //local variables
        Object[] response = new Object [3];
        String method = "POST";
        String url = apiUrl+"indicators/consume/new";
        String body =  new String();
        String fileName = new String();
        String responseBody = new String();
        int statusCode = 0;
        long responseTime = 0;
        int duplicateIndicatorCount = 0;
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
        
        //get json file for request body
        fileName="data/"+testName+".txt";  
        body = fileToString(fileName);
                
        //perform request and get status code and response body
        appLogs.debug("Performing "+method+" request with "+url+".");
        
        response = MethLib.apiPostJson(url, body);
        statusCode = (int) response[0];
        responseTime = (long) response[1]; 
        responseBody = (String) response[2];
        
        //log response time
       appLogs.debug("Response time: "+MethLib.formatMilliseconds(responseTime)+".");
      
       if (statusCode != 201){
       
           appLogs.debug("FAILED - "+url+" returned a status code of "+statusCode+".");
           softAssert.fail();
       
       }
   
       if (responseBody.contentEquals("{}")){
       
           appLogs.debug("FAILED - Message response is blank.");   
           softAssert.fail();
   
       }else if (responseBody.contains("Error in exception handler.") || responseBody.contains("There was an error retrieving records.")){
       
           appLogs.debug("FAILED - Error found in message response: \""+responseBody+"\"");    
           softAssert.fail();
       
       }
        
        //get non-distinct indicator count from db
        duplicateIndicatorCount = MethLib.dbCountDuplicates("indicators", "value", "type_id");
        
        //assert non-distinct count for indicators.value is 0
        if (duplicateIndicatorCount > 0){
            
            appLogs.debug("FAILED - Found "+duplicateIndicatorCount+" duplicate indicator values."); 
            Assert.fail();
        
        }else{
            
            appLogs.debug("Passed - Found "+duplicateIndicatorCount+" duplicate indicator values.");
        
        }
        
    }

}