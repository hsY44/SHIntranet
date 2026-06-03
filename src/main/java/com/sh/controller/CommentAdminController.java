package com.sh.controller;

import java.io.IOException;
import java.util.List;

import com.sh.dao.BoardDAO;
import com.sh.dao.CommentDAO;
import com.sh.dto.BoardDropDTO;
import com.sh.dto.CommentDTO;
import com.sh.dto.CommentDropDTO;
import com.sh.dto.EmpDTO;
import com.sh.util.MyUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/comment/*")
public class CommentAdminController extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		process(req, resp);
	}
	
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		req.setCharacterEncoding("UTF-8");
		String uri = req.getRequestURI();
		
		// 관리자 전용 댓글 리스트 
		if (uri.endsWith("/list.do"))
		{
			list(req, resp);
		}
		
		// 댓글 삭제
		if (uri.endsWith("/delete.do"))
		{
			delete(req, resp);
		}
	}
	
	// 관리자용 댓글 리스트
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
	    CommentDAO dao = null;
		MyUtil util = null;
		String cp;
		String query = "";
		
		try
		{
			cp = req.getContextPath();
			dao = new CommentDAO();
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
			
			// 전체 데이터 갯수
			int dataCount;
			if (kwd.isBlank())
				dataCount = dao.dataCount();
			else
				dataCount = dao.dataCount(schType, kwd);
			
			
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
			
			List<CommentDTO> list = null;
			
			// 검색 안했을 경우
			if (kwd.isBlank())
				list = dao.getAdminList(offset, size);
			
			//검색 했을 경우
			else
				list = dao.getAdminList(offset, size, schType, kwd);
			
			// 게시물 보기
			if (!kwd.isBlank())
			{
				query += "?schType=" + schType + "&kwd=" + util.encodeurl(kwd);
			}
			
			String listUrl = cp + "/admin/comment/list.do" + query;
			
			String paging = util.paging(currentPage, totalPage, listUrl);
			String articleUrl = cp + "/admin/comment/article.do?" + query + "&page=" + currentPage;
			
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
			req.setAttribute("query", query);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		viewPage(req, resp, "commentadmin");
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
	
	// 댓글 삭제
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession();
	    EmpDTO loginEmp = (EmpDTO) session.getAttribute("loginEmp");
	    
		CommentDAO dao = null;
		CommentDropDTO dto = null;
		MyUtil util = new MyUtil();
		
		String page = req.getParameter("page");
		String query = "";
		
		try
		{
			dao = new CommentDAO();
			dto = new CommentDropDTO();
			
			dto.setEmpCd(loginEmp.getEmpCd());
			
			int num = Integer.parseInt(req.getParameter("num"));
			dto.setCommentNum(num);
			dto.setEmpCd(loginEmp.getEmpCd());
			dao.deleteComment(dto);
			
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			
			if (schType == null)
			{
				schType = "content";
				kwd = "";
			}
			
			int dataCount = 0;
	        if (kwd.isBlank()) {
	            dataCount = dao.dataCount();
	        } else {
	            dataCount = dao.dataCount(schType, kwd);
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
			
			query = "page=" + page;
			
			if (!kwd.isBlank())
				query += "&schType=" + schType + "&kwd=" + util.encodeurl(kwd);
			
			
	        
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		viewPage(req, resp, "redirect:/admin/comment/list.do?"+ query);
	}
}
