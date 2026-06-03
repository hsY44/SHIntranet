package com.sh.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sh.dto.EmpDTO;
import com.sh.util.DBConn;
import com.sh.util.DBUtil;

public class EmpDAO
{
	private Connection conn = DBConn.getConnection();

	// 로그인 - EMP_LOGIN 프로시저 사용
	public EmpDTO login(String empCd, String pwd)
	{
		EmpDTO result = null;
		CallableStatement cstmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			cstmt = conn.prepareCall("{ call EMP_LOGIN(?, ?, ?, ?, ?) }");
			cstmt.setString(1, empCd);
			cstmt.setString(2, pwd);
			cstmt.setString(3, null);
			cstmt.registerOutParameter(4, java.sql.Types.VARCHAR); // E_EMP_RESULT
			cstmt.registerOutParameter(5, java.sql.Types.VARCHAR); // E_EMP_NAME
			cstmt.execute();

			if ("SUCCESS".equals(cstmt.getString(4)))
			{
				pstmt = conn.prepareStatement("SELECT * FROM VIEW_EMPLOYEE WHERE EMP_CD = ? AND PWD = ?");
				pstmt.setString(1, empCd);
				pstmt.setString(2, pwd);
				rs = pstmt.executeQuery();
				if (rs.next())
					result = mapRow(rs);
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			DBUtil.close(rs);
			DBUtil.close(pstmt);
			DBUtil.close(cstmt);
		}
		return result;
	}

