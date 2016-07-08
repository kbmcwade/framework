package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class RestApi{

	public static String getToken (String userEmail, String userPassword, String clientId, String apiUrl) throws IOException{
		
		System.out.println("Requesting authorization token for user: \""+userEmail+"\".");
		
		//build http request
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost requestMethod = new HttpPost(apiUrl+"token");
 
        //add request parameters
        List<BasicNameValuePair> requestParameters = new ArrayList<BasicNameValuePair>();
        requestParameters.add(new BasicNameValuePair("grant_type", "password"));
        requestParameters.add(new BasicNameValuePair("client_id", clientId));
        requestParameters.add(new BasicNameValuePair("email", userEmail));
        requestParameters.add(new BasicNameValuePair("password", userPassword));
        HttpEntity requestParams = new UrlEncodedFormEntity(requestParameters);
        requestMethod.setEntity(requestParams);
        
        //make request and calculate response time
        long start = System.currentTimeMillis();
        CloseableHttpResponse httpResponse = httpClient.execute(requestMethod);
        long responseTime = System.currentTimeMillis() - start;
        
        //write input stream response to buffer
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(httpResponse.getEntity().getContent()));

        StringBuffer response = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            
            response.append(line);
        
        }
        
        //write buffer to string
        String responseBody = response.toString();
		
        //get status code from response
        int statusCode = httpResponse.getStatusLine().getStatusCode();
		        
        if (responseTime < 1000){
            
            System.out.println("Response time: "+responseTime+" milliseconds.");
        
        }else if (responseTime > 999 && responseTime < 60000){
        
            float responseTimeSecs = (float)responseTime/1000;
            System.out.println("Response time: "+responseTimeSecs+" seconds.");
            
        }else{
        
            String responseTimeMins = String.format("%02d minutes and %02d seconds", 
                TimeUnit.MILLISECONDS.toMinutes(responseTime) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(responseTime) % TimeUnit.MINUTES.toSeconds(1));
            System.out.println("Response time: "+responseTimeMins+".");
            
        }
        
        if (statusCode != 200){
        
            System.out.println("*Warning* - Received a status code of "+statusCode+".");       
        
        }else{
        
            System.out.println("Received a status code of "+statusCode+".");
        
        }

        // split the response at double quotes to get the token
		String[] parsedResponse = responseBody.split("\"");
		String token=null;
		if(parsedResponse.length<3){
			
			System.out.println("Response to request for authorization token was not as expected. Token set to null.");
			
		}else{

			token=parsedResponse[3];
			System.out.println("Request for authorization token returned: \""+token+"\".");
		
		}
		
		//close http client and return token value
		httpClient.close();
		return token;
		
	}

}
