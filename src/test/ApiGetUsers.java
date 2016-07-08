package tests;

import java.io.IOException;
import java.sql.SQLException;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import core.MethLib;

public class ApiGetUsers extends MethLib{

    
    String testName = "ApiGetUsers";
    
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
    public void testApiGetUsers () throws IOException, SQLException{
        
        //local variables
        Object[] response = new Object [3];
        String apiRequest="users";
        String method = "GET";
        String url = apiUrl+apiRequest;
        String responseBody = null;
        int statusCode = 0;
        long responseTime = 0;
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
        
        //make api request and get status code and response body
        response = MethLib.apiGetJson(url);
        statusCode = (int) response[0];
        responseTime = (long) response[1]; 
        responseBody = (String) response[2];
        
        //log response time
        appLogs.debug("Response time: "+MethLib.formatMilliseconds(responseTime)+".");
       
        // assert status code
        if(!(statusCode == 200)){
        
            appLogs.debug("FAILED - \""+method+" "+url+"\" returned a status code of: "+statusCode+".");
            Assert.fail();
            
        }
        
        //check for data in response
        if (responseBody.contentEquals("{}")){
            
            appLogs.debug("Warning - response body is blank.");
            
        
        }
        
        //if any soft asserts failed, fail the test
        softAssert.assertAll();
        
    }
    
}   

