package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.FormEncodingType;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;

public class MethLib extends TestEngine{

	// This class contains the methods library used to drive the tests package
	
	// *** WORKFLOW METHODS ***

	public static void logIn(String userEmail, String userPassword) throws InterruptedException{

		TimeUnit.SECONDS.sleep(2);
		
		findElementX("usernameField").sendKeys(userEmail);
		findElementX("passwordField").sendKeys(userPassword);
		findElementX("loginButton").click();
			
		TimeUnit.SECONDS.sleep(2);
		
	}
	
	public static void logOut() throws InterruptedException{
			
		findElementX("optionsMenu").click();
		findElementX("logoutButton").click();
	
	}
	
	public static void createNewUser(String userName, String userGroup, String userEmail, String userPassword) throws InterruptedException{
		
		// select Event from the Create drop-down menu
		findElementX("optionsMenu").click();
		findElementX("userManagementButton").click();
		
		
		TimeUnit.SECONDS.sleep(1);
				
		findElementX("usersAddButton").click();
		
		
		TimeUnit.SECONDS.sleep(1);

		//fill in Event details and click Add button
		TimeUnit.SECONDS.sleep(2);
		findElementX("userNameField").sendKeys(userName);
		findElementX("userGroupDropDown").click();
		findElementX("userGroupDropDown").sendKeys(userGroup);
		findElementX("userEmailField").sendKeys(userEmail);
		findElementX("userPasswordField").sendKeys(userPassword);
		findElementX("userRetypePasswordField").sendKeys(userPassword);
		findElementX("addUserButton").click();
		appLogs.debug("New user: \""+userName+"\" created.");
		
		
		TimeUnit.SECONDS.sleep(2);
		
	}
	
	public static void deleteUser(String userEmail) throws InterruptedException{
		
		// search the table for the user email
		
		//if(findElementX("//tbody/tr[td[text()='"+userEmail+"']]") != null){
			
			TimeUnit.SECONDS.sleep(1);
			
			findElementX("//tbody/tr[td[text()[contains(.,'Super')]]]").click();
			findElementX("DeleteSelectedUsersButton").click();
			findElementX("areYouSureDeleteUsersButton").click();
			appLogs.debug("User: \""+userEmail+"\" deleted.");
		
		//}else{
			
			//appLogs.debug("User: \""+userEmail+"\" was not found.");
			
		//}
		
	}
	
	public static void createNewIndicatorSimple(String indicatorName, String indicatorSource){
		
		// select Indicator from the Create drop-down menu
		findElementX("createNewDropDown").click();
		findElementX("createNewIndicator").click();
	
	}

	public static void createNewIndicatorPaste(String indicatorValue, String fileType, String indicatorSource, String indicatorStatus, String attributeKey, String attributeValue) throws InterruptedException{
		
		// select Indicator from the Create drop-down menu
		findElementX("createNewDropDown").click();
		findElementX("createNewIndicator").click();
		
		//paste indicator value and select parser
		findElementX("pasteContentHereTextBox").sendKeys(indicatorValue);
		
		TimeUnit.SECONDS.sleep(1);
		
		findElementX("parserGenericText").click();
		
		TimeUnit.SECONDS.sleep(1);
		
		findElementX("parserNextStepButton").click();
		
		TimeUnit.SECONDS.sleep(1);
		
		// set source, status, and attribute
		findElementX("parserIndicatorSourceField").sendKeys(indicatorSource);
		findElementX("parserGlobalStatusDropDown").sendKeys(indicatorStatus);
		findElementX("parserAttributeKeyField").sendKeys(attributeKey);
		findElementX("parserAttributeValueField").sendKeys(attributeValue);

		TimeUnit.SECONDS.sleep(1);
		
		findElementX("parserNextStepButton").click();

		TimeUnit.SECONDS.sleep(1);
		
		findElementX("parserFinishImportButton").click();
	
	}
	
	/*public static void createNewIndicatorFileUpload(String indicatorName, String fileType, String indicatorSource){
	*	
	*	// select Indicator from the Create drop-down menu
	*	findElement("createNewDropDown").click();
	*	findElement("createNewIndicator").click();
	*
	*}
	*/
	
	public static void deleteIndicator(String indicatorName) throws InterruptedException{
		
		// delete a specified indicator
		
		//use simple search to find the indicator
		doSimpleSearch(indicatorName);
		
		//delete the indicator
		findElementX("deleteThisIndicatorButton").click();
		findElementX("areYouSureOKButton").click();
		
		//verify and clear the alert bar
		verifyAlertBar("Indicator deleted");
		
	}
	
	public static void changeIndicatorType(String indicatorName, String indicatorType) throws InterruptedException{
		
		// change the type of a specified indicator
		
		//use simple search to find the indicator
		doSimpleSearch(indicatorName);
		
		//update the type of the indicator
		findElementX("indicatorTypeButton").click();
		findElementLinkText(indicatorType).click();
		
		//verify and clear the alert bar
		verifyAlertBar("Indicator updated");
		
	}
	
	public static void changeIndicatorStatus(String indicatorName, String indicatorStatus) throws InterruptedException{
		
		// change the status of a specified indicator
		
		//use simple search to find the indicator
		doSimpleSearch(indicatorName);
		
		//update the status of the indicator
		findElementX("indicatorStatusButton").click();
		findElementLinkText(indicatorStatus).click();
		
		//acknowledge are you sure for whitelisted status
		if(indicatorStatus == "Whitelisted"){
			
			findElementX("areYouSureWhitelistIndicatorStatusButton").click();
			
		}
		
		//verify and clear the alert bar
		verifyAlertBar("Indicator updated");
		
	}
	
	public static void addInidicatorAttribute(String indicatorName, String attributeKey, String attributeValue) throws InterruptedException{
		
		//use simple search to find the indicator
		doSimpleSearch(indicatorName);
		
		// click Add Attribute on the details page
		findElementX("addAttribute").click();
		
		
		TimeUnit.SECONDS.sleep(1);
		
		//fill in Attribute details and click Add button
		findElementX("addAttributesKey").sendKeys(attributeKey);
		findElementX("addAttributesValue").sendKeys(attributeValue);
		findElementX("addAttrbutesButton").click();
		
		
		TimeUnit.SECONDS.sleep(1);
		
	}
	
	public static void removeInidicatorAttribute(String indicatorName, String attributeKey, String attributeValue) throws InterruptedException{
		

		
	}
	
	public static void linkIndicatorRelatedIndicator(String indicatorName, String relatedIndicatorName){
		

		
	}
	
	public static void linkIndicatorRelatedAdversary(String indicatorName, String relatedAdversaryName){
		

		
	}
	
	public static void linkIndicatorRelatedEvent(String indicatorName, String relatedEventName){
		

		
	}
	
	public static void linkIndicatorRelatedFile(String indicatorName, String relatedFileName){
		

		
	}
	
	public static void unlinkIndicatorRelatedIndicator(String indicatorName, String relatedIndicatorName){
		

		
	}
	
	public static void unlinkIndicatorRelatedAdversary(String indicatorName, String relatedAdversaryName){
		

		
	}
	
	public static void unlinkIndicatorRelatedEvent(String indicatorName, String relatedEventName){
		

		
	}
	
	public static void unlinkIndicatorRelatedFile(String indicatorName, String relatedFileName){
		

		
	}
	
	public static void createNewEvent(String eventTitle, String eventType, String eventDate, String eventTime) throws InterruptedException{
		
		// select Event from the Create drop-down menu
		findElementX("createNewDropDown").click(); 
		findElementX("createNewEvent").click();
		
		
		TimeUnit.SECONDS.sleep(1);
				
		//fill in Event details and click Add button
		findElementX("eventTitleField").sendKeys(eventTitle);
		findElementX("eventTypeDropDown").click();
		findElementX("eventTypeDropDown").sendKeys(eventType);
		findElementX("eventTypeDropDown").click();
		findElementX("eventDateField").sendKeys(eventDate);
		findElementX("eventTimeField").sendKeys(eventTime);
		findElementX("addEventButton").click();
		
		//verify alert bar
		verifyAlertBar("Event successfully added.");
		
		TimeUnit.SECONDS.sleep(1);
		
	}
	
	public static void deleteEvent() throws InterruptedException{
		
		//delete the event
		findElementX("deleteThisEventButton").click();
		findElementX("areYouSureOKButton").click();
		
		TimeUnit.SECONDS.sleep(1);
		
		//verify alert bar
		verifyAlertBar("Event deleted");
		
		TimeUnit.SECONDS.sleep(1);
		
	}
	
	public static void changeEventType(String eventTitle, String eventType) throws InterruptedException{
		
		// change the type of a specified event
		
		//use simple search to find the event
		doSimpleSearch(eventTitle);
		
		//update the type of the event
		findElementX("eventTypeButton").click();
		findElementLinkText(eventType).click();
		
		//verify and clear the alert bar
		verifyAlertBar("Event updated");
		
	}
	
	public static void createNewAdversary(String adversaryName, String adversaryDescription) throws InterruptedException{
		
		// select Adversary from the Create drop-down menu
		findElementX("createNewDropDown").click();
		findElementX("createNewAdversary").click();
		
		
		TimeUnit.SECONDS.sleep(1);
		
		//fill in Adversary details and click Add button
		findElementX("adversaryNameField").sendKeys(adversaryName);
		findElementX("adversaryDescriptionField").click();
		findElementX("adversaryDescriptionField").sendKeys(adversaryDescription);
		findElementX("addAdversaryButton").click();
		appLogs.debug("New adversary: \""+adversaryName+"\" created.");
		
		
		TimeUnit.SECONDS.sleep(2);
		
	}
	
	public static void deleteAdversary(String adversaryName) throws InterruptedException{
		
		// delete a specified adversary
		
		//use simple search to find the adversary
		doSimpleSearch(adversaryName);
		
		//delete the adversary
		findElementX("deleteThisAdversaryButton").click();
		findElementX("areYouSureOKButton").click();
		
		//verify and clear the alert bar
		verifyAlertBar("Adversary deleted");
		
	}
	
	/*public static void createNewFile(String fileName, String fileDescription){
	*	
	*	select File from the Create drop-down menu
	*	findElementX("createNewDropDown").click();
	*	findElementX("createNewFile").click();
	*	
	*}
	*/
	
	public static void deleteFile(String fileName) throws InterruptedException{
		
		// delete a specified file
		
		//use simple search to find the file
		doSimpleSearch(fileName);
		
		//delete the file
		findElementX("deleteThisFileButton").click();
		findElementX("areYouSureOKButton").click();
		
		//verify and clear the alert bar
		verifyAlertBar("File deleted");
		
	}
	
	public static void doSimpleSearch(String searchValue) throws InterruptedException{
		
		findElementX("searchButton").click();		
		
		TimeUnit.SECONDS.sleep(1);
			
		//need to split the searchValue and add sleep before last character to deal with simple search latency
		String searchChars[] = splitSearchValue(searchValue);
		findElementX("searchField").sendKeys(searchChars[0]);				
			
		
		TimeUnit.SECONDS.sleep(1);
			
		findElementX("searchField").sendKeys(searchChars[1]);		
			
		
		TimeUnit.SECONDS.sleep(2);
	
	}

