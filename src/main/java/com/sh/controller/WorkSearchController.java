package com.sh.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sh.dao.WorkDAO;
import com.sh.dto.WorkDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/worksearch.do")
public class WorkSearchController extends HttpServlet
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
		// 이전 페이지로 부터 넘어온 데이터
		// type , keyword
		
		String type = req.getParameter("type");
		String keyword = req.getParameter("keyword");
		
		if(keyword == null)
		{
			keyword = "";
		}
		
		WorkDAO dao = new WorkDAO();
		
		List<WorkDTO> result = new ArrayList<WorkDTO>(); 
		
		result = dao.getWork(type,keyword);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		
		for(int i = 0; i < result.size(); i++)
		{
			sb.append("{");
			
			sb.append(String.format("\"num\":\"%d\",",result.get(i).getRowNum()));
			sb.append(String.format("\"workType\":\"%s\",",result.get(i).getTypeName()));
			sb.append(String.format("\"workName\":\"%s\",",result.get(i).getWorkName()));
			sb.append(String.format("\"regDt\":\"%s\",",result.get(i).getRegDt().substring(0, 10)));
			sb.append(String.format("\"name\":\"%s\",",result.get(i).getDeptName() + " " +  result.get(i).getEmpName() + " " + result.get(i).getPositionName()));
			sb.append(String.format("\"workCd\":\"%s\"",result.get(i).getWorkCd()));
			
			sb.append("}");
			
			if(i != result.size()-1)
			{
				sb.append(",");
			}
		}
		
		sb.append("]");
		
		resp.setContentType("application/json; charset=UTF-8");
		resp.getWriter().print(sb.toString());
		
		// req.setAttribute("list", result);
		// req.getRequestDispatcher("worksearchresult.jsp").forward(req, resp);
	}
}
