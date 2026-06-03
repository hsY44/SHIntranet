/*===================
 	DBConn.java
======================*/

package com.sh.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConn
{
	private static Connection conn = null;
	private static final String URL = "jdbc:oracle:thin:@//YOUR_DB_HOST:1521/xe";
	private static String USER = "YOUR_DB_USER";
	private static String PASSWORD = "YOUR_DB_PASSWORD";
		
	private DBConn() {}
	
	public static Connection getConnection()
	{
		
		try
		{
			if (conn == null || conn.isClosed())
			{
				Class.forName("oracle.jdbc.driver.OracleDriver");
				conn = DriverManager.getConnection(URL, USER, PASSWORD);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return conn;
	}
	
	public static Connection getConnection(String url, String user, String pwd, String internal_logon)
	{
		if (conn==null)
		{
			try
			{
				Properties info = new Properties();
				info.put("user", user);
				info.put("password", pwd);
				info.put("internal_logon", internal_logon);
				
				Class.forName("oracle.jdbc.driver.OracleDriver");
				conn = DriverManager.getConnection(url,info);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return conn;
	}
	
	public static void close()
	{
		if(conn==null)
			return;
		
			try
			{
				if (!conn.isClosed())
				{
					conn.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		
		conn = null;
	}
}
