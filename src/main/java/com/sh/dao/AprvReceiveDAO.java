package com.sh.dao;

import com.sh.dto.AprvReceiveDTO;
import com.sh.dto.AprvSubmitDTO;
import com.sh.util.DBConn;
import com.sh.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AprvReceiveDAO
{
	private Connection conn = DBConn.getConnection();

    /**
     * selectDataCount : 총 수신내역 수
     *
     * @param aprvEmpCd 결재자사원코드
     * @param typeCd 결재대기/결재완료
     * @return int result 총 수신내역 수
     */
    public int selectDataCount(String aprvEmpCd, String typeCd)
    {
        int result = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;

        try
        {
            sql = "SELECT COUNT(*) AS COUNT ";
            
            if (typeCd.equals("wait"))
            	sql += " FROM VIEW_APRV_RECEIVE_WAIT ";
            else
            	sql += " FROM VIEW_APRV_RECEIVE_COMPLETE ";
            
            sql += """
            		WHERE APRV_EMP_CD = ?
            	   """;
                  
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, aprvEmpCd);

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
     * selectDataCount : 검색조건으로 나온 총 수신내역 수
     *
     * @param aprvEmpCd 결재자사원코드
     * @param typeCd 결재대기/결재완료
     * @param title 제목
     * @return int result 총 상신내역 수
     */
    public int selectDataCount(String aprvEmpCd, String typeCd, String title)
    {
        int result = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;

        try
        {
        	sql = "SELECT COUNT(*) AS COUNT ";
            
            if (typeCd.equals("wait"))
            	sql += " FROM VIEW_APRV_RECEIVE_WAIT ";
            else
            	sql += " FROM VIEW_APRV_RECEIVE_COMPLETE ";
            
            sql += """
            		 WHERE APRV_EMP_CD = ?
            		 AND (INSTR(TITLE, ?) >=1)
            	   """;

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, aprvEmpCd);
            pstmt.setString(2, title);

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
     * selectAprvReceiveList : 수신내역 조회
     *
     * @param aprvEmpCd 결재자사원코드
     * @param typeCd 결재대기/결재완료
     * @param offset 시작
     * @param size N개씩
     * @return List<AprvReceiveDTO> 수신내역 목록 조회
     */
    public List<AprvReceiveDTO> selectAprvReceiveList(String aprvEmpCd, String typeCd, int offset, int size)
    {
        List<AprvReceiveDTO> result = new ArrayList<AprvReceiveDTO>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;

        try
        {
        	if (typeCd.equals("wait"))
        	{
        		sql = """
        				SELECT DOC_CD, PARENT_DOC_CD, WORK_NAME, TITLE
						     , EMP_CD, EMP_NAME, APRV_REG_DT, TYPE_NAME
						     , APRV_EMP_CD, APRV_EMP_NAME
						FROM VIEW_APRV_RECEIVE_WAIT
						WHERE APRV_EMP_CD = ?
						OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        			  """;
        	}
            else
            {
            	sql = """
            			SELECT DOC_CD, PARENT_DOC_CD, WORK_NAME, TITLE
						     , EMP_CD, EMP_NAME, APRV_TYPE_CD, APRV_TYPE_NAME
						     , REG_DT, DOC_TYPE_NAME, STATUS, APRV_EMP_CD
						FROM VIEW_APRV_RECEIVE_COMPLETE
						WHERE APRV_EMP_CD = ?
						OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            		  """; 
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, aprvEmpCd);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, size);

            rs = pstmt.executeQuery();

            while (rs.next())
            {
            	AprvReceiveDTO dto = new AprvReceiveDTO();

            	if (typeCd.equals("wait"))
            	{
            		dto.setDocCd(rs.getString("DOC_CD"));
                    dto.setParentDocCd(rs.getString("PARENT_DOC_CD"));
                    dto.setWorkName(rs.getString("WORK_NAME"));
                    dto.setTitle(rs.getString("TITLE"));
                    dto.setEmpCd(rs.getString("EMP_CD"));
                    dto.setEmpName(rs.getString("EMP_NAME"));
                    dto.setAprvRegDt(rs.getString("APRV_REG_DT"));
                    dto.setTypeName(rs.getString("TYPE_NAME"));
                    dto.setAprvEmpCd(rs.getString("APRV_EMP_CD"));
                    dto.setAprvEmpName(rs.getString("APRV_EMP_NAME"));
            	}
            	else
            	{
            		dto.setDocCd(rs.getString("DOC_CD"));
                    dto.setParentDocCd(rs.getString("PARENT_DOC_CD"));
                    dto.setWorkName(rs.getString("WORK_NAME"));
                    dto.setTitle(rs.getString("TITLE"));
                    dto.setEmpCd(rs.getString("EMP_CD"));
                    dto.setEmpName(rs.getString("EMP_NAME"));
                    dto.setAprvTypeCd(rs.getString("APRV_TYPE_CD"));
                    dto.setAprvTypeName(rs.getString("APRV_TYPE_NAME"));
                    dto.setRegDt(rs.getString("REG_DT"));
                    dto.setDocTypeName(rs.getString("DOC_TYPE_NAME"));
                    dto.setStatus(rs.getString("STATUS"));
                    dto.setAprvEmpCd(rs.getString("APRV_EMP_CD"));
            	}

                result.add(dto);
            }
            
        }
        catch (Exception e)
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


    /**
     * selectAprvReceiveList : 수신내역 조회(검색)
     * 
     * @param aprvEmpCd 결재자사원코드
     * @param typeCd 결재대기/결재완료
     * @param offset 시작
     * @param size N개씩
     * @param title 제목
     * @return List<AprvReceiveDTO> 수신내역 목록 조회
     */
    public List<AprvReceiveDTO> selectAprvReceiveList(String aprvEmpCd, String typeCd, int offset, int size, String title)
    {
        List<AprvReceiveDTO> result = new ArrayList<AprvReceiveDTO>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;

        try
        {
        	if (typeCd.equals("wait"))
        	{
        		sql = """
        				SELECT DOC_CD, PARENT_DOC_CD, WORK_NAME, TITLE
						     , EMP_CD, EMP_NAME, APRV_REG_DT, TYPE_NAME
						     , APRV_EMP_CD, APRV_EMP_NAME
						FROM VIEW_APRV_RECEIVE_WAIT
						WHERE APRV_EMP_CD = ?
        			  """;
        	}
            else
            {
            	sql = """
            			SELECT DOC_CD, PARENT_DOC_CD, WORK_NAME, TITLE
						     , EMP_CD, EMP_NAME, APRV_TYPE_CD, APRV_TYPE_NAME
						     , REG_DT, DOC_TYPE_NAME, STATUS, APRV_EMP_CD
						FROM VIEW_APRV_RECEIVE_COMPLETE
						WHERE APRV_EMP_CD = ?
            		  """; 
            }
        	
        	sql += " AND (INSTR(TITLE, ?) >=1) ";
        	sql += " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ";
        	
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, aprvEmpCd);
            pstmt.setString(2, title);
            pstmt.setInt(3, offset);
            pstmt.setInt(4, size);

            rs = pstmt.executeQuery();

            while (rs.next())
            {
            	AprvReceiveDTO dto = new AprvReceiveDTO();

            	if (typeCd.equals("wait"))
            	{
            		dto.setDocCd(rs.getString("DOC_CD"));
                    dto.setParentDocCd(rs.getString("PARENT_DOC_CD"));
                    dto.setWorkName(rs.getString("WORK_NAME"));
                    dto.setTitle(rs.getString("TITLE"));
                    dto.setEmpCd(rs.getString("EMP_CD"));
                    dto.setEmpName(rs.getString("EMP_NAME"));
                    dto.setAprvRegDt(rs.getString("APRV_REG_DT"));
                    dto.setTypeName(rs.getString("TYPE_NAME"));
                    dto.setAprvEmpCd(rs.getString("APRV_EMP_CD"));
                    dto.setAprvEmpName(rs.getString("APRV_EMP_NAME"));
            	}
            	else
            	{
            		dto.setDocCd(rs.getString("DOC_CD"));
                    dto.setParentDocCd(rs.getString("PARENT_DOC_CD"));
                    dto.setWorkName(rs.getString("WORK_NAME"));
                    dto.setTitle(rs.getString("TITLE"));
                    dto.setEmpCd(rs.getString("EMP_CD"));
                    dto.setEmpName(rs.getString("EMP_NAME"));
                    dto.setAprvTypeCd(rs.getString("APRV_TYPE_CD"));
                    dto.setAprvTypeName(rs.getString("APRV_TYPE_NAME"));
                    dto.setRegDt(rs.getString("REG_DT"));
                    dto.setDocTypeName(rs.getString("DOC_TYPE_NAME"));
                    dto.setStatus(rs.getString("STATUS"));
                    dto.setAprvEmpCd(rs.getString("APRV_EMP_CD"));
            	}

                result.add(dto);
            }
        }
        catch (Exception e)
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



}
