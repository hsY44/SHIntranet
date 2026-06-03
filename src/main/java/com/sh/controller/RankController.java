package com.sh.controller;

import java.io.IOException;

import com.sh.dao.RankDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/rank/*")
public class RankController extends HttpServlet
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
	
	// 공통 포워드 메서드. rank.jsp에 필요한 (직급 목록, 전체 이력 수, 현재 직급 수, 최근 등록 수)를 
	// request에 담아서 JSP로 포워드
	private void forwardList(HttpServletRequest req, HttpServletResponse resp, RankDAO dao) throws ServletException, IOException
	{
		req.setAttribute("rankList",    dao.listRank());
		req.setAttribute("totalCount",  dao.countTotalHistory());
		req.setAttribute("activeCount", dao.countActiveRank());
		req.setAttribute("recentCount", dao.countRecentRank());
		req.getRequestDispatcher("/WEB-INF/views/rank.jsp").forward(req, resp);
	}

	// 작업에 따른 분기 작업.
	private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession(false);

		if (session == null || session.getAttribute("loginEmp") == null)
		{
			resp.sendRedirect(req.getContextPath() + "/login.do");
			return;
		}

		String uri = req.getRequestURI();
		RankDAO dao = new RankDAO();

		if (uri.endsWith("/list.do"))
		{
			forwardList(req, resp, dao);
		}
		else if (uri.endsWith("/insert.do"))
		{
			String positionName = req.getParameter("positionName");
			int    grade        = Integer.parseInt(req.getParameter("grade"));
			if (dao.existsPositionName(positionName))
			{
				req.setAttribute("errorMsg", "이미 존재하는 직급명입니다.");
				forwardList(req, resp, dao);
				return;
			}
			if (dao.existsGrade(grade))
			{
				req.setAttribute("errorMsg", "이미 사용 중인 등급(" + grade + ")입니다.");
				forwardList(req, resp, dao);
				return;
			}
			try
			{
				dao.insertRank(positionName, grade);
			}
			catch (Exception e)
			{
				System.out.println("[RankController] insert 오류: " + e.getMessage());
			}
			resp.sendRedirect(req.getContextPath() + "/rank/list.do");
		}
		else if (uri.endsWith("/update.do"))
		{
			String positionCd   = req.getParameter("positionCd");
			String positionName = req.getParameter("positionName");
			int    grade        = Integer.parseInt(req.getParameter("grade"));
			if (dao.existsGradeExclude(positionCd, grade))
			{
				req.setAttribute("errorMsg", "이미 사용 중인 등급(" + grade + ")입니다.");
				forwardList(req, resp, dao);
				return;
			}
			try
			{
				dao.updateRank(positionCd, positionName, grade);
			}
			catch (Exception e)
			{
				System.out.println("[RankController] update 오류: " + e.getMessage());
			}
			resp.sendRedirect(req.getContextPath() + "/rank/list.do");
		}
		else if (uri.endsWith("/delete.do"))
		{
			String positionCd = req.getParameter("positionCd");
			int    empCount   = dao.countEmpInPosition(positionCd);
			if (empCount > 0)
			{
				req.setAttribute("errorMsg", "소속 직원이 " + empCount + "명 있어 삭제할 수 없습니다.");
				forwardList(req, resp, dao);
				return;
			}
			try
			{
				dao.deleteRank(positionCd);
			}
			catch (Exception e)
			{
				System.out.println("[RankController] delete 오류: " + e.getMessage());
			}
			resp.sendRedirect(req.getContextPath() + "/rank/list.do");
		}
	}
}
