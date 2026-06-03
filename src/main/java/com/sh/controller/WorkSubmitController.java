package com.sh.controller;

import java.io.IOException;
import java.util.List;

import com.sh.dao.JDocDAO;
import com.sh.dto.EmpDTO;
import com.sh.dto.WorkSubmitDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/work-submit/list")
public class WorkSubmitController extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private static final String URL = "work-submit/list";
	
	private static final String LIST_SQL = "SELECT A.DOC_CD, A.TYPE_CD, B.WORK_NAME, A.EMP_CD, A.TITLE, A.CONTENTS, A.REG_DT "
										+ " FROM SH_DOCUMENT A "
										+ " JOIN WORK B "
										+ " ON A.WORK_CD = B.WORK_CD "
										+ " WHERE A.TYPE_CD = '001' "
										+ " AND A.EMP_CD = ? "
										+ " ORDER BY REG_DT DESC";
								
	private static final String COUNT_SQL = "SELECT COUNT(*) AS COUNT "
										+ " FROM SH_DOCUMENT "
										+ " WHERE TYPE_CD = '001' AND EMP_CD = ?";
	
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
		
		EmpDTO emp = (EmpDTO)req.getSession().getAttribute("loginEmp");
		String empCd = emp.getEmpCd();
		
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
		
		String paging = dao.getPaging(page, listCount, empCd, ctx, URL, COUNT_SQL);
		
		List<WorkSubmitDTO> result = dao.getListItem(page, listCount, empCd, LIST_SQL, (rs)->
		{
			WorkSubmitDTO dto = new WorkSubmitDTO();
			
			dto.setDocCd(rs.getString("DOC_CD"));
			dto.setTypeCd(rs.getString("TYPE_CD"));
			dto.setWorkCd(rs.getString("WORK_NAME"));
			dto.setEmpCd(rs.getString("EMP_CD"));
			dto.setTitle(rs.getString("TITLE"));
			dto.setContents(rs.getString("CONTENTS"));
			dto.setRegDt(rs.getString("REG_DT"));
			
			return dto;
		});
		
		req.setAttribute("listCount",listCount);
		req.setAttribute("paging",paging);
		req.setAttribute("result", result);
		
		req.getRequestDispatcher("/WEB-INF/views/work-submit/worksubmitlist.jsp").forward(req, resp);
	}
}
