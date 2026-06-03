package com.sh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sh.dto.BoardFileDTO;
import com.sh.dto.DocFileDTO;
import com.sh.util.DBConn;
import com.sh.util.DBUtil;

public class FileDownloadDAO
{
	Connection conn = DBConn.getConnection();
	
	public DocFileDTO getFile(String docCd)
	{
		DocFileDTO dto = new DocFileDTO();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			String sql = "SELECT ORIGIN_NAME , PATH FROM SH_FILE WHERE FILE_NO = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, Integer.parseInt(docCd));
			
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				dto.setOriginName(rs.getString("ORIGIN_NAME"));
				dto.setPath(rs.getString("PATH"));
			}
		}
		catch (Exception e)
		{
			dto = null;
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
		return dto;
	}
	
	// 게시판 첨부파일
	public BoardFileDTO getBoardFile(int fileNo) 
	{
	    BoardFileDTO dto = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
	    try
	    {
	        String sql = "SELECT ORIGIN_NAME, PATH FROM SH_FILE WHERE FILE_NO = ?";
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setInt(1, fileNo);
	        rs = pstmt.executeQuery();
	        
	        if(rs.next()) {
	            dto = new BoardFileDTO();
	            dto.setOriginName(rs.getString("ORIGIN_NAME"));
	            dto.setPath(rs.getString("PATH"));
	        }
	    } catch (Exception e) 
	    {
	        e.printStackTrace();
	    } finally {
	        DBUtil.close(rs);
	        DBUtil.close(pstmt);
	    }
	    return dto;
	}
	
	
}
