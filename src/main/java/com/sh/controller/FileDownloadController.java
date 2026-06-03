package com.sh.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.sh.dao.FileDownloadDAO;
import com.sh.dto.BoardFileDTO;
import com.sh.dto.DocFileDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/download")
public class FileDownloadController extends HttpServlet
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
		String docFileNo = req.getParameter("docFileNo");
		String fileNoStr = req.getParameter("fileNo");
		
		FileDownloadDAO dao = new FileDownloadDAO();
		String originName = null;
	    String path = null;
		
		if (fileNoStr != null && !fileNoStr.isEmpty()) {
            int fileNo = Integer.parseInt(fileNoStr);
            BoardFileDTO file = dao.getBoardFile(fileNo);
            if (file != null) {
                originName = file.getOriginName();
                path = file.getPath();
            }
        }
		else if (docFileNo != null && !docFileNo.isEmpty()) {
			DocFileDTO file = dao.getFile(docFileNo);
			if (file != null) {
				originName = file.getOriginName();
				path = file.getPath();
			}
		}
		
		if(originName == null || path == null)
		{
			// 파일 찾기 실패
			return;
		}
		
		File realFile = new File(path);
		
		if(!realFile.exists())
		{
			// 존재하지 않는 파일
			return;
		}
		
		 String encodedName = URLEncoder.encode(originName, StandardCharsets.UTF_8)
                 .replace("+", "%20");
		
		resp.setContentType("application/octet-stream");
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + encodedName + "\"; filename*=UTF-8''" + encodedName);
		resp.setContentLengthLong(realFile.length());
		
		try (FileInputStream fis = new FileInputStream(realFile); OutputStream os = resp.getOutputStream())
		{
			
			byte[] buffer = new byte[8192];
			int len;
			
			while ((len = fis.read(buffer)) > 0)
			{
				os.write(buffer, 0, len);
			}
			
			os.flush();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
}
