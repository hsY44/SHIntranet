package com.sh.controller;

import java.io.IOException;

import com.sh.dao.WorkDAO;
import com.sh.dto.EmpDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/workadd.do")
public class WorkAddController extends HttpServlet
{
	private static final long serialVersionUID = 1L;

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
		// 이전 페이지로 부터 받은 데이터 
		// TYPE_CD / WORK_NAME
		
		String typeCd = req.getParameter("TYPE_CD");
		String workName = req.getParameter("WORK_NAME");
		EmpDTO dto = (EmpDTO)req.getSession().getAttribute("loginEmp");
		String empCd = dto.getEmpCd();
		
		//String empCd = "SW0001";
		
		WorkDAO dao = new WorkDAO();
		
		int result = dao.addWork(typeCd, empCd, workName);
		
		resp.setContentType("application/json; charset=UTF-8");
		
		resp.getWriter().print("{\"result\":" + result + "}");
		
		//req.setAttribute("result", result);
		//req.getRequestDispatcher("workaddresult.jsp").forward(req, resp);
	}
}
