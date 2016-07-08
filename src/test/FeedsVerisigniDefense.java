package tests;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import core.MethLib;

public class FeedsVerisigniDefense extends MethLib{

    
    String testName = "FeedsVerisigniDefense";
    
    @BeforeTest
    public void runTest(){
        
        if(! MethLib.runTest(testName)){
            
            appLogs.debug("Run Test set to no for: "+testName+". Skipping test.");
            throw new SkipException("Run Test set to no for: "+testName+". Skipping test.");
            
        }else{
            
            appLogs.debug("Executing test: "+testName+".");
            
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFeedsVerisigniDefense () throws InterruptedException, SQLException, IOException, ParseException{
        
        //local variables
        String feedUrl = new String();
        File response = null;
        Document document = null;
        List<Node> indicators = new ArrayList<Node>();
        String[][] indicator = new String[4][4];
        String[] indicatorNormalization = new String[2];
        String[][] indicatorAttribute = new String[6][2];
        String indicatorStatus = new String();
        String indicatorSource = new String();
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
        
        //define feed characteristics
        feedUrl = "https://ialert.idefense.com/icontent/ipdata/ipservice.xml?servicename=IPservice_TIIncremental_ALL_xml_v3";
        indicatorStatus = "Active";
        indicatorSource = "Verisign iDefense";
        indicatorAttribute[0][0] = "Role";
        indicatorAttribute[1][0] = "Description";
        indicatorAttribute[2][0] = "Verisign iDefense IR DocID";
        indicatorAttribute[3][0] = "Confidence";
        indicatorAttribute[4][0] = "Verisign iDefense Severity";
        indicatorAttribute[5][0] = "Last Observed";
        
        //set file config
        if (config.getProperty("feedFromFile").contentEquals("y")){
            
            config.setProperty("feedFileName", "data/feeds/"+testName+".txt");
        
        }
        
        //make feed request
        response = MethLib.getVerisigniDefenseIndicators(feedUrl);
        
        //parse xml response for indicator records
        SAXReader reader = new SAXReader();
        
        try {
        
            document = reader.read(response);
        
        } catch (DocumentException e) {
        
            e.printStackTrace();
        
        }
        
        indicators = document.selectNodes("/threat_indicators/indicator");
        
        //loop through the indicator record nodes
        for (Node node : indicators) {
            
           //parse indicator details
           indicator[0][0] = node.selectSingleNode("value").getText();
           indicator[0][1] = node.valueOf("@type");

           //map returned indicator type to tq indicator type
           indicator[0][1] = MethLib.mapVerisigniDefenseIndicatorType(indicator[0][1]);

           //parse indicator sample details
           indicator[1][0] = node.selectSingleNode("sample_md5").getText();
           indicator[1][1] = "MD5";
           indicator[2][0] = node.selectSingleNode("sample_sha1").getText();
           indicator[2][1] = "SHA-1";
           indicator[3][0] = node.selectSingleNode("sample_sha256").getText();
           indicator[3][1] = "SHA-256";
                      
           //define indicator attribute array
           indicatorAttribute[0][1] = node.selectSingleNode("role").getText();           
           indicatorAttribute[1][1] = node.selectSingleNode("comment").getText();           
           indicatorAttribute[2][1] = node.selectSingleNode("ref_id").getText();           
           indicatorAttribute[3][1] = node.selectSingleNode("confidence").getText();           
           indicatorAttribute[4][1] = node.selectSingleNode("severity").getText();
           indicatorAttribute[5][1] = node.selectSingleNode("last_observed").getText();
           
           for(int a=0; a<indicator.length;a++){
           
               //skip blank indicator values
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
                   softAssert.fail();
                   continue;
               
               }                                  
                   
               for(int b=0; b<indicatorAttribute.length;b++){
                   
                   //verify non-blank attribute values
                   if (!StringUtils.isBlank(indicatorAttribute[b][1])){
                   
                       //check db for indicator attribute
                       if (!MethLib.dbQueryAttributeExists(indicator[a][0], indicatorSource, indicatorAttribute[b][0], indicatorAttribute[b][1])){
                   
                           appLogs.debug("FAILED - Attribute \'"+indicatorAttribute[b][0]+": "+indicatorAttribute[b][1]+"\' was not found for \'"+indicator[a][0]+"\'.");
                           softAssert.fail();
                   
                       }
                   
                   }

               }

               //check db for object links relation
               for(int b=0; b<indicator.length;b++){

                   if(a!=b && !(StringUtils.isBlank(indicator[a][0]) || StringUtils.isBlank(indicator[b][0]))){
                      
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
