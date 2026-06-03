package com.sh.controller;

import java.io.IOException;
import java.util.List;

import com.sh.dao.WorkDAO;
import com.sh.dto.EmpDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/worktargetlist.do")
public class WorkTaretListController extends HttpServlet
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
		// 이전 페이지로 부터 넘어온 데이터 없음
		// 세션에 저장되어 있는 데이터
		// empCd , deptCd , grade
		
		EmpDTO dto = (EmpDTO)req.getSession().getAttribute("loginEmp");
		String empCd = dto.getEmpCd();
		String deptCd = dto.getDeptCd();
		int grade = dto.getGrade();
		
		// String empCd = "SW0001";
		// String deptCd = "DE01";
		// int grade = 55;
		
		WorkDAO dao = new WorkDAO();
		
		List<EmpDTO> result = dao.getWorkTarget(empCd,deptCd, grade);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		
		for(int i = 0; i < result.size(); i++)
		{
			sb.append("{");
			
			sb.append(String.format("\"empCd\":\"%s\",",result.get(i).getEmpCd()));
			sb.append(String.format("\"empName\":\"%s\",",result.get(i).getEmpName()));
			sb.append(String.format("\"positionName\":\"%s\"",result.get(i).getPositionName()));
			
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
		// req.getRequestDispatcher("worktargetresult.jsp").forward(req, resp);
	}
}
