package com.sh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sh.dto.BoardDTO;
import com.sh.dto.CommentDTO;
import com.sh.dto.CommentDropDTO;
import com.sh.util.DBConn;
import com.sh.util.DBUtil;

public class CommentDAO
{
	private Connection conn = DBConn.getConnection();
	
	// 전체 데이터 갯수
	public int dataCount()
	{
		int result = 0;
		Statement stmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try
		{
			sql = """
					SELECT COUNT(*) AS COUNT FROM BOARD_COMMENT C
					WHERE NOT EXISTS 
					(SELECT 1 
					 FROM COMMENT_DROP_LOG D 
					 WHERE D.COMMENT_NO = C.COMMENT_NO)
				  """;
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			if (rs.next())
			{
				result = rs.getInt("COUNT");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		DBUtil.close(rs);
		DBUtil.close(stmt);
		
		return result;
	}
	
	// 검색에서 데이터 갯수
	public int dataCount(String schType, String kwd)
	{
		int result = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		try
		{
			sql = """
					SELECT COUNT(*) AS COUNT
					FROM BOARD_COMMENT C
					JOIN EMP E ON C.EMP_CD = E.EMP_CD
					WHERE NOT EXISTS 
					(SELECT 1 
					 FROM COMMENT_DROP_LOG D 
					 WHERE D.COMMENT_NO = C.COMMENT_NO)
					""";
			
			if (kwd != null && kwd.length() != 0) {
		        if (schType.equals("emp_name")) {
		            sql += " AND INSTR(UPPER(E.EMP_NAME), UPPER(?)) >= 1 ";
		        } else if (schType.equals("contents")) {
		            sql += " AND INSTR(UPPER(C.CONTENTS), UPPER(?)) >= 1 ";
		        }
		    }
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, kwd);
			
			rs = pstmt.executeQuery();
			
			if (rs.next())
				result = rs.getInt("COUNT");
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		DBUtil.close(rs);
		DBUtil.close(pstmt);
		
		return result;
	}
	
	// 댓글 리스트
	public List<CommentDTO> getList(int boardNum)
	{
		List<CommentDTO> result = new ArrayList<CommentDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try
		{
			sql = """
					SELECT C.COMMENT_NO AS NUM, C.BOARD_NO AS BOARDNUM
				        , E.EMP_NAME AS NAME, C.CONTENTS AS CONTENTS
				        , TO_CHAR(C.REG_DT, 'YYYY-MM-DD HH24:MI') AS REG_DT
				        , C.EMP_CD AS EMPCD
				        , NVL(D.LOG_NO, 0) AS DROPNUM
				    FROM BOARD_COMMENT C
				    JOIN EMP E ON E.EMP_CD = C.EMP_CD
				    LEFT JOIN COMMENT_DROP_LOG D ON C.COMMENT_NO = D.COMMENT_NO
				    WHERE C.BOARD_NO = ?
				    ORDER BY C.REG_DT ASC
					""";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, boardNum);
			rs = pstmt.executeQuery();
			
			while (rs.next())
			{
				CommentDTO dto = new CommentDTO();
				dto.setNum(rs.getInt("NUM"));
				dto.setBoardNum(rs.getInt("BOARDNUM"));
				dto.setName(rs.getString("NAME"));
				dto.setContents(rs.getString("CONTENTS"));
				dto.setRegDate(rs.getString("REG_DT"));
				dto.setEmpCd(rs.getString("EMPCD"));
				dto.setDropNum(rs.getInt("DROPNUM"));
				
				result.add(dto);
			}
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
	
		return result;
	}
	
	// 관리자용 댓글 리스트
	public List<CommentDTO> getAdminList(int offset, int size)
	{
		List<CommentDTO> result = new ArrayList<CommentDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try
		{
			sql = """
					SELECT C.COMMENT_NO AS NUM, C.BOARD_NO AS BOARDNUM
						, E.EMP_NAME AS NAME, C.CONTENTS AS CONTENTS
						, TO_CHAR(C.REG_DT, 'YYYY-MM-DD HH24:MI') AS REG_DT
						, C.EMP_CD AS EMPCD
					FROM BOARD_COMMENT C
					JOIN EMP E ON E.EMP_CD = C.EMP_CD
					WHERE NOT EXISTS (
				    SELECT 1 
				    FROM COMMENT_DROP_LOG D 
				    WHERE D.COMMENT_NO = C.COMMENT_NO)
					ORDER BY C.REG_DT DESC
					OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
					""";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			while (rs.next())
			{
				CommentDTO dto = new CommentDTO();
				dto.setNum(rs.getInt("NUM"));
				dto.setBoardNum(rs.getInt("BOARDNUM"));
				dto.setName(rs.getString("NAME"));
				dto.setContents(rs.getString("CONTENTS"));
				dto.setRegDate(rs.getString("REG_DT"));
				dto.setEmpCd(rs.getString("EMPCD"));
				
				result.add(dto);
			}
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return result;
	}
	
	// 관리자용 댓글 검색결과 리스트
	public List<CommentDTO> getAdminList(int offset, int size, String schType, String kwd)
	{
		List<CommentDTO> result = new ArrayList<CommentDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try
		{
			sql = """
					SELECT C.COMMENT_NO AS NUM, C.BOARD_NO AS BOARDNUM
						, E.EMP_NAME AS EMP_NAME, C.CONTENTS AS CONTENTS
						, TO_CHAR(C.REG_DT, 'YYYY-MM-DD HH24:MI') AS REG_DT
						, C.EMP_CD AS EMPCD
					FROM BOARD_COMMENT C
					JOIN EMP E ON E.EMP_CD = C.EMP_CD
					AND NOT EXISTS (
				    SELECT 1 
				    FROM COMMENT_DROP_LOG D 
				    WHERE D.COMMENT_NO = C.COMMENT_NO)
					""";

			sql += " AND (INSTR(UPPER("+schType+ "), UPPER(?)) >= 1)"
					+ " ORDER BY C.REG_DT DESC"
					+ " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
			
			
			pstmt = conn.prepareStatement(sql);
			

			pstmt.setString(1, kwd);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
	
			
			rs = pstmt.executeQuery();
			
			while (rs.next())
			{
				CommentDTO dto = new CommentDTO();
				
				dto.setNum(rs.getInt("NUM"));
				dto.setBoardNum(rs.getInt("BOARDNUM"));
				dto.setName(rs.getString("EMP_NAME"));
				dto.setContents(rs.getString("CONTENTS"));
				dto.setRegDate(rs.getString("REG_DT"));
				dto.setEmpCd(rs.getString("EMPCD"));
				
				result.add(dto);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		DBUtil.close(rs);
		DBUtil.close(pstmt);
		
		return result;
	}
	
	// 댓글 등록
	public void insertComment(CommentDTO dto)
	{
		PreparedStatement pstmt = null;
		String sql;
		
		try
		{
			sql = """
					INSERT INTO BOARD_COMMENT(COMMENT_NO, BOARD_NO, EMP_CD, CONTENTS, REG_DT)
					VALUES(BOARD_COMMENT_SEQ.NEXTVAL, ?, ?, ?, SYSDATE)
					""";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getBoardNum());
			pstmt.setString(2, dto.getName());
			pstmt.setString(3, dto.getContents());
			
			pstmt.executeUpdate();
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			DBUtil.close(pstmt);
		}
	}
	
	// 댓글 삭제이력
	public void deleteComment(CommentDropDTO dto)
	{
		PreparedStatement pstmt = null;
		String sql;
		try {
			
			sql = """
					INSERT INTO COMMENT_DROP_LOG (LOG_NO, COMMENT_NO, EMP_CD, DROP_DT)
					VALUES (COMMENT_DROP_LOG_SEQ.NEXTVAL, ?, ?, SYSDATE)
					""";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getCommentNum());
			pstmt.setString(2, dto.getEmpCd());
			
			pstmt.executeUpdate();
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			DBUtil.close(pstmt);
		}
		
	}
}
