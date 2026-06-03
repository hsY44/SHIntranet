package com.sh.controller;

import java.io.IOException;

import com.sh.dao.WorkDAO;
import com.sh.dto.EmpDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/deptworkadd.do")
public class DeptWorkAddController extends HttpServlet
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
		// 이전 페이지로 부터 넘어온 데이터
		// workCd , 세션에 있는 데이터 empCd
		
		String workCd = req.getParameter("workCd");
		
		EmpDTO dto = (EmpDTO)req.getSession().getAttribute("loginEmp");
		
		//String empCd = "SW0001";
		
		// String empCd = dto.getEmpCd();
		
		String deptCd = dto.getDeptCd();
		
		WorkDAO dao = new WorkDAO();
		
		int result = dao.deptWorkAdd(deptCd, workCd);
		
		resp.setContentType("application/json; charset=UTF-8");
		resp.getWriter().print("{\"result\":" + result + "}");
		
		// req.setAttribute("result", result);
		// req.getRequestDispatcher("deptworkaddresult.jsp").forward(req, resp);
	}
}
