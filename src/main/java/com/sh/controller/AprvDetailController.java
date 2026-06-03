package com.sh.controller;

import java.io.IOException;
import java.util.List;

import com.sh.dao.AprvDetailDAO;
import com.sh.dao.WorkTargetDAO;
import com.sh.dto.AprvDetailDTO;
import com.sh.dto.AprvLineDTO;
import com.sh.dto.DocFileDTO;
import com.sh.dto.EmpDTO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/aprv/detail/*")
public class AprvDetailController extends HttpServlet
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
			getDetailPage(req, resp, sessionEmpCd);
		}
		else if (method.equalsIgnoreCase("post"))
		{
            if (uri.endsWith("/addAprvLog"))
            {
            	addAprvLog(req, resp, sessionEmpCd);
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
	
	// 문서 상세 페이지
	protected void getDetailPage(HttpServletRequest req, HttpServletResponse resp, String sessionEmpCd) throws ServletException, IOException
	{
		String docCd = req.getParameter("docCd");
		AprvDetailDAO dao = new AprvDetailDAO();
		
		try
		{
			AprvDetailDTO detail = dao.selectAprvDetail(docCd);
			String typeCd = detail.getTypeCd(); 
			
			// 업무지시
			if (typeCd.equals("001"))
			{
				WorkTargetDAO wt = new WorkTargetDAO();
				
				List<String> workTargets = wt.getTarget(docCd);
				
				req.setAttribute("target", workTargets);
			}
			else
			{
				// 결재선 가져오기
				List<AprvLineDTO> lineList = dao.selectAprvLine(docCd);
				int currentFlow = dao.selectAprvCurrentFlow(docCd);
				req.setAttribute("lineList", lineList);
				req.setAttribute("currentFlow", currentFlow);
			}
			
			List<DocFileDTO> fileList = null;
			// 첨부파일이 있을 경우
			if (detail.getFileCnt() > 0)
			{
				fileList = dao.selectAprvDocFile(docCd);
			}
			
			req.setAttribute("data", detail);
			req.setAttribute("typeCd", typeCd);
			req.setAttribute("sessionEmpCd", sessionEmpCd);
			req.setAttribute("fileList", fileList);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		req.setAttribute("sessionEmpCd", sessionEmpCd);
        viewPage(req, resp, "aprv/aprvDetail");
	}
	
	// 결재이력 등록
	protected void addAprvLog(HttpServletRequest req, HttpServletResponse resp, String sessionEmpCd) throws ServletException, IOException
	{
        req.setCharacterEncoding("UTF-8");

        String doc_cd = req.getParameter("doc_cd");
        String type_cd = req.getParameter("type_cd");
        String comments = req.getParameter("comments");
        
        AprvLineDTO aprvLine = new AprvLineDTO();
        aprvLine.setDocCd(doc_cd);
        aprvLine.setEmpCd(sessionEmpCd);
        aprvLine.setTypeCd(type_cd);
        aprvLine.setComments(comments);
        
		AprvDetailDAO dao = new AprvDetailDAO();
		int result = dao.insertAprvLog(aprvLine);
        		
        String jsonResponse;
        if (result > 0)
            jsonResponse = "{\"success\": true, \"message\": \"결재가 성공적으로 등록되었습니다.\"}";
        else
            jsonResponse = "{\"success\": false, \"message\": \"등록에 실패했습니다. 다시 시도해주세요.\"}";

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(jsonResponse);
	}
}
