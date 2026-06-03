package com.sh.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sh.util.MyUtil;
import com.sh.dao.CommentDAO;
import com.sh.dto.CommentDTO;
import com.sh.dto.CommentDropDTO;
import com.sh.dto.EmpDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/comment/*"	)
public class CommentController extends HttpServlet
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
		
		// 댓글 등록 및 삭제 후 목록 갱신 
		if (uri.endsWith("/list.do"))
		{
			list(req, resp);
		}
		
		// 댓글 등록
		if (uri.endsWith("/insert.do"))
		{
			insert(req, resp);
		}
		
		// 댓글 삭제
		if (uri.endsWith("/delete.do"))
		{
			delete(req, resp);
		}
	}
	
	// 댓글 리스트
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		int num = Integer.parseInt(req.getParameter("num"));
		CommentDAO dao = null;
		
		try
		{
			dao = new CommentDAO();
			List<CommentDTO> list = new ArrayList<CommentDTO>();
			
			list = dao.getList(num);
			StringBuilder json = new StringBuilder();
			
	        json.append("[");
	        
	        for(int i=0; i<list.size(); i++) {
	            CommentDTO dto = list.get(i);
	            json.append("{");
	            json.append("\"num\":" + dto.getNum() + ",");
	            json.append("\"name\":\"" + dto.getName() + "\",");
	            json.append("\"empCd\":\"" + dto.getEmpCd() + "\",");
	            json.append("\"regDate\":\"" + dto.getRegDate() + "\",");
	            
	            String contents = dto.getContents().replace("\"", "\\\"").replace("\n", "<br>");
	            json.append("\"contents\":\"" + contents + "\",");
	            json.append("\"dropNum\":" + dto.getDropNum());
	            json.append("}");
	            
	            if(i < list.size() - 1) 
	            	json.append(",");
	        }
	        json.append("]");

	        resp.setContentType("application/json; charset=UTF-8");
	        resp.getWriter().print(json.toString());
	        resp.getWriter().flush();
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// 댓글 입력
	protected void insert(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession();
	    EmpDTO loginEmp = (EmpDTO) session.getAttribute("loginEmp");
	    
		CommentDAO dao = null;
		CommentDTO dto = null;
		MyUtil util = new MyUtil();
		
		try
		{
			dao = new CommentDAO();
			dto = new CommentDTO();
			
			dto.setName(loginEmp.getEmpCd());
			
			int num = Integer.parseInt(req.getParameter("num"));
			String contents = req.getParameter("contents");
			contents = util.htmlSymbols(contents);
			dto.setBoardNum(num);
			dto.setContents(contents);
			
			dao.insertComment(dto);
			
			// 에러 발생 시 에러 메세지
			resp.setContentType("application/json; charset=UTF-8");
	        resp.getWriter().print("{\"result\": \"success\"}");
	        resp.getWriter().flush();
	        
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// 댓글 삭제
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession();
	    EmpDTO loginEmp = (EmpDTO) session.getAttribute("loginEmp");
	    
		CommentDAO dao = null;
		CommentDropDTO dto = null;
		MyUtil util = new MyUtil();
		
		try
		{
			dao = new CommentDAO();
			dto = new CommentDropDTO();
			
			dto.setEmpCd(loginEmp.getEmpCd());
			
			int num = Integer.parseInt(req.getParameter("num"));
			dto.setCommentNum(num);
			dto.setEmpCd(loginEmp.getEmpCd());
			
			dao.deleteComment(dto);
			
			// 에러 발생 시 에러 메세지
			resp.setContentType("application/json; charset=UTF-8");
	        resp.getWriter().print("{\"result\": \"success\"}");
	        resp.getWriter().flush();
	        
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
