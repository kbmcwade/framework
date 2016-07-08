package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import core.MethLib;

public class FeedsCrowdStrikeGetIndicators extends MethLib{

    
    String testName = "FeedsCrowdStrikeGetIndicators";
    
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
    public void testFeedsCrowdStrikeGetIndicators () throws IOException, ParseException, InterruptedException, SQLException{
        
        //local variables
        String url = new String();
        String csId = new String();
        String csKey = new String();
        String csStartDate = new String();
        String csEndDate = new String();
        String csResponse = new String();
        String fileName = new String();
        long gte;
        long lte;
        int iterate = 1;

        //get import start and end date from testbed.properties
        csStartDate = config.getProperty("csStartDate");
        csEndDate = config.getProperty("csEndDate");
            
        //convert import dates to epoch time
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date start = format.parse(csStartDate);
        gte = start.getTime() / 1000;
        Date end = format.parse(csEndDate);
        lte = end.getTime() / 1000;
            
        while(!csResponse.contentEquals("[]")){
            
            //url = "https://intelapi.crowdstrike.com/indicator/v1/search/published_date?gte="+gte+"&lte="+lte+"&order=asc&perPage=50000&page="+iterate;
            url = "https://intelapi.crowdstrike.com/indicator/v1/search/last_updated?perPage=1000&gte=1467924616&lte=1467928218&page=1";
            
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //get crowdstrike configuration
            csId = config.getProperty("csId");
            csKey = config.getProperty("csKey");
            
            //add x-csix headers
            con.setRequestProperty("X-CSIX-CUSTID", csId);
            con.setRequestProperty("X-CSIX-CUSTKEY", csKey);
        
            //verify valid response code
            int statusCode = con.getResponseCode();
        
            if (!(statusCode == 200)){
            
                appLogs.debug("FAILED - Status code for CrowdStrike api request was: "+statusCode+".");
                Assert.fail();
    
            }

            //write response to buffer
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer sb = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
        
                sb.append(inputLine);
       
            }
        
            in.close();
            csResponse = sb.toString();
        
            //create json txt file
            if(!csResponse.contentEquals("[]")){

                fileName = "crowd"+iterate;
                MethLib.stringtoFile(csResponse, fileName);
        
            }
            
            iterate++;
            
        }
        
    }

}
    