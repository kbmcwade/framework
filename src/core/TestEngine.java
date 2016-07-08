package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import util.XlsRead;
import util.RestApi;
import util.DbManager;
import util.SendEmail;

public class TestEngine {

	/* 
	 * This class initializes properties files, test data,
	 * and WebDriver; creates the MySQL db connection; sets
	 * a global implicit wait; generates logs; and e-mails
	 * test results
	 */
	
	public static Properties config = new Properties();
	public static Properties object = new Properties();
	public static Properties run = new Properties();
	public static String clientId = null;
	public static XlsRead excel = null;
	public static String testUrl = null;
	public static String server = null;
	public static WebDriver driver = null;
	public static WebDriver api = null;
	public static String apiUrl = null;
	public static String token = null;
	public static DbManager mySql = null;
	public static Logger appLogs = Logger.getLogger("devpinoyLogger");

	
	@BeforeSuite
	public void init() throws IOException, AddressException, ClassNotFoundException, SQLException, MessagingException, NoSuchAlgorithmException, KeyManagementException{

		System.setProperty("jsse.enableSNIExtension", "false");
		
		if(driver == null){
			
			// load config.properties file
			appLogs.debug("Loading the testbed.properties file.");
			FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"/src/config/testbed.properties");
			config.load(fis);
			
			// load test run config and test data spreadsheet
			switch (config.getProperty("suite")) {
			
				case "api":
					appLogs.debug("Loading the api-data.xls file.");
					excel = new XlsRead(System.getProperty("user.dir")+"/src/index/api-data.xls");
			        appLogs.debug("Loading the run-api.properties file.");
			        fis = new FileInputStream(System.getProperty("user.dir")+"/src/config/run-api.properties");
			        run.load(fis);
			        break;
			        
                case "fe":

                    // load object.properties file
                    appLogs.debug("Loading the object.properties file.");
                    fis = new FileInputStream(System.getProperty("user.dir")+"/src/index/object.properties");
                    object.load(fis);
                    
                    appLogs.debug("Loading the fe-data.xls file.");
                    excel = new XlsRead(System.getProperty("user.dir")+"/src/index/fe-data.xls");
                    appLogs.debug("Loading the run-fe.properties file.");
                    fis = new FileInputStream(System.getProperty("user.dir")+"/src/config/run-fe.properties");
                    run.load(fis);
                    break;
                    
                case "feeds":
                    appLogs.debug("Loading the feeds-data.xls file.");
                    excel = new XlsRead(System.getProperty("user.dir")+"/src/index/feeds-data.xls");
                    appLogs.debug("Loading the run-feeds.properties file.");
                    fis = new FileInputStream(System.getProperty("user.dir")+"/src/config/run-feeds.properties");
                    run.load(fis);
                    break;
                    
                case "v1":
                    appLogs.debug("Loading the v1-data.xls file.");
                    excel = new XlsRead(System.getProperty("user.dir")+"/src/index/v1-data.xls");
                    appLogs.debug("Loading the run-v1.properties file.");
                    fis = new FileInputStream(System.getProperty("user.dir")+"/src/config/run-v1.properties");
                    run.load(fis);
                    break;
                    
                case "create-data":
                    appLogs.debug("Loading the create-data.xls file.");
                    excel = new XlsRead(System.getProperty("user.dir")+"/src/index/create-data.xls");
                    appLogs.debug("Loading the run-create.properties file.");
                    fis = new FileInputStream(System.getProperty("user.dir")+"/src/config/run-create.properties");
                    run.load(fis);
                    break;
					
			}		
			
			// assign test bed server
			if (!config.getProperty("remote").isEmpty()){
			    
			    server = config.getProperty("remote").replaceAll(".novalocal", "");
			    
			}else{
			    
			server = config.getProperty("server");
			
			}
			
			testUrl = "https://"+server;
            appLogs.debug("Setting test bed to: "+testUrl);
			
			//initialize database connection
			appLogs.debug("Initializing the database connection.");
			mySql = new DbManager();
			mySql.setMysqlDbConnection();
			
			//get/set client id
	        clientId = mySql.getMysqlQuery("SELECT client_id FROM gate_oauth2_clients WHERE name='ThreatQ Front End';").get(0);
			
			//get authoization token for api if applicable
            if(config.getProperty("suite").equals("api")){
                
                String userEmail = config.getProperty("email");
                String userPassword = config.getProperty("password");
                apiUrl = testUrl+"/api/";
                appLogs.debug("API url set to: "+apiUrl+".");
                token = RestApi.getToken(userEmail, userPassword, clientId, apiUrl);
                
                if (token == null){
                
                    appLogs.debug("Response to request for authorization token was not as expected. Token set to null.");
            
                }else{
            
                    appLogs.debug("Received API authorization token: "+token+" for user "+userEmail+".");
            
                }
            
            }

            // initialize WebDriver
			if(config.getProperty("browser").equals("firefox")){
					
				appLogs.debug("Initializing FirefoxDriver.");
				FirefoxProfile firefoxProfile = new FirefoxProfile();
				firefoxProfile.setPreference("webdriver.load.strategy", "unstable");
				firefoxProfile.setAcceptUntrustedCertificates (true);
				firefoxProfile.setAssumeUntrustedCertificateIssuer(false);
				firefoxProfile.setPreference("reader.parse-on-load.enabled",false);
				System.setProperty("jsse.enableSNIExtension", "false");
				driver = new FirefoxDriver(firefoxProfile);

			}else if(config.getProperty("browser").equals("chrome")){
				
				appLogs.debug("Initializing ChromeDriver.");
				ChromeOptions options = new ChromeOptions();
				Map<String, Object> prefs = new HashMap<String, Object>();
				prefs.put("profile.default_content_settings.popups", 0);
				options.setExperimentalOption("prefs", prefs);
				System.setProperty("webdriver.chrome.driver", "chromedriver");
				DesiredCapabilities caps = new DesiredCapabilities();
				caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
				driver = new ChromeDriver();
				
			}else if(config.getProperty("browser").equals("safari")){
				
				appLogs.debug("Initializing SafariDriver.");
				System.setProperty("webdriver.safari.noinstall", "true");
				driver = new SafariDriver();

			}else if(config.getProperty("browser").equals("ie")){
				
				appLogs.debug("Initializing InternetExplorerDriver.");
				System.setProperty("webdriver.ie.driver", "IEDriverServer.exe");
				DesiredCapabilities caps = new DesiredCapabilities();
				caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
				driver = new InternetExplorerDriver();
				
			}else if(config.getProperty("browser").equals("phantomjs")){
				
				appLogs.debug("Initializing PhantomJSDriver.");
				Capabilities caps = new DesiredCapabilities();
                ((DesiredCapabilities) caps).setJavascriptEnabled(true);                
                ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);  
                ((DesiredCapabilities) caps).setCapability(
                        PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                        "phantomjs"
                    );
                driver = new  PhantomJSDriver(caps);
			}

