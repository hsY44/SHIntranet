package com.sh.dao;

import com.sh.dto.AprvSubmitDTO;
import com.sh.util.DBConn;
import com.sh.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AprvSubmitDAO
{
	private Connection conn = DBConn.getConnection();

    /**
     * selectDataCount : 총 상신내역 수
     *
     * @param empCd 사원코드
     * @return int result 총 상신내역 수
     */
    public int selectDataCount(String empCd)
    {
        int result = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;

        try
        {
            sql = """
				  SELECT COUNT(*) AS COUNT
                  FROM VIEW_APRV_SUBMIT_LIST
                  WHERE EMP_CD = ?
				  """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, empCd);

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
     * selectDataCount : 검색조건으로 나온 총 상신내역 수
     *
     * @param empCd 사원코드
     * @param status 상태
     * @param title 제목
     * @return int result 총 상신내역 수
     */
    public int selectDataCount(String empCd, String status, String title)
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
                  FROM VIEW_APRV_SUBMIT_LIST
                  WHERE EMP_CD = ?
				  """;

            // 상태 검색
            if (!status.equals("none"))
                sql += " AND (INSTR(STATUS, ?) >=1) ";

            // 제목 검색
            if (!("".equals(title)))
            {
                sql += " AND (INSTR(TITLE, ?) >=1) ";
            }

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(parameterIndex++, empCd);

            // 상태 or 제목 검색 둘중 하나는 필수
            if (!status.equals("none"))
                pstmt.setString(parameterIndex++, status);

            if (!("".equals(title)))
            {
                pstmt.setString(parameterIndex++, title);
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
     * selectAprvSubmitList : 상신내역 조회
     *
     * @param empCd 사원코드
     * @param offset 시작
     * @param size N개씩
     * @return List<AprvSubmitDTO> 상신내역 목록 조회
     */
    public List<AprvSubmitDTO> selectAprvSubmitList(String empCd, int offset, int size)
    {
        List<AprvSubmitDTO> result = new ArrayList<AprvSubmitDTO>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;

        try
        {
            sql = """
                  SELECT
                      ROW_NUMBER() OVER(ORDER BY A.DOC_CD DESC) AS ROW_NUM
                    , DOC_CD, WORK_NAME, TITLE, REG_DT, TYPE_CD
                    , TYPE_NAME, STATUS, CHILD_CNT
                    FROM VIEW_APRV_SUBMIT_LIST A
                    WHERE EMP_CD = ?
                    OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                  """;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, empCd);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, size);

            rs = pstmt.executeQuery();

            while (rs.next())
            {
                AprvSubmitDTO dto = new AprvSubmitDTO();

                dto.setRowNum(rs.getInt("ROW_NUM"));
                dto.setDocCd(rs.getString("DOC_CD"));
                dto.setWorkName(rs.getString("WORK_NAME"));
                dto.setTitle(rs.getString("TITLE"));
                dto.setRegDt(rs.getString("REG_DT"));
                dto.setTypeCd(rs.getString("TYPE_CD"));
                dto.setTypeName(rs.getString("TYPE_NAME"));
                dto.setStatus(rs.getString("STATUS"));
                dto.setChildCnt(rs.getInt("CHILD_CNT"));

                result.add(dto);
            }
        }
        catch (Exception e)
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
     * selectAprvSubmitList : 상신내역 조회(검색)
     * 
     * @param empCd 사원코드
     * @param offset 시작
     * @param size N개씩
     * @param status 상태
     * @param title 제목
     * @return List<AprvSubmitDTO> 상신내역 목록 조회
     */
    public List<AprvSubmitDTO> selectAprvSubmitList(String empCd, int offset, int size, String status, String title)
    {
        List<AprvSubmitDTO> result = new ArrayList<AprvSubmitDTO>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;
        int parameterIndex = 1;

        try
        {
            sql = """
                  SELECT
                      ROW_NUMBER() OVER(ORDER BY A.DOC_CD DESC) AS ROW_NUM
                    , DOC_CD, WORK_NAME, TITLE, REG_DT, TYPE_CD
                    , TYPE_NAME, STATUS, CHILD_CNT
                  FROM VIEW_APRV_SUBMIT_LIST A
                  WHERE EMP_CD = ?
                  """;

            // 상태 검색
            if (!status.equals("none"))
                sql += " AND (INSTR(STATUS, ?) >=1) ";

            // 제목 검색
            if (!("".equals(title)))
            {
                sql += " AND (INSTR(TITLE, ?) >=1) ";
            }

            sql += " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ";

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(parameterIndex++, empCd);

            // 상태 or 제목 검색 둘중 하나는 필수
            if (!status.equals("none"))
                pstmt.setString(parameterIndex++, status);

            if (!("".equals(title)))
            {
                pstmt.setString(parameterIndex++, title);
            }

            pstmt.setInt(parameterIndex++, offset);
            pstmt.setInt(parameterIndex++, size);

            rs = pstmt.executeQuery();

            while (rs.next())
            {
                AprvSubmitDTO dto = new AprvSubmitDTO();

                dto.setRowNum(rs.getInt("ROW_NUM"));
                dto.setDocCd(rs.getString("DOC_CD"));
                dto.setWorkName(rs.getString("WORK_NAME"));
                dto.setTitle(rs.getString("TITLE"));
                dto.setRegDt(rs.getString("REG_DT"));
                dto.setTypeCd(rs.getString("TYPE_CD"));
                dto.setTypeName(rs.getString("TYPE_NAME"));
                dto.setStatus(rs.getString("STATUS"));
                dto.setChildCnt(rs.getInt("CHILD_CNT"));

                result.add(dto);
            }
        }
        catch (Exception e)
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
