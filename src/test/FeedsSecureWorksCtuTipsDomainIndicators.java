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

public class FeedsSecureWorksCtuTipsDomainIndicators extends MethLib{

    
    String testName = "FeedsSecureWorksCtuTipsDomainIndicators";
    
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
    public void testFeedsSecureWorksCtuTipsDomainIndicators () throws InterruptedException, SQLException, ParseException, IOException{
        
        //local variables
        String feedUrl = new String();
        String feedFile = new String();
        List<String> indicators = new ArrayList<String>();
        String[] indicatorRecord = new String[4];
        String[] indicatorNormalization = new String[2];
        String indicatorValue = new String();
        String indicatorType = new String();
        String indicatorStatus = new String();
        String indicatorClass = new String();
        String indicatorSource = new String();
        String attributeKeyA = new String();
        String attributeValueA = new String();
        String attributeKeyB = new String();
        String attributeValueB = new String();
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
        
        //define feed characteristics
        feedUrl = "https://portal.secureworks.com/attackerdb/blackList?Token="+config.getProperty("swApiToken")+"&type=domainname&schemaVersion=v1&format=csv&listid=12";
        feedFile = "data/feeds/secureworks/SecureWorksCTUTIPSDomainIndicators.csv";
        indicatorType = "FQDN";
        indicatorStatus = "Active";
        indicatorClass = "network";
        indicatorSource = "SecureWorks CTU TIPS Domain Indicators";
        attributeKeyA = "SecureWorks Comment";
        attributeKeyB = "Published Date";
        
        //set file config
        if (config.getProperty("feedFromFile").contentEquals("y")){
            
            config.setProperty("feedFileName", feedFile);
        
        }
        
        //make feed request
        indicators = MethLib.getFeedIndicators(feedUrl);
        
        for(int nu=0; nu<indicators.size(); nu++){
            
            //skip csv file header and blank records
            if (indicators.get(nu).startsWith("\"WatchList\",\"HostAddress\"") || StringUtils.isBlank(indicators.get(nu))){
                
                continue;
                
            }
           
            //parse comma-separated record for indicator value; comment and member since attributes
            indicatorRecord = indicators.get(nu).split("\",\"");
            indicatorValue = indicatorRecord[1].replaceAll("^\"|\"$", "");
            attributeValueA = indicatorRecord[2].replaceAll("^\"|\"$", "");
            attributeValueB = indicatorRecord[3].replaceAll("^\"|\"$", "");
            
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
            
            //check db for comment attribute
            if (!MethLib.dbQueryAttributeExists(indicatorValue, indicatorSource, attributeKeyA, attributeValueA)){
                
                appLogs.debug("FAILED - Attribute \'"+attributeKeyA+": "+attributeValueA+"\' was not found for \'"+indicatorValue+"\'.");
                softAssert.fail();
                
            }
            
            //check db for member since attribute            
            if (!MethLib.dbQueryAttributeExists(indicatorValue, indicatorSource, attributeKeyB, attributeValueB)){
                
                appLogs.debug("FAILED - Attribute \'"+attributeKeyB+": "+attributeValueB+"\' was not found for \'"+indicatorValue+"\'.");
                softAssert.fail();
                
            }
            
        }
        
        //if any soft asserts failed, fail the test
        softAssert.assertAll();
        
    }
            
}
        