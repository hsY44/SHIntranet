package com.sh.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sh.dto.RankDTO;
import com.sh.util.DBConn;
import com.sh.util.DBUtil;

public class RankDAO {

    private Connection getConn() {
        return DBConn.getConnection();
    }

    // 직급 목록 (재직 중인 소속 인원 수 포함)
    public List<RankDTO> listRank()
    {
        List<RankDTO> result = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            String sql =
                "SELECT P.POSITION_CD, P.POSITION_NAME, P.GRADE, NVL(E.CNT, 0) AS EMP_CNT " +
                "FROM EMP_POSITION P " +
                "LEFT JOIN (SELECT POSITION_CD, COUNT(*) AS CNT FROM VIEW_EMPLOYEE GROUP BY POSITION_CD) E " +
                "  ON P.POSITION_CD = E.POSITION_CD " +
                "ORDER BY P.GRADE";
            pstmt = getConn().prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                RankDTO dto = new RankDTO();
                dto.setPositionCd(rs.getString("POSITION_CD").trim());
                dto.setPositionName(rs.getString("POSITION_NAME"));
                dto.setGrade(rs.getInt("GRADE"));
                dto.setEmpCount(rs.getInt("EMP_CNT"));
                result.add(dto);
            }
        }
        catch (SQLException e) { e.printStackTrace(); }
        finally { DBUtil.close(rs); DBUtil.close(pstmt); }
        return result;
    }

    // 전체 직급 수 (POSITION_HISTORY 기준)
    public int countTotalHistory()
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try
        {
            pstmt = getConn().prepareStatement("SELECT COUNT(DISTINCT POSITION_CD) FROM POSITION_HISTORY");
            rs = pstmt.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        }
        catch (SQLException e) { e.printStackTrace(); }
        finally { DBUtil.close(rs); DBUtil.close(pstmt); }
        return count;
    }

    // 사용 중인 직급 수 (EMP_POSITION 기준)
    public int countActiveRank()
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try
        {
            pstmt = getConn().prepareStatement("SELECT COUNT(*) FROM EMP_POSITION");
            rs = pstmt.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        }
        catch (SQLException e) { e.printStackTrace(); }
        finally { DBUtil.close(rs); DBUtil.close(pstmt); }
        return count;
    }

    // 최근 한달 이내 추가된 직급 수 (POSITION_HISTORY 기준)
    public int countRecentRank()
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try
        {
            pstmt = getConn().prepareStatement(
                "SELECT COUNT(*) FROM (" +
                "SELECT H.POSITION_CD, MIN(H.REG_DT) AS FIRST_DT FROM POSITION_HISTORY H " +
                "JOIN EMP_POSITION P ON H.POSITION_CD = P.POSITION_CD " +
                "GROUP BY H.POSITION_CD" +
                ") WHERE FIRST_DT >= ADD_MONTHS(SYSDATE, -1)");
            rs = pstmt.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        }
        catch (SQLException e) { e.printStackTrace(); }
        finally { DBUtil.close(rs); DBUtil.close(pstmt); }
        return count;
    }

    // 직급명 중복 체크 (신규 등록용)
    public boolean existsPositionName(String positionName)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            pstmt = getConn().prepareStatement("SELECT COUNT(*) FROM EMP_POSITION WHERE POSITION_NAME = ?");
            pstmt.setString(1, positionName);
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        catch (SQLException e) { e.printStackTrace(); }
        finally { DBUtil.close(rs); DBUtil.close(pstmt); }
        return false;
    }

    // 등급 중복 체크 (신규 등록용)
    public boolean existsGrade(int grade)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            pstmt = getConn().prepareStatement("SELECT COUNT(*) FROM EMP_POSITION WHERE GRADE = ?");
            pstmt.setInt(1, grade);
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        catch (SQLException e) { e.printStackTrace(); }
        finally { DBUtil.close(rs); DBUtil.close(pstmt); }
        return false;
    }

    // 등급 중복 체크 (수정용 — 자기 자신 제외)
    public boolean existsGradeExclude(String positionCd, int grade)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            pstmt = getConn().prepareStatement(
                "SELECT COUNT(*) FROM EMP_POSITION WHERE GRADE = ? AND TRIM(POSITION_CD) != TRIM(?)");
            pstmt.setInt(1, grade);
            pstmt.setString(2, positionCd);
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        catch (SQLException e) { e.printStackTrace(); }
        finally { DBUtil.close(rs); DBUtil.close(pstmt); }
        return false;
    }

    // 해당 직급의 재직 중인 소속 직원 수
    public int countEmpInPosition(String positionCd)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try
        {
            pstmt = getConn().prepareStatement(
                "SELECT COUNT(*) FROM VIEW_EMPLOYEE WHERE TRIM(POSITION_CD) = TRIM(?)");
            pstmt.setString(1, positionCd);
            rs = pstmt.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        }
        catch (SQLException e) { e.printStackTrace(); }
        finally { DBUtil.close(rs); DBUtil.close(pstmt); }
        return count;
    }

    // 직급 등록
    public void insertRank(String positionName, int grade) throws SQLException
    {
        CallableStatement cstmt = null;
        try
        {
            cstmt = getConn().prepareCall("{ call POSITION_HISTORY_CUD(?, ?, ?, ?) }");
            cstmt.setString(1, "INSERT");
            cstmt.setNull(2, java.sql.Types.CHAR);
            cstmt.setString(3, positionName);
            cstmt.setInt(4, grade);
            cstmt.execute();
        }
        catch (SQLException e) { e.printStackTrace(); throw e; }
        finally { DBUtil.close(cstmt); }
    }

    // 직급 수정
    public void updateRank(String positionCd, String positionName, int grade) throws SQLException
    {
        CallableStatement cstmt = null;
        try
        {
            cstmt = getConn().prepareCall("{ call POSITION_HISTORY_CUD(?, ?, ?, ?) }");
            cstmt.setString(1, "UPDATE");
            cstmt.setString(2, positionCd);
            cstmt.setString(3, positionName);
            cstmt.setInt(4, grade);
            cstmt.execute();
        }
        catch (SQLException e) { e.printStackTrace(); throw e; }
        finally { DBUtil.close(cstmt); }
    }

    // 직급 삭제
    public void deleteRank(String positionCd) throws SQLException
    {
        CallableStatement cstmt = null;
        try
        {
            cstmt = getConn().prepareCall("{ call POSITION_HISTORY_CUD(?, ?, ?, ?) }");
            cstmt.setString(1, "DELETE");
            cstmt.setString(2, positionCd);
            cstmt.setNull(3, java.sql.Types.NVARCHAR);
            cstmt.setNull(4, java.sql.Types.NUMERIC);
            cstmt.execute();
        }
        catch (SQLException e) { e.printStackTrace(); throw e; }
        finally { DBUtil.close(cstmt); }
    }
}
