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

@WebServlet("/work-receive/list")
public class WorkReceiveController extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private static final String URL = "work-receive/list";
	
	private static final String LIST_SQL = "SELECT A.DOC_CD, C.WORK_NAME, B.TITLE, B.REG_DT, "
										+ " D.EMP_NAME, E.DEPT_NAME, F.POSITION_NAME "
										+ " FROM WORK_TARGET A "
										+ " JOIN SH_DOCUMENT B "
										+ " ON A.DOC_CD = B.DOC_CD "
										+ " JOIN WORK C "
										+ " ON B.WORK_CD = C.WORK_CD "
										+ " JOIN EMP D "
										+ " ON D.EMP_CD = B.EMP_CD "
										+ " JOIN DEPT E "
										+ " ON E.DEPT_CD = D.DEPT_CD "
										+ " JOIN EMP_POSITION F "
										+ " ON D.POSITION_CD = F.POSITION_CD "
										+ " WHERE A.EMP_CD = ?"
										+ " ORDER BY REG_DT DESC";
	
	private static final String COUNT_SQL = "SELECT COUNT(*) AS COUNT "
										+ " FROM WORK_TARGET A JOIN SH_DOCUMENT B ON A.DOC_CD = B.DOC_CD "
										+ " WHERE A.EMP_CD = ?";
	
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
		
		String paging = dao.getPaging(page, listCount, empCd, ctx, URL,COUNT_SQL);
		
		List<WorkSubmitDTO> result = dao.getListItem(page, listCount, empCd, LIST_SQL,(rs)->
		{
			WorkSubmitDTO dto = new WorkSubmitDTO();
			
			dto.setDocCd(rs.getString("DOC_CD"));
			dto.setWorkCd(rs.getString("WORK_NAME"));
			dto.setTitle(rs.getString("TITLE"));
			dto.setRegDt(rs.getString("REG_DT"));
			dto.setEmpCd(rs.getString("DEPT_NAME") + " " + rs.getString("EMP_NAME") + " " + rs.getString("POSITION_NAME"));
			
			return dto;
		});
		
		req.setAttribute("listCount", listCount);
		req.setAttribute("paging", paging);
		req.setAttribute("result", result);
		
		req.getRequestDispatcher("/WEB-INF/views/work-submit/workreceive.jsp").forward(req, resp);
		
	}
}
