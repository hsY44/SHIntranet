package com.sh.controller;

import java.io.IOException;

import com.sh.dao.AttendDAO;
import com.sh.dto.EmpDTO;
import com.sh.util.MyUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/attend/*")
public class AttendController extends HttpServlet
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

	private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession(false);
		
		if (session == null || session.getAttribute("loginEmp") == null)
		{
			resp.sendRedirect(req.getContextPath() + "/login.do");
			
			return;
		}

		String uri = req.getRequestURI();
		String ctx = req.getContextPath();

		if (uri.equals(ctx + "/attend/in.do"))
			attendIn(req, resp);
		
		else if (uri.equals(ctx + "/attend/out.do"))
			attendOut(req, resp);
		
		else
			list(req, resp);
	}

	// 출근 처리
	private void attendIn(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		EmpDTO loginEmp = (EmpDTO) req.getSession().getAttribute("loginEmp");
		AttendDAO dao   = new AttendDAO();

		if (dao.hasTodayIn(loginEmp.getEmpCd()))
		{
			req.getSession().setAttribute("attendError", "이미 오늘 출근 처리가 완료되었습니다.");
			resp.sendRedirect(req.getContextPath() + "/myinfo.do");
			return;
		}
		try
		{
			dao.insertAttendIn(loginEmp.getEmpCd());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		resp.sendRedirect(req.getContextPath() + "/myinfo.do");
	}

	// 퇴근 처리
	private void attendOut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		EmpDTO loginEmp = (EmpDTO) req.getSession().getAttribute("loginEmp");
		AttendDAO dao   = new AttendDAO();

		if (!dao.hasTodayIn(loginEmp.getEmpCd()))
		{
			req.getSession().setAttribute("attendError", "출근 기록이 없어 퇴근 처리할 수 없습니다.");
			resp.sendRedirect(req.getContextPath() + "/myinfo.do");
			return;
		}
		if (dao.hasTodayOut(loginEmp.getEmpCd()))
		{
			req.getSession().setAttribute("attendError", "이미 오늘 퇴근 처리가 완료되었습니다.");
			resp.sendRedirect(req.getContextPath() + "/myinfo.do");
			return;
		}
		try
		{
			dao.insertAttendOut(loginEmp.getEmpCd());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		resp.sendRedirect(req.getContextPath() + "/myinfo.do");
	}

	// 출퇴근 기록 목록 (인사부 전용)
	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String schType = req.getParameter("schType");
		String kwd = req.getParameter("kwd");
		
		if (schType == null)
			schType = "";
		
		if (kwd == null)
			kwd = "";

		int pageSize = 10;
		int page = 1;
		String pageParam = req.getParameter("page");
		if (pageParam != null && !pageParam.isEmpty())
		{
			try
			{
				page = Integer.parseInt(pageParam);
			}
			catch (NumberFormatException e)
			{
				page = 1;
			}
		}
		if (page < 1)
			page = 1;

		String st = schType.isEmpty() ? null : schType;
		String kw = kwd.isEmpty() ? null : kwd;

		AttendDAO dao = new AttendDAO();
		int dataCount = dao.countAttend(st, kw);
		MyUtil myUtil = new MyUtil();
		
		int totalPage = myUtil.pageCount(dataCount, pageSize);
		
		if (totalPage < 1)
			totalPage = 1;
		
		if (page > totalPage)
			page = totalPage;

		int start = (page - 1) * pageSize + 1;
		int end = start + pageSize - 1;

		String listUrl = req.getContextPath() + "/attend/list.do";
		
		if (st != null)
			listUrl += "?schType=" + st + "&kwd=" + myUtil.encodeurl(kwd);

		req.setAttribute("attendList", dao.listAttendPage(st, kw, start, end));
		req.setAttribute("schType", schType);
		req.setAttribute("kwd", kwd);
		req.setAttribute("paging", myUtil.paging(page, totalPage, listUrl));
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("page", page);
		req.getRequestDispatcher("/WEB-INF/views/attend.jsp").forward(req, resp);
	}

}
