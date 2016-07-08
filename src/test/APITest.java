package tests;

        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.util.Date;
        import java.text.SimpleDateFormat;

        import javax.crypto.Mac;
        import javax.crypto.spec.SecretKeySpec;

        import java.net.URI;
        import java.net.HttpURLConnection;
        import java.util.Formatter;

        /**
        *
        * Test Class for API 2.0 integration.
        *
        */
        public class APITest

            {
                /** File that contains User Keys - User keys must be securely stored */
                private final String KEY_FILE = "keys.txt";

                /** Header that contains the API Key */
                private final String X_AUTH = "X-Auth";

                /** Header that contains the calculated authentication HMAC-SHA256 Hash */
                private final String X_AUTH_HASH = "X-Auth-Hash";

                /** HTTP Accept header */
                private final String ACCEPT = "Accept";

                /** Accept-Version header */
                private final String ACCEPT_VERSION = "Accept-Version";

                /** Date header */
                private final String DATE = "Date";

                /** Content-Type header for POST requests */
                private final String CONTENT_TYPE = "Content-Type";

                private final String LINE_FEED = "\r\n";

                /** Scheme should be https */
                private final String SCHEME = "https";

                private final String API_HOST = "api.isightpartners.com";

                private String apiKey;
                private String secretKey;

                public APITest() throws Exception
                {
                    
                    // Read in the API Key and Secret
                    apiKey = "4b997a1790aafdf9f30e7c60c3224657747b82052099590e6439b5b3e864231f";
                    secretKey = "88a29bbce3a0f41b58541c4771b706d68210b49682edb63e2b3ba855e1ffd357";

                }

                /**
                * Constructs and sends a GET request for the given path and retrieves the response in a string variable.
                * Returns the response.
                *
                * @param endpoint The endpoint to query.  This will be the URL Path.
                * @param query The query parameters.  This will be the URL query string.
                * @param accept Value of the accept header.
                *
                * @return The response from the API in String form.
                */
                private String sendGet(String endpoint, String query, String accept) throws Exception
                 {
                    String acceptVersion = "2.2";
                    Date now = new Date();

                    // The format in which to put current date in the Date header (RFC 822 compatible)
                    SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");

                    String dateString = format.format(now);

                    String uriPath = endpoint + "?" + query;
                    if (query == null){
                        uriPath = endpoint;
                    }

                    URI uri = new URI(SCHEME + "://" + API_HOST + uriPath);

                    System.out.println("URIPath - " + uriPath);

                    // Concatenate Request and headers and calculate HMAC-SHA256
                    String authString = uriPath+ acceptVersion + accept + dateString;
                    System.out.println("authString - "+authString);
                    Mac hmacSha256 = Mac.getInstance("HmacSHA256");
                    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
                    hmacSha256.init(secretKeySpec);
                    hmacSha256.update(authString.getBytes("UTF-8"));
                    byte[] hashBytes = hmacSha256.doFinal();

                    Formatter formatter = new Formatter();
                    for (byte b : hashBytes) {
                        formatter.format("%02x", b);
                        }
                    String hash = formatter.toString();
                    formatter.close();

                    try
                    {
                        HttpURLConnection conn = (HttpURLConnection)(uri.toURL()).openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty(X_AUTH, apiKey);
                        conn.setRequestProperty(X_AUTH_HASH, hash);
                        conn.setRequestProperty(ACCEPT, accept);
                        conn.setRequestProperty(ACCEPT_VERSION, acceptVersion);
                        conn.setRequestProperty(DATE, dateString);
                        conn.setDoInput(true);

                        conn.connect();

                        // Check for response code
                        int responseCode = conn.getResponseCode();

                        if(responseCode == HttpURLConnection.HTTP_OK)
                        {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                            String response = new String();
                            String line = null;

                            while((line = reader.readLine()) != null)
                            {
                            response += line;
                            }

                            return response;
                        }
                        else {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

                            String response = new String();
                            String line = null;

                            while((line = reader.readLine()) != null) {
                            response += line;
                            }

                        return response;
                        }
                    }
                    catch(Exception e) {
                            // Log errors and other handling
                            return null;
                    }
                }

                /**
                * Method to test the heartbeat (test) endpoint.
                * @throws Exception
                */
                public void testHeartbeat() throws Exception
                {
                    String endpoint = "/test";
                    String accept = "application/json";

                    String response = sendGet(endpoint, null, accept);

                    processResponse(response);
                }

                /**
                * Method to test the report index endpoint.
                * @throws Exception
                */
                public void testReportIndex() throws Exception
                {
                    String endpoint = "/report/index";
                    String accept = "application/json";

                    long end = System.currentTimeMillis() / 1000;  // We need seconds
                    long begin = end - 2592000;  // 30 days ago

                    String query = "startDate=" + begin + "&endDate=" + end + "";
                    
                    String response = sendGet(endpoint, query, accept);
                    processResponse(response);
                }
                
                public void testReport() throws Exception
                {
                    String reportId = "16-00008650";
                    String endpoint = "/report/"+reportId;
                    String accept = "application/json";

                    String query = "format=json";
                    
                    String response = sendGet(endpoint, query, accept);
                    processResponse(response);
                }
                
                /**
                * Method to test IOCs endpoing
                */
                public void testIocs() throws Exception
                {
                    String request = "/view/indicators";
                    String accept = "application/json";

                    long end = System.currentTimeMillis() / 1000;  // We need seconds
                    long begin = end - 2592000;  // 30 days ago

                    String query = "format=json&startDate=" + begin + "&endDate=" + end + "";

                    String response = sendGet(request, query, accept);
                    processResponse(response);
                }

                /**
                * Processing the endpoint response.  Just prints it to the console.
                * @param response
                */
                private void processResponse(String response)
                {
                    System.out.println(response);
                }

                public static void main(String[] args) throws Exception
                {
                    APITest apiTest = new APITest();
                    //apiTest.testHeartbeat();
                    //apiTest.testReportIndex();
                    //apiTest.testReport();
                    apiTest.testIocs();
                }

            }


