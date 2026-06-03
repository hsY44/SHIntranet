package com.sh.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.sh.dto.ManagerDTO;
import com.sh.util.DBConn;
import com.sh.util.DBUtil;

public class ManagerDAO
{
	private Connection conn = DBConn.getConnection();
	
    /**
     * selectDataCount : 총 담당자 수
     *
     * @return int result 총 담당자 수
     */
	public int selectDataCount()
	{
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try
		{
			sql = """
				  SELECT COUNT(*) AS COUNT
				  FROM VIEW_MANAGER
				  """;
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if (rs.next())
				result = rs.getInt("COUNT");
			
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

    /**
     *  selectDataCount : 검색조건으로 나온 총 담당자 수
     *
     * @param deptCd 부서코드
     * @param schType 검색조건(업무명/사원명)
     * @param kwd 검색어
     * @return int result 총 담당자 수
     */
	public int selectDataCount(String deptCd, String schType, String kwd)
	{
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int parameterIndex = 1;
		
		try
		{
			sql = """
				  SELECT COUNT(*) AS COUNT
				  FROM VIEW_MANAGER
				  """;
			
			// 부서 검색
			if (!deptCd.equals("none"))
				sql += " WHERE (INSTR(DEPT_CD, ?) >=1) ";
			
			// 업무명/사원명 검색
			if (schType.equals("work_name") || schType.equals("manager_emp_name"))
			{
				// 부서 검색 여부로 WHERE / AND
				if (deptCd.equals("none"))
					sql += " WHERE ";
				else
					sql += " AND ";
				
				sql += " (INSTR("+ schType +", ?) >=1) ";
			}
			
			pstmt = conn.prepareStatement(sql);

			// 부서 or 업무명/사원명 검색 둘중 하나는 필수
			if (!deptCd.equals("none"))
				pstmt.setString(parameterIndex++, deptCd);
			
			if (!schType.equals("none"))
			{
				pstmt.setString(parameterIndex++, kwd);
			}
			
			rs = pstmt.executeQuery();
			
			if (rs.next())
				result = rs.getInt("COUNT");
			
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

    /**
     * selectManagerList : 담당자 조회
     *
     * @param offset 시작
     * @param size N개씩
     * @return List<ManagerDTO> 담당자 목록 조회
     */
	public List<ManagerDTO> selectManagerList(int offset, int size)
	{
		List<ManagerDTO> result = new ArrayList<ManagerDTO>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try
		{
			sql = """
				  SELECT
					  ROW_NUM, MANAGER_NO, TYPE_NAME, WORK_NAME, DEPT_CD, DEPT_NAME, 
					  MANAGER_EMP_CD, MANAGER_EMP_NAME, S_ISSUE_DATE, E_ISSUE_DATE, 
					  REG_EMP_CD, REG_EMP_NAME, WORK_CD
				  FROM VIEW_MANAGER
				  ORDER BY ROW_NUM ASC
				  OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
				  """;
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			while (rs.next())
			{
				ManagerDTO dto = new ManagerDTO();

				dto.setRowNum(rs.getInt("ROW_NUM"));
				dto.setManagerNo(rs.getInt("MANAGER_NO"));
				dto.setTypeName(rs.getString("TYPE_NAME"));
				dto.setWorkName(rs.getString("WORK_NAME"));
				dto.setDeptCd(rs.getString("DEPT_CD"));
				dto.setDeptName(rs.getString("DEPT_NAME"));
				dto.setManagerEmpCd(rs.getString("MANAGER_EMP_CD"));
				dto.setManagerEmpName(rs.getString("MANAGER_EMP_NAME"));
				dto.setStartIssueDate(rs.getDate("S_ISSUE_DATE"));
				dto.setEndIssueDate(rs.getDate("E_ISSUE_DATE"));
				dto.setRegEmpCd(rs.getString("REG_EMP_CD"));
				dto.setRegEmpName(rs.getString("REG_EMP_NAME"));
                dto.setWorkCd(rs.getString("WORK_CD"));
				
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
	
	/**
	 * selectManagerList : 담당자 조회(검색)
	 * 
	 * @param offset 시작
	 * @param size N개씩
	 * @param deptCd 부서코드
	 * @param schType 검색조건(업무명/사원명)
	 * @param kwd 검색어
	 * @return List<ManagerDTO> 담당자 목록 조회
	 */
	public List<ManagerDTO> selectManagerList(int offset, int size, String deptCd, String schType, String kwd)
	{
		List<ManagerDTO> result = new ArrayList<ManagerDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		int parameterIndex = 1;
		
		try
		{
			sb.append("SELECT ROW_NUM, MANAGER_NO, TYPE_NAME ");
			sb.append(", WORK_NAME, DEPT_CD, DEPT_NAME, MANAGER_EMP_CD ");
			sb.append(", MANAGER_EMP_NAME, S_ISSUE_DATE, E_ISSUE_DATE ");
			sb.append(", REG_EMP_CD, REG_EMP_NAME, WORK_CD ");
			sb.append(" FROM VIEW_MANAGER ");
			
			// 부서 검색
			if (!deptCd.equals("none"))
				sb.append(" WHERE (INSTR(DEPT_CD, ?) >=1) ");

			// 업무명/사원명 검색
			if (schType.equals("work_name") || schType.equals("manager_emp_name"))
			{
				// 부서 검색 여부로 WHERE / AND
				if (deptCd.equals("none"))
					sb.append(" WHERE ");
				else
					sb.append(" AND ");

				sb.append(" (INSTR("+ schType +", ?) >=1) ");
			}
			sb.append(" ORDER BY MANAGER_NO DESC ");
			sb.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			// 부서 or 업무명/사원명 검색 둘중 하나는 필수
			if (!deptCd.equals("none"))
				pstmt.setString(parameterIndex++, deptCd);

			if (!schType.equals("none"))
			{
				pstmt.setString(parameterIndex++, kwd);
			}

        	pstmt.setInt(parameterIndex++, offset);
        	pstmt.setInt(parameterIndex++, size);

			rs = pstmt.executeQuery();
			
			while (rs.next())
			{
				ManagerDTO dto = new ManagerDTO();
				
				dto.setRowNum(rs.getInt("ROW_NUM"));
				dto.setManagerNo(rs.getInt("MANAGER_NO"));
				dto.setTypeName(rs.getString("TYPE_NAME"));
				dto.setWorkName(rs.getString("WORK_NAME"));
				dto.setDeptCd(rs.getString("DEPT_CD"));
				dto.setDeptName(rs.getString("DEPT_NAME"));
				dto.setManagerEmpCd(rs.getString("MANAGER_EMP_CD"));
				dto.setManagerEmpName(rs.getString("MANAGER_EMP_NAME"));
				dto.setStartIssueDate(rs.getDate("S_ISSUE_DATE"));
				dto.setEndIssueDate(rs.getDate("E_ISSUE_DATE"));
				dto.setRegEmpCd(rs.getString("REG_EMP_CD"));
				dto.setRegEmpName(rs.getString("REG_EMP_NAME"));
                dto.setWorkCd(rs.getString("WORK_CD"));
				
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

    /**
     * selectManagerIssue : 담당자의 마지막 이력 조회(발생종류, 발생일시)
     *
     * @param workCd 업무코드
     * @param empCd 사원코드
     * @return ManagerDTO 담당자(발생종류:ISSUE_TYPE, 발생일시:ISSUE_DATE)
     */
    public ManagerDTO selectManagerIssue(String workCd, String empCd)
    {
        ManagerDTO result =  new ManagerDTO();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;

        try
        {
            sql = """
                    SELECT ISSUE_TYPE, TO_CHAR(ISSUE_DATE, 'YYYY-MM-DD') AS ISSUE_DATE 
                    FROM (
                        SELECT ISSUE_TYPE, ISSUE_DATE
                        FROM MANAGER
                        WHERE WORK_CD = ?
                        AND MANAGER_EMP_CD = ?
                        ORDER BY ISSUE_DATE DESC, MANAGER_NO DESC
                    ) WHERE ROWNUM = 1
                    """;

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, workCd);
            pstmt.setString(2, empCd);

            rs = pstmt.executeQuery();

            if (rs.next())
            {
                result.setIssueType(rs.getString("ISSUE_TYPE"));
                result.setIssueDate(rs.getString("ISSUE_DATE"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            DBUtil.close(rs);
            DBUtil.close(pstmt);
        }

        return result;
    }

    /**
     * insertManager : 담당자 등록
     *
     * @param dto 담당자 DTO
     *            (업무코드, 담당자사원코드, 등록자사원코드, 발생종류, 발생일시)
     * @return int result ROWCOUNT ? 아니면
     */
    public int insertManager(ManagerDTO dto)
    {
        int result = 0;

        CallableStatement cstmt = null;
        ResultSet rs = null;
        String sql;

        try
        {
            sql = "{ call PRC_MANAGER_INSERT(?, ?, ?, ?, ?) }";

            cstmt = conn.prepareCall(sql);

            cstmt.setString(1, dto.getWorkCd());
            cstmt.setString(2, dto.getManagerEmpCd());
            cstmt.setString(3, dto.getRegEmpCd());
            cstmt.setString(4, dto.getIssueType());
            cstmt.setString(5, dto.getIssueDate());

            cstmt.execute();
            result = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            DBUtil.close(rs);
            DBUtil.close(cstmt);
        }

        return  result;
    }
	
	
}
