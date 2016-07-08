package tests;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import core.MethLib;

public class FeedsiSightReports extends MethLib{

    
    String testName = "FeedsiSightReports";
    
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
    public void testFeedsiSightReports () throws IOException, ParseException, InterruptedException, SQLException{
        
        List<String> reportList = new ArrayList<String>();
        String reportResponse = new String();
        String reportId = new String();
        String reportTitle = new String();
        String reportType = new String();
        String reportVersion = new String();
        String productTag = new String();
        List<String> tagSectionList =  new ArrayList<String>();
        String tagSection = new String();
        String attachmentName = new String();
        String attachmentTitle = new String();
        String attachmentId = new String();
        String attachmentSource = new String();
        long startDate;
        long endDate;
        
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
        
        //define feed characteristics
        reportType = "iSight ThreatScape Intelligence Report";
        attachmentSource = "iSight Partners";
        tagSectionList = MethLib.getiSightTagSections();
        
        //convert import dates to epoch time
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date start = format.parse(config.getProperty("isStartDate"));
        startDate = start.getTime() / 1000;
        Date end = format.parse(config.getProperty("isEndDate"));
        endDate = end.getTime() / 1000;
                    
        //get report index list
        reportList = getReportsFromIndex(startDate, endDate);
        
        if (reportList.isEmpty()){
            
            Assert.fail();

        }
        
        for(int num=0; num<reportList.size(); num++){

            try{
                
                reportResponse = MethLib.iSightReportEndpoint(reportList.get(num));
                
            }catch (Exception e){

                e.printStackTrace();
            }
               
            //write response to file
            MethLib.stringtoFile(reportResponse, reportList.get(num)+".json");
            
            //parse the root object
            JsonElement jelement = new JsonParser().parse(reportResponse);
            JsonObject  jobject = jelement.getAsJsonObject();
        
            boolean success = jobject.get("success").getAsBoolean();
        
            if (!success){
            
                appLogs.debug("FAILED - Report Detail API response: "+success+".");
                softAssert.fail();

            }
        
            //get report object from message
            JsonObject report = jobject.getAsJsonObject("message").getAsJsonObject("report");
            
            reportId = report.get("reportId").toString().replace("\"", "");
            reportTitle = report.get("title").toString().replace("\"", "");
            reportVersion = report.get("version").toString().replace("\"", "");
                       
            //format name
            attachmentName = "isight-threatscape-"+reportId+".pdf";
            
            //prepend report id to title
            attachmentTitle = reportId+" "+reportTitle;
 
            //check for attachment in database
            attachmentId = MethLib.dbQueryGetAttachmentId(attachmentName, attachmentTitle, reportType);
            
            if (StringUtils.isBlank(attachmentId)){
                    
                appLogs.debug("FAILED - Attachment \'"+reportId+"\' was not found.");
                softAssert.fail();
                continue;
            
            }
            
            //check for version attribute in database
            if (!dbQueryAttachmentAttributeExists(attachmentName, attachmentSource, "Version", reportVersion)){
                    
                appLogs.debug("FAILED - Attribute \'Version\' with value \'"+reportVersion+"\' was not found for report \'"+reportId+"\'.");
                softAssert.fail();
                
            }

            //parse the threatscape product array
            JsonArray product = report.getAsJsonObject("ThreatScape").getAsJsonArray("product");

            for(int numb=0; numb<product.size(); numb++){
    
                productTag = product.get(numb).toString().replace("\"", "").replaceFirst("ThreatScape ", "");

                //check for product tag in database
                if (!dbQueryTagExists(productTag, attachmentId, "attachment")){
                    
                    appLogs.debug("FAILED - Tag \'"+productTag+"\' was not found for report \'"+reportId+"\'.");
                    softAssert.fail();
                
                }
            
            }
                
            //parse the tag sections, if they exist            
            for(int numb=0; numb<tagSectionList.size(); numb+=2){               
            
                JsonArray tagSections = null;
            
                try{ 
                    
                    tagSections = report.getAsJsonObject("tagSection").getAsJsonObject("main").getAsJsonObject(tagSectionList.get(numb+1)).getAsJsonArray(tagSectionList.get(numb));
                    
                }catch (Exception ignored) {}

                if (tagSections == null){

                    continue;
            
                }else{
                
                    appLogs.debug("Found: "+tagSectionList.get(numb)+" section for "+reportId);
                    
                    // check for each tag in database
                    for(int n=0; n<tagSections.size(); n++){
    
                        tagSection = tagSections.get(n).toString().replace("\"", "").split(" >>")[0];

                        if (!dbQueryTagExists(tagSection, attachmentId, "attachment")){
                    
                            appLogs.debug("FAILED - Tag \'"+tagSection+"\' was not found for report \'"+reportId+"\'.");
                            softAssert.fail();
                
                        }
                        
                    }
                
                }
            
            }
            
            //handle rate limiting
            TimeUnit.MILLISECONDS.sleep(300);
        
        }
        
        //if any soft asserts failed, fail the test
        softAssert.assertAll();

    }

    public List<String> getReportsFromIndex(long startDate, long endDate) throws InterruptedException, SQLException{
        
        //local variables
        String reportIndex = new String();
        List<String> reportList = new ArrayList<String>();
    
        //make report index request
        try {
            
            reportIndex = MethLib.iSightReportIndexEndpoint(startDate, endDate);
        
        } catch (Exception e) {
            
            e.printStackTrace();
            
        }
  
        //write response to file
        MethLib.stringtoFile(reportIndex, "iSight-Report-Index.json");
       
       //parse the index root object
        JsonElement jelement = new JsonParser().parse(reportIndex);
        JsonObject  jobject = jelement.getAsJsonObject();
    
        boolean success = jobject.get("success").getAsBoolean();
    
        if (!success){
        
            appLogs.debug("FAILED - Report Index API response: "+success+".");
            return reportList;

        }else{
        
            //parse the index message array
            JsonArray jarray = jobject.getAsJsonArray("message");
    
            for(int num=0; num<jarray.size(); num++){
        
                jobject = jarray.get(num).getAsJsonObject();
                reportList.add(jobject.get("reportId").toString().replace("\"", ""));

            }

            return reportList;
        
        }
    
    }
    
}
