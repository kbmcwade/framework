package tests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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

public class ApiGetAttachments extends MethLib{

	
	String testName = "ApiGetAttachments";
	
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
	public void testApiGetAttachments () throws IOException, SQLException{
		
	    //local variables
	    Object[] response = new Object [3];
	    String apiRequest="attachments";
		String method = "GET";
		String url = apiUrl+apiRequest;
		String responseBody = null;
		String mySqlQuery = null;
		String attachmentId = null;
		String attachmentTypeId = null;
		String attachmentTitle = null;
		String attachmentName = null;
		String attachmentHash = null;
		String attachmentContentTypeId = null;
		String attachmentFileSize = null;
		String attachmentMalwareLocked = null;
		String attachmentCreatedAt = null;
	    String attachmentUpdatedAt = null;
	    int statusCode = 0;
	    long responseTime = 0;
	    
		//declare new soft assertion so test will complete before setting to fail
		SoftAssert softAssert = new SoftAssert();
		
        //make api request and get status code and response body
        response = MethLib.apiGetJson(url);
        statusCode = (int) response[0];
        responseTime = (long) response[1];
        responseBody = (String) response[2];
        
        //log response time
        appLogs.debug("Response time: "+MethLib.formatMilliseconds(responseTime)+".");
        
        // assert status code
        if(!(statusCode == 200)){
        
            appLogs.debug("FAILED - \""+method+" "+url+"\" returned a status code of: "+statusCode+".");
            Assert.fail();
            
        }
		
		//check for data in response
        if (responseBody.contentEquals("{}")){
            
            appLogs.debug("Warning - response body is blank.");
            
        }else{
            
            //parse the root object
            JsonElement jelement = new JsonParser().parse(responseBody);
            JsonObject  jobject = jelement.getAsJsonObject();
	            
            //compare total returned from api to total in db that are active
            mySqlQuery = "SELECT COUNT(id) FROM attachments WHERE deleted_at IS NULL;";
            List<String> totalDb = mySql.getMysqlQuery(mySqlQuery);
		
            String totalApi = jobject.get("total").toString();
	    
            appLogs.debug("Total returned from api: "+totalApi+".");
            appLogs.debug("Total returned from db: "+totalDb.get(0)+".");
		
            if (!totalDb.get(0).equals(totalApi)){
			
                appLogs.debug("FAILED - Total returned from api does not match total active attachments in database.");
                softAssert.fail();

            }else{
			
                appLogs.debug("Passed - Total returned from api matches total active attachments in database.");
		
            }
		
            //parse the data array
            JsonArray jarray = jobject.getAsJsonArray("data");
	    
            for(int num=0; num<jarray.size(); num++){
			
                jobject = jarray.get(num).getAsJsonObject();
                attachmentId = jobject.get("id").toString();
                attachmentTypeId = jobject.get("type_id").toString();
                attachmentTitle = jobject.get("title").toString().replace("\"", "");
                attachmentName = jobject.get("name").toString().replace("\"", "");
                attachmentHash = jobject.get("hash").toString().replace("\"", "");
                attachmentContentTypeId = jobject.get("content_type_id").toString();
                attachmentFileSize = jobject.get("file_size").toString();
                attachmentMalwareLocked = jobject.get("malware_locked").toString();
                attachmentCreatedAt = jobject.get("created_at").toString().replace("\"", "");
                attachmentUpdatedAt = jobject.get("updated_at").toString().replace("\"", "");
	    	
                appLogs.debug("Comparing data for Attachment: "+attachmentName);
	    	
                //verify the attachment is active in the db
                mySqlQuery = "SELECT deleted_at FROM attachments WHERE id = \'"+attachmentId+"\';";
                List<String> attachmentDeletedDb = mySql.getMysqlQuery(mySqlQuery);
                if (attachmentDeletedDb.get(0) != null){
				
                    appLogs.debug("FAILED - Attachment is not active in the database.");
                    softAssert.fail();

                }else{
			
                    appLogs.debug("Passed - Attachment is active in the database.");
		
                }
	    	
                //verify attachment id against db
                mySqlQuery = "SELECT id FROM attachments WHERE hash = \'"+attachmentHash+"\' AND deleted_at IS NULL;";
                List<String> attachmentIdDb = mySql.getMysqlQuery(mySqlQuery);
	    	
                if (!attachmentIdDb.get(0).equals(attachmentId)){
				
                    appLogs.debug("FAILED - Attachment id returned does not match the database.");
                    softAssert.fail();

                }else{
			
                    appLogs.debug("Passed - Attachment id returned matches the database.");
		
                }
	    	
                //verify attachment type_id against db
                mySqlQuery = "SELECT type_id FROM attachments WHERE id = \'"+attachmentId+"\';";
                List<String> attachmentTypeIdDb = mySql.getMysqlQuery(mySqlQuery);
	    	
                if (!attachmentTypeIdDb.get(0).equals(attachmentTypeId)){
				
                    appLogs.debug("FAILED - Attachment type_id returned does not match the database.");
                    softAssert.fail();

                }else{
			
                    appLogs.debug("Passed - Attachment type_id returned matches the database.");
		
                }
	    	
                //verify attachment title against db
                mySqlQuery = "SELECT title FROM attachments WHERE id = \'"+attachmentId+"\';";
                List<String> attachmentTitleDb = mySql.getMysqlQuery(mySqlQuery);
	    	
                if (attachmentTitleDb.get(0) != null && !attachmentTitleDb.get(0).equals(attachmentTitle)){
				
                    appLogs.debug("FAILED - Attachment title returned does not match the database.");
                    softAssert.fail();

                }else if (attachmentTitleDb.get(0) != null && attachmentTitle.equals(null)){
			
                    appLogs.debug("FAILED - Attachment title returned does not match the database.");
                    softAssert.fail();
	    		
                }else{
	    		
                    appLogs.debug("Passed - Attachment title returned matches the database.");
		
                }
	    	
                //verify attachment name against db
                mySqlQuery = "SELECT name FROM attachments WHERE id = \'"+attachmentId+"\';";
                List<String> attachmentNameDb = mySql.getMysqlQuery(mySqlQuery);
	    	
                if (!attachmentNameDb.get(0).equals(attachmentName)){
				
                    appLogs.debug("FAILED - Attachment name returned does not match the database.");
                    softAssert.fail();

                }else{
			
                    appLogs.debug("Passed - Attachment name returned matches the database.");
		
                }
	    	
                //verify attachment hash against db
                mySqlQuery = "SELECT hash FROM attachments WHERE id = \'"+attachmentId+"\';";
                List<String> attachmentHashDb = mySql.getMysqlQuery(mySqlQuery);
	    	
                if (!attachmentHashDb.get(0).equals(attachmentHash)){
				
                    appLogs.debug("FAILED - Attachment hash returned does not match the database.");
                    softAssert.fail();

                }else{
			
                    appLogs.debug("Passed - Attachment hash returned matches the database.");
		
                }
	    	
                //verify attachment content_type_id against db
                mySqlQuery = "SELECT content_type_id FROM attachments WHERE id = \'"+attachmentId+"\';";
                List<String> attachmentContentTypeIdDb = mySql.getMysqlQuery(mySqlQuery);
	    	
                if (!attachmentContentTypeIdDb.get(0).equals(attachmentContentTypeId)){
				
                    appLogs.debug("FAILED - Attachment content_type_id returned does not match the database.");
                    softAssert.fail();

                }else{
			
                    appLogs.debug("Passed - Attachment content_type_id returned matches the database.");
		
                }
	    	
                //verify attachment file_size against db
                mySqlQuery = "SELECT file_size FROM attachments WHERE id = \'"+attachmentId+"\';";
                List<String> attachmentFileSizeDb = mySql.getMysqlQuery(mySqlQuery);
	    	
                if (!attachmentFileSizeDb.get(0).equals(attachmentFileSize)){
				
                    appLogs.debug("FAILED - Attachment file_size returned does not match the database.");
                    softAssert.fail();

                }else{
			
                    appLogs.debug("Passed - Attachment file_size returned matches the database.");
		
                }
	    	
                //verify attachment malware_locked against db
                mySqlQuery = "SELECT malware_locked FROM attachments WHERE id = \'"+attachmentId+"\';";
                List<String> attachmentMalwareLockedDb = mySql.getMysqlQuery(mySqlQuery);
	    	
                if (!attachmentMalwareLockedDb.get(0).equals(attachmentMalwareLocked)){
				
                    appLogs.debug("FAILED - Attachment malware_locked returned does not match the database.");
                    softAssert.fail();

                }else{
			
                    appLogs.debug("Passed - Attachment malware_locked returned matches the database.");
		
                }
	    	
                //verify attachment created_at against db
                mySqlQuery = "SELECT created_at FROM attachments WHERE id = \'"+attachmentId+"\';";
                List<String> attachmentCreatedAtDb = mySql.getMysqlQuery(mySqlQuery);
                String attachmentCreatedAtDbTs = attachmentCreatedAtDb.get(0).substring(0, attachmentCreatedAtDb.get(0).indexOf('.'));
	    	
                if (!attachmentCreatedAtDbTs.equals(attachmentCreatedAt)){
				
                    appLogs.debug("FAILED - Attachment created_at returned does not match the database.");
                    softAssert.fail();

                }else{
			
                    appLogs.debug("Passed - Attachment created_at returned matches the database.");
		
                }
	    	
                //verify attachment updated_at against db
                mySqlQuery = "SELECT updated_at FROM attachments WHERE id = \'"+attachmentId+"\';";
                List<String> attachmentUpdatedAtDb = mySql.getMysqlQuery(mySqlQuery);
                String attachmentUpdatedAtDbTs = attachmentUpdatedAtDb.get(0).substring(0, attachmentUpdatedAtDb.get(0).indexOf('.'));
	    	
                if (!attachmentUpdatedAtDbTs.equals(attachmentUpdatedAt)){
				
                    appLogs.debug("FAILED - Attachment updated_at returned does not match the database.");
                    softAssert.fail();

                }else{
			
                    appLogs.debug("Passed - Attachment updated_at returned matches the database.");
		
                }                     
                
            }
            
        }
        
        //if any soft asserts failed, fail the test
        softAssert.assertAll();
	
	}
	
}	

