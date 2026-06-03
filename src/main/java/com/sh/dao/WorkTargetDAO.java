package com.sh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.sh.util.DBConn;

public class WorkTargetDAO
{
	private Connection conn = DBConn.getConnection();
	
	public List<String> getTarget(String docCd)
	{
		List<String> result = new ArrayList<String>();
		
		PreparedStatement pstmt = null;
		
		ResultSet rs = null;
		
		try
		{
			String sql = "SELECT A.EMP_CD , B.EMP_NAME, C.POSITION_NAME "
					+ " FROM WORK_TARGET A "
					+ " JOIN EMP B "
					+ " ON A.EMP_CD = B.EMP_CD "
					+ " JOIN EMP_POSITION C "
					+ " ON B.POSITION_CD = C.POSITION_CD "
					+ " WHERE DOC_CD = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, docCd);
			
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				StringBuilder sb = new StringBuilder();
				
				sb.append(rs.getString("EMP_CD"));
				sb.append(" ");
				sb.append(rs.getString("EMP_NAME"));
				sb.append(" ");
				sb.append(rs.getString("POSITION_NAME"));
				
				result.add(sb.toString());
			}
					
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		
		finally 
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}
				
				if(pstmt != null)
				{
					pstmt.close();
				}
			} 
			catch (Exception e)
			{
				System.out.println(e.toString());
			}
		}
		
		return result;
	}
}
