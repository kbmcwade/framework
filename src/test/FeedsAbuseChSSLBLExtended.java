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

public class FeedsAbuseChSSLBLExtended extends MethLib{

    
    String testName = "FeedsAbuseChSSLBLExtended";
    
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
    public void testFeedsAbuseChSSLBLExtended () throws InterruptedException, SQLException, IOException, ParseException{
        
        //local variables
        String feedUrl = new String();
        List<String> indicators = new ArrayList<String>();
        String[] indicatorRecord = new String[3];
        String[][] indicator = new String[3][4];
        String[] indicatorNormalization = new String[2];
        String[][] indicatorAttribute = new String[2][2];
        String indicatorStatus = new String();
        String indicatorSource = new String();
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
        
        //define feed characteristics
        feedUrl = "https://sslbl.abuse.ch/downloads/ssl_extended.csv";
        indicator[0][1] = "IP Address";
        indicatorStatus = "Active";
        indicatorSource = "abuse.ch SSLBL (Extended)";
        indicatorAttribute[0][0] = "Port";
        indicatorAttribute[1][0] = "Malware Family";
        
        //set file config
        if (config.getProperty("feedFromFile").contentEquals("y")){
            
            config.setProperty("feedFileName", "data/feeds/"+testName+".txt");
        
        }
        
        //make feed request
        indicators = MethLib.getFeedIndicators(feedUrl);
        
        for(int nu=0; nu<indicators.size(); nu++){
            
            //skip csv file header, comments, and blank lines
            if (indicators.get(nu).startsWith("#") || StringUtils.isBlank(indicators.get(nu))) continue;
           
            //parse comma-separated record for indicator value, samples, and attributes
            indicatorRecord = indicators.get(nu).split(",");
            
           //parse indicator details
           indicator[0][0] = indicatorRecord[2];

           //parse indicator sample details
           indicator[1][0] = indicatorRecord[1];
           indicator[1][1] = "MD5";
           indicator[2][0] = indicatorRecord[4];
           indicator[2][1] = "SHA-1";
                      
           //define indicator attribute array
           indicatorAttribute[0][1] = indicatorRecord[3];           
           indicatorAttribute[1][1] = indicatorRecord[5];          
           
           for(int a=0; a<indicator.length;a++){
           
               //verify the indicator is not blank
               if (StringUtils.isBlank(indicator[a][0])) continue;
               
               //get indicator class
               indicator[a][2] = MethLib.dbQueryGetClass(indicator[a][1]);
           
               //verify the indicator is a recognized type
               if (StringUtils.isBlank(indicator[a][2])){
       
                   appLogs.debug("Skipped - \'"+indicator[a][0]+"\' is unrecognized type \'"+indicator[a][1]+"\'");
                   continue;
       
               }
           
               //normalize the indicator
               indicatorNormalization = MethLib.normalizeIndicator(indicator[a][0], indicator[a][1]);
           
               if (!indicatorNormalization[0].contentEquals(indicator[a][0])){
               
                   appLogs.debug("Indicator value \'"+indicator[a][0]+"\' normalized to \'"+indicatorNormalization[0]+"\'.");
                   indicator[a][0] = indicatorNormalization[0];
               
               }

               if (!indicatorNormalization[1].contentEquals(indicator[a][1])){
               
                   appLogs.debug("Indicator type \'"+indicator[a][1]+"\' normalized to \'"+indicatorNormalization[1]+"\'.");
                   indicator[a][1] = indicatorNormalization[1];
               
               }

               //validate indicator            
               if (!MethLib.validateIndicator(indicator[a][0], indicator[a][1])){
               
                   appLogs.debug("Skipped - Indicator \'"+indicator[a][0]+"\' is not a valid "+indicator[a][1]+".");
                   continue;
               
               }
           
               //check db for indicator and get id
               indicator[a][3] = MethLib.dbQueryGetIndicatorId(indicator[a][0], indicator[a][1], indicatorStatus, indicator[a][2], indicatorSource);
           
               if (StringUtils.isBlank(indicator[a][3])){
               
                   appLogs.debug("FAILED - Indicator \'"+indicator[a][0]+"\' was not found.");
                   indicator[a][0] = "";
                   softAssert.fail();
                   continue;
               
               }                                  
                   
           }
           
           //loop through all indicator values
           for(int a=0; a<indicator.length;a++){ 
               
               for(int b=0; b<indicatorAttribute.length;b++){
                   
                   //verify non-blank indicator and attribute values
                   if (!(StringUtils.isBlank(indicator[a][0]) || StringUtils.isBlank(indicatorAttribute[b][0]))){
                   
                       //check db for indicator attribute
                       if (!MethLib.dbQueryAttributeExists(indicator[a][0], indicatorSource, indicatorAttribute[b][0], indicatorAttribute[b][1])){
                   
                           appLogs.debug("FAILED - Attribute \'"+indicatorAttribute[b][0]+": "+indicatorAttribute[b][1]+"\' was not found for \'"+indicator[a][0]+"\'.");
                           softAssert.fail();
                               
                       }
                   
                   }

               }

               for(int b=0; b<indicator.length;b++){
                   
                   //verify non-blank indicator values
                   if(a!=b && !(StringUtils.isBlank(indicator[a][0]) || StringUtils.isBlank((indicator[b][0])))){
                      
                       //check db for object links relation
                       if (!MethLib.dbQueryIndicatorLinkExists(indicator[a][3], indicator[b][3])){
               
                           appLogs.debug("FAILED - Object link of "+indicator[a][1]+": \'"+indicator[a][0]+"\' to "+indicator[b][1]+": \'"+indicator[b][0]+"\' was not found.");
                           softAssert.fail();
               
                       }
                       
                   }
                       
               }
                       
           }
           
       }
       
       //if any soft asserts failed, fail the test
       softAssert.assertAll();
       
   }
           
}
