package com.sh.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sh.dto.AttendDTO;
import com.sh.util.DBConn;
import com.sh.util.DBUtil;

public class AttendDAO
{

	private Connection conn = DBConn.getConnection();

	// 오늘 출근 기록 존재 여부 (TYPE_CD '001' = 출근)
	public boolean hasTodayIn(String empCd)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = conn.prepareStatement(
				"SELECT COUNT(*) FROM ATTEND_LOG " +
				"WHERE TRIM(EMP_CD) = TRIM(?) AND TYPE_CD = '001' AND TRUNC(REG_DT) = TRUNC(SYSDATE)");
			pstmt.setString(1, empCd);
			rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1) > 0;
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
		return false;
	}

	// 오늘 퇴근 기록 존재 여부 (TYPE_CD '002' = 퇴근)
	public boolean hasTodayOut(String empCd)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = conn.prepareStatement(
				"SELECT COUNT(*) FROM ATTEND_LOG " +
				"WHERE TRIM(EMP_CD) = TRIM(?) AND TYPE_CD = '002' AND TRUNC(REG_DT) = TRUNC(SYSDATE)");
			pstmt.setString(1, empCd);
			rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1) > 0;
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
		return false;
	}

	// 출근 기록 - ATTEND_LOG_ATT_C 프로시저 사용
	public void insertAttendIn(String empCd) throws SQLException
	{
		CallableStatement cstmt = null;
		try
		{
			cstmt = conn.prepareCall("{ call ATTEND_IN(?) }");
			cstmt.setString(1, empCd);
			cstmt.execute();
			
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			throw e;
		} 
		finally
		{
			DBUtil.close(cstmt);
		}
	}

	// 퇴근 기록 - ATTEND_LOG_EXIT_C 프로시저 사용
	public void insertAttendOut(String empCd) throws SQLException
	{
		CallableStatement cstmt = null;
		try
		{
			cstmt = conn.prepareCall("{ call ATTEND_OUT(?) }");
			cstmt.setString(1, empCd);
			cstmt.execute();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			throw e;
		} 
		finally
		{
			DBUtil.close(cstmt);
		}
	}

	// 전체 출퇴근 기록 수 (검색 포함)
	public int countAttend(String schType, String kwd)
	{
		int count = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM VIEW_ATTEND_LOG WHERE 1=1");
			appendSearch(sb, schType, kwd);
			
			pstmt = conn.prepareStatement(sb.toString());
			
			setSearch(pstmt, schType, kwd, 1);
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

	// 페이지별 출퇴근 기록 목록
	public List<AttendDTO> listAttendPage(String schType, String kwd, int start, int end)
	{
		List<AttendDTO> result = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			StringBuilder inner = new StringBuilder("SELECT * FROM VIEW_ATTEND_LOG WHERE 1=1");
			appendSearch(inner, schType, kwd);
			inner.append(" ORDER BY LOG_NO DESC");

			String sql = "SELECT * FROM (SELECT rownum rn, q.* FROM (" + inner + ") q) WHERE rn >= ? AND rn <= ?";
			pstmt = conn.prepareStatement(sql);
			
			int idx = setSearch(pstmt, schType, kwd, 1);
			
			pstmt.setInt(idx++, start);
			pstmt.setInt(idx, end);

			rs = pstmt.executeQuery();
			
			while (rs.next())
				result.add(mapRow(rs));
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
		return result;
	}

	// 검색 조건 쿼리 추가
	private void appendSearch(StringBuilder sb, String schType, String kwd)
	{
		if (schType != null && kwd != null && !kwd.isEmpty())
		{
			switch (schType)
			{
				case "empName" -> sb.append(" AND INSTR(EMP_NAME, ?) >= 1");
				case "empCd" -> sb.append(" AND INSTR(EMP_CD, ?) >= 1");
				case "deptName" -> sb.append(" AND INSTR(DEPT_NAME, ?) >= 1");
				case "positionName" -> sb.append(" AND INSTR(POSITION_NAME, ?) >= 1");
			}
		}
	}

	// 검색 파라미터 바인딩, 다음 인덱스 반환
	private int setSearch(PreparedStatement pstmt, String schType, String kwd, int startIdx) throws SQLException
	{
		if (schType != null && kwd != null && !kwd.isEmpty())
		{
			pstmt.setString(startIdx, kwd);
			return startIdx + 1;
		}
		return startIdx;
	}

	private AttendDTO mapRow(ResultSet rs) throws SQLException
	{
		AttendDTO dto = new AttendDTO();
		dto.setLogNo(rs.getLong("LOG_NO"));
		dto.setTypeCd(rs.getString("TYPE_CD").trim());
		dto.setTypeName(rs.getString("TYPE_NAME"));
		dto.setEmpCd(rs.getString("EMP_CD").trim());
		dto.setEmpName(rs.getString("EMP_NAME"));
		dto.setDeptCd(rs.getString("DEPT_CD").trim());
		dto.setDeptName(rs.getString("DEPT_NAME"));
		dto.setPositionCd(rs.getString("POSITION_CD").trim());
		dto.setPositionName(rs.getString("POSITION_NAME"));
		dto.setRegDt(rs.getString("REG_DT"));
		
		return dto;
	}
}
