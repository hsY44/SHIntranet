package com.sh.dao;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Collection;
import java.util.UUID;

import com.sh.dto.AprvDTO;
import com.sh.util.DBConn;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;

public class AprvDAO
{
	private Connection conn = DBConn.getConnection();
	
	/*
	 * 결재 문서 등록하기
	 * 등록된 문서 코드를 반환
	 */
	public String aprvDocInsert(AprvDTO dto, Collection<Part> parts)
	{
		CallableStatement cstmt = null;
		
		String result = "";
		
		PreparedStatement pstmt = null;
		
		try
		{
			conn.setAutoCommit(false);
			
			String sql = "{call PRC_APRV_DOC_INSERT(?,?,?,?,?,?,?)}";
			
			cstmt = conn.prepareCall(sql);
			
			cstmt.setString(1, dto.getParentCd());
			cstmt.setString(2, dto.getDocTypeCd());
			cstmt.setString(3, dto.getWorkCd());
			cstmt.setString(4, dto.getEmpCd());
			cstmt.setString(5, dto.getTitle());
			cstmt.setString(6, dto.getContent());
			cstmt.registerOutParameter(7, Types.VARCHAR);
			
			cstmt.execute();
			
			result = cstmt.getString(7).trim();
			
			// System.out.println(result);
			
			if(result == null || result.equals(""))
			{
				throw new Exception("문서 등록 실패");
			}
			
			String sql2 = "INSERT INTO DOC_FILE VALUES(DOC_FILE_SEQ.NEXTVAL,?,?)";
			
			pstmt = conn.prepareStatement(sql2);
			
			pstmt.setString(1, result);
			
			/*
			 * 파라미터로 넘겨받은 parts 에 있는 모든 데이터를 하나씩 체크함
			 */
			for(Part part : parts)
			{
				String originalFileName = part.getSubmittedFileName();
				
				// 만약 첨부파일이라면 해당 데이터는 null이 아님
				if(originalFileName != null && !originalFileName.trim().equals(""))
				{
					// txt / jpg ....
					String type = originalFileName.substring(originalFileName.lastIndexOf("."));
					
					// a1b2c3 .... z9 + txt
					String saveFileName = UUID.randomUUID().toString() + type;
					
					// String saveDir = context.getRealPath("/WEB-INF/file");
					
					String saveDir = "C:" + File.separator + "savefile";
					
					File dir = new File(saveDir);
					
					if(!dir.exists())
					{
						dir.mkdirs();
					}
					
					String path = saveDir + File.separator + saveFileName;
					
					int fileNo = insertFile(dto.getEmpCd(), originalFileName, saveFileName, path);
					
					if(fileNo == -1)
					{
						throw new Exception("파일 등록 실패");
					}
					
					pstmt.setInt(2, fileNo);
					
					//System.out.print(result);
					//System.out.print(fileNo);
					
					int res = pstmt.executeUpdate();
						
					if(res <= 0)
					{
						throw new Exception("문서 파일 등록 실패");
					}
					
					/*
					 * 실제 경로에 저장
					 * 다만 현재 구조 상 여러개의 첨부파일을 등록하는 경우
					 * 1번 파일은 등록이 되었으나 2번 파일이 등록 실패하는 경우
					 * DB 에서는 전부 rollback 되지만 
					 * 실제 물리적 파일로 저장된 1번 파일의 경우는 삭제되지 않는다
					 */
					part.write(path);
				}
			}
			
			/*
			 * 모든 작업이 문제 없이 동작했다면 커밋
			 */
			conn.commit();
		} 
		catch (Exception e)
		{
			result = "";
			System.out.println(e.toString());
			
			try
			{
				conn.rollback();
			}
			catch (Exception e2)
			{
				System.out.println(e2.toString());
			}
		}
		
		finally 
		{
			try
			{
				conn.setAutoCommit(true);
				
				if(pstmt != null)
				{
					pstmt.close();
				}
				
				if(cstmt != null)
				{
					cstmt.close();
				}
			} 
			catch (Exception e)
			{
				System.out.println(e.toString());
			}
		}
		
		return result;
	}
	
