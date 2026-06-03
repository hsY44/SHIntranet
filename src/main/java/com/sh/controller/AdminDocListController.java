package com.sh.controller;

import java.io.IOException;
import java.util.List;

import com.sh.dao.JDocDAO;
import com.sh.dto.WorkSubmitDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/adminList")
public class AdminDocListController extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private static final String URL = "adminList";
	
	private static final String LIST_SQL = "SELECT A.DOC_CD, A.PARENT_DOC_CD, B.TYPE_NAME, "
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
	
	private static final String COUNT_SQL = "SELECT COUNT(*) AS COUNT "
										+ " FROM SH_DOCUMENT";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		process(req, resp);
	}

	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		// 이전 페이지로부터 넘어온 데이터 
		// listCount , page
		
		String listCountStr = req.getParameter("listCount");
		String pageStr = req.getParameter("page");
		
		int listCount;
		int page;
		
		if(listCountStr == null)
		{
			listCount = 3;
		}
		else
		{
			listCount = Integer.parseInt(listCountStr);
		}
		
		if(pageStr == null)
		{
			page = 1;
		}
		else
		{
			page = Integer.parseInt(pageStr);
		}
		
		String ctx= req.getContextPath();
		
		JDocDAO dao = new JDocDAO();
		
		String paging = dao.getPaging(page, listCount, null, ctx, URL, COUNT_SQL);
		
		List<WorkSubmitDTO> result = dao.getListItem(page, listCount, null, LIST_SQL,(rs)->
		{
			WorkSubmitDTO dto = new WorkSubmitDTO();
			
			dto.setDocCd(rs.getString("DOC_CD"));
			dto.setParentCd(rs.getString("PARENT_DOC_CD"));
			dto.setTypeCd(rs.getString("TYPE_NAME"));
			dto.setWorkCd(rs.getString("WORK_NAME"));
			dto.setTitle(rs.getString("TITLE"));
			dto.setEmpCd(rs.getString("DEPT_NAME") + " " + rs.getString("EMP_NAME") + " " + rs.getString("POSITION_NAME"));
			dto.setRegDt(rs.getString("REG_DT"));
			
			return dto;
		});
		
		req.setAttribute("listCount",listCount);
		req.setAttribute("paging",paging);
		req.setAttribute("result", result);
		
		req.getRequestDispatcher("/WEB-INF/views/insa-document/documentlist.jsp").forward(req, resp);
	}
}
