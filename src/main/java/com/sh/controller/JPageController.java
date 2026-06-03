package com.sh.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/page/*")
public class JPageController extends HttpServlet
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
		// req.getRequestDispatcher("/WEB-INF/views/worklist.jsp").forward(req, resp);;
		
		String parentCd = req.getParameter("parentCd");
		
		if(parentCd != null)
		{
			req.setAttribute("parentCd", parentCd);
		}
		
		String uri = req.getPathInfo();
		
		String path = "/WEB-INF/views/" + uri.substring(1,uri.indexOf(".")) + ".jsp";
		
		req.getRequestDispatcher(path).forward(req, resp);
	}
}