	public static boolean doSimpleSearch(String searchValue, String selectValue) throws InterruptedException{
		
		findElementX("searchButton").click();		
		
		
		TimeUnit.SECONDS.sleep(1);
			
		//need to split the searchValue and add sleep before last character to deal with simple search latency
		String searchChars[] = splitSearchValue(searchValue);
		findElementX("searchField").sendKeys(searchChars[0]);				
			
		
		TimeUnit.SECONDS.sleep(1);
			
		findElementX("searchField").sendKeys(searchChars[1]);		
			
		TimeUnit.SECONDS.sleep(2);
	
		//find and select the supplied selectValue
		WebElement searchResultsList = findElementX("searchResultsList");
		List<WebElement> searchResults = searchResultsList.findElements(By.tagName("a"));
		for (int i=0; i<searchResults.size(); i++){
			
			if (searchResults.get(i).getText().contains(selectValue)){
				
				appLogs.debug("Expected search result: \""+selectValue+"\" was found.");
				searchResults.get(i).click();
				TimeUnit.SECONDS.sleep(2);
				return true;
				
			}			
			
		}
			
		appLogs.debug("Expected search result: \""+selectValue+"\" was not found.");
		return false;
		
	}
	
	public static void assertEventDetails(String eventTitle, String expectedEventType, String expectedEventDate, String expectedEventTime) throws InterruptedException, ParseException{
		
		//local variables
		String actualTitle = new String();
		String actualType = new String();
		String actualDate = new String();
		String[] parsedActualDate = new String[4];
		
		//declare new soft assertion so test will complete before setting to fail
		SoftAssert softAssert = new SoftAssert();
		
		//assert event title
		actualTitle=findElementX("eventDetailsTitle").getText();
		if (!compareElements(eventTitle+" edit", actualTitle)){
				
			appLogs.debug("FAIL: Event title \""+eventTitle+"\" was not found on details page.");
			softAssert.fail();		
				
		}
			
		//assert event type		
		actualType=findElementX("eventDetailsType").getText();
		if (!compareElements(expectedEventType, actualType)){
						
			appLogs.debug("FAIL: Event type \""+expectedEventType+"\" was not found on details page.");
			softAssert.fail();		
						
		}
					
		//get event date and time		
		actualDate=findElementX("eventDetailsDateTime").getText();

		// parse event date field for date and time values
		parsedActualDate = actualDate.split("\\s+");
		
        //convert formatting for event date
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = formatter.parse(expectedEventDate);
        formatter = new SimpleDateFormat("MM/dd/yy");
        expectedEventDate = formatter.format(date).toString();
                
		//convert formatting for event time
		expectedEventTime = expectedEventTime.replaceAll("\\s+","").toLowerCase();
		
		//assert event date
		if (!compareElements(expectedEventDate, parsedActualDate[2])){
						
			appLogs.debug("FAIL: Event date \""+expectedEventDate+"\" was not found on details page.");
			softAssert.fail();		
						
		}
			
		//assert event time
		if (!compareElements(expectedEventTime, parsedActualDate[3])){
			
			TimeUnit.SECONDS.sleep(1);
			appLogs.debug("FAIL: Event time \""+expectedEventTime+"\" was not found on details page.");
			softAssert.fail();		
						
		}
            
    }
	   
    public static void assertAdversaryDetails(String adversaryName) throws InterruptedException{
		
		//local variables
		String actualName = new String();

		//declare new soft assertion so test will complete before setting to fail
		SoftAssert softAssert = new SoftAssert();
		
		TimeUnit.SECONDS.sleep(2);
		
		actualName=findElementX("adversaryDetailsName").getText();
		if (!MethLib.compareElements(adversaryName+" edit", actualName)){
			
			appLogs.debug("FAIL: Adversary name \""+adversaryName+"\" was not found on details page.");
			softAssert.fail();		
			
		}
	
	}	

     // *** COMPARISON METHODS ***
	
