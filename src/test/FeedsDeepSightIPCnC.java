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

public class FeedsDeepSightIPCnC extends MethLib{

    
    String testName = "FeedsDeepSightIPCnC";
    
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
    public void testFeedsDeepSightIPCnC () throws InterruptedException, SQLException, IOException, ParseException{
        
        //local variables
        String feedUrl = new String();
        File response = null;
        Document document = null;
        List<Node> indicators = new ArrayList<Node>();
        String indicator = new String();
        String indicatorType = new String();
        String indicatorClass = new String();
        String[] indicatorNormalization = new String[2];
        String[][] indicatorAttribute = new String[8][2];
        List<Node> cncNames = new ArrayList<Node>();
        String[] cncNameAttribute = new String[2];
        List<Node> domainNames = new ArrayList<Node>();
        String [] domainNameAttribute = new String[2];
        String indicatorStatus = new String();
        String indicatorSource = new String();
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
        
        //define feed characteristics
        feedUrl = "";
        indicatorStatus = "Active";
        indicatorSource = "DeepSight Advanced IP Reputation CnC XML Feed";
        indicatorAttribute[0][0] = "ASN";
        indicatorAttribute[1][0] = "DeepSight Intelligence Confidence"; 
        indicatorAttribute[2][0] = "DeepSight Intelligence First Seen";
        indicatorAttribute[3][0] = "DeepSight Intelligence Hostility";
        indicatorAttribute[4][0] = "DeepSight Intelligence Last Seen";
        indicatorAttribute[5][0] = "DeepSight Intelligence Reputation Rating";
        indicatorAttribute[6][0] = "City";       
        indicatorAttribute[7][0] = "Country";
        cncNameAttribute[0] = "DeepSight Intelligence CNC Name";
        domainNameAttribute[0] = "DeepSight Intelligence Domain";
        
        //set file config
        if (config.getProperty("feedFromFile").contentEquals("y")){
            
            config.setProperty("feedFileName", "data/feeds/deepsight/"+testName+".xml");
        
        }
        
        //make feed request
        response = MethLib.getDeepSightIndicators(feedUrl);
        
        //parse xml response for indicator records
        SAXReader reader = new SAXReader();
        
        try {
        
            document = reader.read(response);
        
        } catch (DocumentException e) {
        
            e.printStackTrace();
        
        }
        
        indicators = document.selectNodes("/feed/data/ipAddress");
        
        //loop through the data record nodes
        for (Node node : indicators) {
            
            //parse indicator details
            indicator = node.valueOf("@address");
            indicatorType = "IP Address";
                     
            //define base indicator attribute array
            indicatorAttribute[0][1] = node.valueOf("@asn");           
            indicatorAttribute[1][1] = node.valueOf("@confidence");           
            indicatorAttribute[2][1] = node.valueOf("@first_seen");           
            indicatorAttribute[3][1] = node.valueOf("@hostility");           
            indicatorAttribute[4][1] = node.valueOf("@last_seen");
            indicatorAttribute[5][1] = node.valueOf("@reputation_rating");
            indicatorAttribute[6][1] = node.selectSingleNode("location").valueOf("@city");
            indicatorAttribute[7][1] = node.selectSingleNode("location").valueOf("@country");
           
            //get indicator class
            indicatorClass = MethLib.dbQueryGetClass(indicatorType);
           
            //verify the indicator is a recognized type
            if (StringUtils.isBlank(indicatorClass)){
       
                appLogs.debug("Skipped - \'"+indicator+"\' is unrecognized type \'"+indicatorType+"\'");
                continue;
       
            }
           
            //normalize the indicator
            indicatorNormalization = MethLib.normalizeIndicator(indicator, indicatorType);
           
            if (!indicatorNormalization[0].contentEquals(indicator)){
               
                appLogs.debug("Indicator value \'"+indicator+"\' normalized to \'"+indicatorNormalization[0]+"\'.");
                indicator = indicatorNormalization[0];
               
            }

            if (!indicatorNormalization[1].contentEquals(indicatorType)){
               
                appLogs.debug("Indicator type \'"+indicatorType+"\' normalized to \'"+indicatorNormalization[1]+"\'.");
                indicatorType = indicatorNormalization[1];
               
            }

            //validate indicator            
            if (!MethLib.validateIndicator(indicator, indicatorType)){
               
                appLogs.debug("Skipped - Indicator \'"+indicator+"\' is not a valid "+indicatorType+".");
                continue;
               
            }
           
            //check db for indicator
            if (!MethLib.dbQueryCountIndicator(indicator, indicatorType, indicatorStatus, indicatorClass, indicatorSource)){
               
                appLogs.debug("FAILED - Indicator \'"+indicator+"\' was not found.");
                softAssert.fail();
                continue;
               
            }                                  
             
            //check db for base indicator attributes
            for(int a=0; a<indicatorAttribute.length;a++){
                   
                //verify non-blank attribute values
                if (!StringUtils.isBlank(indicatorAttribute[a][1])){
                   
                    //check db for indicator attribute
                    if (!MethLib.dbQueryAttributeExists(indicator, indicatorSource, indicatorAttribute[a][0], indicatorAttribute[a][1])){
                   
                        appLogs.debug("FAILED - Attribute \'"+indicatorAttribute[a][0]+": "+indicatorAttribute[a][1]+"\' was not found for \'"+indicator+"\'.");
                        softAssert.fail();
                   
                    }
                   
                }

            }
           
            //parse cnc names node
            cncNames = node.selectNodes("/cnc_names/cnc_name");
           
            //loop through the cnc names record nodes
            for (Node cnNode : cncNames) {
               
                //parse cnc name details
                cncNameAttribute[1] = cnNode.valueOf("@name");
                  
                //verify non-blank attribute values
                if (!StringUtils.isBlank(cncNameAttribute[1])){
                      
                    //check db for indicator attribute
                    if (!MethLib.dbQueryAttributeExists(indicator, indicatorSource, cncNameAttribute[0], cncNameAttribute[1])){
                      
                        appLogs.debug("FAILED - Attribute \'"+cncNameAttribute[0]+": "+cncNameAttribute[1]+"\' was not found for \'"+indicator+"\'.");
                       softAssert.fail();
                      
                    }

                }
                
                //parse domain names node
                domainNames = cnNode.selectNodes("/domain_names/domain");
               
                //loop through the domain names record nodes
                for (Node dnNode : domainNames) {
                   
                    //parse domain name details
                    domainNameAttribute[1] = dnNode.valueOf("@name");
                      
                    //verify non-blank attribute values
                    if (!StringUtils.isBlank(domainNameAttribute[1])){
                          
                        //check db for indicator attribute
                        if (!MethLib.dbQueryAttributeExists(indicator, indicatorSource, domainNameAttribute[0], domainNameAttribute[1])){
                          
                            appLogs.debug("FAILED - Attribute \'"+domainNameAttribute[0]+": "+domainNameAttribute[1]+"\' was not found for \'"+indicator+"\'.");
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