package com.sh.controller;

import com.sh.dao.AprvSubmitDAO;
import com.sh.dto.AprvSubmitDTO;
import com.sh.dto.EmpDTO;
import com.sh.util.MyUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/aprv-submit/*")
public class AprvSubmitController extends HttpServlet
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
                getList(req, resp, sessionEmpCd);
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

    // 목록 조회
    protected void getList(HttpServletRequest req, HttpServletResponse resp, String sessionEmpCd) throws ServletException, IOException
    {

        AprvSubmitDAO dao = new AprvSubmitDAO();
        MyUtil util = new MyUtil();

        String cp = req.getContextPath();

        req.setCharacterEncoding("UTF-8");

        try
        {
            String page = req.getParameter("page");
            String status = req.getParameter("status");
            String title = req.getParameter("title");

            // 페이지 번호
            int currentPage = 1;
            if (page != null)
                currentPage = Integer.parseInt(page);

            // 검색
            if (status == null)
            {
                status = "none";
            }
            if (title == null)
            {
                title = "";
            }
            title = util.decodeUrl(title);

            // 전체 데이터 갯수(검색 여부 체크)
            int dataCount;
            if (status.equals("none") && "".equals(title))
                dataCount = dao.selectDataCount(sessionEmpCd);
            else
                dataCount = dao.selectDataCount(sessionEmpCd, status, title);

            // 전체 페이지 수
            int size = 10;
            int totalPage = util.pageCount(dataCount, size);
            if (currentPage>totalPage)
                currentPage=totalPage;

            // 상신내역 가져오기
            int offset = (currentPage - 1) * size;
            if (offset < 0)
                offset = 0;

            List<AprvSubmitDTO> list = null;
            if (status.equals("none") && "".equals(title))
                list = dao.selectAprvSubmitList(sessionEmpCd, offset, size);
            else
                list = dao.selectAprvSubmitList(sessionEmpCd, offset, size, status, title);

            // 페이징 처리
            String query = "";
            String listUrl = cp + "/aprv-submit/list";
            String detailUrl = cp + "/aprv/detail?page=" + currentPage;

            // 검색 상태
            if (!status.equals("none"))
                query = "status=" + status;

            // 검색 제목
            if (!("".equals(title)))
            {
                if (!query.isBlank())
                    query += "&";
                query += "title=" + util.encodeurl(title);
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
            req.setAttribute("status", status);
            req.setAttribute("title", title);
            req.setAttribute("query", query);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        viewPage(req, resp, "aprv/aprvSubmitList");
    }
	


}
