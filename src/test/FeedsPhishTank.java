package tests;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import core.MethLib;

public class FeedsPhishTank extends MethLib{

    
    String testName = "FeedsPhishTank";
    
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
    public void testFeedsPhishTank () throws InterruptedException, SQLException, IOException, ParseException{
        
        //local variables
        String feedUrl = new String();
        String indicators = new String();
        String[][] indicator = new String[3][4];
        String[] indicatorNormalization = new String[2];
        String[][] indicatorAttribute = new String[6][2];
        String indicatorStatus = new String();
        String indicatorSource = new String();
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
        
        //define feed characteristics
        feedUrl = "http://data.phishtank.com/data/6a824362f38ec50d6706325bce0c77f5ea716ade087656b5c47903d130996dba/online-valid.json.gz";
        indicatorAttribute[0][0] = "PhishTank ID";
        indicatorAttribute[1][0] = "PhishTank URL";
        indicatorAttribute[2][0] = "Target";
        indicatorAttribute[3][0] = "Announcing Network";
        indicatorAttribute[4][0] = "RIR";
        indicatorAttribute[5][0] = "Country";
        indicatorStatus = "Active";
        indicatorSource = "PhishTank";
        
        //set file config
        if (config.getProperty("feedFromFile").contentEquals("y")){
            
            config.setProperty("feedFileName", "data/feeds/"+testName+".txt");
        
        }
        
        //make feed request
        indicators = MethLib.getFeedIndicators(feedUrl).get(0);
        
        //parse the root object
        JsonArray primary = (JsonArray) new JsonParser().parse(indicators);

        for(int nu=0; nu<primary.size(); nu++){

            indicator[0][1] = "URL";
            indicator[1][1] = "IP Address";
            indicator[2][1] = "CIDR Block";

            indicator[0][0] = ((JsonObject)primary.get(nu)).get("url").toString().replace("\"", "");
            indicatorAttribute[0][1] = ((JsonObject)primary.get(nu)).get("phish_id").toString().replace("\"", "");
            indicatorAttribute[1][1] = ((JsonObject)primary.get(nu)).get("phish_detail_url").toString().replace("\"", "");
            indicatorAttribute[2][1] = ((JsonObject)primary.get(nu)).get("target").toString().replace("\"", "");       

            //parse the details array
            JsonArray details = ((JsonObject)primary.get(nu)).getAsJsonArray("details");

            for(int num=0; num<details.size(); num++){
    
                JsonObject detailsObject = details.get(num).getAsJsonObject();
                indicator[1][0] = detailsObject.get("ip_address").toString().replace("\"", "");
                indicator[2][0] = detailsObject.get("cidr_block").toString().replace("\"", "");
                indicatorAttribute[3][1] = detailsObject.get("announcing_network").toString().replace("\"", "");
                indicatorAttribute[4][1] = detailsObject.get("rir").toString().replace("\"", "");
                indicatorAttribute[5][1] = detailsObject.get("country").toString().replace("\"", "");

                //loop through all indicator values
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
               
                    //loop through all attribute values
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
           
        }
       
        //if any soft asserts failed, fail the test
        softAssert.assertAll();
       
    }
           
}
