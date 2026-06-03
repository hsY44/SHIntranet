package com.sh.controller;

import java.io.IOException;
import java.util.List;

import com.sh.dao.WorkDAO;
import com.sh.dto.WorkDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/worklist.do")
public class WorkListController extends HttpServlet
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
		// 넘어온 데이터 없음
		
		WorkDAO dao = new WorkDAO();
		
		List<WorkDTO> result = dao.getWorkType();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		
		for(int i = 0; i < result.size(); i++)
		{
			sb.append("{");
			
			sb.append(String.format("\"code\":\"%s\",",result.get(i).getTypeCd()));
			sb.append(String.format("\"name\":\"%s\"",result.get(i).getTypeName()));
			
			sb.append("}");
			
			if(i != result.size()-1)
			{
				sb.append(",");
			}
		}
		
		sb.append("]");
		
		resp.setContentType("application/json; charset=UTF-8");
		resp.getWriter().print(sb.toString());
		
		// req.setAttribute("result", result);
		// req.getRequestDispatcher("typelist.jsp").forward(req, resp);
	}
}
