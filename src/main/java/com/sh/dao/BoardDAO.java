package com.sh.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.sh.util.DBUtil;

import jakarta.servlet.http.Part;

import com.sh.dto.BoardDTO;
import com.sh.dto.BoardDropDTO;
import com.sh.dto.BoardFileDTO;
import com.sh.util.DBConn;

public class BoardDAO
{
	private Connection conn = DBConn.getConnection();
	
	// 게시물 번호 최대값
	public int getMaxNum()
	{
		int result = 0;
		Statement stmt = null;
		ResultSet rs = null;
		
		try
		{
			String sql = "SELECT NVL(MAX(BOARD_NO), 0) AS MAX FROM BOARD";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			if (rs.next())
				result = rs.getInt("MAX");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		DBUtil.close(rs);
		DBUtil.close(stmt);
	
		return result;
	}
	// 전체 데이터 갯수
	public int dataCount(String type)
	{
		int result = 0;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try
		{
			sql = """
					SELECT COUNT(*) AS COUNT FROM BOARD B
					WHERE B.TYPE_CD = ?
					AND NOT EXISTS 
					(SELECT 1 
					 FROM BOARD_DROP_LOG D 
					 WHERE D.BOARD_NO = B.BOARD_NO)
					""";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, type);
			rs = pstmt.executeQuery();
			
			if (rs.next())
			{
				result = rs.getInt("COUNT");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		DBUtil.close(rs);
		DBUtil.close(pstmt);
		
		return result;
	}
	
	// 검색에서 데이터 갯수
	public int dataCount(String schType, String kwd, String type)
	{
		int result = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		try
		{
			sql = """
					SELECT COUNT(*) AS COUNT
					FROM BOARD B
					JOIN EMP E ON B.EMP_CD = E.EMP_CD
					WHERE B.TYPE_CD = ?
					AND NOT EXISTS 
					(SELECT 1 
					 FROM BOARD_DROP_LOG D 
					 WHERE D.BOARD_NO = B.BOARD_NO)
					""";
			
			// 제목+내용
			if (schType.equals("all"))
				sql += " AND (INSTR(UPPER(TITLE), UPPER(?)) >= 1 OR INSTR(UPPER(CONTENTS), UPPER(?)) >= 1) ";
			else
				sql += " AND INSTR(UPPER("+schType+"), UPPER(?)) >=1";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, type);
			pstmt.setString(2, kwd);
			if (schType.equals("all"))
				pstmt.setString(3, kwd);
			
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
	
	// 게시물 목록
	public List<BoardDTO> getList(int offset, int size, String type)
	{
		List<BoardDTO> result = new ArrayList<BoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try
		{
			sb.append("SELECT B.BOARD_NO AS NUM, E.EMP_NAME AS NAME, B.TITLE AS TITLE"
					+ ", TO_CHAR(B.REG_DT, 'YYYY-MM-DD') AS REG_DT, B.TYPE_CD AS TYPE"
					+ ", T.TYPE_NAME AS TYPE_NAME"
					+ " FROM BOARD B"
					+ " JOIN EMP E ON B.EMP_CD = E.EMP_CD"
					+ " JOIN BOARD_TYPE T ON B.TYPE_CD = T.TYPE_CD"
					+ " WHERE B.TYPE_CD = ?"
					+ " AND NOT EXISTS ("
					+ " SELECT 1 FROM BOARD_DROP_LOG D WHERE D.BOARD_NO = B.BOARD_NO)"
					+ " ORDER BY BOARD_NO DESC"
					+ " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			

			pstmt.setString(1, type);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
			
			rs = pstmt.executeQuery();
			
			while (rs.next())
			{
				BoardDTO dto = new BoardDTO();
				
				dto.setNum(rs.getInt("NUM"));
				dto.setName(rs.getString("NAME"));
				dto.setTypeName(rs.getString("TYPE_NAME"));
				dto.setTitle(rs.getString("TITLE"));
				dto.setRegDate(rs.getString("REG_DT"));
				dto.setType(rs.getString("TYPE"));
				
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
	
	// 게시물 검색결과 목록
	public List<BoardDTO> getList(int offset, int size, String type, String schType, String kwd)
	{
		List<BoardDTO> result = new ArrayList<BoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try
		{
			sb.append("SELECT B.BOARD_NO AS NUM, E.EMP_NAME AS EMP_NAME, B.TITLE AS TITLE"
					+ ", TO_CHAR(B.REG_DT, 'YYYY-MM-DD') AS REG_DT, B.TYPE_CD AS TYPE"
					+ ", T.TYPE_NAME AS TYPE_NAME"
					+ " FROM BOARD B"
					+ " JOIN EMP E ON B.EMP_CD = E.EMP_CD"
					+ " JOIN BOARD_TYPE T ON B.TYPE_CD = T.TYPE_CD"
					+ " WHERE B.TYPE_CD = ?");

			if (schType.equals("all"))
				sb.append(" AND (INSTR(UPPER(B.TITLE), UPPER(?)) >= 1 OR INSTR(UPPER(B.CONTENTS), UPPER(?)) >= 1)");
			else
				sb.append(" AND (INSTR(UPPER("+schType+ "), UPPER(?)) >= 1)");

			sb.append(" AND NOT EXISTS ("
					+ " SELECT 1 FROM BOARD_DROP_LOG D WHERE D.BOARD_NO = B.BOARD_NO)"
					+ " ORDER BY B.BOARD_NO DESC"
					+ " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			if (schType.equals("all"))
			{
				pstmt.setString(1, type);
				pstmt.setString(2, kwd);
				pstmt.setString(3, kwd);
				pstmt.setInt(4, offset);
				pstmt.setInt(5, size);
			}
			else 
			{
				pstmt.setString(1, type);
				pstmt.setString(2, kwd);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, size);
			}
			
			rs = pstmt.executeQuery();
			
			while (rs.next())
			{
				BoardDTO dto = new BoardDTO();
				
				dto.setNum(rs.getInt("NUM"));
				dto.setName(rs.getString("EMP_NAME"));
				dto.setTypeName(rs.getString("TYPE_NAME"));
				dto.setTitle(rs.getString("TITLE"));
				dto.setRegDate(rs.getString("REG_DT"));
				dto.setType(rs.getString("TYPE"));
				
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
	
	// 게시물 열람
	public BoardDTO getReadData(int num)
	{
		BoardDTO result = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try
		{
			sql = """
					SELECT B.BOARD_NO AS NUM, E.EMP_NAME AS NAME
					, B.TITLE AS TITLE, B.CONTENTS AS CONTENTS, B.EMP_CD AS EMPCD
					, TO_CHAR(B.REG_DT, 'YYYY-MM-DD') AS REG_DT, B.TYPE_CD AS TYPE
					FROM BOARD B
					JOIN EMP E ON B.EMP_CD = E.EMP_CD
					WHERE BOARD_NO = ?
				  """;
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			
			if (rs.next())
			{
				result = new BoardDTO();
				result.setNum(rs.getInt("NUM"));
				result.setName(rs.getString("NAME"));
				result.setTitle(rs.getString("TITLE"));
				result.setContents(rs.getString("CONTENTS"));
				result.setRegDate(rs.getString("REG_DT"));
				result.setType(rs.getString("TYPE"));
				result.setEmpCd(rs.getString("EMPCD"));
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
	
	// 게시물 수정
	public void updateBoard(BoardDTO dto)
	{
		PreparedStatement pstmt = null;
		
		String sql;
		
		try
		{
			sql = """
					UPDATE BOARD
					SET TITLE = ?, CONTENTS = ?
					WHERE BOARD_NO = ?
					""";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getTitle());
			pstmt.setString(2, dto.getContents());
			pstmt.setInt(3, dto.getNum());
			
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
	
	// 게시물 삭제이력
	public void deleteBoard(BoardDropDTO dto)
	{
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			
			sql = """
					INSERT INTO BOARD_DROP_LOG (LOG_NO, BOARD_NO, EMP_CD, DROP_DT)
					VALUES (BOARD_DROP_LOG_SEQ.NEXTVAL, ?, ?, SYSDATE)
					""";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getBoardNum());
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
	
	// 게시판 글쓰기
	public void insertBoard(BoardDTO dto, Collection<Part> parts)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int boardNo = 0;
		
		try 
		{
			conn.setAutoCommit(false);
			
			sql = """
					INSERT INTO BOARD(BOARD_NO, EMP_CD, TITLE, CONTENTS, TYPE_CD, REG_DT)
                    VALUES(BOARD_SEQ.NEXTVAL, ?, ?, ?, ?, SYSDATE)
					""";
			
			String[] generatedColumns = {"BOARD_NO"};
			pstmt = conn.prepareStatement(sql, generatedColumns);
			
			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getTitle());
			pstmt.setString(3, dto.getContents());
			pstmt.setString(4, dto.getType());
			
			pstmt.executeUpdate();
			
			// 생성된 게시글 번호 가져오기
	        rs = pstmt.getGeneratedKeys();
	        if (rs.next()) {
	            boardNo = rs.getInt(1);
	        }

	        if (parts != null && boardNo > 0) 
	        {
	            for (Part part : parts) 
	            {
	                String originalFileName = part.getSubmittedFileName();
	                
	                if (originalFileName != null && !originalFileName.trim().isEmpty()) 
	                {
	                    String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
	                    String saveFileName = UUID.randomUUID().toString() + ext;
	                    String path = "C:/savefile/" + saveFileName;
	                    
	                    int fileNo = insertFile(dto.getEmpCd(), originalFileName, saveFileName, path);
	                    
	                    if (fileNo != -1) 
	                    {
	                        insertBoardFileLog(boardNo, fileNo, "C");
	                        part.write(path);
	                    } else {
	                        throw new Exception("파일 마스터 정보(sh_file) 등록 중 오류 발생");
	                    }
	                }
	            }
	        }

	        conn.commit();

	    } catch (Exception e) {
	        try 
	        { 
	        	if (conn != null) conn.rollback();
	        } catch (Exception e2) {}
	        e.printStackTrace();
	    } finally {
	        DBUtil.close(rs);
	        DBUtil.close(pstmt);
	        try { conn.setAutoCommit(true); } catch (Exception e) {}
	    }
	}
			
		
	// 게시글에 첨부파일 추가
	public int insertFile(String empCd, String originalFileName, String saveFileName, String savePath) {
	    int result = -1;
	    CallableStatement cstmt = null;
	    try {
	        String sql = "{call PRC_FILE_INSERT(?,?,?,?,?)}";
	        cstmt = conn.prepareCall(sql);
	        cstmt.setString(1, empCd);
	        cstmt.setString(2, originalFileName);
	        cstmt.setString(3, saveFileName);
	        cstmt.setString(4, savePath);
	        cstmt.registerOutParameter(5, java.sql.Types.INTEGER);
	        
	        cstmt.execute();
	        result = cstmt.getInt(5);
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        DBUtil.close(cstmt);
	    }
	    return result;
	}

	// 게시판 파일 로그 매핑 (등록: 'C', 삭제: 'D' 처리)
	public void insertBoardFileLog(int boardNo, int fileNo, String issueType) throws SQLException {
	    PreparedStatement pstmt = null;
	    String sql = "INSERT INTO BOARD_FILE_LOG (LOG_NO, BOARD_NO, FILE_NO, ISSUE_TYPE, ISSUE_DATE) "
	               + "VALUES (BOARD_FILE_LOG_SEQ.NEXTVAL, ?, ?, ?, SYSDATE)";
	    try 
	    {
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setInt(1, boardNo);
	        pstmt.setInt(2, fileNo);
	        pstmt.setString(3, issueType); // 등록시 'C', 삭제시 'D'
	        
	        pstmt.executeUpdate();
	        
	    } finally {
	        DBUtil.close(pstmt);
	    }
	}
	
	// 첨부파일 리스트
	public List<BoardFileDTO> filelist(int boardNum)
	{
		List<BoardFileDTO> result = new ArrayList<BoardFileDTO>();
	
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String sql;
	    
	    try
	    {
	        sql = """
				  SELECT 
					    A.FILE_NO AS FILE_NO
					  , B.ORIGIN_NAME AS ORIGIN_NAME
					  , B.SAVED_NAME AS SAVED_NAME
					  , B.PATH AS PATH
					  , A.BOARD_NO AS BOARD_NO
					FROM BOARD_FILE_LOG A
					JOIN SH_FILE B ON A.FILE_NO = B.FILE_NO
					WHERE BOARD_NO = ?
					  AND A.ISSUE_TYPE = 'C'
					  AND A.FILE_NO NOT IN (  
		                  SELECT FILE_NO FROM BOARD_FILE_LOG 
		                  WHERE BOARD_NO = ? AND ISSUE_TYPE = 'D'
		                  )
		            ORDER BY A.LOG_NO ASC 
				  """;
	        
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setInt(1, boardNum);
	        pstmt.setInt(2, boardNum);
	
	        rs = pstmt.executeQuery();
	
	        while (rs.next())
	        {
	        	BoardFileDTO dto = new BoardFileDTO();
	        	
	        	dto.setFileNo(rs.getInt("FILE_NO"));
	            dto.setOriginName(rs.getString("ORIGIN_NAME"));
	            dto.setSavedName(rs.getString("SAVED_NAME"));
	            dto.setPath(rs.getString("PATH"));
	            dto.setBoardNum(rs.getInt("BOARD_NO"));
	            
	            result.add(dto);
	        }
	
	    } catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	    finally {
	        DBUtil.close(rs);
	        DBUtil.close(pstmt);
	    }
	
	    return result;
	}
	

	
	
	
}
