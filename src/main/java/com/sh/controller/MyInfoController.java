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

@WebServlet("/myinfo.do")
public class MyInfoController extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	// 내 정보 조회 - 뷰 forward
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession(false);
		
		if (session == null || session.getAttribute("loginEmp") == null)
		{
			resp.sendRedirect(req.getContextPath() + "/login.do");
			return;
		}
		
		req.getRequestDispatcher("/WEB-INF/views/myinfo.jsp").forward(req, resp);
	}

	// 내 정보 수정 처리 - redirect
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession(false);
		
		if (session == null || session.getAttribute("loginEmp") == null)
		{
			resp.sendRedirect(req.getContextPath() + "/login.do");
			return;
		}

		EmpDTO loginEmp = (EmpDTO) session.getAttribute("loginEmp");

		EmpDTO dto = new EmpDTO();
		
		dto.setEmpCd(loginEmp.getEmpCd());
		dto.setEmpName(req.getParameter("empName"));
		dto.setTel(req.getParameter("tel"));
		dto.setEmail(req.getParameter("email"));
		dto.setAddr(req.getParameter("addr"));

		String newPwd = req.getParameter("pwd");
		
		dto.setPwd((newPwd == null || newPwd.isEmpty()) ? loginEmp.getPwd() : newPwd);

		try
		{
			EmpDAO dao = new EmpDAO();
			dao.updateMyInfo(dto);
			
			EmpDTO updated = dao.findByEmpCd(loginEmp.getEmpCd());
			
			session.setAttribute("loginEmp", updated);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		resp.sendRedirect(req.getContextPath() + "/myinfo.do");
	}
}