	/*
	 * 업무 지시 문서를 등록하는 메소드
	 */
	public String workTargetDocInsert(AprvDTO dto, Collection<Part> parts)
	{
		String docCd = "";
		
		PreparedStatement pstmt =  null;
		
		PreparedStatement pstmt2 = null;
		
		try
		{
			// auto 커밋 비활성화
			conn.setAutoCommit(false);
			
			docCd = docInsert(dto).trim();
			
			// System.out.print("등록 문서 : " + docCd);
			// -- DC0001
			
			// 문서 등록 실패 시
			if(docCd.equals(""))
			{
				throw new Exception("문서 등록 실패");
			}
			
			String sql = "INSERT INTO WORK_TARGET VALUES(WORK_TARGET_SEQ.NEXTVAL,?,?)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, docCd);
			
			String[] targetCd = dto.getWorkTargetEmpCd();
			
			if(targetCd == null || targetCd.length == 0)
			{
				throw new Exception("대상자 없음");
			}
			
			for(String empCd : targetCd)
			{
				pstmt.setString(2, empCd);
				
				if(pstmt.executeUpdate() <= 0)
				{
					throw new Exception("업무 지시 생성 실패 대상자 : " + empCd);
				}
			}
			
			String sql2 = "INSERT INTO DOC_FILE VALUES(DOC_FILE_SEQ.NEXTVAL,?,?)";
			
			pstmt2 = conn.prepareStatement(sql2);
			
			pstmt2.setString(1, docCd);
			
			for(Part part : parts)
			{
				String originalFileName = part.getSubmittedFileName();
				
				if(originalFileName != null && !originalFileName.trim().equals(""))
				{
					String emp = originalFileName.substring(originalFileName.lastIndexOf("."));
					String saveFileName = UUID.randomUUID().toString() + emp;
					String path = "C:/savefile/" + saveFileName;
					
					int fileNo = insertFile(dto.getEmpCd(), originalFileName, saveFileName, path);
					
					if(fileNo == -1)
					{
						throw new Exception("파일 등록 실패");
					}
					
					pstmt2.setInt(2, fileNo);
					
					int res = pstmt2.executeUpdate();
						
					if(res <= 0)
					{
						throw new Exception("문서 파일 등록 실패");
					}
					
					File dir = new File("C:/savefile");
					
					if(!dir.exists())
					{
						dir.mkdir();
					}
					
					part.write(path);
				}
			}
			
			conn.commit();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			
			try
			{
				docCd = "";
				conn.rollback();
			}
			catch (Exception e2)
			{
				System.out.println(e2.toString());
			}
		}
		finally 
		{
			try
			{
				conn.setAutoCommit(true);
				
				if(pstmt != null)
				{
					pstmt.close();
				}
				
				if(pstmt2 != null)
				{
					pstmt2.close();
				}
			}
			catch (Exception e)
			{
				System.out.println(e.toString());
			}
		}
		
		return docCd;
	}
	
	/*
	 * 문서 등록하는 메소드
	 */
	private String docInsert(AprvDTO dto)
	{
		String result = "";
		
		CallableStatement cstmt = null;
		
		try
		{
			String sql = "{call PRC_DOC_INSERT(?,?,?,?,?,?,?)}";
			
			cstmt = conn.prepareCall(sql);
			
			cstmt.setString(1, dto.getParentCd());
			cstmt.setString(2, dto.getDocTypeCd());
			cstmt.setString(3, dto.getWorkCd());
			cstmt.setString(4, dto.getEmpCd());
			cstmt.setString(5, dto.getTitle());
			cstmt.setString(6, dto.getContent());
			cstmt.registerOutParameter(7, Types.VARCHAR);
			
			cstmt.execute();
			
			result = cstmt.getString(7);
			
			
		}
		catch (Exception e)
		{
			result = "";
			System.out.println(e.toString());
		}
		
		finally 
		{
			try
			{
				if(cstmt != null)
				{
					cstmt.close();
				}
			}
			catch (Exception e)
			{
				System.out.println(e.toString());
			}
		}
		return result;
	}
	
	/*
	 * 문서 첨부파일 등록하는 프로시저
	 * 등록된 파일 번호를 반환한다 
	 */
	private int insertFile(String empCd, String originalFileName, String saveFileName, String savePath)
	{
		int result = -1;
		
		CallableStatement cstmt = null;
		
		try
		{
			String sql = "{call PRC_FILE_INSERT(?,?,?,?,?)}";
			
			cstmt = conn.prepareCall(sql);
			
			cstmt.setString(1, empCd);
			cstmt.setString(2, originalFileName);
			cstmt.setString(3, saveFileName);
			cstmt.setString(4, savePath);
			
			cstmt.registerOutParameter(5, Types.INTEGER);
			
			cstmt.execute();
			
			result = cstmt.getInt(5);
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			result = -1;
		}
		
		finally 
		{
			try
			{
				if(cstmt != null)
				{
					cstmt.close();
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
