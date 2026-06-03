package com.sh.controller;

import java.io.IOException;
import java.util.List;

import com.sh.dao.DeptDAO;
import com.sh.dto.DeptDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/dept/*")
public class DeptController extends HttpServlet
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
	
	// 공통 포워드 메서드. buseo.jsp에 필요한 (부서 목록, 전체 이력 수, 현재 부서 수, 최근 등록 수)를 
	// request에 담아서 JSP로 포워드
	private void forwardList(HttpServletRequest req, HttpServletResponse resp, DeptDAO dao) throws ServletException, IOException
	{
		req.setAttribute("deptList",    dao.listDept());
		req.setAttribute("totalCount",  dao.countTotalHistory());
		req.setAttribute("activeCount", dao.countActiveDept());
		req.setAttribute("recentCount", dao.countRecentDept());
		req.getRequestDispatcher("/WEB-INF/views/buseo.jsp").forward(req, resp);
	}

	// 분기 처리
	private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession(false);

		if (session == null || session.getAttribute("loginEmp") == null)
		{
			resp.sendRedirect(req.getContextPath() + "/login.do");
			return;
		}

		String uri = req.getRequestURI();
		DeptDAO dao = new DeptDAO();

		if (uri.endsWith("/list.do"))
		{
			forwardList(req, resp, dao);
		}
		else if (uri.endsWith("/insert.do"))
		{
			String deptName = req.getParameter("deptName");
			if (dao.existsDeptName(deptName))
			{
				req.setAttribute("errorMsg", "이미 존재하는 부서명입니다.");
				forwardList(req, resp, dao);
				return;
			}
			try
			{
				dao.insertDept(deptName);
			}
			catch (Exception e)
			{
				System.out.println("[DeptController] insert 오류: " + e.getMessage());
			}
			resp.sendRedirect(req.getContextPath() + "/dept/list.do");
		}
		else if (uri.endsWith("/update.do"))
		{
			String deptCd   = req.getParameter("deptCd");
			String deptName = req.getParameter("deptName");
			if (dao.existsDeptNameExclude(deptCd, deptName))
			{
				req.setAttribute("errorMsg", "이미 존재하는 부서명입니다.");
				forwardList(req, resp, dao);
				return;
			}
			try
			{
				dao.updateDept(deptCd, deptName);
			}
			catch (Exception e)
			{
				System.out.println("[DeptController] update 오류: " + e.getMessage());
			}
			resp.sendRedirect(req.getContextPath() + "/dept/list.do");
		}
		else if (uri.endsWith("/delete.do"))
		{
			String deptCd  = req.getParameter("deptCd");
			int    empCount = dao.countEmpInDept(deptCd);
			if (empCount > 0)
			{
				req.setAttribute("errorMsg", "소속 직원이 " + empCount + "명 있어 삭제할 수 없습니다.");
				forwardList(req, resp, dao);
				return;
			}
			try
			{
				dao.deleteDept(deptCd);
			}
			catch (Exception e)
			{
				System.out.println("[DeptController] delete 오류: " + e.getMessage());
			}
			resp.sendRedirect(req.getContextPath() + "/dept/list.do");
		}
		// 페이지 이동 없이 부서 데이터를 가져오기 위해 JSON으로 변환 후 사용
		else if (uri.endsWith("/api/list"))
		{
			List<DeptDTO> list = dao.listDept();
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			for (int i = 0; i < list.size(); i++)
			{
				sb.append("{");
				sb.append("\"deptCd\":\"" + list.get(i).getDeptCd() + "\",");
				sb.append("\"deptName\":\"" + list.get(i).getDeptName() + "\"");
				sb.append("}");
				if (i < list.size() - 1) sb.append(",");
			}
			sb.append("]");
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().print(sb.toString());
		}
	}
}
