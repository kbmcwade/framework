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

public class FeedsEmergingThreatsIQRiskRepListFQDNs extends MethLib{

    
    String testName = "FeedsEmergingThreatsIQRiskRepListFQDNs";
    
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
    public void testFeedsEmergingThreatsIQRiskRepListFQDNs () throws InterruptedException, SQLException, ParseException, IOException{
        
        //local variables
        String feedUrl = new String();
        List<String> indicators = new ArrayList<String>();
        String[] indicatorRecord = new String[3];
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
        feedUrl = "https://rules.emergingthreats.net/"+config.getProperty("etClientKey")+"/reputation/iprepdata.csv";
        indicatorType = "FQDN";
        indicatorStatus = "Active";
        indicatorClass = "network";
        indicatorSource = "Emerging Threats IQRisk Rep List FQDNs";
        attributeKeyA = "IQRisk Category";
        attributeKeyB = "IQRisk Score";
        
        //set file config
        if (config.getProperty("feedFromFile").contentEquals("y")){
            
            config.setProperty("feedFileName", "data/feeds/"+testName+".txt");
        
        }
        
        //make feed request
        indicators = MethLib.getFeedIndicators(feedUrl);
        
        for(int nu=0; nu<indicators.size(); nu++){
            
            //skip csv file header
            if (indicators.get(nu).contentEquals("domain, category, score")){
                
                continue;
                
            }
           
            //parse comma-separated record for indicator value, category, and score 
            indicatorRecord = indicators.get(nu).split(",");
            indicatorValue = indicatorRecord[0];
            attributeValueA = indicatorRecord[1];
            attributeValueB = indicatorRecord[2];
            
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
            
            //check db for iqrisk category attribute
            if (!MethLib.dbQueryAttributeExists(indicatorValue, indicatorSource, attributeKeyA, attributeValueA)){
                
                appLogs.debug("FAILED - Attribute \'"+attributeKeyA+": "+attributeValueA+"\' was not found for \'"+indicatorValue+"\'.");
                softAssert.fail();
                
            }
            
            //check db for iqrisk score attribute            
            if (!MethLib.dbQueryAttributeExists(indicatorValue, indicatorSource, attributeKeyB, attributeValueB)){
                
                appLogs.debug("FAILED - Attribute \'"+attributeKeyB+": "+attributeValueB+"\' was not found for \'"+indicatorValue+"\'.");
                softAssert.fail();
                
            }
            
        }
        
        //if any soft asserts failed, fail the test
        softAssert.assertAll();
        
    }
            
}
        