package com.sh.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sh.dao.WorkDAO;
import com.sh.dto.DeptWorkDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/deptworksearch.do")
public class DeptWorkController extends HttpServlet
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
		
		WorkDAO dao = new WorkDAO();
		
		List<DeptWorkDTO> result = new ArrayList<>(); 
		
		result = dao.getDeptWork(type,keyword);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		
		for(int i = 0; i < result.size(); i++)
		{
			sb.append("{");
			
			sb.append(String.format("\"deptName\":\"%s\",",result.get(i).getDeptName()));
			sb.append(String.format("\"workType\":\"%s\",",result.get(i).getWorkType()));
			sb.append(String.format("\"workName\":\"%s\",",result.get(i).getWorkName()));
			sb.append(String.format("\"flow\":\"%d\"",result.get(i).getFlow()));
			
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
		// req.getRequestDispatcher("deptworksearchresult.jsp").forward(req, resp);
	}
}