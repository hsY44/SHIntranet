package com.sh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.sh.dto.DeptWorkDTO;
import com.sh.dto.EmpDTO;
import com.sh.dto.WorkDTO;
import com.sh.util.DBConn;

public class WorkDAO
{
	private Connection conn = DBConn.getConnection();
	
	/*
	 * 회사 업무 조회(검색)
	 */
	public List<WorkDTO> getWork(String type, String keyword)
	{
		List<WorkDTO> result = new ArrayList<WorkDTO>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			String sql = "SELECT ROW_NUM, TYPE_NAME, "
					+ " WORK_NAME, REG_DT, EMP_CD, "
					+ " DEPT_NAME, EMP_NAME, POSITION_NAME, WORK_CD "
					+ " FROM VIEW_WORK_SEARCH";
			
			sql += " WHERE WORK_NAME LIKE '%" + keyword + "%'";
			
			// 000 인 경우 전체 검색
			if(!type.equals("000"))
			{
					 sql += " AND TYPE_CD = ?";
					 
					 pstmt = conn.prepareStatement(sql);
					 
					 pstmt.setString(1, type);
			}
			
			else
			{
				pstmt = conn.prepareStatement(sql);
			}
		   
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				WorkDTO dto = new WorkDTO();
				
				dto.setRowNum(rs.getInt("ROW_NUM"));
				dto.setTypeName(rs.getString("TYPE_NAME"));
				dto.setWorkName(rs.getString("WORK_NAME"));
				dto.setRegDt(rs.getString("REG_DT"));
				dto.setEmpCd(rs.getString("EMP_CD"));
				dto.setDeptName(rs.getString("DEPT_NAME"));
				dto.setEmpName(rs.getString("EMP_NAME"));
				dto.setPositionName(rs.getString("POSITION_NAME"));
				dto.setWorkCd(rs.getString("WORK_CD"));
				
				result.add(dto);
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
	
	/*
	 * 부서 업무 조회(검색)
	 */
	public List<DeptWorkDTO> getDeptWork(String type, String keyword)
	{
		List<DeptWorkDTO> result = new ArrayList<>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			String sql = "SELECT DEPT_NAME, TYPE_NAME, WORK_NAME, FLOW"
					+ " FROM VIEW_DEPT_WORK_SEARCH";
			
			// sql += " WHERE " + type + " LIKE \'%" + keyword + "%\'"; 
			
			sql += " WHERE " + type + " LIKE ?";
			
		    pstmt = conn.prepareStatement(sql);
			
		    pstmt.setString(1, "%" + keyword + "%");
		    
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				DeptWorkDTO dto = new DeptWorkDTO();
				
				dto.setDeptName(rs.getString("DEPT_NAME"));
				dto.setWorkType(rs.getString("TYPE_NAME"));
				dto.setWorkName(rs.getString("WORK_NAME"));
				dto.setFlow(rs.getInt("FLOW"));
				
				result.add(dto);
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
	
	/*
	 * 회사 업무 등록
	 * -1 : 중복
	 *  0 : 실패
	 *  1 : 성공
	 */
	public int addWork(String typeCd, String empCd, String workName)
	{
		if(workCheck(workName))
		{
			return -1;
		}
		
		PreparedStatement pstmt = null;
		
		int result = 0;
		
		try
		{
			String sql = "INSERT INTO WORK VALUES('WK' || TO_CHAR(WORK_SEQ.NEXTVAL,'FM0000'),?,?,?,SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, typeCd);
			pstmt.setString(2, empCd);
			pstmt.setString(3, workName);
			
			result = pstmt.executeUpdate();
		}
		catch (Exception e)
		{
			result = 0;
			System.out.println(e.toString());
		}
		
		finally 
		{
			try
			{
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
	
	// 종류와 이름이 같은 업무가 존재하는지 확인하는 함수
	private boolean workCheck(String workName)
	{
		String sql = "SELECT COUNT(*) AS COUNT FROM WORK WHERE WORK_NAME = ?";
		
		PreparedStatement pstmt = null;
		
		ResultSet rs = null;
		
		int result = 0;
		
		try
		{
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, workName);
			
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				result = rs.getInt("COUNT");
			}
			
			if(result == 0)
			{
				// 중복 없음
				return false;
			}
			else
			{
				// 중복 있음
				return true;
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
		
		return true;
	}
	
	/*
	 * 부서 업무 추가
	 * -1 : 중복 있음
	 *  0 : 등록 실패
	 *  1 : 등록 완료
	 */
	public int deptWorkAdd(String deptCd, String workCd)
	{
		if(deptCd == null)
		{
			return 0;
		}
		
		// 중복 체크
		if(deptWorkCheck(deptCd,workCd))
		{
			return -1;
		}
		
		int result = 0;
		
		// 순서 계산
		int flow = getFlow(workCd);
		
		if(flow == 0)
		{
			return result;
		}
		
		PreparedStatement pstmt = null;
		
		try
		{
			String sql = "INSERT INTO WORK_DEPT_RULE VALUES(WORK_DEPT_RULE_SEQ.NEXTVAL,?,?,?)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, workCd);
			pstmt.setString(2, deptCd);
			pstmt.setInt(3, flow);
			
			result = pstmt.executeUpdate();
		} 
		catch (Exception e)
		{
			result = 0;
			System.out.println(e.toString());
		}
		
		finally 
		{
			try
			{
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
	
	/*
	 * 부서 업무 중복 체크
	 */
	private boolean deptWorkCheck(String deptCd, String workCd)
	{
		PreparedStatement pstmt = null;
		
		ResultSet rs = null;
		
		try
		{
			String sql = "SELECT COUNT(*) AS COUNT"
					+ " FROM WORK_DEPT_RULE"
					+ " WHERE WORK_CD = ? AND DEPT_CD = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, workCd);
			pstmt.setString(2, deptCd);
			
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				if(rs.getInt("COUNT") >= 1)
				{
					return true;
				}
				else 
				{
					return false;
				}
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
		
		return true;
	}
	
	/*
	 * 현재 순서 계산하기
	 */
	private int getFlow(String workCd)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			String sql = "SELECT NVL(MAX(FLOW),0) + 1 AS FLOW"
					+ " FROM WORK_DEPT_RULE"
					+ " WHERE WORK_CD = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, workCd);
			
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				return rs.getInt("FLOW");
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
		
		return 0;
	}

	/*
	 * 업무 종류와 업무 코드 얻어오기
	 */
	public List<WorkDTO> getWorkType()
	{
		List<WorkDTO> result = new ArrayList<>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			String sql = "SELECT TYPE_CD, TYPE_NAME FROM WORK_TYPE ORDER BY TYPE_CD";
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				WorkDTO dto = new WorkDTO();
				
				dto.setTypeCd(rs.getString("TYPE_CD"));
				dto.setTypeName(rs.getString("TYPE_NAME"));
				
				result.add(dto);
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
	
	/*
	 * 업무 지시 가능한 목록 가져오기
	 */
	public List<EmpDTO> getWorkTarget(String empCd,String deptCd, int grade)
	{
		List<EmpDTO> result = new ArrayList<>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			String sql = "SELECT EMP_CD, EMP_NAME, POSITION_NAME "
					+ " FROM VIEW_WORK_TARGET "
					+ " WHERE DEPT_CD = ? AND GRADE < ? AND EMP_CD != ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, deptCd);
			pstmt.setInt(2, grade);
			pstmt.setString(3, empCd);
			
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				EmpDTO dto = new EmpDTO();
				
				dto.setEmpCd(rs.getString("EMP_CD"));
				dto.setEmpName(rs.getString("EMP_NAME"));
				dto.setPositionName(rs.getString("POSITION_NAME"));
				
				result.add(dto);
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
