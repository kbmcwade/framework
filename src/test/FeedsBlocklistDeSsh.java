package tests;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import core.MethLib;

public class FeedsBlocklistDeSsh extends MethLib{

    
    String testName = "FeedsBlocklistDeSsh";
    
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
    public void testFeedsBlocklistDeSsh () throws InterruptedException, SQLException, ParseException, IOException{
        
        //local variables
        String feedUrl = new String();
        List<String> indicators = new ArrayList<String>();
        String[] indicatorNormalization = new String[2];
        String indicatorValue = new String();
        String indicatorType = new String();
        String indicatorStatus = new String();
        String indicatorClass = new String();
        String indicatorSource = new String();
        boolean indicatorValid = false;
        boolean indicatorExists = false;
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
        
        //define feed characteristics
        feedUrl = "http://lists.blocklist.de/lists/ssh.txt";
        indicatorType = "IP Address";
        indicatorStatus = "Active";
        indicatorClass = "network";
        indicatorSource = "blocklist.de (Ssh)";
        
        //set file config
        if (config.getProperty("feedFromFile").contentEquals("y")){
            
            config.setProperty("feedFileName", "data/feeds/"+testName+".txt");
        
        }
        
        //make feed request
        indicators = MethLib.getFeedIndicators(feedUrl);
        
        for(int nu=0; nu<indicators.size(); nu++){
            
            indicatorValue = indicators.get(nu);
            
            //normalize the indicator
            indicatorNormalization = MethLib.normalizeIndicator(indicatorValue, indicatorType);
            
            if (!indicatorNormalization[0].contentEquals(indicatorValue)){
                
                appLogs.debug("Indicator value \'"+indicatorValue+"\' normalized to \'"+indicatorNormalization[0]+"\'.");
                indicatorValue = indicatorNormalization[0];
                
            }

            if (!indicatorNormalization[1].contentEquals(indicatorType)){
                
                appLogs.debug("Indicator type \'"+indicatorType+"\' normalized to \'"+indicatorNormalization[1]+"\'.");
                indicatorType = indicatorNormalization[1];
                
            }

            //validate indicator
            indicatorValid = MethLib.validateIndicator(indicatorValue, indicatorType);
            
            if (!indicatorValid){
                
                appLogs.debug("Skipped - Indicator \'"+indicatorValue+"\' is not a valid "+indicatorType+".");
                continue;
                
            }
            
            //check db for indicator
            indicatorExists = MethLib.dbQueryCountIndicator(indicatorValue, indicatorType, indicatorStatus, indicatorClass, indicatorSource);
            
            if (!indicatorExists){
                
                appLogs.debug("FAILED - Indicator \'"+indicatorValue+"\' was not found.");
                softAssert.fail();
                continue;
                
            }
                
        }
        
        //if any soft asserts failed, fail the test
        softAssert.assertAll();
        
    }
            
}
        