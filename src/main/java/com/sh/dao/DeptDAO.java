package com.sh.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sh.dto.DeptDTO;
import com.sh.util.DBConn;
import com.sh.util.DBUtil;

public class DeptDAO
{

	private Connection getConn()
	{
		return DBConn.getConnection();
	}

	// 부서 목록 (재직 중인 소속 인원 수 포함)
	public List<DeptDTO> listDept()
	{
		List<DeptDTO> result = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			String sql =
				"SELECT D.DEPT_CD, D.DEPT_NAME, NVL(E.CNT, 0) AS EMP_CNT " +
				"FROM DEPT D " +
				"LEFT JOIN (SELECT DEPT_CD, COUNT(*) AS CNT FROM VIEW_EMPLOYEE GROUP BY DEPT_CD) E " +
				"  ON D.DEPT_CD = E.DEPT_CD " +
				"ORDER BY D.DEPT_CD";
			pstmt = getConn().prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next())
			{
				DeptDTO dto = new DeptDTO();
				dto.setDeptCd(rs.getString("DEPT_CD").trim());
				dto.setDeptName(rs.getString("DEPT_NAME"));
				dto.setEmpCount(rs.getInt("EMP_CNT"));
				result.add(dto);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return result;
	}

	// 전체 부서 수 (DEPT_HISTORY 기준)
	public int countTotalHistory()
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		try
		{
			pstmt = getConn().prepareStatement("SELECT COUNT(DISTINCT DEPT_CD) FROM DEPT_HISTORY");
			rs = pstmt.executeQuery();
			if (rs.next())
				count = rs.getInt(1);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return count;
	}

	// 사용 중인 부서 수 (DEPT 기준)
	public int countActiveDept()
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		try
		{
			pstmt = getConn().prepareStatement("SELECT COUNT(*) FROM DEPT");
			rs = pstmt.executeQuery();
			
			if (rs.next())
				count = rs.getInt(1);
			
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return count;
	}

	// 최근 한달 이내 추가된 부서 수 (DEPT 기준)
	public int countRecentDept()
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		
		try
		{
			pstmt = getConn().prepareStatement(
				"SELECT COUNT(*) FROM (" +
				"SELECT H.DEPT_CD, MIN(H.REG_DT) AS FIRST_DT FROM DEPT_HISTORY H " +
				"JOIN DEPT D ON H.DEPT_CD = D.DEPT_CD " +
				"GROUP BY H.DEPT_CD" +
				") WHERE FIRST_DT >= ADD_MONTHS(SYSDATE, -1)");
			rs = pstmt.executeQuery();
			if (rs.next())
				count = rs.getInt(1);
		} catch (SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return count;
	}

	// 부서명 중복 체크 (신규 등록용)
	public boolean existsDeptName(String deptName)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = getConn().prepareStatement("SELECT COUNT(*) FROM DEPT WHERE DEPT_NAME = ?");
			pstmt.setString(1, deptName);
			rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1) > 0;
		}
		catch (SQLException e) { e.printStackTrace(); }
		finally { DBUtil.close(rs); DBUtil.close(pstmt); }
		return false;
	}

	// 부서명 중복 체크 (수정용 — 자기 자신 제외)
	public boolean existsDeptNameExclude(String deptCd, String deptName)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = getConn().prepareStatement(
				"SELECT COUNT(*) FROM DEPT WHERE DEPT_NAME = ? AND TRIM(DEPT_CD) != TRIM(?)");
			pstmt.setString(1, deptName);
			pstmt.setString(2, deptCd);
			rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1) > 0;
		}
		catch (SQLException e) { e.printStackTrace(); }
		finally { DBUtil.close(rs); DBUtil.close(pstmt); }
		return false;
	}

	// 해당 부서의 재직 중인 소속 직원 수
	public int countEmpInDept(String deptCd)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		try
		{
			pstmt = getConn().prepareStatement(
				"SELECT COUNT(*) FROM VIEW_EMPLOYEE WHERE TRIM(DEPT_CD) = TRIM(?)");
			pstmt.setString(1, deptCd);
			rs = pstmt.executeQuery();
			if (rs.next()) count = rs.getInt(1);
		}
		catch (SQLException e) { e.printStackTrace(); }
		finally { DBUtil.close(rs); DBUtil.close(pstmt); }
		return count;
	}

	// 부서 등록
	public void insertDept(String deptName) throws SQLException
	{
		CallableStatement cstmt = null;
		try
		{
			cstmt = getConn().prepareCall("{ call DEPT_HISTORY_CUD(?, ?, ?) }");
			cstmt.setString(1, "INSERT");
			cstmt.setNull(2, java.sql.Types.CHAR);
			cstmt.setString(3, deptName);
			cstmt.execute();
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw e;
		} finally
		{
			DBUtil.close(cstmt);
		}
	}

	// 부서 수정
	public void updateDept(String deptCd, String deptName) throws SQLException
	{
		CallableStatement cstmt = null;
		try
		{
			cstmt = getConn().prepareCall("{ call DEPT_HISTORY_CUD(?, ?, ?) }");
			cstmt.setString(1, "UPDATE");
			cstmt.setString(2, deptCd);
			cstmt.setString(3, deptName);
			cstmt.execute();
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw e;
		} finally
		{
			DBUtil.close(cstmt);
		}
	}

	// 부서 삭제
	public void deleteDept(String deptCd) throws SQLException
	{
		CallableStatement cstmt = null;
		try
		{
			cstmt = getConn().prepareCall("{ call DEPT_HISTORY_CUD(?, ?, ?) }");
			cstmt.setString(1, "DELETE");
			cstmt.setString(2, deptCd);
			cstmt.setNull(3, java.sql.Types.NVARCHAR);
			cstmt.execute();
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw e;
		} finally
		{
			DBUtil.close(cstmt);
		}
	}
}
