package tests;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import core.MethLib;

public class FeedsCrowdStrikeAttributes extends MethLib{

    
    String testName = "FeedsCrowdStrikeAttributes";
    
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
    public void testFeedsCrowdStrikeAttributes () throws InterruptedException, SQLException, IOException, ParseException{
        
        //local variables
        Object[] indicatorAssessment = new Object[4];
        String[] indicatorNormalization = new String[2];
        String[] indicatorPublishedDate = new String[2];
        String[] labelSplit = new String [2];
        String csResponse = new String();
        String indicatorValue = new String();
        String indicatorId = new String();
        String indicatorType = new String();
        String indicatorClass = new String();
        String indicatorTypeCs = new String();
        String indicatorLabel = new String();
        String labelName = new String();
        String labelNameCs = new String();
        String labelValue = new String();
        boolean createIndicator = false;
        int iterate = 1;
        long publishedDate = 0L;
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();

        //request crowdstrike indicators
        while(!csResponse.contentEquals("[]")){
        
            csResponse = MethLib.getCsIndFeed(iterate);
            
            if(!csResponse.contentEquals("[]")){
        
                //parse the root object
                JsonArray primary = (JsonArray) new JsonParser().parse(csResponse);
        
                for(int nu=0; nu<primary.size(); nu++){
        
                    indicatorValue = ((JsonObject)primary.get(nu)).get("indicator").toString().replace("\"", "");
                    indicatorTypeCs = ((JsonObject)primary.get(nu)).get("type").toString().replace("\"", "");
            
                    //skip any indicators of type campaign_id
                    if (indicatorTypeCs.contentEquals("campaign_id")){
                
                        continue;
                
                    }
            
                    //assess the indicator
                    indicatorAssessment = MethLib.mapCsIndicatorType(indicatorTypeCs);
                    indicatorType = (String) indicatorAssessment[0];
                    createIndicator = (boolean) indicatorAssessment[1];
                    indicatorClass = (String) indicatorAssessment[3];
                    
                    //verify the primary indicator is a recognized type
                    if (StringUtils.isBlank(indicatorType)){
                
                        appLogs.debug("Primary indicator \'"+indicatorValue+"\' is unrecognized type \'"+indicatorTypeCs+"\'");
                        continue;
                
                    }
            
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
                    
                    //query the db for the indicator
                    indicatorId = dbQueryGetId(indicatorValue, indicatorClass, indicatorType);
            
                    if (!createIndicator && !StringUtils.isBlank(indicatorId)){
                
                        appLogs.debug("FAILED - Primary indicator \'"+indicatorValue+"\' of unsupported type \'"+indicatorType+"\' was found.");
                        softAssert.fail();
                        continue;
                
                    } else if (createIndicator && StringUtils.isBlank(indicatorId)){
                    
                        appLogs.debug("FAILED - Primary indicator \'"+indicatorValue+"\' of type \'"+indicatorType+"\' was not found.");
                        softAssert.fail();
                        continue;

                    } else if (!createIndicator){
                
                        continue;

                    }
            
                    //verify published date attribute
                    indicatorPublishedDate[0] = "Published Date";
                    publishedDate = ((JsonObject)primary.get(nu)).get("published_date").getAsLong();
                    
                    //convert epoch timestamp to simple date format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    indicatorPublishedDate[1] = dateFormat.format(new Date(publishedDate*1000L));
                
                    if (!dbQueryAttributes(indicatorId, indicatorPublishedDate[0], indicatorPublishedDate[1])){
                            
                        //appLogs.debug("FAILED - "+indicatorPublishedDate[0]+": \'"+indicatorPublishedDate[1]+"\' was not found for primary indicator \'"+indicatorValue+"\' of type \'"+indicatorType+"\'.");
                        //softAssert.fail();
                                 
                    }
  
                    //parse the labels array
                    JsonArray relations = ((JsonObject)primary.get(nu)).getAsJsonArray("labels");
        
                    for(int num=0; num<relations.size(); num++){
            
                        JsonObject jobject = relations.get(num).getAsJsonObject();
                        indicatorLabel = jobject.get("name").toString().replace("\"", "");
                
                        //get the label name and value
                        labelSplit = indicatorLabel.split("/");
                        labelNameCs = labelSplit[0];
                        labelValue = labelSplit[1];
                
                        //get threatq label name and id
                        labelName = MethLib.mapCsLabelName(labelNameCs);

                        //skip campaign label
                        if (labelName.contentEquals("Campaign")){
                    
                            continue;
                    
                        }
                
                        //handle unrecognized label names
                        if (StringUtils.isBlank(labelName)){
                    
                            appLogs.debug("Label Name \'"+labelNameCs+"\' is unrecognized.");
                            continue;
                    
                        }else{
                    
                            //query attributes_indicators to verify label value associated with primary indicator
                            if (!dbQueryAttributes(indicatorId, labelName, labelValue)){
                            
                                appLogs.debug("FAILED - Label Name \'"+labelName+"\' with value \'"+labelValue+"\' was not found for primary indicator \'"+indicatorValue+"\' of type \'"+indicatorType+"\'.");
                                softAssert.fail();
                            
                            }
                    
                            //verify linked reports
                            if (labelName.contentEquals("Reference")){
                                
                                if (!FeedsCrowdStrikeAttributes.dbQueryAttachmentLinkExists(indicatorId, labelValue+".pdf")){
                                    
                                    appLogs.debug("FAILED - Attachment \'"+labelValue+"\' was not found for primary indicator \'"+indicatorValue+"\' of type \'"+indicatorType+"\'.");
                                    softAssert.fail();
                                
                                } 
                                
                            }
                        }
                
                    }
        
                }
        
            }
            
            iterate++;
        
        }
        
        //if any soft asserts failed, fail the test
        softAssert.assertAll();
        
    }
    