			// initialize browser if applicable
			if(!config.getProperty("browser").equals("none")){
				
				// global implicit wait and timeouts
				driver.manage().timeouts().implicitlyWait(15L, TimeUnit.SECONDS);
				driver.manage().timeouts().pageLoadTimeout(900L, TimeUnit.SECONDS);
				driver.manage().timeouts().setScriptTimeout(900L, TimeUnit.SECONDS);
			
				// browse to ThreatQ test bed url
				driver.manage().window().maximize();
				appLogs.debug("Browsing to test environment: "+testUrl+".");
				driver.get(testUrl);
			
			}
			
		}
		
	}
	
	@AfterSuite
	public static void QuitDriver() throws AddressException, MessagingException, SQLException{
		
		
		// close database connection
		appLogs.debug("Closing database connection.");
		mySql.tearDown();
		
		// quit browser if applicable
		if(!config.getProperty("browser").equals("none")){
		
			appLogs.debug("Shutting down Webdriver.");
			driver.quit();
		
		}
		
		SendEmail mail = new SendEmail();
		//mail.sendMail(UtilConfig.mailServer, UtilConfig.from, UtilConfig.to, UtilConfig.subject, UtilConfig.messageBody, UtilConfig.attachmentPath, UtilConfig.attachmentName);
		
	}
	
}