	public static boolean verifyAlertBar(String expectedAlert) throws InterruptedException{
	
		TimeUnit.SECONDS.sleep(1);
		
		WebDriverWait wait = new WebDriverWait(driver, 3L);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(object.getProperty("alertBarMessage"))));
		
		if (driver.findElements(By.xpath(object.getProperty("alertBarMessage"))).size() !=0){
			
			String actualAlert = findElementX("alertBarMessage").getText(); 
			
			if (actualAlert.equals(expectedAlert)) {
				
				appLogs.debug("Alert Bar displayed \""+actualAlert+"\" as expected.");
				findElementX("closeAlertBarMessage").click();
				return true;	
			
			}else{			

				appLogs.debug("Alert Bar displayed \""+actualAlert+"\" which was not as expected.");
				findElementX("closeAlertBarMessage").click();
				return false;

			}			

		}else{
			
			appLogs.debug("WARNING: The alert bar was not found.");
			return false;
			
		}
		
	}
	
	public static boolean compareCurrentUrl(String expected){
		
		if (driver.getCurrentUrl().equals(expected)) {
			
			appLogs.debug("Compare passed: Current URL is \""+driver.getCurrentUrl()+"\" as expected.");
			return true;
		
		}else{
			
			appLogs.debug("Compare failed: Current URL is \""+driver.getCurrentUrl()+"\" which was not as expected.");
			return false;
			
		}
	}

	public static boolean compareElements(String expected, String actual){
		
		if (actual.equals(expected)) {
			
			appLogs.debug("Compare passed: Actual was \""+actual+"\" as expected.");
			return true;
			
		}else{
			
			appLogs.debug("Compare failed: Actual was \""+actual+"\" but \""+expected+"\" was expected.");	
			return false;
			
		}
	}
		
	
	// *** UTILITY METHODS ***

	public static boolean isElementPresent(String objectTag){
		
		//try/catch to check for element on page
		try{
			
			driver.findElement(By.xpath(object.getProperty(objectTag)));
			return true;
			
		}catch(Throwable t){
			
			return false;
			
		}
		
	}
	
	public static void waitForElement(String objectTag){
		
		//wait for the presence of the specified element on the page
		WebDriverWait wait = new WebDriverWait(driver, 10L);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(object.getProperty(objectTag))));

	}
	
	public static void waitForPageLoad(){

		//wait for the page to load by confirming presence of html tag
		//WebDriverWait wait = new WebDriverWait(driver, 10L);
		//wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("html")));
		
		//fluent wait code if needed
		 Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
			.withTimeout(10, TimeUnit.SECONDS)
			.pollingEvery(1, TimeUnit.MILLISECONDS)
			.ignoring(NoSuchElementException.class)
			.withMessage("Page load did not complete after 10 seconds.");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("html")));

	}
	
	public static WebElement findElementX(String objectTag){
		
		if (!isElementPresent(objectTag)){
			waitForElement(objectTag);
		}
		
		List<WebElement> elements = driver.findElements(By.xpath(object.getProperty(objectTag)));
		if (elements != null && !elements.isEmpty()){
			
			return elements.get(0);
			
		}else{
			
			appLogs.debug("Error: Object \""+objectTag+"\" was not found on the page.");
			captureScreenshot("missing_"+objectTag);
			return null;
			
		}
		
	}
	
	public static WebElement findElementID(String objectTag){
		
		if (!isElementPresent(objectTag)){
			waitForElement(objectTag);
		}
		
		List<WebElement> elements = driver.findElements(By.id(object.getProperty(objectTag)));
		if (elements != null && !elements.isEmpty()){
			
			return elements.get(0);
			
		}else{
			
			appLogs.debug("Error: Object \""+objectTag+"\" was not found on the page.");
			captureScreenshot("missing_"+objectTag);
			return null;
			
		}
		
	}
	
	public static WebElement findElementCss(String objectTag){
		
		List<WebElement> elements = driver.findElements(By.cssSelector(object.getProperty(objectTag)));
		if (elements != null && !elements.isEmpty()){
			
			return elements.get(0);
			
		}else{
			
			appLogs.debug("Error: Object \""+objectTag+"\" was not found on the page.");
			captureScreenshot("missing_"+objectTag);
			return null;
			
		}
		
	}
	
	public static WebElement findElementLinkText(String objectTag){
		
		List<WebElement> elements = driver.findElements(By.linkText(object.getProperty(objectTag)));
		if (elements != null && !elements.isEmpty()){
			
			return elements.get(0);
			
		}else{
			
			appLogs.debug("Error: Object \""+objectTag+"\" was not found on the page.");
			captureScreenshot("missing_"+objectTag);
			return null;
			
		}
		
	}
	
	public static Object[][] getTableData(int rowCount, int colCount, String xpathStart, String xpathMid, String xpathEnd){
		
		//extract the data from an entire table when the expected row and column count is known
		Object tableData[][] = new Object[rowCount][colCount];
		
		for(int rowNum=1; rowNum<=rowCount; rowNum++){
			for(int colNum=0; colNum<colCount; colNum++){
				
				tableData[rowNum][colNum] = driver.findElement(By.xpath(xpathStart+rowNum+xpathMid+colNum+xpathEnd)).getText();
			
			}			

		}

		return tableData;
		
	}
	
	public static Object[][] getTableData(String xpathStart, String xpathMid, String xpathEnd){
		
		//extract the data from an entire table
		int rowCount=driver.findElements(By.xpath("//tbody/tr")).size();
		int colCount=driver.findElements(By.xpath("//tbody/tr/td")).size();
		Object tableData[][] = new Object[rowCount][colCount];
		
		for(int rowNum=1; rowNum<=rowCount; rowNum++){
			for(int colNum=0; colNum<colCount; colNum++){
				
				tableData[rowNum][colNum] = driver.findElement(By.xpath(xpathStart+rowNum+xpathMid+colNum+xpathEnd)).getText();
			
			}			

		}

		return tableData;
		
	}
	
	public static boolean searchPageSource(String searchValue){
		
		//search the page source for a value
		if(driver.getPageSource().contains(searchValue)){
			
			return true;
		
		}else{
			
			return false;
			
		}
		
	}
	
	public static String[] splitSearchValue(String searchValue){
			
		String[] searchChars = new String[2];
		searchChars[0] = searchValue.substring(0, searchValue.length()-1);
		searchChars[1] = searchValue.substring(searchValue.length()-1);
		
		return searchChars;
	}
		
	/*public static boolean runTest(String testName){
		
		for(int rownum=2; rownum<=excel.getRowCount("testsuite"); rownum++){
			
			if(excel.getCellData("testsuite", "testname",  rownum).equals(testName)){
				if(excel.getCellData("testsuite", "runtest", rownum).equals("y")){
						
					return true;
						
				}
				else{
				
					return false;
				
				}	
					
			}
		
		}
		
		return false;	
			
	}*/
	
	public static boolean runTest(String testName){
	    
	    if(run.getProperty(testName).contentEquals("y")){
                    
            return true;
                    
        }
        
        else if(run.getProperty(testName).contentEquals("n")){
            
            return false;
            
        }
    
        appLogs.debug("Test run configuration not found for: "+testName);
        return false;
	    
	}

	public static Object[][] getData(String sheetName){
				
		int rows = excel.getRowCount(sheetName);
		int cols = excel.getColumnCount(sheetName);
		Object data[][] = new Object[rows-1][cols];
		
		for(int rowNum=2; rowNum<=rows; rowNum++){
			for(int colNum=0; colNum<cols; colNum++){
				
				data[rowNum-2][colNum]=excel.getCellData(sheetName, colNum, rowNum);
			
			}			

		}

		return data;
		
	}

	public static int getIterationCount(String sheetName){
		
		int iterations = excel.getRowCount(sheetName)-1;

		return iterations;
		
	}
    
    public static String fileToString(String fileName){
        
        StringBuilder contents=new StringBuilder();
        String workingDir = System.getProperty("user.dir");
        File filePath = new File(workingDir + File.separator);

        try{
        	
        	BufferedReader br = new BufferedReader(new FileReader(filePath+File.separator+fileName));
        	String s;

            while ((s = br.readLine()) != null){
                
            	contents.append(s);
        
            }
            
            br.close();
            
        }catch (IOException e) {
          
        	appLogs.debug("Cannot read the text file: "+fileName);
        
        }
        
        return contents.toString();
    
    }

    public static void stringtoFile(String text, String fileName){
        
        try(PrintWriter out = new PrintWriter(fileName)){
            
            out.println(text);
            
        }catch (IOException e) {
          
            appLogs.debug("Cannot write the text file: "+fileName);
        
        }
    
    }
    

    public static List<String> fileToList(String fileName){
        
        //local variables
        List<String> response = new ArrayList<String>();
        String inputLine = new String();
        String workingDir = System.getProperty("user.dir");
        File filePath = new File(workingDir + File.separator);

        try{
            
            //write response to array list
            BufferedReader in = new BufferedReader(new FileReader(filePath+File.separator+fileName));
            while ((inputLine = in.readLine()) != null){
            
                response.add(inputLine);
            
            }
        
            //close buffered reader
            in.close();
            
        }catch (IOException e) {
          
            appLogs.debug("Cannot read the text file: "+fileName);
        
        }
        
        return response;
    
    }
    
    public static void captureScreenshot(String fileName){
		
		Calendar cal = new GregorianCalendar();
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int sec =cal.get(Calendar.SECOND);
		int min =cal.get(Calendar.MINUTE);
		int date = cal.get(Calendar.DATE);
		int day =cal.get(Calendar.HOUR_OF_DAY);
			
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		try{
			
			String screenshotPath = System.getProperty("user.dir")+"/screenshots/"+fileName+"_"+year+"_"+date+"_"+(month+1)+"_"+day+"_"+min+"_" +sec+".jpg";
			FileUtils.copyFile(scrFile, new File(screenshotPath));
		
		}catch (IOException e){
			
			e.printStackTrace();
			
		}
		
	}
	
	public static String escStr(String str){
		
		String escapedResults = StringEscapeUtils.escapeEcmaScript(str);

		return escapedResults;
		
	}
	
    public static int stringDecToInt(String str){
        
        //convert a string decimal to an int
        double dbl = Double.parseDouble(str);
        int i = (int) dbl;

        return i;
        
    }
    
    public static String randomTime(String format){
       
        // create a random timestamp
        Random rnd = new Random();
        int millisInDay = 24*60*60*1000;
        Time time = new Time((long)rnd.nextInt(millisInDay));

        //convert time to specified format
        DateFormat formatter = new SimpleDateFormat(format);
        String randomTime = formatter.format(time);
        
        return randomTime;
        
    }
    
    public static String randomDatePastMoth(String format){
        
        // create a random datestamp within the past 30 days
        Random rnd = new Random();
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        cal.add(Calendar.DATE, -(rnd.nextInt(31)));
        Date dateRandom = cal.getTime();

        //convert the date to specified format
        DateFormat formatter = new SimpleDateFormat(format);
        String randomDate = formatter.format(dateRandom);
        
        return randomDate;
        
    }
    
    public static String formatMilliseconds(long timeMillis){
    
        //local variables
        String formattedTime = new String();
        
        //format a string response in minutes, seconds, and/or milliseconds as appropriate
        if (timeMillis < 1000){
            
            formattedTime = timeMillis+" milliseconds";
        
        }else if (timeMillis > 999 && timeMillis < 60000){
        
            float responseTimeSecs = (float)timeMillis/1000;
            formattedTime = responseTimeSecs+" seconds";
            
        }else{
        
            String responseTimeMins = String.format("%02d minutes and %02d seconds", 
                TimeUnit.MILLISECONDS.toMinutes(timeMillis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(timeMillis) % TimeUnit.MINUTES.toSeconds(1));
            formattedTime = responseTimeMins;
            
        }
        
        return formattedTime;
        
    }

    public static long ipToLong(InetAddress ip) {
        
        //local variables
        byte[] octets = null;
        long result = 0;
        
        octets = ip.getAddress();
        for (byte octet : octets){
        
            result <<= 8;
            result |= octet & 0xff;
        
        }
        
        return result;
    
    }
    
    public static String netblockToNetmask(String netblock){
        
        //local variables
        String netmask = new String();
        
        //map netblock to netmask
        switch (netblock) {
        
        case "16":
            netmask = "255.255.0.0";
            break;
            
        case "17": 
            netmask = "255.255.128.0";
            break;
            
        case "18": 
            netmask = "255.255.192.0";
            break;
            
        case "19": 
            netmask = "255.255.224.0";
            break;
            
        case "20": 
            netmask = "255.255.240.0";
            break;

        case "21":
            netmask = "255.255.248.0";
            break;
            
        case "22": 
            netmask = "255.255.252.0";
            break;
            
        case "23": 
            netmask = "255.255.254.0";
            break;
            
        case "24": 
            netmask = "255.255.255.0";
            break;
            
        case "25": 
            netmask = "255.255.255.128";
            break;
            
        case "26":
            netmask = "255.255.255.192";
            break;
            
        case "27": 
            netmask = "255.255.255.224";
            break;
            
        case "28": 
            netmask = "255.255.255.240";
            break;
            
        case "29": 
            netmask = "255.255.255.248";
            break;
            
        case "30": 
            netmask = "255.255.255.252";
            break;
            
        default:
            netmask = "";
            break;
            
    }
        
        return netmask;
        
    }
    
    // *** REST API METHODS ***
	
    public static Object[] apiGetJson(String requestUrl) throws IOException{
        
        //build http request
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet requestMethod = new HttpGet(requestUrl);
  
        //add headers
        requestMethod.setHeader("Authorization", "Bearer "+token);
        requestMethod.setHeader("Content-type", "application/json");
        requestMethod.setHeader("Accept", "application/json");
        
        //make request and calculate response time
        long start = System.currentTimeMillis();
        CloseableHttpResponse httpResponse = httpClient.execute(requestMethod);
        long responseTime = System.currentTimeMillis() - start;
        
        //get status code from response
        int statusCode = httpResponse.getStatusLine().getStatusCode();  

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
                
        //close http client
        httpClient.close();
        
        // return the status code and response as string in an object
        Object[] apiResponse = new Object[3];
        apiResponse[0] = statusCode;
        apiResponse[1] = responseTime;
        apiResponse[2] = responseBody;
        
        return apiResponse;
        
    }

    public static Object[] apiPostJson(String requestUrl, String requestBody) throws IOException{
        
        //build http request
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost requestMethod = new HttpPost(requestUrl);
  
        //add headers
        requestMethod.setHeader("Authorization", "Bearer "+token);
        requestMethod.setHeader("Content-type", "application/json");
        requestMethod.setHeader("Accept", "application/json");
        
        //add json request body
        StringEntity entity = new StringEntity(requestBody);
        requestMethod.setEntity(entity);
        
        //make request and calculate response time
        long start = System.currentTimeMillis();
        CloseableHttpResponse httpResponse = httpClient.execute(requestMethod);
        long responseTime = System.currentTimeMillis() - start;
        
        //get status code from response
        int statusCode = httpResponse.getStatusLine().getStatusCode();  

        //write input stream response to buffer
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(httpResponse.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            
            result.append(line);
        
        }
        
        //write buffer to string
        String response = result.toString();
                
        //close http client
        httpClient.close();
        
        // return the status code and response as string in an object
        Object[] apiResponse = new Object[3];
        apiResponse[0] = statusCode;
        apiResponse[1] = responseTime;
        apiResponse[2] = response;
        
        return apiResponse;
        
    }

    public static Object[] apiPutJson(String requestUrl, String requestBody) throws IOException{
        
        //build http request
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut requestMethod = new HttpPut(requestUrl);
  
        //add headers
        requestMethod.setHeader("Authorization", "Bearer "+token);
        requestMethod.setHeader("Content-type", "application/json");
        requestMethod.setHeader("Accept", "application/json");
        
        //add json request body
        StringEntity entity = new StringEntity(requestBody);
        requestMethod.setEntity(entity);
        
        //make request and calculate response time
        long start = System.currentTimeMillis();
        CloseableHttpResponse httpResponse = httpClient.execute(requestMethod);
        long responseTime = System.currentTimeMillis() - start;
        
        //get status code from response
        int statusCode = httpResponse.getStatusLine().getStatusCode();  

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
                
        //close http client
        httpClient.close();
        
        // return the status code and response as string in an object
        Object[] apiResponse = new Object[3];
        apiResponse[0] = statusCode;
        apiResponse[1] = responseTime;
        apiResponse[2] = responseBody;
        
        return apiResponse;
        
    }

    public static Object[] apiDeleteJson(String requestUrl) throws IOException{
        
        //build http request
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete requestMethod = new HttpDelete(requestUrl);
  
        //add headers
        requestMethod.setHeader("Authorization", "Bearer "+token);
        requestMethod.setHeader("Content-type", "application/json");
        requestMethod.setHeader("Accept", "application/json");

        //make request and calculate response time
        long start = System.currentTimeMillis();
        CloseableHttpResponse httpResponse = httpClient.execute(requestMethod);
        long responseTime = System.currentTimeMillis() - start;
        
        //get status code from response
        int statusCode = httpResponse.getStatusLine().getStatusCode();  

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
                
        //close http client
        httpClient.close();
        
        // return the status code and response as string in an object
        Object[] apiResponse = new Object[3];
        apiResponse[0] = statusCode;
        apiResponse[1] = responseTime;
        apiResponse[2] = responseBody;
        
        return apiResponse;
        
    }

    public int apiResponseStatusCode(String method, String url, String body) throws FailingHttpStatusCodeException, IOException{
		
		//local variables for constructing an api request
		HttpMethod requestMethod = null;
		FormEncodingType encodingType = FormEncodingType.URL_ENCODED;
		
		//make api request with specified method and get response status code
		switch (method) {
		
			case "GET":
				requestMethod = HttpMethod.GET;
				break;
				
			case "HEAD": 
				requestMethod = HttpMethod.HEAD;
				break;
				
			case "POST": 
				requestMethod = HttpMethod.POST;
				break;
				
			case "PUT": 
				requestMethod = HttpMethod.PUT;
		        break;
		        
			case "DELETE": 
				requestMethod = HttpMethod.DELETE;
		        break;
		        
		}
		
		WebClient webClient = new WebClient();
		WebRequest webRequest = new WebRequest(new URL(url), requestMethod);
		webRequest.setAdditionalHeader("Authorization", "Bearer "+token);
		
		//set request body and content-type if applicable
		if (method == "POST" || method == "PUT"){ 
				
			webRequest.setRequestBody(body);
			webRequest.setEncodingType(encodingType);
		
		}
		
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setTimeout(900000);
		int statusCode = webClient.getPage(webRequest).getWebResponse().getStatusCode();
		webClient.close();
		
		return statusCode;

	}
    
	public static String apiResponseAsString(String method, String url, String body) throws FailingHttpStatusCodeException, IOException{
		
		//local variables for constructing an api request
		HttpMethod requestMethod = null;
		FormEncodingType encodingType = FormEncodingType.URL_ENCODED;
		
		//make api request with specified method and get response status code
		switch (method) {
		
			case "GET":
				requestMethod = HttpMethod.GET;
				break;
				
			case "HEAD": 
				requestMethod = HttpMethod.HEAD;
				break;
				
			case "POST": 
				requestMethod = HttpMethod.POST;
				break;
				
			case "PUT": 
				requestMethod = HttpMethod.PUT;
		        break;
		        
			case "DELETE": 
				requestMethod = HttpMethod.DELETE;
		        break;
		        
		}
		
		WebClient webClient = new WebClient();
		WebRequest webRequest = new WebRequest(new URL(url), requestMethod);
		webRequest.setAdditionalHeader("Authorization", "Bearer "+token);
		
		//set request body and content-type if applicable
		if (method == "POST" || method == "PUT"){  
				
			webRequest.setRequestBody(body);
			webRequest.setEncodingType(encodingType);
		
		}
		
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setTimeout(900000);
		String response = webClient.getPage(webRequest).getWebResponse().getContentAsString();
		webClient.close();
		
		return response;
		
	}

	public static Object[] apiResponseObject(String method, String url, String body) throws FailingHttpStatusCodeException, IOException{
		
		//local variables for constructing an api request
		HttpMethod requestMethod = null;
		FormEncodingType encodingType = FormEncodingType.URL_ENCODED;
		
		//make api request with specified method and get response status code
		switch (method) {
		
			case "GET":
				requestMethod = HttpMethod.GET;
				break;
				
			case "HEAD": 
				requestMethod = HttpMethod.HEAD;
				break;
				
			case "POST": 
				requestMethod = HttpMethod.POST;
				break;
				
			case "PUT": 
				requestMethod = HttpMethod.PUT;
		        break;
		        
			case "DELETE": 
				requestMethod = HttpMethod.DELETE;
		        break;
		        
		}
		
		WebClient webClient = new WebClient();
		WebRequest webRequest = new WebRequest(new URL(url), requestMethod);
		webRequest.setAdditionalHeader("Authorization", "Bearer "+token);
		
		//set request body and content-type if applicable
		if (method == "POST" || method == "PUT"){  
				
			webRequest.setRequestBody(body);
			webRequest.setEncodingType(encodingType);
		
		}
		
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setTimeout(900000);
		WebResponse response = webClient.getPage(webRequest).getWebResponse();
		int statusCode = response.getStatusCode();
		long loadTime = response.getLoadTime();
		String responseAsString = response.getContentAsString();
		webClient.close();
		
		// return the status code and response as string in an object
		Object[] apiResponse = new Object[3];
		apiResponse[0] = statusCode;
		apiResponse[1] = responseAsString;
		apiResponse[2] = loadTime;
		return apiResponse;
		
	}
    
    // *** FEEDS METHODS ***
	
	public static List<String> getFeedIndicators(String url) throws IOException{

        //local variables
        List<String> response = new ArrayList<String>();
        String inputLine = new String();
 
        //check config to get feed from file or url
        if (config.getProperty("feedFromFile").contentEquals("y")){
        
            response = fileToList(config.getProperty("feedFileName"));
            
        }else if (config.getProperty("feedFromFile").contentEquals("n")){    
        
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
               
            //verify valid response code
            int statusCode = con.getResponseCode();
        
            if (!(statusCode == 200)){
            
                appLogs.debug("FAILED - Status code for feed request was: "+statusCode+".");
                response = null;
                Assert.fail();
    
            }

            //write response to array list
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((inputLine = in.readLine()) != null){
            
                response.add(inputLine);
                Assert.fail();
                
            }
        
            //close buffered reader
            in.close();

        }else{
            
            appLogs.debug("FAILED - Could not determine feedFromFile config setting.");
            response = null;
            
        }
            
        //return response list
        return response;

    }
	
    public static File getVerisigniDefenseIndicators(String feedUrl) throws IOException{

        //local variables
        File response = null;
        String username = new String();
        String password = new String();
        String userpass = new String();
        String basicAuth = new String();
        
        //check config to get feed from file or url
        if (config.getProperty("feedFromFile").contentEquals("y")){
        
            response = new File(config.getProperty("feedFileName"));
            
        }else if (config.getProperty("feedFromFile").contentEquals("n")){    
        
            URL url = new URL(feedUrl);
            HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();

            //get verisign idefense credentials
            username = config.getProperty("viUsername");
            password = config.getProperty("viPassword");
            
            userpass = username + ":" + password;
            basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            urlCon.setRequestProperty ("Authorization", basicAuth);
            
            FileUtils.copyURLToFile(url, response);

        }else{
            
            appLogs.debug("FAILED - Could not determine feedFromFile config setting.");
            response = null;
            
        }
            
        //return response list
        return response;

    }
    
    public static String getCsIndFeed(int iteration) throws IOException, ParseException{

        //local variables
        String url = new String();
        String response = new String();
        String csId = new String();
        String csKey = new String();
        String csStartDate = new String();
        String csEndDate = new String();
        long gte;
        long lte;
        
        //check config to get feed from file or url
        if (config.getProperty("feedFromFile").contentEquals("y") && iteration < 2){
        
            response = fileToString(config.getProperty("feedFileName"));
        
        }else if (config.getProperty("feedFromFile").contentEquals("y") && !(iteration < 2)){ 
        
            //return empty response if reading from file and on the second iteration
            response = "[]";
        
        }else if (config.getProperty("feedFromFile").contentEquals("n")){     
        
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
            
            url = "https://intelapi.crowdstrike.com/indicator/v1/search/published_date?gte="+gte+"&lte="+lte+"&order=asc&perPage=1000&page="+iteration;
            
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
        response = sb.toString();

        }else{
            
            appLogs.debug("FAILED - Could not determine feedFromFile config setting.");
            response = null;
            
        }
    
   //return response as string
    return response;
        
    }
    
    public static String iSightAPIRequest(String endpoint, String query, String accept) throws Exception{
       
        final String X_AUTH = "X-Auth";
        final String X_AUTH_HASH = "X-Auth-Hash";
        final String ACCEPT = "Accept";
        final String ACCEPT_VERSION = "Accept-Version";
        final String DATE = "Date";
        final String CONTENT_TYPE = "Content-Type";
        final String LINE_FEED = "\r\n";
        final String SCHEME = "https";
        final String API_HOST = "api.isightpartners.com";
        String apiKey;
        String secretKey;
        
        String acceptVersion = "2.2";
        Date now = new Date();

        //get isight configuration
        apiKey = config.getProperty("isPublicApi");
        secretKey = config.getProperty("isPrivateApi");
        
        // The format in which to put current date in the Date header (RFC 822 compatible)
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");

        String dateString = format.format(now);

        String uriPath = endpoint + "?" + query;
        if (query == null){
            
            uriPath = endpoint;
            
        }

        URI uri = new URI(SCHEME + "://" + API_HOST + uriPath);

        // Concatenate Request and headers and calculate HMAC-SHA256
        String authString = uriPath+ acceptVersion + accept + dateString;
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        hmacSha256.init(secretKeySpec);
        hmacSha256.update(authString.getBytes("UTF-8"));
        byte[] hashBytes = hmacSha256.doFinal();

        Formatter formatter = new Formatter();
        for (byte b : hashBytes){
            
            formatter.format("%02x", b);
               
        }
        
        String hash = formatter.toString();
        formatter.close();

        try{
           
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

            if(responseCode == HttpURLConnection.HTTP_OK){
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String response = new String();
                String line = null;

                while((line = reader.readLine()) != null){
               
                    response += line;
               
                }

                return response;
           
            }else{
               
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

                String response = new String();
                String line = null;

                while((line = reader.readLine()) != null){
                    
                    response += line;
               
                }

                return response;
           
            }
       
        }catch(Exception e){
            
               return null;
    
       }
    
    }

    public static String iSightReportIndexEndpoint(long startDate, long endDate) throws Exception{
        
        String endpoint = "/report/index";
        String accept = "application/json";

        String query = "startDate=" + startDate + "&endDate=" + endDate + "";
        
        String response = iSightAPIRequest(endpoint, query, accept);
        
        return response;
    
    }
    
    public static String iSightReportEndpoint(String reportId) throws Exception{
        
        String endpoint = "/report/"+reportId;
        String accept = "application/json";

        String query = "format=json";
        
        String response = iSightAPIRequest(endpoint, query, accept);
        
        return response;
    }
    
    public static String iSightViewIndicatorsEndpoint(long startDate, long endDate) throws Exception{
        
        String endpoint = "/view/indicators";
        String accept = "application/json";

        String query = "format=json&startDate=" + startDate + "&endDate=" + endDate + "";

        String response = iSightAPIRequest(endpoint, query, accept);

        return response;
    }
    
    public static File getDeepSightIndicators(String feedUrl) throws IOException{

        //local variables
        File response = null;
        String username = new String();
        String password = new String();
        String userpass = new String();
        String basicAuth = new String();
        
        //check config to get feed from file or url
        if (config.getProperty("feedFromFile").contentEquals("y")){
        
            response = new File(config.getProperty("feedFileName"));
            
        }else if (config.getProperty("feedFromFile").contentEquals("n")){    
        
            URL url = new URL(feedUrl);
            HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();

            //get deepSight credentials
            username = config.getProperty("dsUsername");
            password = config.getProperty("dsPassword");
            
            userpass = username + ":" + password;
            basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            urlCon.setRequestProperty ("Authorization", basicAuth);
            
            FileUtils.copyURLToFile(url, response);

        }else{
            
            appLogs.debug("FAILED - Could not determine feedFromFile config setting.");
            response = null;
            
        }
            
        //return response list
        return response;

    }
    
    public static Object[] mapCsIndicatorType(String indicatorType){
        
        //local variables
        String threatqIndicatorType = "";
        String threatqIndicatorClass = "";
        boolean relatedIndicator = false;
        boolean relatedAttribute = false;       
        
        // set flags for db query based on indicator type
        switch (indicatorType) {
        
            case "device_name":
                threatqIndicatorType = "Device Name";
                threatqIndicatorClass = "host";
                relatedIndicator = false;
                relatedAttribute = true;
                break;
            
            case "event_name":
                threatqIndicatorType = "Event Name";
                threatqIndicatorClass = "host";
                relatedIndicator = false;
                relatedAttribute = true;
                break;
            
            case "file_mapping":
                threatqIndicatorType = "File Mapping";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "file_path":
                threatqIndicatorType = "Filepath";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "hash_ion":
                threatqIndicatorType = "Hash ION";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "persona_name":
                threatqIndicatorType = "Persona Name";
                threatqIndicatorClass = "network";
                relatedIndicator = false;
                relatedAttribute = true;
                break;
        
            case "phone_number":
                threatqIndicatorType = "Phone Number";
                threatqIndicatorClass = "network";
                relatedIndicator = false;
                relatedAttribute = true;
                break;
        
            case "semaphore_name":
                threatqIndicatorType = "Semaphore Name";
                threatqIndicatorClass = "host";
                relatedIndicator = false;
                relatedAttribute = true;
                break;
        
            case "x509_serial":
                threatqIndicatorType = "x509 Serial";
                threatqIndicatorClass = "network";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "x509_subject":
                threatqIndicatorType = "x509 Subject";
                threatqIndicatorClass = "network";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "service_name":
                threatqIndicatorType = "Service Name";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "compile_time":
                threatqIndicatorType = "Compile Time";
                threatqIndicatorClass = "host";
                relatedIndicator = false;
                relatedAttribute = true;
                break;
        
            case "binary_string":
                threatqIndicatorType = "Binary String";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "registry":
                threatqIndicatorType = "Registry Key";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
            
            case "port":
                threatqIndicatorType = "Port";
                threatqIndicatorClass = "network";
                relatedIndicator = false;
                relatedAttribute = true;
                break;
        
            case "password":
                threatqIndicatorType = "Password";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "file_name":
                threatqIndicatorType = "Filename";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "mutex_name":
                threatqIndicatorType = "Mutex";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "username":
                threatqIndicatorType = "Username";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "domain":
                threatqIndicatorType = "FQDN";
                threatqIndicatorClass = "network";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "email_address":
                threatqIndicatorType = "Email Address";
                threatqIndicatorClass = "network";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "email_subject":
                threatqIndicatorType = "Email Subject";
                threatqIndicatorClass = "network";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "hash_md5":
                threatqIndicatorType = "MD5";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "hash_sha1":
                threatqIndicatorType = "SHA-1";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "hash_sha256":
                threatqIndicatorType = "SHA-256";
                threatqIndicatorClass = "host";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "ip_address":
                threatqIndicatorType = "IP Address";
                threatqIndicatorClass = "network";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "ip_address_block":
                threatqIndicatorType = "CIDR Block";
                threatqIndicatorClass = "network";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "url":
                threatqIndicatorType = "URL";
                threatqIndicatorClass = "network";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
        
            case "user_agent":
                threatqIndicatorType = "User-agent";
                threatqIndicatorClass = "network";
                relatedIndicator = true;
                relatedAttribute = true;
                break;
                    
        }
        
        //build and return array
        Object[] queryArray = new Object[4];
        queryArray[0] = threatqIndicatorType;
        queryArray[1] = relatedIndicator;
        queryArray[2] = relatedAttribute;
        queryArray[3] = threatqIndicatorClass;
        return queryArray;
        
    }
    
    public static String mapCsLabelName(String labelName){
        
        //local variables
        String threatqLabel = new String();      
        
        // map cs label name to threatq label name
        switch (labelName) {
        
            case "Actor":
                threatqLabel = "Campaign";
                break;
            
            case "CSD":
                threatqLabel = "Reference";
                break;
                
            case "MaliciousConfidence":
                threatqLabel = "Confidence";
                break;
                
            case "Malware":
                threatqLabel = "Malware Family";
                break;
            
            case "KillChain":
                threatqLabel = "Attack Phase";
                break;
                
            case "DomainType":
                threatqLabel = "CrowdStrike Domain Type";
                break;
                
            case "EmailAddressType":
                threatqLabel = "CrowdStrike Email Address Type";
                break;
                
            case "IntelNews":
                threatqLabel = "CrowdStrike Intel News";
                break;
                
            case "IPAddressType":
                threatqLabel = "CrowdStrike IP Address Type";
                break;
                
            case "IpAddressType":
                threatqLabel = "CrowdStrike IP Address Type";
                break;

            case "Status":
                threatqLabel = "CrowdStrike Status";
                break;
                
            case "Target":
                threatqLabel = "Target";
                break;
                
            case "ThreatType":
                threatqLabel = "CrowdStrike Threat Type";
                break;
                
            case "Vulnerability":
                threatqLabel = "CVE";
                break;
                
            default:
                threatqLabel = "";
                break;
                    
        }
        
        //return mapped label name
        return threatqLabel;
        
    }

    public static String[] mapiSightIndicatorType(String indicatorType){
        
        //local variables
        String[] indicatorDetails = new String[2];
        String threatqIndicatorType = "";
        String threatqIndicatorClass = "";     
        
        // set flags for db query based on indicator type
        switch (indicatorType) {
        
            case "senderAddress":
                threatqIndicatorType = "Email Address";
                threatqIndicatorClass = "network";
                break;
            
            case "sourceDomain":
                threatqIndicatorType = "FQDN";
                threatqIndicatorClass = "network";
                break;
            
            case "sourceIp":
                threatqIndicatorType = "IP Address";
                threatqIndicatorClass = "network";
                break;
        
            case "subject":
                threatqIndicatorType = "Email Subject";
                threatqIndicatorClass = "network";
                break;
        
            case "fileName":
                threatqIndicatorType = "Filename";
                threatqIndicatorClass = "host";
                break;
        
            case "fuzzyHash":
                threatqIndicatorType = "Fuzzy Hash";
                threatqIndicatorClass = "host";
                break;
        
            case "md5":
                threatqIndicatorType = "MD5";
                threatqIndicatorClass = "host";
                break;
        
            case "sha1":
                threatqIndicatorType = "SHA-1";
                threatqIndicatorClass = "host";
                break;
        
            case "sha256":
                threatqIndicatorType = "SHA-256";
                threatqIndicatorClass = "host";
                break;
        
            case "userAgent":
                threatqIndicatorType = "User-agent";
                threatqIndicatorClass = "network";
                break;
        
            case "registry":
                threatqIndicatorType = "Registry Key";
                threatqIndicatorClass = "host";
                break;
        
            case "cidr":
                threatqIndicatorType = "CIDR Block";
                threatqIndicatorClass = "network";
                break;
        
            case "domain":
                threatqIndicatorType = "FQDN";
                threatqIndicatorClass = "network";
                break;       
                    
            case "ip":
                threatqIndicatorType = "IP Address";
                threatqIndicatorClass = "network";
                break;       
                    
            case "url":
                threatqIndicatorType = "URL";
                threatqIndicatorClass = "network";
                break;       
                    
        }
        
        //build and return array
        indicatorDetails[0] = threatqIndicatorType;
        indicatorDetails[1] = threatqIndicatorClass;
        return indicatorDetails;
        
    }

    public static String mapiSightAttribute(String attributeKey){
        
        //local variables
        String threatqAttributeKey = "";     
        
        // set flags for db query based on indicator type
        switch (attributeKey) {
        
            case "emailIdentifier":
                threatqAttributeKey = "Email Identifier";
                break;

            case "senderName":
                threatqAttributeKey = "Sender Name";
                break;
                 
            case "language":
                threatqAttributeKey = "Language";
                break;
                 
            case "fileName":
                threatqAttributeKey = "Filename";
                break;
                 
            case "fileSize":
                threatqAttributeKey = "File Size";
                break;
                 
            case "fileIdentifier":
                threatqAttributeKey = "File Identifier";
                break;
                 
            case "description":
                threatqAttributeKey = "Description";
                break;
                 
            case "fileType":
                threatqAttributeKey = "File Type";
                break;
                 
            case "packer":
                threatqAttributeKey = "Packer";
                break;
                 
            case "networkName":
                threatqAttributeKey = "Network Name";
                break;
                 
            case "asn":
                threatqAttributeKey = "ASN";
                break;
                 
            case "domainTimeOfLookup":
                threatqAttributeKey = "Domain T?ime Of Lookup";
                break;
                 
            case "networkIdentifier":
                threatqAttributeKey = "Network Identifier";
                break;
                 
            case "port":
                threatqAttributeKey = "Port";
                break;
                 
            case "protocol":
                threatqAttributeKey = "Protocol";
                break;
                 
            case "registrantName":
                threatqAttributeKey = "Registrant Name";
                break;
                 
            case "registrantEmail":
                threatqAttributeKey = "Registrant Email";
                break;
                 
            case "networkType":
                threatqAttributeKey = "Network Type";
                break;
                 
            case "malwareFamily":
                threatqAttributeKey = "Malware Family";
                break;
                 
            case "observationTime":
                threatqAttributeKey = "Observation Time";
                break;
                 
        }
        
        return threatqAttributeKey;
        
    }

    public static List<String> getiSightTagSections(){
    
        //local variables
        List<String> tagSectionList = new ArrayList<String>();
        
        tagSectionList.add("motivation");
        tagSectionList.add("motivations");
        tagSectionList.add("intent");
        tagSectionList.add("intents");
        tagSectionList.add("language");
        tagSectionList.add("languages");
        tagSectionList.add("impact");
        tagSectionList.add("impacts");
        tagSectionList.add("affectedIndustry");
        tagSectionList.add("affectedIndustries");
        tagSectionList.add("sourceGeography");
        tagSectionList.add("sourceGeographies");
        tagSectionList.add("targetGeography");
        tagSectionList.add("targetGeographies");
        tagSectionList.add("affectedSystem");
        tagSectionList.add("affectedSystems");
        
        return tagSectionList;
    
    }
       
    public static List<String> getiSightViewIndicatorsTypes(){
        
        //local variables
        List<String> types = new ArrayList<String>();
        
        types.add("senderAddress");
        types.add("sourceDomain");
        types.add("sourceIp");
        types.add("subject");
        types.add("fileName");
        types.add("fuzzyHash");
        types.add("md5");
        types.add("sha1");
        types.add("sha256");
        types.add("userAgent");
        types.add("registry");
        types.add("cidr");
        types.add("domain");
        types.add("ip");
        types.add("url");
        
        return types;
    
    }
    
    public static List<String> getiSightViewIndicatorsAttributes(){
        
        //local variables
        List<String> attributes = new ArrayList<String>();
        
        attributes.add("emailIdentifier");
        attributes.add("senderName");
        attributes.add("language");
        attributes.add("fileName");
        attributes.add("fileSize");
        attributes.add("fileIdentifier");
        attributes.add("description");
        attributes.add("fileType");
        attributes.add("packer");
        attributes.add("networkName");
        attributes.add("asn");
        attributes.add("domainTimeOfLookup");
        attributes.add("networkIdentifier");
        attributes.add("port");
        attributes.add("protocol");
        attributes.add("registrantName");
        attributes.add("registrantEmail");
        attributes.add("networkType");
        attributes.add("malwareFamily");
        attributes.add("observationTime");
        
        return attributes;
    
    }
    
    public static String mapVerisigniDefenseIndicatorType(String indicatorType){
        
        //local variables
        String threatqIndicatorType = new String();
        
        // set flags for db query based on indicator type
        switch (indicatorType) {
        
            case "domain":
                threatqIndicatorType = "FQDN";
                break;       
        
            case "ipv4_addr":
                threatqIndicatorType = "IP Address";
                break;
        
            case "ipv4":
                threatqIndicatorType = "IP Address";
                break;
        
            case "URL":
                threatqIndicatorType = "URL";
                break;
        
            case "user_agent":
                threatqIndicatorType = "User-agent";
                break;
           
            default:
                threatqIndicatorType = "unknown";
                break;
        }
        
        //return mapped tq indicator type
        return threatqIndicatorType;
        
    }

    // *** DATABASE QUERY METHODS ***
    
    public static String dbGetConfigV1(String configName) throws InterruptedException, SQLException{
        
        //local variables
        List<String> configQuery = null;
        String mySqlQuery = null;
        String configValue = null;
        
        //get count of indicator value from network table
        mySqlQuery = "SELECT value FROM configuration WHERE name = \'"+configName+"\';";
        configQuery = mySql.getMysqlQuery(mySqlQuery);
        
        if (configQuery.size() > 0){
            
            configValue = configQuery.get(0);
            return configValue;
        
        }else{
            
            configValue = "";
            return configValue;
            
        }
        
    }
    
    public static int dbCountDuplicates(String dbTable, String dbColumn) throws InterruptedException, SQLException{
        
        //local variables
        List<String> duplicateQuery = null;
        String mySqlQuery = null;
        int duplicateCount = 0;
        
        //get count of indicator value from indicators table
        mySqlQuery = "SELECT COUNT(1) FROM "+dbTable+" GROUP BY "+dbColumn+" HAVING COUNT(1) > 1;";
        duplicateQuery = mySql.getMysqlQuery(mySqlQuery);
        duplicateCount = duplicateQuery.size();
       
        return duplicateCount;
       
    }
    
    public static int dbCountDuplicates(String dbTable, String dbColumnA, String dbColumnB) throws InterruptedException, SQLException{
        
        //local variables
        List<String> duplicateQuery = null;
        String mySqlQuery = null;
        int duplicateCount = 0;
        
        //get count of indicator value from indicators table
        mySqlQuery = "SELECT COUNT(1) FROM "+dbTable+" GROUP BY "+dbColumnA+","+dbColumnB+" HAVING COUNT(1) > 1;";
        duplicateQuery = mySql.getMysqlQuery(mySqlQuery);
        duplicateCount = duplicateQuery.size();
       
        return duplicateCount;
       
    }
    
    public static String dbQueryGetClass(String indicatorType) throws InterruptedException, SQLException{
        
        //local variables
        List<String> idQuery = null;
        String mySqlQuery = null;
        String indicatorClass = null;
        
        //get count of indicator value from indicators table
        mySqlQuery = "SELECT class FROM indicator_types WHERE name = \'"+MethLib.escStr(indicatorType)+"\';";
        idQuery = mySql.getMysqlQuery(mySqlQuery);
        
        if (idQuery.size() > 0){
            
            indicatorClass = idQuery.get(0);
            return indicatorClass;
        
        }else{
            
            indicatorClass = "";
            return indicatorClass;
            
        }
        
    }
    
    public static String dbQueryGetIndicatorId(String indicatorValue, String indicatorType, String indicatorStatus, String indicatorClass, String indicatorSource) throws InterruptedException, SQLException{
        
        //local variables
        List<String> indicatorQuery = null;
        String mySqlQuery = null;
        String indicatorId = new String();
        
        //get indicator id from db table
        mySqlQuery = "SELECT i.id FROM indicators i LEFT JOIN indicator_sources ins ON ins.indicator_id = i.id LEFT JOIN sources s ON s.id = ins.source_id LEFT JOIN indicator_types t ON t.id = i.type_id LEFT JOIN indicator_statuses st ON st.id = i.status_id WHERE i.value = \'"+MethLib.escStr(indicatorValue)+"\' AND t.name = \'"+indicatorType+"\' AND st.name = \'"+indicatorStatus+"\' AND i.class = \'"+indicatorClass+"\' AND s.name = \'"+indicatorSource+"\' AND i.deleted_at IS null;";
        indicatorQuery = mySql.getMysqlQuery(mySqlQuery);
        
        if (indicatorQuery.size() > 0){
            
            indicatorId = indicatorQuery.get(0);
            return indicatorId;
        
        }else{
            
            indicatorId = "";
            return indicatorId;
            
        }
        
    }
    
    public static boolean dbQueryCountIndicator(String indicatorValue, String indicatorType, String indicatorStatus, String indicatorClass, String indicatorSource) throws InterruptedException, SQLException{
        
        //local variables
        List<String> indicatorQuery = null;
        String mySqlQuery = null;
        int indicatorCount = 0;
        boolean indicatorExists = false;
        
        //get count of indicator value from db table
        mySqlQuery = "SELECT COUNT(i.id) FROM indicators i LEFT JOIN indicator_sources ins ON ins.indicator_id = i.id LEFT JOIN sources s ON s.id = ins.source_id LEFT JOIN indicator_types t ON t.id = i.type_id LEFT JOIN indicator_statuses st ON st.id = i.status_id WHERE i.value = \'"+MethLib.escStr(indicatorValue)+"\' AND t.name = \'"+indicatorType+"\' AND st.name = \'"+indicatorStatus+"\' AND i.class = \'"+indicatorClass+"\' AND s.name = \'"+indicatorSource+"\' AND i.deleted_at IS null;";
        indicatorQuery = mySql.getMysqlQuery(mySqlQuery);
        indicatorCount = Integer.parseInt(indicatorQuery.get(0));
            
        if (indicatorCount > 0){
            
            indicatorExists = true;
            
        }
        
        return indicatorExists;
        
    }

    public static String dbQueryGetAttachmentId(String name, String title, String type) throws InterruptedException, SQLException{
        
        //local variables
        List<String> attachmentQuery = null;
        String mySqlQuery = null;
        String attachmentId = new String();
        
        //get indicator id from db table
        mySqlQuery = "SELECT a.id FROM attachments a LEFT JOIN attachment_types at ON at.id = a.type_id WHERE a.title = \'"+MethLib.escStr(title)+"\' AND a.name = \'"+name+"\' AND at.name = \'"+type+"\' AND a.deleted_at IS null;";
        attachmentQuery = mySql.getMysqlQuery(mySqlQuery);
        
        if (attachmentQuery.size() > 0){
            
            attachmentId = attachmentQuery.get(0);
            return attachmentId;
        
        }else{
            
            attachmentId = "";
            return attachmentId;
            
        }
        
    }

    public static boolean dbQueryIndicatorLinkExists(String indicatorId, String indicatorRelatedId) throws InterruptedException, SQLException{
        
        //local variables
        List<String> indicatorQuery = null;
        String mySqlQuery = null;
        int indicatorCount = 0;
        boolean indicatorExists = false;
        
        //get count of linked indicator as destination object from object_links table
        mySqlQuery = "SELECT COUNT(id) FROM object_links WHERE src_object_id = \'"+indicatorId+"\' AND src_type = \'indicator\' AND dest_object_id  = \'"+indicatorRelatedId+"\' AND dest_type = \'indicator\' AND deleted_at IS null;";
        
        indicatorQuery = mySql.getMysqlQuery(mySqlQuery);
        indicatorCount = Integer.parseInt(indicatorQuery.get(0));
        
        //get count of linked indicator as source object from object_links table
        mySqlQuery = "SELECT COUNT(id) FROM object_links WHERE src_object_id = \'"+indicatorRelatedId+"\' AND src_type = \'indicator\' AND dest_object_id  = \'"+indicatorId+"\' AND dest_type = \'indicator\' AND deleted_at IS null;";
        
        indicatorQuery = mySql.getMysqlQuery(mySqlQuery);
        indicatorCount = (indicatorCount + Integer.parseInt(indicatorQuery.get(0)));
       
        if (indicatorCount > 0){
            
            indicatorExists = true;
            
        }
        
        return indicatorExists;
        
    }
    
    public static boolean dbQueryAttachmentLinkExists(String indicatorValue, String indicatorSource, String attachmentName) throws InterruptedException, SQLException{
        
        //local variables
        List<String> indicatorQuery = null;
        String mySqlQuery = null;
        int linkCount = 0;
        boolean attachmentLinkExists = false;
        
        //get count of linked indicator as destination object from object_links table
        mySqlQuery = "SELECT COUNT(o.id) FROM object_links o LEFT JOIN indicators i ON o.src_object_id = i.id LEFT JOIN indicator_sources ins ON ins.indicator_id = i.id LEFT JOIN sources s ON s.id = ins.source_id LEFT JOIN attachments a ON o.dest_object_id = a.id WHERE i.value = \'"+indicatorValue+"\' AND s.name  = \'"+indicatorSource+"\' AND o.src_type = \'indicator\' AND a.name  = \'"+attachmentName+"\' AND o.dest_type = \'attachment\' AND o.deleted_at IS null;";
        
        indicatorQuery = mySql.getMysqlQuery(mySqlQuery);
        linkCount = Integer.parseInt(indicatorQuery.get(0));
        
        //get count of linked indicator as source object from object_links table
        mySqlQuery = "SELECT COUNT(o.id) FROM object_links o LEFT JOIN indicators i ON o.dest_object_id = i.id LEFT JOIN indicator_sources ins ON ins.indicator_id = i.id LEFT JOIN sources s ON s.id = ins.source_id LEFT JOIN attachments a ON o.src_object_id = a.id WHERE i.value = \'"+indicatorValue+"\' AND s.name  = \'"+indicatorSource+"\' AND o.src_type = \'attachment\' AND a.name  = \'"+attachmentName+"\' AND o.dest_type = \'indicator\' AND o.deleted_at IS null;";
        
        indicatorQuery = mySql.getMysqlQuery(mySqlQuery);
        linkCount = (linkCount + Integer.parseInt(indicatorQuery.get(0)));
       
        if (linkCount > 0){
            
            attachmentLinkExists = true;
            
        }
        
        return attachmentLinkExists;
        
    }
    
    public static boolean dbQueryAttributeExists(String indicatorValue, String indicatorSource, String attributeKey, String attributeValue) throws InterruptedException, SQLException{
        
        //local variables
        List<String> attributeQuery = null;
        String mySqlQuery = null;
        int attributeCount = 0;
        boolean attributeExists = false;

        //get count of attribute value from attributes_indicators
        mySqlQuery = "SELECT COUNT(ia.id) FROM indicator_attributes ia LEFT JOIN attributes a ON ia.attribute_id = a.id LEFT JOIN indicators i ON ia.indicator_id = i.id LEFT JOIN indicator_attribute_sources ias ON ia.id = ias.indicator_attribute_id LEFT JOIN sources s ON ias.source_id = s.id WHERE i.value = \'"+MethLib.escStr(indicatorValue)+"\' AND s.name = \'"+indicatorSource+"\' AND a.name = \'"+attributeKey+"\' AND ia.value = \'"+attributeValue+"\'AND ia.deleted_at IS null;";
        attributeQuery = mySql.getMysqlQuery(mySqlQuery);
        attributeCount = Integer.parseInt(attributeQuery.get(0));        

        if (attributeCount > 0){
            
            attributeExists = true;
            
        }
        
        return attributeExists;
        
    }
    
    public static boolean dbQueryAttachmentAttributeExists(String attachmentName, String attachmentSource, String attributeKey, String attributeValue) throws InterruptedException, SQLException{
        
        //local variables
        List<String> attributeQuery = null;
        String mySqlQuery = null;
        int attributeCount = 0;
        boolean attributeExists = false;

        //get count of attribute value from attributes_indicators
        mySqlQuery = "SELECT COUNT(aa.id) FROM attachment_attributes aa LEFT JOIN attributes a ON aa.attribute_id = a.id LEFT JOIN attachments att ON aa.attachment_id = att.id LEFT JOIN attachment_attribute_sources aas ON aa.id = aas.attachment_attribute_id LEFT JOIN sources s ON aas.source_id = s.id WHERE att.name = \'"+MethLib.escStr(attachmentName)+"\' AND s.name = \'"+MethLib.escStr(attachmentSource)+"\' AND a.name = \'"+attributeKey+"\' AND aa.value = \'"+attributeValue+"\' AND aa.deleted_at IS null;";
        attributeQuery = mySql.getMysqlQuery(mySqlQuery);
        attributeCount = Integer.parseInt(attributeQuery.get(0));      

        if (attributeCount > 0){
            
            attributeExists = true;
            
        }
        
        return attributeExists;
        
    }
    
    public static boolean dbQueryTagExists(String value, String objectId, String type) throws InterruptedException, SQLException{
        
        //local variables
        List<String> tagQuery = null;
        String mySqlQuery = null;
        int tagCount = 0;
        boolean tagExists = false;

        //get count of attribute value from attributes_indicators
        mySqlQuery = "SELECT COUNT(t.name) FROM tagged_objects obj LEFT JOIN tags t ON obj.tag_id = t.id WHERE t.name = \'"+MethLib.escStr(value)+"\' AND obj.object_id = \'"+objectId+"\' AND obj.object_type = \'"+type+"\';";
        tagQuery = mySql.getMysqlQuery(mySqlQuery);
        tagCount = Integer.parseInt(tagQuery.get(0));
        

        if (tagCount > 0){
            
            tagExists = true;
            
        }
        
        return tagExists;
        
    }
    
    public static void assertEventDetailsDb(String eventTitle, String eventType, String eventDate, String eventTime) throws SQLException, ParseException {
        
        //local variables
        String mySqlQuery = null;
        List<String> actualId = null;
        List<String> actualTitle = null;
        List<String> actualType = null;
        List<String> actualHappenedAt = null;
        String actualHappenedAtNml = null;
        String[] actualHappenedAtParsed = new String[2];
        String actualDate = null;
        String actualTime = null;
            
        //declare new soft assertion so test will complete before setting to fail
        SoftAssert softAssert = new SoftAssert();
            
        //get event id
        mySqlQuery = "SELECT id FROM events WHERE description=\'"+MethLib.escStr(eventTitle)+"\' AND deleted_at IS NULL;";
        actualId = mySql.getMysqlQuery(mySqlQuery);

        //assert event title
        mySqlQuery = "SELECT description FROM events WHERE id="+actualId.get(0)+";";
        actualTitle = mySql.getMysqlQuery(mySqlQuery);
            
        if (!compareElements(eventTitle, actualTitle.get(0))){
                
            appLogs.debug("FAIL: Event title \""+eventTitle+"\" does not match db: "+actualTitle.get(0));
            softAssert.fail();      
                    
        }
            
        //assert event type 
        mySqlQuery = "SELECT evt.name FROM events ev INNER JOIN event_types evt ON evt.id = ev.type_id WHERE ev.id="+actualId.get(0)+";";
        actualType = mySql.getMysqlQuery(mySqlQuery);
            
        if (!compareElements(eventType, actualType.get(0))){
                
            appLogs.debug("FAIL: Event type \""+eventType+"\" does not match db: "+actualType.get(0));
            softAssert.fail();      
                    
        }
            
        //parse happened_at from db to assert event date and time
        mySqlQuery = "SELECT happened_at FROM events WHERE id="+actualId.get(0)+";";
        actualHappenedAt = mySql.getMysqlQuery(mySqlQuery);
        actualHappenedAtNml = actualHappenedAt.get(0).substring(0, actualHappenedAt.get(0).indexOf('.'));
        actualHappenedAtParsed = actualHappenedAtNml.split("\\s+");
            
        //convert date formatting returned from db
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(actualHappenedAtParsed[0]);
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        actualDate = formatter.format(date).toString();
            
        if (!compareElements(eventDate, actualDate)){
                
            appLogs.debug("FAIL: Event date \""+eventDate+"\" does not match db: "+actualDate);
            softAssert.fail();      
                    
        }
            
        //convert time formatting returned from db
        formatter = new SimpleDateFormat("hh:mm:ss");
        Date time = formatter.parse(actualHappenedAtParsed[1]);
        formatter = new SimpleDateFormat("hh:mm a");
        actualTime = formatter.format(time).toString();
            
        if (!compareElements(eventTime, actualTime)){
                
            appLogs.debug("FAIL: Event time \""+eventTime+"\" does not match db: "+actualTime);
            softAssert.fail();      
                    
        }
        
    }

    public static void assertAdversaryDetailsDb(String adversaryName) throws SQLException {
         
         //local variables
         String mySqlQuery = null;
         List<String> actualId = null;
         List<String> actualName = null;
         
         //declare new soft assertion so test will complete before setting to fail
         SoftAssert softAssert = new SoftAssert();
         
         //get adversary id
         mySqlQuery = "SELECT id FROM events WHERE description=\'"+MethLib.escStr(adversaryName)+"\' AND deleted_at IS NULL;";
         actualId = mySql.getMysqlQuery(mySqlQuery);

         //assert adversary name
         mySqlQuery = "SELECT name FROM adversaries WHERE id="+actualId.get(0)+";";
         actualName = mySql.getMysqlQuery(mySqlQuery);
         
         if (!compareElements(adversaryName, actualName.get(0))){
             
             appLogs.debug("FAIL: Event title \""+adversaryName+"\" does not match db: "+actualName.get(0));
             softAssert.fail();      
                 
         }
         
     }
    
    // *** VALIDATION METHODS ***
    
    public static boolean validateIndicator(String indicator, String indicatorType){
        
        //local variables
        boolean validated = false;
        
        //validate indicator value based on type
        switch (indicatorType) {
        
            case "CIDR Block":
                
                validated = validateCidrBlock(indicator);
                break;
                
            case "E-mail Address": 
                
                validated = validateEmailAddress(indicator);
                break;
                
           case "FQDN": 
                
                validated = validateFqdn(indicator);
                break;
                
           case "Fuzzy Hash": 
              
               validated = validateFuzzyHash(indicator);
               break;
               
           case "GOST Hash": 
               
               validated = validateGostHash(indicator);
               break;
               
            case "IP Address": 
                
                validated = validateIpAddress(indicator);
                break;
                
            case "MD5": 
                
                validated = validateMd5(indicator);
                break;
                
            case "SHA-1": 
                
                validated = validateSha1(indicator);
                break;
            
            case "SHA-256": 
                
                validated = validateSha256(indicator);
                break;
                
            case "SHA-384": 
                
                validated = validateSha384(indicator);
                break;
             
            case "SHA-512": 
                
                validated = validateSha512(indicator);
                break;
                
            case "URL": 
                
                validated = validateUrl(indicator);
                break;
                
            case "URL Path": 
                
                validated = validateUrlPath(indicator);
                break;
             
            default:
                validated = true;
                break;    
                
        }
        
        return validated;
        
    }

    public static boolean validateCidrBlock(String indicator){
        
        //local variables
        Pattern pattern = null;
        Matcher match = null;
        boolean validated = false;
        
        //validate as cidr block
        pattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(\\/(\\d|[1-2]\\d|3[0-2]))$");
        match = pattern.matcher(indicator);
        validated = match.matches();
        
        return validated;
        
    }

    public static boolean validateEmailAddress(String indicator){
        
        //local variables
        boolean validated = false;
        
        //get an EmailValidator
        EmailValidator validator = EmailValidator.getInstance();

        //validate as email address
        validated = validator.isValid(indicator);

        return validated;
        
    }
    
    public static boolean validateFqdn(String indicator){
        
        //local variables
        boolean validated = false;;

        //get a DomainValidator
        DomainValidator validator = DomainValidator.getInstance();

        //validate as fqdn
        validated = validator.isValid(indicator);
        
        return validated;
        
    }
    
    public static boolean validateFuzzyHash(String indicator){
        
        //local variables
        boolean validated = false;
        
        return validated;
        
    }
    
    public static boolean validateGostHash(String indicator){
        
        //local variables
        boolean validated = false;
        
        //validate as GOST
        validated = indicator.matches("[a-fA-F0-9]{64}");
        
        return validated;
        
    }

    public static boolean validateIpAddress(String indicator){
        
        //local variables
        long ipLo = 0;
        long ipHi = 0;
        long ipToTest = 0;
        boolean validated = false;
        
        //get an InetAddressValidator
        InetAddressValidator validator = InetAddressValidator.getInstance();
        
        //validate as ip address
        validated = validator.isValidInet4Address(indicator);
        
       if (validated){
            
            //check if ip address is local host
            validated = !(indicator.contentEquals("127.0.0.1") || indicator.contentEquals("0.0.0.0"));
            
        }

        if (validated){
            
            //check if ip address is in private range 192.168.0.0 - 192.168.255.255
            try {
                
                ipLo = ipToLong(InetAddress.getByName("192.168.0.0"));
                ipHi = ipToLong(InetAddress.getByName("192.168.255.255"));
                ipToTest = ipToLong(InetAddress.getByName(indicator));
            
            } catch (UnknownHostException e) {
                
                e.printStackTrace();
                
            }
            
            validated = !(ipToTest >= ipLo && ipToTest <= ipHi);
            
        }
        
        if (validated){
            
            //check if ip address is in private range 172.16.0.0 - 172.31.255.255
            try {
                
                ipLo = ipToLong(InetAddress.getByName("172.16.0.0"));
                ipHi = ipToLong(InetAddress.getByName("172.31.255.255"));
                ipToTest = ipToLong(InetAddress.getByName(indicator));
            
            } catch (UnknownHostException e) {
                
                e.printStackTrace();
                
            }
            
            validated = !(ipToTest >= ipLo && ipToTest <= ipHi);
            
        }
        
        if (validated){
            
            //check if ip address is in private range 10.0.0.0 - 10.255.255.255 
            try {
                
                ipLo = ipToLong(InetAddress.getByName("10.0.0.0"));
                ipHi = ipToLong(InetAddress.getByName("10.255.255.255"));
                ipToTest = ipToLong(InetAddress.getByName(indicator));
            
            } catch (UnknownHostException e) {
                
                e.printStackTrace();
                
            }
            
            validated = !(ipToTest >= ipLo && ipToTest <= ipHi);
            
        }
        
        return validated;
        
    }
    
    public static boolean validateMd5(String indicator){
        
        //local variables
        boolean validated = false;
        
        //validate as md5
        validated = indicator.matches("[a-fA-F0-9]{32}");
        
        return validated;
        
    }
    
    public static boolean validateSha1(String indicator){
        
        //local variables
        boolean validated = false;
        
      //validate as sha-1
        validated = indicator.matches("[a-fA-F0-9]{40}");
        
        return validated;
        
    }
    
    public static boolean validateSha256(String indicator){
        
        //local variables
        boolean validated = false;
        
      //validate as sha-256
        validated = indicator.matches("[a-fA-F0-9]{64}");
        
        return validated;
        
    }
    
    public static boolean validateSha384(String indicator){
        
        //local variables
        boolean validated = false;
        
      //validate as sha-384
        validated = indicator.matches("[a-fA-F0-9]{96}");
        
        return validated;
        
    }
    
    public static boolean validateSha512(String indicator){
        
        //local variables
        boolean validated = false;
        
      //validate as sha-512
        validated = indicator.matches("[a-fA-F0-9]{128}");
        
        return validated;
        
    }
    
    public static boolean validateUrl(String indicator){
        
        //local variables
        boolean validated = false;
        
        //get a UrlValidator
        UrlValidator validator = new UrlValidator();
        
        //validate as url
        if(config.getProperty("normailizationEnabled").contentEquals("y")){
            
            validated = validator.isValid("http://"+indicator);
        
        }else{
            
            validated = validator.isValid(indicator);
            
        }
        
        return validated;
        
    }
    
    public static boolean validateUrlPath(String indicator){
        
        //local variables
        boolean validated = false;
        
        //get a UrlValidator
        UrlValidator validator = new UrlValidator();
        
        //validate as url
        if(config.getProperty("normailizationEnabled").contentEquals("y")){
            
            validated = validator.isValid("http://"+indicator);
        
        }else{
            
            validated = validator.isValid(indicator);
            
        }
        
        return validated;
        
    }

    // *** NORMALIZATION METHODS ***
    
    public static String[] normalizeIndicator(String indicatorValue, String indicatorType){
        
        //check if normalization config is enabled
        if(config.getProperty("normailizationEnabled").contentEquals("y")){
            
            //normalize each indicator value as applicable
            switch (indicatorType) {
        
                case "CIDR Block":
                    
                    indicatorValue = normalizeCidrBlock(indicatorValue);
                    
                    break;
                
                case "Email Address":
                    
                    indicatorValue = normalizeEmailAddress(indicatorValue);
                    
                    break;
            
                case "FQDN":
                    
                    indicatorValue = normalizeFqdn(indicatorValue);
                    
                    break;
               
                case "Fuzzy Hash":    
                    
                    indicatorValue = normalizeFuzzyHash(indicatorValue);
                    
                    break;
                
                case "GOST Hash":
                    
                    indicatorValue = normalizeGostHash(indicatorValue);
                    
                    break;
                
                case "IP Address":
                    
                    indicatorValue = normalizeIpAddress(indicatorValue);
                    
                    break;
                
                case "MD5":
                    
                    indicatorValue = normalizeMd5(indicatorValue);
                    
                    break;

                case "SHA-1":
                    
                    indicatorValue = normalizeSha1(indicatorValue);
                    
                    break;

                case "SHA-256":
                    
                    indicatorValue = normalizeSha256(indicatorValue);
                    
                    break;

                case "SHA-384":
                    
                    indicatorValue = normalizeSha384(indicatorValue);
                    
                    break;

                case "SHA-512":
                    
                    indicatorValue = normalizeSha512(indicatorValue);
                    
                    break;

                case "URL":
                    
                    indicatorValue = normalizeUrl(indicatorValue);
                    
                    break;

                case "URL Path":
                    
                    indicatorValue = normalizeUrlPath(indicatorValue);
                    
                    break;

            }
        
            //validate url indicators to check if type should be changed
            if (indicatorType.contentEquals("URL")){
                
                //check if trailing slash exists
                if (!indicatorValue.substring(indicatorValue.length() - 1).contentEquals("/")){
                
                    if(validateFqdn(indicatorValue)){
                    
                        indicatorType = "FQDN";
                    
                    }else if(validateIpAddress(indicatorValue)){
                    
                        indicatorType = "IP Address";
                    
                    }
                
                }else{
                                       
                    if(validateFqdn(indicatorValue.substring(0, indicatorValue.length() - 1))){
                        
                        indicatorType = "FQDN";
                        indicatorValue = indicatorValue.substring(0, indicatorValue.length() - 1);
                    
                    }else if(validateIpAddress(indicatorValue.substring(0, indicatorValue.length() - 1))){
                    
                        indicatorType = "IP Address";
                        indicatorValue = indicatorValue.substring(0, indicatorValue.length() - 1);
                        
                    }

                }
                
            }
            
        }
        
        //build and return string array
        String[] indicatorDetails = new String[2];
        indicatorDetails[0] = indicatorValue;
        indicatorDetails[1] = indicatorType;
        
        return indicatorDetails;
        
    }
    
    public static String normalizeCidrBlock(String indicatorValue){

        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove all spaces
        indicatorValue = indicatorValue.replaceAll("\\s+","");
        
        //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
        
        return indicatorValue;
        
    }

    public static String normalizeEmailAddress(String indicatorValue){
        
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove all spaces
        indicatorValue = indicatorValue.replaceAll("\\s+","");

        //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
        
        //refang neutered values
        indicatorValue = refangNeuteredValues(indicatorValue);
        
        return indicatorValue;
        
    }
    
    public static String normalizeFqdn(String indicatorValue){
        
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove all spaces
        indicatorValue = indicatorValue.replaceAll("\\s+","");

        //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
                
        //refang neutered values
        indicatorValue = refangNeuteredValues(indicatorValue);       

        //remove ftp(s) http(s)
        indicatorValue = indicatorValue.replaceFirst("ftp://","")
                .replaceFirst("ftps://","")
                .replaceFirst("http://","")
                .replaceFirst("https://","");

        //remove port value if it exists
        indicatorValue = StringUtils.substringBefore(indicatorValue, ":");     
        
        return indicatorValue;
        
    }
    
    public static String normalizeFuzzyHash(String indicatorValue){
        
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove all spaces
        indicatorValue = indicatorValue.replaceAll("\\s+","");

        //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
        
        return indicatorValue;
        
    }
    
    public static String normalizeGostHash(String indicatorValue){
        
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove all spaces
        indicatorValue = indicatorValue.replaceAll("\\s+","");

        //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
                        
        return indicatorValue;
        
    }

    public static String normalizeIpAddress(String indicatorValue){
                
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove all spaces
        indicatorValue = indicatorValue.replaceAll("\\s+","");

        //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
                        
        //refang neutered values
        indicatorValue = refangNeuteredValues(indicatorValue);

        //remove ftp(s) http(s)
        indicatorValue = indicatorValue.replaceFirst("ftp://","")
                .replaceFirst("ftps://","")
                .replaceFirst("http://","")
                .replaceFirst("https://","");

        //remove port value if it exists
        indicatorValue = StringUtils.substringBefore(indicatorValue, ":");
      
        //remove zero padding
        indicatorValue = indicatorValue.replaceAll("(^|[^0-9A-Fa-f])0+([0-9A-Fa-f])", "$1$2");
        
        return indicatorValue;
        
    }
    
    public static String normalizeMd5(String indicatorValue){
                
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove all spaces
        indicatorValue = indicatorValue.replaceAll("\\s+","");

        //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
                        
        return indicatorValue;
        
    }
    
    public static String normalizeSha1(String indicatorValue){
               
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove all spaces
        indicatorValue = indicatorValue.replaceAll("\\s+","");

        //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
                
        return indicatorValue;
        
    }
    
    public static String normalizeSha256(String indicatorValue){
        
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove all spaces
        indicatorValue = indicatorValue.replaceAll("\\s+","");

        //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
                
        return indicatorValue;
        
    }
    
    public static String normalizeSha384(String indicatorValue){
        
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove all spaces
        indicatorValue = indicatorValue.replaceAll("\\s+","");

       //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
                        
        return indicatorValue;
        
    }
    
    public static String normalizeSha512(String indicatorValue){
        
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove all spaces
        indicatorValue = indicatorValue.replaceAll("\\s+","");

       //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
                        
        return indicatorValue;
        
    }
    
    public static String normalizeUrl(String indicatorValue){
        
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
                        
        //refang neutered values
        indicatorValue = refangNeuteredValues(indicatorValue);

      //remove ftp(s) http(s)
        indicatorValue = indicatorValue.replaceFirst("ftp://","")
                .replaceFirst("ftps://","")
                .replaceFirst("http://","")
                .replaceFirst("https://","");

        //remove port value if it exists at the end of the url
        if(StringUtils.substringAfter(indicatorValue, ":").matches("[0-9]+")){
            
                indicatorValue = StringUtils.substringBefore(indicatorValue, ":");
           
        }
        
        //remove http parameters if they exist
        indicatorValue = StringUtils.substringBefore(indicatorValue, "?");
        
        return indicatorValue;
        
    }
    
    public static String normalizeUrlPath(String indicatorValue){
        
        //replace special characters
        indicatorValue = replaceSpecialCharacters(indicatorValue);
        
        //remove leading and trailing quotes
        indicatorValue = indicatorValue.replaceAll("^\"|\"$", "");
        indicatorValue = indicatorValue.replaceAll("^\'|\'$", "");
        
        return indicatorValue;
        
    }

    public static String replaceSpecialCharacters(String indicatorValue){
        
        //remove leading and trailing spaces
        indicatorValue = indicatorValue.trim();       
        
        //remove new lines
        indicatorValue = indicatorValue.replaceAll("(\\r|\\n|\\r\\n)+", "");

        //replace or remove special characters, if applicable
        /*indicatorValue = indicatorValue.replace("\\x96", "-")
        *    .replace("\\x145", "\'")
        *    .replace("\\x146", "\'")
        *    .replace("\\x147", "\"")
        *    .replace("\\x148", "\"")
        *    .replace("\\x151", "-")
        *    .replace("\\x255", " ");
        */
        
        //remove control characters
        indicatorValue = indicatorValue.replaceAll("[\u0000-\u001f]", "");

        //remove non-printable characters
        indicatorValue = indicatorValue.replaceAll("\\p{C}", "");

        //convert cp1252 characters
        try {
           
            indicatorValue = new String(indicatorValue.getBytes("windows-1252"), "utf-8");
        
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        return indicatorValue;
        
    }
    
    public static String refangNeuteredValues(String indicatorValue){
        
        //refang neutered values, if applicable
        indicatorValue = indicatorValue.replace("[.]",".")
            .replace("[dot]",".")
            .replace("[d]",".")
            .replace("-dot-",".")
            .replace("_dot_",".")
            .replace("hxxp://","http://")
            .replace("hxxx://","http://")
            .replace("hxxps://","https://")
            .replace("hxxxs://","https://")
            .replace("[hxxp]://","http://")
            .replace("hxtp://","http://")
            .replace("htxp://","http://")
            .replace("hxtps://","https://")
            .replace("htxps://","https://")
            .replace("[http]://","http://")
            .replace("[http://]","http://")
            .replace("[https]","https://")
            .replace("[https://]","https://")
            .replace("[at]","@")
            .replace("-at-","@")
            .replace("_at_","@")
            .replace("-@-","@")
            .replace("_@_","@")
            .replace("[@]","@")
            .replace("[www]","www");
        
        return indicatorValue;
        
    }

}
	