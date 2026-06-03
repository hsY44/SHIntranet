package com.sh.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sh.dao.BoardDAO;
import com.sh.dto.BoardDTO;
import com.sh.dto.BoardDropDTO;
import com.sh.dto.BoardFileDTO;
import com.sh.dto.EmpDTO;
import com.sh.util.MyUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/board/*")
public class BoardAdminController extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	// get
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		process(req, resp);
	}
	
	// post
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		process(req, resp);
	}
	
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		req.setCharacterEncoding("UTF-8");
		
		HttpSession session = req.getSession(false);
		
		if (session == null || session.getAttribute("loginEmp") == null)
		{
			resp.sendRedirect(req.getContextPath() + "/login.do");
			return;
		}
		
		// get / post
		String method = req.getMethod();
		
		String uri = req.getRequestURI();
		
		// GET
		if (method.equalsIgnoreCase("GET"))
		{
			// 관리자용 게시글 리스트
			if (uri.endsWith("/list.do"))
				list(req, resp);
			
			// 게시물 삭제
			else if (uri.endsWith("/delete.do"))
				delete(req, resp);
			
			// 게시물 상세
			else if (uri.endsWith("/article.do"))
				article(req, resp);
		}
		
		// POST
		else if (method.equalsIgnoreCase("POST"))
		{
		}
		
		
	}
	
	// 포워딩 처리
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
	
	// 관리자용 게시글 리스트
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
	    BoardDAO dao = null;
		MyUtil util = null;
		String cp;
		
		try
		{
			cp = req.getContextPath();
			dao = new BoardDAO();
			util = new MyUtil();
			
			
			// 페이지 번호
			String page = req.getParameter("page");
			int currentPage = 1;
			
			if (page != null)
				currentPage = Integer.parseInt(page);
			
			// 검색
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if (schType==null)
			{
				schType = "all";
				kwd = "";
			}
			
			// 디코딩 처리
			kwd = util.decodeUrl(kwd);
			
			String type = req.getParameter("type");
			if (type == null)
				type = "002";
			
			// 전체 데이터 갯수
			int dataCount;
			if (kwd.isBlank())
				dataCount = dao.dataCount(type);
			else
				dataCount = dao.dataCount(schType, kwd, type);
			
			
			// 전체 페이지 수
			
			// 페이지에 출력할 게시물 갯수 
			int size = 10;
			int totalPage = util.pageCount(dataCount, size);
			if (currentPage > totalPage)
				currentPage = totalPage;
			
			// 게시물 리스트
			int offset = (currentPage - 1) * size;
			if (offset < 0)
				offset = 0;
			
			List<BoardDTO> list = null;
			
			// 검색 안했을 경우
			if (kwd.isBlank())
				list = dao.getList(offset, size, type);
			
			//검색 했을 경우
			else
				list = dao.getList(offset, size, type, schType, kwd);
			
			String query = "type=" + type;
			
			// 게시물 보기
			if (!kwd.isBlank())
			{
				query += "&schType=" + schType + "&kwd=" + util.encodeurl(kwd);
			}
			
			String listUrl = cp + "/admin/board/list.do?" + query;
			String articleUrl = cp + "/admin/board/article.do?" + query + "&page=" + currentPage;
			
			String paging = util.paging(currentPage, totalPage, listUrl);
			
			// 뷰 페이지에 속성 넘기기
			req.setAttribute("list", list);
			req.setAttribute("page", currentPage);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("size", size); 
			req.setAttribute("totalPage", totalPage); 
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("paging", paging); 
			req.setAttribute("schType", schType);
			req.setAttribute("kwd", kwd);
			req.setAttribute("type", type);
			req.setAttribute("query", query);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		viewPage(req, resp, "boardadmin");
	}
	
	// 게시글 삭제
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession();
	    EmpDTO loginEmp = (EmpDTO) session.getAttribute("loginEmp");
	    
		BoardDAO dao = null;
		BoardDropDTO dto = null;
		MyUtil util = new MyUtil();
		
		String page = req.getParameter("page");
		String type = req.getParameter("type");
		String query = "";
		
		try
		{
			dao = new BoardDAO();
			dto = new BoardDropDTO();
			
			dto.setEmpCd(loginEmp.getEmpCd());
			
			int num = Integer.parseInt(req.getParameter("num"));
			dto.setBoardNum(num);
			dto.setEmpCd(loginEmp.getEmpCd());
			dao.deleteBoard(dto);
			
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if (schType==null)
			{
				schType = "all";
				kwd = "";
			}
			
			int dataCount = 0;
	        if (kwd.isBlank()) {
	            dataCount = dao.dataCount(type);
	        } else {
	            dataCount = dao.dataCount(schType, kwd, type);
	        }
	        
	        int size = 10;
	        int totalPage = new MyUtil().pageCount(dataCount, size);

	        int currentPage = Integer.parseInt(page);
	        if (currentPage > totalPage) {
	            currentPage = totalPage;
	        }
	        
	        if (currentPage < 1) 
	        	currentPage = 1;
	        
	        page = String.valueOf(currentPage);
	        
			kwd = util.decodeUrl(kwd);
			
			query = "type=" + type + "&page=" + page;
			
			if (!kwd.isBlank())
				query += "&schType=" + schType + "&kwd=" + util.encodeurl(kwd);
			
			
	        
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		viewPage(req, resp, "redirect:/admin/board/list.do?"+ query);
	}
	
	// 게시물 상세
	protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		BoardDAO dao = null;
		BoardDTO dto = null;
		String page = req.getParameter("page");
		String type = req.getParameter("type");
		String query = "type=" + type + "&page=" + page;
		MyUtil util = null;
		
		try
		{
			dao = new BoardDAO();
			dto = new BoardDTO();
			util = new MyUtil();
			int num = Integer.parseInt(req.getParameter("num"));
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			
			if (schType == null)
			{
				schType = "all";
				kwd = "";
			}
			kwd = util.decodeUrl(kwd);
			
			if (!kwd.isBlank())
				query += "&schType=" + schType + "&kwd=" + util.encodeurl(kwd);
			
			// 게시글 불러오기
			dto = dao.getReadData(num);
			
			// 게시글이 없다면
			if (dto == null)
			{
				viewPage(req, resp, "redirect:/admin/board/list.do?" + query);
				return;
			}
			
			// 첨부파일 리스트
			List<BoardFileDTO> flist = new ArrayList<BoardFileDTO>();
			
			flist = dao.filelist(num);
			
			req.setAttribute("flist", flist);
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("type", type);
			req.setAttribute("query", query);
			
			viewPage(req, resp, "adminarticle");
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
