package com.sh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sh.dto.WorkSubmitDTO;
import com.sh.util.DBConn;
import com.sh.util.JFunction;

public class JDocDAO
{
Connection conn = DBConn.getConnection();
	
	/*
	 * 전체 개수 가져와서 페이징 처리하는 메소드
	 */
	public String getPaging(int currentPage, int listCount, String empCd, String ctx,String url, String pageSql)
	{
		StringBuffer sb = new StringBuffer();
		
		// 전체 리스트 개수
		int countList = getCount(empCd, pageSql);
		
		// 가장 마지막 페이지 수
		int maxPage = countList / listCount;
		
		if(countList % listCount != 0)
		{
			maxPage += 1;
		}
		
		// 페이지는 최대 7개까지 보여준다.
		int pageCount = 7;
		
		// 현재 페이지 - 3 부터
		int startPage = currentPage - (pageCount/2);
		
		// 현재 페이지 + 3 까지 보여준다
		int endPage = currentPage + (pageCount/2);
		
		// 현재 페이지 - 3 이 1 이하라면 
		// 그 만큼 endPage 를 증가시킨다
		if(startPage < 1)
		{
			endPage += (1-startPage);
			startPage = 1;
		}
		
		// 현재 페이지 + 3 이 maxPage 를 넘는다면
		// 그 만큼 startPage 를 감소 시킨다
		if(endPage > maxPage)
		{
			startPage -= (endPage - maxPage);
			endPage = maxPage;
		}
		
		// 결과적으로 startPage 가 1 이하라면(전체 페이지가 7개가 안된다면)
		// startPage 를 1로 고정한다
		if(startPage < 1)
		{
			startPage = 1;
		}
		
		// startPage 부터 endPage 까지 만드는데
		// 현재 페이지는 색상을 바꿔서 만든다
		for(int i = startPage; i <= endPage; i++)
		{
			if(i == currentPage)
			{
				sb.append("<a href='" + ctx + "/" + url + "?page=" + i + "&listCount=" + listCount + "' style='color:orange'>" + i + "</a>");
			}
			else
			{
				sb.append("<a href='" + ctx + "/" + url + "?page=" + i + "&listCount=" + listCount + "'>" + i + "</a>");
			}
			
			sb.append("&nbsp;&nbsp");
		}
		
		return sb.toString();
	}

	
	/*
	 * 현재 페이지에 해당하는 목록만 전달하는 메소드
	 */
	public List<WorkSubmitDTO> getListItem(int currentPage, int listCount,String empCd, String listSql, JFunction func)
	{
		List<WorkSubmitDTO> result = new ArrayList<>();
		
		PreparedStatement pstmt = null;
		
		ResultSet rs = null;
		
		try
		{
			/*
			String sql = "SELECT A.DOC_CD, A.PARENT_DOC_CD, B.TYPE_NAME, "
					+ " C.WORK_NAME, A.TITLE, E.DEPT_NAME, D.EMP_NAME, "
					+ " F.POSITION_NAME, A.REG_DT "
					+ " FROM SH_DOCUMENT A "
					+ " JOIN DOC_TYPE B "
					+ " ON A.TYPE_CD = B.TYPE_CD "
					+ " JOIN WORK C "
					+ " ON A.WORK_CD = C.WORK_CD "
					+ " JOIN EMP D "
					+ " ON A.EMP_CD = D.EMP_CD "
					+ " JOIN DEPT E "
					+ " ON D.DEPT_CD = E.DEPT_CD "
					+ " JOIN EMP_POSITION F "
					+ " ON D.POSITION_CD = F.POSITION_CD"
					+ " ORDER BY REG_DT DESC";
			*/
			
			pstmt = conn.prepareStatement(listSql);
			
			if(empCd != null)
			{
				pstmt.setString(1, empCd);
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				/*
				WorkSubmitDTO dto = new WorkSubmitDTO();
				
				dto.setDocCd(rs.getString("DOC_CD"));
				dto.setParentCd(rs.getString("PARENT_DOC_CD"));
				dto.setTypeCd(rs.getString("TYPE_NAME"));
				dto.setWorkCd(rs.getString("WORK_NAME"));
				dto.setTitle(rs.getString("TITLE"));
				dto.setEmpCd(rs.getString("DEPT_NAME") + " " + rs.getString("EMP_NAME") + " " + rs.getString("POSITION_NAME"));
				dto.setRegDt(rs.getString("REG_DT"));
				*/
				
				// result.add(dto);
				
				result.add(func.getDto(rs));
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
		
		/*
		 * 본래는 DB 에서 부분만 잘라서 가져오는게 맞지만 ....
		 * 빠른 개발을 위해서 일단 전부 가져와서
		 * 자바에서 잘라내기...
		 * 만약 데이터가 1억개가 있고 그 중에 10개를 가져와야 하는 상황이라면
		 * 10개를 가져오기 위해서 1억개를 전부 가져오는 매우 비효율적인 상황이다
		 * 그치만 우린 작으니까 ㅎ
		 */
		return result
				.stream()									// 스트림 변환
				.skip((long)((currentPage-1)*listCount))	// (현재페이지-1) * 한 페이지의 개수 만큼 스킵
				.limit(listCount)							// 한 페이지 개수 만큼 가져옴
				.collect(Collectors.toList());				// 모아서 리스트로 만듬
	}
	
	/*
	 * 모든 문서의 개수를 가져오는 메소드
	 */
	private int getCount(String empCd, String countSql)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int result = -1;
		
		try
		{
			/*
			String sql = "SELECT COUNT(*) AS COUNT "
					+ " FROM SH_DOCUMENT";
			*/
			
			pstmt = conn.prepareStatement(countSql);
			
			if(empCd != null)
			{
				pstmt.setString(1, empCd);
			}
			
			rs = pstmt.executeQuery();
					
			if(rs.next())
			{
				result = rs.getInt("COUNT");
			}
		}
		catch (Exception e)
		{
			result = -1;
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
