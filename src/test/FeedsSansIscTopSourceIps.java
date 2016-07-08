package tests;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import core.MethLib;

public class FeedsSansIscTopSourceIps extends MethLib{

    
    String testName = "FeedsSansIscTopSourceIps";
    
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
    public void testFeedsSansIscTopSourceIps () throws InterruptedException, SQLException, ParseException, IOException{
        
        //local variables
        String feedUrl = new String();
        List<String> indicators = new ArrayList<String>();
        String[] indicatorNormalization = new String[2];
        String indicatorValue = new String();
        String indicatorType = new String();
        String indicatorStatus = new String();
        String indicatorClass = new String();
        String indicatorSource = new String();
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
        
        //define feed characteristics
        feedUrl = "https://isc.sans.edu/ipsascii.html?limit=10000";
        indicatorType = "IP Address";
        indicatorStatus = "Active";
        indicatorClass = "network";
        indicatorSource = "SANS ISC Top Source IPs";
        
        //set file config
        if (config.getProperty("feedFromFile").contentEquals("y")){
            
            config.setProperty("feedFileName", "data/feeds/"+testName+".txt");
        
        }
        
        //make feed request
        indicators = MethLib.getFeedIndicators(feedUrl); 
        
        for(int nu=0; nu<indicators.size(); nu++){
            
            indicatorValue = indicators.get(nu);
            
            //skip comments and blank lines
            if (indicatorValue.startsWith("#") || StringUtils.isBlank(indicatorValue)){
                
                continue;
                
            }
            
            //strip all parameters after ip address from record
            indicatorValue = indicatorValue.substring(0, indicatorValue.indexOf('\t'));
            
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
            if (!MethLib.validateIndicator(indicatorValue, indicatorType)){
                
                appLogs.debug("Skipped - Indicator \'"+indicatorValue+"\' is not a valid "+indicatorType+".");
                continue;
                
            }
            
            //check db for indicator
            if (!MethLib.dbQueryCountIndicator(indicatorValue, indicatorType, indicatorStatus, indicatorClass, indicatorSource)){
                
                appLogs.debug("FAILED - Indicator \'"+indicatorValue+"\' was not found.");
                softAssert.fail();
                continue;
                
            }
                
        }
        
        //if any soft asserts failed, fail the test
        softAssert.assertAll();
        
    }
            
}
    