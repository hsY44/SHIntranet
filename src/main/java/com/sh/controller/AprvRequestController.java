package com.sh.controller;

import java.io.IOException;
import java.util.Collection;

import com.sh.dao.AprvDAO;
import com.sh.dto.AprvDTO;
import com.sh.dto.EmpDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/aprvrequest.do")
@MultipartConfig
(
	    fileSizeThreshold = 1024 * 1024,      // 1MB - 메모리 임계값
	    maxFileSize       = 1024 * 1024 * 10, // 10MB - 파일 1개 최대 크기
	    maxRequestSize    = 1024 * 1024 * 50  // 50MB - 요청 전체 최대 크기
)
public class AprvRequestController extends HttpServlet 
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

	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		// 이전 페이지로부터 넘어온 데이터
		// title | content | docType | workCd | targetCds | parentCd
		// 세션에 저장되어 있는 empCd
		
		String parentCd = req.getParameter("parentCd");
		
		String title = req.getParameter("title");
		String content = req.getParameter("content");
		String docType = req.getParameter("docType");
		String workCd = req.getParameter("workCd");
		
		EmpDTO emp = (EmpDTO)req.getSession().getAttribute("loginEmp");
		String empCd = emp.getEmpCd();
		
		// String empCd = "SW0001";
		
		String[] workTarget = req.getParameterValues("target");
		
		Collection<Part> parts = req.getParts();
		
		AprvDTO dto = new AprvDTO();
		
		dto.setParentCd(parentCd);
		dto.setTitle(title);
		dto.setContent(content);
		dto.setDocTypeCd(docType);
		dto.setWorkCd(workCd);
		dto.setEmpCd(empCd);
		
		if(workTarget != null)
		{
			dto.setWorkTargetEmpCd(workTarget);
		}
		
		AprvDAO dao = new AprvDAO();
		
		String result = "";
		
		if(!docType.equals("001"))
		{
			result = dao.aprvDocInsert(dto, parts);
		}
		else
		{
			result = dao.workTargetDocInsert(dto, parts);
		}
		
		resp.setContentType("application/json; charset=UTF-8");
		resp.getWriter().print("{\"result\":\"" + result + "\"}");
		
		// req.setAttribute("result", result);
		// req.getRequestDispatcher("aprvrequestresult.jsp").forward(req, resp);;
	}
}
