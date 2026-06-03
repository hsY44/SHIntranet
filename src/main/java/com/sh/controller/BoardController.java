package com.sh.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sh.dao.BoardDAO;
import com.sh.dao.CommentDAO;
import com.sh.dto.BoardDTO;
import com.sh.dto.BoardDropDTO;
import com.sh.dto.BoardFileDTO;
import com.sh.dto.CommentDTO;
import com.sh.dto.CommentDropDTO;
import com.sh.dto.EmpDTO;
import com.sh.util.MyUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/board/*")
@MultipartConfig
(
	    fileSizeThreshold = 1024 * 1024,      // 1MB - 메모리 임계값
	    maxFileSize       = 1024 * 1024 * 10, // 10MB - 파일 1개 최대 크기
	    maxRequestSize    = 1024 * 1024 * 50  // 50MB - 요청 전체 최대 크기
)
public class BoardController extends HttpServlet
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
			// 게시물 리스트
			if (uri.endsWith("/list.do"))
				list(req, resp);
			
			// 게시물 상세
			else if (uri.endsWith("/article.do"))
				article(req, resp);
			
			// 게시물 글쓰기 폼
			else if (uri.endsWith("/write.do"))
				writeForm(req, resp);
			
			// 게시물 수정 폼
			else if (uri.endsWith("/update.do"))
				updateForm(req, resp);
			
			// 게시물 삭제이력
			else if (uri.endsWith("/delete.do"))
				delete(req, resp);
		}
		
		// POST
		else if (method.equalsIgnoreCase("POST"))
		{
			if(uri.endsWith("/write.do"))
			{
				// 게시물 등록 액션
				writeSubmit(req, resp);
			}
			else if (uri.endsWith("/update.do"))
			{
				// 게시물 수정 액션
				updateSubmit(req, resp);
			}
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

	// 게시물 리스트
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
			
			// 게시판 타입
			String type = req.getParameter("type");
			if (type == null)
				type = "002";
			
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
			
			String listUrl = cp + "/board/list.do?" + query;
			String articleUrl = cp + "/board/article.do?" + query + "&page=" + currentPage;
			
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
		
		viewPage(req, resp, "boardlist");
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
				viewPage(req, resp, "redirect:/board/list.do?" + query);
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
			
			viewPage(req, resp, "boardarticle");
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// 게시물 글쓰기 폼
	protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		req.setAttribute("mode", "write");
		
		String type = req.getParameter("type");
		if (type == null)
			type = "002";
		req.setAttribute("type", type);
		
		viewPage(req, resp, "boardwrite");
	}
	
	// 게시물 수정 폼
	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		
		BoardDAO dao = null;
		BoardFileDTO fdto = null;
		MyUtil util = null;
		String page = req.getParameter("page");
		String type = req.getParameter("type");
		String query = "type=" + type + "&page=" + page;
		
		try
		{
			dao = new BoardDAO();
			util = new MyUtil();
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			
			if (schType==null)
			{
				schType = "all";
				kwd = "";
			}
			kwd = util.decodeUrl(kwd);
			
			// 검색 상태인 경우
			if (!kwd.isBlank())
			{
				query += "&schType=" + schType + "&kwd=" + util.encodeurl(kwd);
			}
			
			int num = Integer.parseInt(req.getParameter("num"));
			
			// DB 의 테이블에서 해당 게시물 가져오기
			BoardDTO dto = dao.getReadData(num);
			
			// 게시물이 존재하지 않을 경우
			// → 리스트 페이지를 다시 요청할 수 있도록 안내
			if (dto==null)
			{
				viewPage(req, resp, "redirect:/board/list.do?type=" + type +"&page=" + page);
				return;
			}
			
			// 첨부파일 리스트
			List<BoardFileDTO> flist = new ArrayList<BoardFileDTO>();
			
			flist = dao.filelist(num);
			
			req.setAttribute("flist", flist);
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("mode", "update");
			req.setAttribute("query", query);
			req.setAttribute("schType", schType);
			req.setAttribute("kwd", kwd);
			req.setAttribute("type", type);
			
			
			viewPage(req, resp, "boardwrite");
			return;
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		viewPage(req, resp, "redirect:/board/list.do?" + query);
	}
	
	// 게시물 등록
	protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		HttpSession session = req.getSession();
	    EmpDTO loginEmp = (EmpDTO) session.getAttribute("loginEmp");
	    
		String type = req.getParameter("type");
		String query = "type=" + type;
		
		BoardDAO dao = null;
		BoardDTO dto = null;
		MyUtil util = new MyUtil();
		try
		{
			dao = new BoardDAO();
			dto = new BoardDTO();
			
			dto.setTitle(req.getParameter("title"));
			
			String contents = req.getParameter("contents");
			contents = util.htmlSymbols(contents);
			dto.setContents(contents);
			dto.setType(type);
			
			// 작성자 사번
			dto.setEmpCd(loginEmp.getEmpCd());
			// 작성자 이름
			dto.setName(loginEmp.getEmpCd());
			
			// 첨부파일 파트
			Collection<jakarta.servlet.http.Part> parts = req.getParts();
			
			dao.insertBoard(dto, parts);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		viewPage(req, resp, "redirect:/board/list.do?" + query);
	}
	// 게시물 수정
	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession();
	    EmpDTO loginEmp = (EmpDTO) session.getAttribute("loginEmp");
	    
		BoardDAO dao = null;
		
		String page = req.getParameter("page");
		String type = req.getParameter("type");
		String query = "type=" + type + "&page=" + page;
		MyUtil util = new MyUtil();
		
		try
		{
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			
			if (schType==null)
			{
				schType = "all";
				kwd = "";
			}
			kwd = util.decodeUrl(kwd);
			
			// 검색 상태인 경우
			if (!kwd.isBlank())
			{
				query += "&schType=" + schType + "&kwd=" + util.encodeurl(kwd);
			}
			
			dao = new BoardDAO();
			BoardDTO dto = new BoardDTO();
			BoardFileDTO fdto = new BoardFileDTO();
			
			dto.setNum(Integer.parseInt(req.getParameter("num")));
			dto.setName(loginEmp.getEmpCd());
			dto.setTitle(req.getParameter("title"));
			
			String contents = req.getParameter("contents");
			contents = util.htmlSymbols(contents);
			dto.setContents(contents);
			
			dao.updateBoard(dto);
			
			// 첨부파일 삭제
			// jsp에서 보낸 삭제처리할 파일들 번호 배열
			String[] delFiles = req.getParameterValues("delFiles");
			if (delFiles != null) {
				for (String fNo : delFiles) {
					int fileNo = Integer.parseInt(fNo);
					
					// 게시판첨부파일이력에 추가 'D' : 삭제
					dao.insertBoardFileLog(dto.getNum(), fileNo, "D");
				}
			}
			
			// 첨부파일 추가 처리
	        Collection<Part> parts = req.getParts();
	        for (Part part : parts) 
	        {
	            String originalFileName = part.getSubmittedFileName();
	            
	            // 전송된 파라미터 중 파일이 있고 파일명이 존재할 때만 처리
	            if (part.getName().equals("selectFile") && originalFileName != null && !originalFileName.isEmpty()) 
	            {
	                String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
	                String saveFileName = java.util.UUID.randomUUID().toString() + ext;
	                String path = "C:/savefile/" + saveFileName;
	                
	                // 3-1. 파일 마스터 테이블(SH_FILE) 등록
	                int fileNo = dao.insertFile(loginEmp.getEmpCd(), originalFileName, saveFileName, path);
	                
	                if (fileNo != -1) 
	                {
	                    dao.insertBoardFileLog(dto.getNum(), fileNo, "C");
	                    part.write(path);
	                }
	            }
	        }
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		viewPage(req, resp, "redirect:/board/list.do?" + query);
	}
	
	// 게시글 삭제이력
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession();
	    EmpDTO loginEmp = (EmpDTO) session.getAttribute("loginEmp");
	    
		BoardDAO dao = null;
		BoardDropDTO dto = null;
		MyUtil util = new MyUtil();
		
		String page = req.getParameter("page");
		String type = req.getParameter("type");
		String query = "type=" + type + "&page=" + page;
		
		try
		{
			dao = new BoardDAO();
			dto = new BoardDropDTO();
			
			dto.setEmpCd(loginEmp.getEmpCd());
			
			int num = Integer.parseInt(req.getParameter("num"));
			dto.setBoardNum(num);
			dto.setEmpCd(loginEmp.getEmpCd());
			
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
			
			dao.deleteBoard(dto);
			
	        
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		viewPage(req, resp, "redirect:/board/list.do?"+ query);
	}
	
	
	
	
}
