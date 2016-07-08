package config;

public class UtilConfig {	

	//SendEmail configuration
	public static String mailServer="smtp-relay.gmail.com:587";
	public static String from = "tests@threatq.com";
	public static String password = "";
	public static String[] to ={"kristin.mcwade@threatq.com"};
	public static String subject = "Test Report";
	public static String messageBody ="TestMessage";
	public static String attachmentPath=System.getProperty("user.dir")+"/src/screenshots/";
	public static String attachmentName="Error.jpg";
	
	//MySQL configuration
	public static Object [] mySqlConfig (String server) {
		
	    //local variables
	    String mySqlDriver=null;
	    String mySqlUrl = null;
	    String mySqlUserName = null;
	    String mySqlPassword = null;
		
	    switch (server) {
		
	        case "v1-test01.threatq.com":
	            mySqlDriver="com.mysql.jdbc.Driver";
	            mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient";
	            mySqlUserName = "threatqa";
	            mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
	            break;
			
	        case "v1-qa01.threatq.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;
                
            case "v1-qa02.threatq.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;
                
            case "v1-staging01.threatq.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;
            
            case "v2-test01.threatq.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient2";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;
                
            case "v2-test02.threatq.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient2";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;
                
            case "v2-qa01.threatq.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient2";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;
			
            case "v2-qa02.threatq.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient2";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;
                
            case "v2-qa03.threatq.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient2";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;
                
            case "v2-qa04.threatq.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient2";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;
                
            case "v2-staging01.threatq.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient2";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;
            
            case "www.threatqdev.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient2";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;

            case "ova-test3.threatq.com":
                mySqlDriver="com.mysql.jdbc.Driver";
                mySqlUrl = "jdbc:mysql://"+server+":3306/threatquotient2";
                mySqlUserName = "threatqa";
                mySqlPassword = "9wfLnYhhsmpxAGfiPEaciPsRMHYifcbwYGKqoxXeD6pRkcFMKT";
                break;
           
	    }
	
	    // return MySQL properties for specified server
	    Object[] mySqlProperties = new Object[4];
	    mySqlProperties[0] = mySqlDriver;
	    mySqlProperties[1] = mySqlUrl;
	    mySqlProperties[2] = mySqlUserName;
	    mySqlProperties[3] = mySqlPassword;
	    return mySqlProperties;

	}

}	
