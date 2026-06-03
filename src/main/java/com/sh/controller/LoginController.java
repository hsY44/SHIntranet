package com.sh.controller;

import java.io.IOException;

import com.sh.dao.EmpDAO;
import com.sh.dto.EmpDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login.do")
public class LoginController extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	// GET: 로그인 폼만 표시
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession(false);
		
		if (session != null && session.getAttribute("loginEmp") != null)
		{
			resp.sendRedirect(req.getContextPath() + "/myinfo.do");
			return;
		}
		req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
	}

	// POST: 로그인 처리
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String empCd = req.getParameter("empid");
		String pwd   = req.getParameter("emppw");

		EmpDAO dao = new EmpDAO();
		EmpDTO emp = dao.login(empCd, pwd);

		if (emp == null)
		{
			req.setAttribute("errorMsg", "사원코드 또는 비밀번호가 올바르지 않습니다.");
			req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
		}
		else
		{
			HttpSession session = req.getSession();
			
			session.setAttribute("loginEmp", emp);
			resp.sendRedirect(req.getContextPath() + "/myinfo.do");
		}
	}
}
