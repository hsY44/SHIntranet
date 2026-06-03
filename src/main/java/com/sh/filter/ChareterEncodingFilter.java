package com.sh.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletRequest;

@WebFilter( urlPatterns ="/*"
            ,initParams = @WebInitParam(name="charset",value="UTF-8"))
public class ChareterEncodingFilter implements Filter
{
	// check~!!!
	private String charset;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		charset = filterConfig.getInitParameter("charset");
		if (charset==null || charset.length()==0)
		{
			charset="UTF-8";
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException
	{
		if ((req instanceof HttpServletRequest request))
		{
			if(request.getMethod().equalsIgnoreCase("POST"))
				request.setCharacterEncoding(charset);
		}
		 chain.doFilter(req, resp);
	}
	
}