    public boolean dbQueryAttributes(String indicatorId, String attributeName, String attributeValue) throws InterruptedException, SQLException{
        
        //local variables
        List<String> attributeQuery = null;
        String mySqlQuery = null;
        int attributeCount = 0;
        boolean attributeExists = false;

        //get count of attribute value from attributes_indicators
        mySqlQuery = "SELECT COUNT(i.id) FROM indicator_attributes i LEFT JOIN attributes a ON i.attribute_id = a.id WHERE i.value = \'"+attributeValue+"\'AND i.indicator_id = \'"+indicatorId+"\' AND a.name = \'"+attributeName+"\';";
        attributeQuery = mySql.getMysqlQuery(mySqlQuery);
        attributeCount = Integer.parseInt(attributeQuery.get(0));
        

        if (attributeCount > 0){
            
            attributeExists = true;
            
        }
        
        return attributeExists;
        
    }
    
    public static boolean dbQueryAttachmentLinkExists(String indicatorId, String attachmentName) throws InterruptedException, SQLException{
        
        //local variables
        List<String> indicatorQuery = null;
        String mySqlQuery = null;
        int linkCount = 0;
        boolean attachmentLinkExists = false;
        
        //get count of linked indicator as destination object from object_links table
        mySqlQuery = "SELECT COUNT(o.id) FROM object_links o LEFT JOIN attachments a ON o.dest_object_id = a.id WHERE o.src_object_id = \'"+indicatorId+"\' AND o.src_type = \'indicator\' AND a.name  = \'"+attachmentName+"\' AND o.dest_type = \'attachment\' AND o.deleted_at IS null;";
        
        indicatorQuery = mySql.getMysqlQuery(mySqlQuery);
        linkCount = Integer.parseInt(indicatorQuery.get(0));
        
        //get count of linked indicator as source object from object_links table
        mySqlQuery = "SELECT COUNT(o.id) FROM object_links o LEFT JOIN attachments a ON o.src_object_id = a.id WHERE a.name = \'"+attachmentName+"\' AND o.src_type = \'attachment\' AND o.dest_object_id  = \'"+indicatorId+"\' AND o.dest_type = \'indicator\' AND o.deleted_at IS null;";
        
        indicatorQuery = mySql.getMysqlQuery(mySqlQuery);
        linkCount = (linkCount + Integer.parseInt(indicatorQuery.get(0)));
       
        if (linkCount > 0){
            
            attachmentLinkExists = true;
            
        }
        
        return attachmentLinkExists;
        
    }
    
    public String dbQueryGetId(String indicatorValue, String indicatorClass, String indicatorTypeCs) throws InterruptedException, SQLException{
        
        //local variables
        List<String> idQuery = null;
        String mySqlQuery = null;
        String indicatorId = null;
        
        //get count of indicator value from indicators table
        mySqlQuery = "SELECT i.id FROM indicators i LEFT JOIN indicator_statuses s ON i.status_id = s.id LEFT JOIN indicator_types t ON i.type_id = t.id WHERE i.value = \'"+indicatorValue+"\' AND i.class = \'"+indicatorClass+"\' AND t.name = \'"+MethLib.escStr(indicatorTypeCs)+"\';";
        idQuery = mySql.getMysqlQuery(mySqlQuery);
        
        if (idQuery.size() > 0){
            
            indicatorId = idQuery.get(0);
            return indicatorId;
        
        }else{
            
            indicatorId = "";
            return indicatorId;
            
        }
        
    }
    
}   
