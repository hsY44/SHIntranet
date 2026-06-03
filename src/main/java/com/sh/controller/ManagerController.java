package com.sh.controller;

import java.io.IOException;
import java.util.List;

import com.sh.dao.DeptDAO;
import com.sh.dao.ManagerDAO;
import com.sh.dto.EmpDTO;
import com.sh.dto.ManagerDTO;
import com.sh.util.MyUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/manager-mgmt/*")
public class ManagerController extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		handleRequest(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		handleRequest(req, resp);
	}
	
	protected void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
        HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("loginEmp") == null)
		{
			resp.sendRedirect(req.getContextPath() + "/login.do");
			return;
		}

        // 로그인한 사원의 사원코드
        String sessionEmpCd = ((EmpDTO) session.getAttribute("loginEmp")).getEmpCd();

		String method = req.getMethod();
		String uri = req.getRequestURI();
		
		if (method.equalsIgnoreCase("get"))
		{
			if (uri.endsWith("/list"))
			{
				getList(req, resp);
			}
            else if (uri.endsWith("/write"))
            {
                addManagerForm(req, resp);
            }
            else if (uri.endsWith("/update"))
            {
                updateManagerForm(req, resp);
            }
            if (uri.endsWith("/api/getManagerIssue"))
            {
                getManagerIssue(req, resp);
            }
		}
		else if (method.equalsIgnoreCase("post"))
		{
            if (uri.endsWith("/addManager"))
            {
                addManager(req, resp, sessionEmpCd);
            }
		}
	}
	
	protected void viewPage(HttpServletRequest req, HttpServletResponse resp, String viewName) throws ServletException, IOException
	{
		final String REDIRECT_PREFIX = "redirect:";
		final String FORWARD_PREFIX = "/WEB-INF/views/";
		final String FORWARD_SUFFIX = ".jsp";
		
		if (viewName.startsWith(REDIRECT_PREFIX))
		{
			String cp = req.getContextPath();
			String uri = cp + viewName.substring(REDIRECT_PREFIX.length());
			resp.sendRedirect(uri);
			
			return;
		}
		
		RequestDispatcher dispatcher = req.getRequestDispatcher(FORWARD_PREFIX + viewName + FORWARD_SUFFIX);
		dispatcher.forward(req, resp);
	}
	
    // 담당자 목록 조회 및 이동
	protected void getList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		ManagerDAO dao = new ManagerDAO();
		MyUtil util = new MyUtil();
		
		String cp = req.getContextPath();
		
		try
		{
			String page = req.getParameter("page");
			String deptCd = req.getParameter("deptCd");
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");

			// 페이지 번호
			int currentPage = 1;
			if (page != null)
				currentPage = Integer.parseInt(page);

			// 검색
			if (deptCd == null)
			{
				deptCd = "none";
			}
			if (schType == null || kwd == null)
			{
				schType = "none";
				kwd = "";
			}
			kwd = util.decodeUrl(kwd);

			// 전체 데이터 갯수(검색 여부 체크)
			int dataCount;
			if (deptCd.equals("none") && schType.equals("none"))
				dataCount = dao.selectDataCount();
			else
				dataCount = dao.selectDataCount(deptCd, schType, kwd);
			
			// 전체 페이지 수
			int size = 10;
			int totalPage = util.pageCount(dataCount, size);
			if (currentPage>totalPage)
				currentPage=totalPage;
			
			// 담당자 가져오기
			int offset = (currentPage - 1) * size;
			if (offset < 0)
				offset = 0;
			
			List<ManagerDTO> list = null;
			if (deptCd.equals("none") && schType.equals("none"))
				list = dao.selectManagerList(offset, size);
			else
				list = dao.selectManagerList(offset, size, deptCd, schType, kwd);
			
			// 페이징 처리
			String query = "";
			String listUrl = cp + "/manager-mgmt/list";
			String detailUrl = cp + "/manager-mgmt/detail?page=" + currentPage;

			// 검색 부서코드
			if (!deptCd.equals("none"))
				query = "deptCd=" + deptCd;
			
			// 검색 업무명/사원명
			if (!schType.equals("none"))
			{
				if (!query.isBlank())
					query += "&";
				query += "schType=" + schType + "&kwd=" + util.encodeurl(kwd);
			}
			
			if (!query.isBlank())
			{
				listUrl += "?" + query;
				detailUrl += "&" + query;
			}
			
			String paging = util.paging(currentPage, totalPage, listUrl);

			// 뷰 페이지(jsp)에 넘겨줄 속성(attribute) 구성
			req.setAttribute("list", list);
			req.setAttribute("page", currentPage);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("size", size);
			req.setAttribute("totalPage", totalPage);
			req.setAttribute("detailUrl", detailUrl);
			req.setAttribute("paging", paging);
			req.setAttribute("schType", schType);
			req.setAttribute("kwd", kwd);
			req.setAttribute("query", query);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		viewPage(req, resp, "manager-mgmt/managerManagement");
		
	}

    // 담당자 등록 페이지로 이동
    protected void addManagerForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String page = req.getParameter("page");

        req.setAttribute("page", page);
        req.setAttribute("issueType", "S");

        viewPage(req, resp, "manager-mgmt/managerWrite");
    }

    // 담당자 등록 페이지로 이동
    protected void updateManagerForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String page = req.getParameter("page");

        req.setAttribute("page", page);
        req.setAttribute("issueType", "E");

        viewPage(req, resp, "manager-mgmt/managerWrite");
    }

    // 담당자 등록/수정하기 전 기간 체크
    protected void getManagerIssue(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String workCd = req.getParameter("work_cd");
        String empCd = req.getParameter("emp_cd");

        ManagerDAO dao = new ManagerDAO();
        ManagerDTO dto = dao.selectManagerIssue(workCd, empCd);

        StringBuffer sb = new StringBuffer();
        sb.append("{")
                .append("\"issueType\":\"" + dto.getIssueType() + "\",")
                .append("\"issueDate\":\"" + dto.getIssueDate() + "\"")
                .append("}");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(sb.toString());
    }

    // 담당자 등록
    protected void addManager(HttpServletRequest req, HttpServletResponse resp, String sessionEmpCd) throws ServletException, IOException
    {
        req.setCharacterEncoding("UTF-8");

        String work_cd = req.getParameter("work_cd");
        String emp_cd = req.getParameter("emp_cd");
        String date = req.getParameter("date");
        String issue_type = req.getParameter("issue_type");
        String reg_emp_cd = sessionEmpCd; // TODO 등록자 사원 코드 !!

        ManagerDTO manager = new ManagerDTO();
        manager.setWorkCd(work_cd);
        manager.setManagerEmpCd(emp_cd);
        manager.setRegEmpCd(reg_emp_cd);
        manager.setIssueDate(date);
        manager.setIssueType(issue_type);

        ManagerDAO dao = new ManagerDAO();
        int result = dao.insertManager(manager);

        String jsonResponse;
        if (result > 0)
            jsonResponse = "{\"success\": true, \"message\": \"담당자가 성공적으로 등록되었습니다.\"}";
        else
            jsonResponse = "{\"success\": false, \"message\": \"등록에 실패했습니다. 다시 시도해주세요.\"}";

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(jsonResponse);

    }

}