	// 재직 중인 전체 사원 수 (검색 조건 없음 - 통계용)
	public int countActiveEmp()
	{
		int count = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = conn.prepareStatement("SELECT COUNT(*) FROM VIEW_EMPLOYEE");
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

	// EMP 원본 테이블에 사원 존재 여부 (퇴사자 판별용 - VIEW_EMPLOYEE는 재직자만 포함)
	public boolean existsInEmpTable(String empCd)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = conn.prepareStatement("SELECT COUNT(*) FROM EMP WHERE TRIM(EMP_CD) = TRIM(?)");
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

	// 전체 사원 수 (검색 포함)
	public int countEmp(String schType, String kwd)
	{
		int count = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM VIEW_EMPLOYEE WHERE 1=1");
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

	// 페이지별 사원 목록
	public List<EmpDTO> listEmpPage(String schType, String kwd, int start, int end)
	{
		List<EmpDTO> result = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			StringBuilder inner = new StringBuilder("SELECT * FROM VIEW_EMPLOYEE WHERE 1=1");
			appendSearch(inner, schType, kwd);
			inner.append(" ORDER BY EMP_CD");
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



	// 사원 단건 조회
	public EmpDTO findByEmpCd(String empCd)
	{
		EmpDTO result = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			pstmt = conn.prepareStatement("SELECT * FROM VIEW_EMPLOYEE WHERE EMP_CD = ?");
			pstmt.setString(1, empCd);
			rs = pstmt.executeQuery();
			
			if (rs.next())
				result = mapRow(rs);
			
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

	// 사원 등록 - EMP_CREATE + EMP_INFO_CREATE 프로시저 사용
	public void insertEmp(EmpDTO dto) throws SQLException
	{
		CallableStatement cstmt = null;
		try
		{
			// 1. EMP 테이블에 사원 등록 
			cstmt = conn.prepareCall("{ call EMP_CREATE(?, ?, ?, ?) }");
			cstmt.setNull(1, java.sql.Types.CHAR); // EMP_CD 자동 생성
			cstmt.setString(2, dto.getDeptCd());
			cstmt.setString(3, dto.getPositionCd());
			cstmt.setString(4, dto.getEmpName());
			cstmt.execute();
			DBUtil.close(cstmt);

			// 2. 방금 등록된 사원코드 조회 (시퀀스 현재값 기준)
			String empCd = null;
			java.sql.PreparedStatement pstmt = conn.prepareStatement(
				"SELECT 'SW' || LPAD(EMP_SEQ.CURRVAL, 4, '0') FROM DUAL");
			java.sql.ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				empCd = rs.getString(1);
			DBUtil.close(rs);
			DBUtil.close(pstmt);

			// 3. EMP_INFO 테이블에 사원 정보 등록 (6개 파라미터 - HIRE_DATE 포함)
			cstmt = conn.prepareCall("{ call EMP_INFO_CREATE(?, ?, ?, ?, ?, ?) }");
			cstmt.setString(1, empCd);
			cstmt.setString(2, dto.getPwd());
			cstmt.setString(3, dto.getTel());
			cstmt.setString(4, dto.getEmail());
			cstmt.setString(5, dto.getAddr());
			// hireDate: 입력값 있으면 해당 날짜, 없으면 null (프로시저에서 SYSDATE 대체)
			String hd = dto.getHireDate();
			if (hd != null && !hd.isEmpty())
				cstmt.setDate(6, java.sql.Date.valueOf(hd));
			else
				cstmt.setNull(6, java.sql.Types.DATE);
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

	// 내 정보 수정 (본인) - EMP_INFO_UPDATE 프로시저 사용
	public void updateMyInfo(EmpDTO dto) throws SQLException
	{
		CallableStatement cstmt = null;
		
		try
		{
			cstmt = conn.prepareCall("{ call EMP_INFO_UPDATE(?, ?, ?, ?, ?, ?) }");
			cstmt.setString(1, dto.getEmpCd());
			cstmt.setString(2, dto.getEmpName());
			cstmt.setString(3, dto.getTel());
			cstmt.setString(4, dto.getEmail());
			cstmt.setString(5, dto.getAddr());
			cstmt.setString(6, dto.getPwd());
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

	// HR 사원 정보 수정 - EMP_UPDATE + EMP_INFO_UPDATE 프로시저 사용
	public void updateEmpByHr(EmpDTO dto) throws SQLException
	{
		CallableStatement cstmt = null;
		
		try
		{
			cstmt = conn.prepareCall("{ call EMP_UPDATE(?, ?, ?, ?) }");
			cstmt.setString(1, dto.getEmpCd());
			cstmt.setString(2, dto.getDeptCd());
			cstmt.setString(3, dto.getPositionCd());
			cstmt.setString(4, dto.getEmpName());
			cstmt.execute();
			DBUtil.close(cstmt);

			cstmt = conn.prepareCall("{ call EMP_INFO_UPDATE(?, ?, ?, ?, ?, ?) }");
			cstmt.setString(1, dto.getEmpCd());
			cstmt.setString(2, dto.getEmpName());
			cstmt.setString(3, dto.getTel());
			cstmt.setString(4, dto.getEmail());
			cstmt.setString(5, dto.getAddr());
			cstmt.setString(6, dto.getPwd());
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

	// 퇴사 처리 - EMP_EXIT 프로시저 사용
	public void exitEmp(String empCd) throws SQLException
	{
		CallableStatement cstmt = null;
		
		try
		{
			cstmt = conn.prepareCall("{ call EMP_EXIT(?) }");
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

	// 검색 조건 쿼리 추가
	private void appendSearch(StringBuilder sb, String schType, String kwd)
	{
		if (schType != null && kwd != null && !kwd.isEmpty())
		{
			switch (schType)
			{
			case "empName" -> sb.append(" AND INSTR(EMP_NAME, ?) >= 1");
			case "deptName" -> sb.append(" AND INSTR(DEPT_NAME, ?) >= 1");
			case "positionName" -> sb.append(" AND INSTR(POSITION_NAME, ?) >= 1");
			case "empCd" -> sb.append(" AND INSTR(EMP_CD, ?) >= 1");
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

	private EmpDTO mapRow(ResultSet rs) throws SQLException
	{
		EmpDTO dto = new EmpDTO();
		dto.setEmpCd(rs.getString("EMP_CD").trim());
		dto.setEmpName(rs.getString("EMP_NAME"));
		dto.setDeptCd(rs.getString("DEPT_CD").trim());
		dto.setDeptName(rs.getString("DEPT_NAME"));
		dto.setPositionCd(rs.getString("POSITION_CD").trim());
		dto.setPositionName(rs.getString("POSITION_NAME"));
		dto.setGrade(rs.getInt("GRADE"));
		dto.setTel(rs.getString("TEL") != null ? rs.getString("TEL").trim() : "");
		dto.setEmail(rs.getString("EMAIL"));
		dto.setAddr(rs.getString("ADDR"));
		dto.setPwd(rs.getString("PWD"));
		dto.setHireDate(rs.getString("HIRE_DATE"));
		dto.setRegDt(rs.getString("REG_DT"));
		return dto;
	}
}