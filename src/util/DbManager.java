package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import config.UtilConfig;
import core.TestEngine;

public class DbManager {

	private static Connection con = null;
	
	public void setMysqlDbConnection() throws SQLException, ClassNotFoundException, AddressException, MessagingException{
	
		//get mySql properties
		Object mySqlProperties [] = UtilConfig.mySqlConfig(TestEngine.server);
		String mySqlDriver = (String) mySqlProperties[0];
		String mySqlUrl = (String) mySqlProperties[1];
		String mySqlUserName = (String) mySqlProperties[2];
		String mySqlPassword = (String) mySqlProperties[3];
		
		try{
        
			Class.forName (mySqlDriver).newInstance ();
			con = DriverManager.getConnection (mySqlUrl, mySqlUserName, mySqlPassword);
			if(!con.isClosed())
				System.out.println("Successfully connected to MySQL server");
			
		}catch (Exception e){
			
			System.err.println ("Cannot connect to database server");
			SendEmail mail = new SendEmail();
			mail.sendMail(UtilConfig.mailServer, UtilConfig.from, UtilConfig.to, UtilConfig.subject+" - (Script failed with Error, database connection not established)", UtilConfig.messageBody, UtilConfig.attachmentPath, UtilConfig.attachmentName);
		
		}
   
	}
		
	public List<String> getMysqlQuery(String query) throws SQLException{
			
		Statement stmt = (Statement) con.createStatement();
		ResultSet result = (ResultSet) stmt.executeQuery(query);
		List<String> values1 = new ArrayList<String>();
		while(result.next()){
			
			values1.add(result.getString(1));
			
		}
		
		return values1;
	
	}
	
    public void tearDown() throws SQLException {
        // Close DB connection
        if (con != null) {
        con.close();
        
     }
 }

}
