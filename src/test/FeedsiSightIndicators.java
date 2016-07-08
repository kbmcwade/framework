package tests;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import core.MethLib;

public class FeedsiSightIndicators extends MethLib{

    
    String testName = "FeedsiSightIndicators";
    
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
    public void testFeedsiSightIndicators () throws IOException, ParseException, InterruptedException, SQLException{
        
        String reportId = new String();
        String threatScape = new String();
        long publishedDate;
        String publishedDateAttribute = new String();
        String indicators = new String();
        List<String> indicatorList = new ArrayList<String>();
        String[] indicatorMapping = new String[2];
        String indicatorType = new String();
        String indicatorClass = new String();
        String indicatorStatus = new String();
        String indicatorSource = new String();
        String[] indicatorNormalization = new String[2];
        List<String> indicatorAttributeList = new ArrayList<String>();
        String[] indicatorAttribute = new String[2];
        String attachmentName = new String();
        List<String> threatScapeList = new ArrayList<String>();
        long startDate;
        long endDate;
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
        
        //define feed characteristics
        indicatorStatus = "Active";
        indicatorSource = "iSight Partners";
        indicatorList = MethLib.getiSightViewIndicatorsTypes();
        indicatorAttributeList = MethLib.getiSightViewIndicatorsAttributes();
        
        //convert import dates to epoch time
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date start = format.parse(config.getProperty("isStartDate"));
        startDate = start.getTime() / 1000;
        Date end = format.parse(config.getProperty("isEndDate"));
        endDate = end.getTime() / 1000;
        
        //make view indicators request
        try{
            
            indicators = MethLib.iSightViewIndicatorsEndpoint(startDate, endDate);
        
        }catch (Exception e){
            
            e.printStackTrace();
            
        }
  
        //write response to file
        MethLib.stringtoFile(indicators, "iSight-View-Indicators.json");
       
        //parse the root object
        JsonElement jelement = new JsonParser().parse(indicators);
        JsonObject  jobject = jelement.getAsJsonObject();
    
        boolean success = jobject.get("success").getAsBoolean();
    
        if (!success){
        
            appLogs.debug("FAILED - View Indicators API response: "+success+".");
            softAssert.fail();

        }
    
        //parse the message array
        JsonArray indicatorArray = jobject.getAsJsonArray("message");
    
        //loop through array of indicator records
        for(int nu=0; nu<indicatorArray.size(); nu++){
        
            reportId = indicatorArray.get(nu).getAsJsonObject().get("reportId").toString().replace("\"", "");
            publishedDate = indicatorArray.get(nu).getAsJsonObject().get("publishDate").getAsLong();
            threatScape = indicatorArray.get(nu).getAsJsonObject().get("ThreatScape").toString().replace("\"", "");
            
            //assess record for indicators
            for(int num=0; num<indicatorList.size(); num++){               
                
                String indicatorValue = null;
            
                try{ 
                    
                    indicatorValue = indicatorArray.get(nu).getAsJsonObject().get(indicatorList.get(num)).toString().replace("\"", "");
                    
                }catch (Exception ignored) {}

                if (indicatorValue.equals("null")){

                    continue;
            
                }else{
                
                    //map the indicator type and class
                    indicatorMapping = MethLib.mapiSightIndicatorType(indicatorList.get(num));
                    indicatorType = indicatorMapping[0];
                    indicatorClass = indicatorMapping[1];
            
                    //replace double backslashes with single backslash
                    indicatorValue = indicatorValue.replaceAll("\\\\\\\\", "\\\\");
            
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
         
                    //set status to indirect if a related identifier attribute value exists
                    if(indicatorArray.get(nu).getAsJsonObject().get("emailIdentifier").equals("Related") ||
                        indicatorArray.get(nu).getAsJsonObject().get("fileIdentifier").equals("Related") ||
                        indicatorArray.get(nu).getAsJsonObject().get("networkIdentifier").equals("Related")){
                            
                        indicatorStatus = "Indirect";
                        appLogs.debug("Status updated to \'Indirect\'");
                    
                    }
                    
                    //check db for indicator
                    if (!MethLib.dbQueryCountIndicator(indicatorValue, indicatorType, indicatorStatus, indicatorClass, indicatorSource)){
                        
                        appLogs.debug("FAILED - Indicator \'"+indicatorValue+"\' of type \'"+indicatorType+"\' was not found.");
                        softAssert.fail();
                        continue;
                        
                    }
                    
                }
            
                //assess record for attributes
                for(int numb=0; numb<indicatorAttributeList.size(); numb++){               
                    
                    indicatorAttribute[1] = null;
                    
                    try{ 
                        
                        indicatorAttribute[1] = indicatorArray.get(nu).getAsJsonObject().get(indicatorAttributeList.get(numb)).toString().replace("\"", "");
                        
                    }catch (Exception ignored) {}

                    if (indicatorAttribute[1].equals("null")){

                        continue;
                
                    }else{
                    
                        //map attribute key
                        indicatorAttribute[0] = mapiSightAttribute(indicatorAttributeList.get(numb));
                        
                        if(indicatorType.equals("Filename") && indicatorAttribute[0].equals("Filename")) continue;
                            
                        //check db for attribute
                        if (!MethLib.dbQueryAttributeExists(indicatorValue, indicatorSource, indicatorAttribute[0], indicatorAttribute[1])){
                            
                            appLogs.debug("FAILED - Attribute \'"+indicatorAttribute[0]+": "+indicatorAttribute[1]+"\' was not found for \'"+indicatorValue+"\' of type \'"+indicatorType+"\'.");
                            softAssert.fail();
                            
                        }
                        
                    }
                    
                    //verify linked report
                    attachmentName = "isight-threatscape-"+reportId+".pdf";
                    
                    if (!MethLib.dbQueryAttachmentLinkExists(indicatorValue, indicatorSource, attachmentName)){
                            
                        appLogs.debug("FAILED - Linked report \'"+reportId+"\' was not found for indicator \'"+indicatorValue+"\' of type \'"+indicatorType+"\'.");
                        softAssert.fail();
                        
                    }
                    
                    //verify published date attribute
                    //convert epoch timestamp to simple date format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    publishedDateAttribute = dateFormat.format(new Date(publishedDate*1000L)).toString();
                    
                    if (!MethLib.dbQueryAttributeExists(indicatorValue, indicatorSource, "Published Date", publishedDateAttribute)){
                        
                        appLogs.debug("FAILED - Attribute \'Published Date: "+publishedDateAttribute+"\' was not found for \'"+indicatorValue+"\' of type \'"+indicatorType+"\'.");
                        softAssert.fail();
                        
                    }
                    
                    //verify threatscape attributes
                    threatScapeList = Arrays.asList(threatScape.split(","));
                    
                    for(int numbe=0; numbe<threatScapeList.size(); numbe++){               
                        
                        if (!MethLib.dbQueryAttributeExists(indicatorValue, indicatorSource, "iSight ThreatScape Product", threatScapeList.get(numbe))){
                            
                            appLogs.debug("FAILED - Attribute \'iSight ThreatScape Product: "+threatScapeList.get(numbe)+"\' was not found for \'"+indicatorValue+"\'.");
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
