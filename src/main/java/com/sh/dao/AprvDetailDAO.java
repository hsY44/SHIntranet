package com.sh.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.sh.dto.AprvDetailDTO;
import com.sh.dto.AprvLineDTO;
import com.sh.dto.DocFileDTO;
import com.sh.util.DBConn;
import com.sh.util.DBUtil;

public class AprvDetailDAO
{
	private Connection conn = DBConn.getConnection();
	
	/**
	 * AprvDetailDTO : 문서 상세 조회
	 * 
	 * @param docCd
	 * @return AprvDetailDTO
	 */
	public AprvDetailDTO selectAprvDetail(String docCd)
	{
		AprvDetailDTO result = new AprvDetailDTO();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;
        
        try
        {
            sql = """
				  SELECT 
					    A.DOC_CD
					  , A.TITLE
					  , A.REG_DT
					  , A.EMP_CD
            		  , FN_GET_EMP_NAME(A.EMP_CD) AS EMP_NAME
					  , B.WORK_NAME
					  , A.TYPE_CD
					  , C.TYPE_NAME
					  , A.PARENT_DOC_CD
					  , (SELECT TITLE FROM SH_DOCUMENT WHERE DOC_CD = A.PARENT_DOC_CD) AS PARENT_DOC_TITLE
					  , A.CONTENTS
					  , (SELECT COUNT(*) FROM DOC_FILE WHERE DOC_CD = A.DOC_CD) AS FILE_CNT
					FROM SH_DOCUMENT A
					JOIN WORK B ON A.WORK_CD = B.WORK_CD
					JOIN DOC_TYPE C ON A.TYPE_CD = C.TYPE_CD
					WHERE DOC_CD = ?
				  """;
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, docCd);

            rs = pstmt.executeQuery();

            if (rs.next())
            {
            	result.setDocCd(rs.getString("DOC_CD"));
                result.setTitle(rs.getString("TITLE"));
                result.setRegDt(rs.getString("REG_DT"));
                result.setEmpCd(rs.getString("EMP_CD"));
                result.setEmpName(rs.getString("EMP_NAME"));
                result.setWorkName(rs.getString("WORK_NAME"));
                result.setTypeName(rs.getString("TYPE_NAME"));
                result.setTypeCd(rs.getString("TYPE_CD"));
                result.setParentDocCd(rs.getString("PARENT_DOC_CD"));
                result.setParentDocTitle(rs.getString("PARENT_DOC_TITLE"));
                result.setContents(rs.getString("CONTENTS"));
                result.setFileCnt(rs.getInt("FILE_CNT"));
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
	 * selectAprvCurrentFlow : 해당 문서의 현재 결재 순서 
	 * 
	 * @param docCd 문서코드
	 * @return currentFlow 현재 결재 순서
	 */
	public int selectAprvCurrentFlow(String docCd)
	{
		int result = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;

        try
        {
            sql = "SELECT FN_GET_APRV_CURRENT_FLOW(?) AS CURRENT_FLOW FROM DUAL";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, docCd);

            rs = pstmt.executeQuery();

            if (rs.next())
                result = rs.getInt("CURRENT_FLOW");

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
	 * selectAprvLine : 결재라인 조회
	 * 
	 * @param docCd
	 * @return List<AprvLineDTO>
	 */
	public List<AprvLineDTO> selectAprvLine(String docCd)
	{
		List<AprvLineDTO> result = new ArrayList<AprvLineDTO>();
	
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String sql;
	    
	    try
	    {
	        sql = """
				  SELECT
					    A.DOC_CD
					  , A.FLOW
					  , A.EMP_CD
					  , FN_GET_EMP_NAME(A.EMP_CD) AS EMP_NAME
					  , B.COMMENTS
					  , B.REG_DT
					  , B.TYPE_CD
					  , (SELECT TYPE_NAME FROM APRV_TYPE WHERE TYPE_CD = B.TYPE_CD) AS TYPE_NAME    -- 결재종류명
					FROM APRV_LINE A
					LEFT JOIN APRV_LOG B ON A.DOC_CD = B.DOC_CD AND A.EMP_CD = B.EMP_CD
					WHERE A.DOC_CD = ?
					ORDER BY FLOW ASC
				  """;
	        
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, docCd);
	
	        rs = pstmt.executeQuery();
	
	        while (rs.next())
	        {
	        	AprvLineDTO dto = new AprvLineDTO();
	        	
	        	dto.setDocCd(rs.getString("DOC_CD"));
                dto.setFlow(rs.getInt("FLOW"));
                dto.setEmpCd(rs.getString("EMP_CD"));
                dto.setEmpName(rs.getString("EMP_NAME"));
                dto.setComments(rs.getString("COMMENTS"));
                dto.setRegDt(rs.getString("REG_DT"));
                dto.setTypeCd(rs.getString("TYPE_CD"));
                dto.setTypeName(rs.getString("TYPE_NAME"));
                
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
	 * selectAprvDocFile : 문서 첨부파일 조회
	 * 
	 * @param docCd
	 * @return
	 */
	public List<DocFileDTO> selectAprvDocFile(String docCd)
	{
		List<DocFileDTO> result = new ArrayList<DocFileDTO>();
	
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String sql;
	    
	    try
	    {
	        sql = """
				  SELECT 
					    A.DOC_FILE_NO
					  , B.ORIGIN_NAME
					  , B.SAVED_NAME
					  , B.PATH
					  , A.DOC_CD
					  , B.FILE_NO
					FROM DOC_FILE A
					JOIN SH_FILE B ON A.FILE_NO = B.FILE_NO
					WHERE DOC_CD = ?
				  """;
	        
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, docCd);
	
	        rs = pstmt.executeQuery();
	
	        while (rs.next())
	        {
	        	DocFileDTO dto = new DocFileDTO();
	        	
	        	dto.setDocFileNo(rs.getInt("DOC_FILE_NO"));
                dto.setOriginName(rs.getString("ORIGIN_NAME"));
                dto.setSavedName(rs.getString("SAVED_NAME"));
                dto.setPath(rs.getString("PATH"));
                dto.setDocCd(rs.getString("DOC_CD"));
                dto.setFileNo(rs.getInt("FILE_NO"));
                
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
	 * insertAprvLog : 결재이력 등록
	 * 
	 * @param dto 결재자 DTO
	 * 			  (문서코드, 결재자코드, 결재종류, 코멘트)
	 * @return
	 */
	public int insertAprvLog(AprvLineDTO dto)
	{
		int result = 0;

        CallableStatement cstmt = null;
        ResultSet rs = null;
        String sql;

        try
        {
            sql = "{ call PRC_APRV_LOG_INSERT(?, ?, ?, ?) }";

            cstmt = conn.prepareCall(sql);

            cstmt.setString(1, dto.getDocCd());
            cstmt.setString(2, dto.getTypeCd());
            cstmt.setString(3, dto.getEmpCd());
            cstmt.setString(4, dto.getComments());

            cstmt.execute();
            result = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            DBUtil.close(rs);
            DBUtil.close(cstmt);
        }
		
		return result;
	}
}
