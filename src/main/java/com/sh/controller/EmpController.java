package com.sh.controller;

import java.io.IOException;
import java.util.List;

import com.sh.dao.DeptDAO;
import com.sh.dao.EmpDAO;
import com.sh.dao.RankDAO;
import com.sh.dto.DeptDTO;
import com.sh.dto.EmpDTO;
import com.sh.util.MyUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/emp/*")
public class EmpController extends HttpServlet
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

	private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("loginEmp") == null)
		{
			resp.sendRedirect(req.getContextPath() + "/login.do");
			return;
		}

		String uri = req.getRequestURI();
		String ctx = req.getContextPath();

		if (uri.equals(ctx + "/emp/list.do"))
			list(req, resp);
		
		else if (uri.equals(ctx + "/emp/search.do"))
			search(req, resp);
		
		else if (uri.equals(ctx + "/emp/insertForm.do"))
			insertForm(req, resp);
		
		else if (uri.equals(ctx + "/emp/insert.do"))
			insert(req, resp);
		
		else if (uri.equals(ctx + "/emp/edit.do"))
			editForm(req, resp);
		
		else if (uri.equals(ctx + "/emp/update.do"))
			update(req, resp);
		
		else if (uri.equals(ctx + "/emp/exitForm.do"))
			exitForm(req, resp);
		
		else if (uri.equals(ctx + "/emp/exitConfirm.do"))
			exitConfirm(req, resp);
		
		else if (uri.equals(ctx + "/emp/exit.do"))
			exit(req, resp);
		
		else if (uri.equals(ctx + "/emp/api/list"))
			empListApi(req, resp);
	}
	
	private void doListPage(HttpServletRequest req, HttpServletResponse resp, String viewPath, String baseUrl)throws ServletException, IOException
	{
		String schType = req.getParameter("schType");
		String kwd = req.getParameter("kwd");
		
		if (schType == null)
			schType = "";
		
		if (kwd == null)
			kwd = "";

		int pageSize = 5;
		int page = 1;
		
		String pageParam = req.getParameter("page");
		
		if (pageParam != null && !pageParam.isEmpty())
		{
			try
			{
				page = Integer.parseInt(pageParam);
			}
			catch (NumberFormatException e)
			{
				page = 1;
			}
		}
		
		if (page < 1)
			page = 1;

		String st = schType.isEmpty() ? null : schType;
		String kw = kwd.isEmpty() ? null : kwd;

		EmpDAO dao = new EmpDAO();
		int dataCount = dao.countEmp(st, kw);
		MyUtil myUtil = new MyUtil(pageSize);
		
		int totalPage = myUtil.pageCount(dataCount, pageSize);
		
		if (totalPage < 1)
			totalPage = 1;
		
		if (page > totalPage)
			page = totalPage;

		int start = (page - 1) * pageSize + 1;
		int end = start + pageSize - 1;

		String listUrl = baseUrl;
		
		if (st != null)
			listUrl += "?schType=" + st + "&kwd=" + myUtil.encodeurl(kwd);

		req.setAttribute("empList", dao.listEmpPage(st, kw, start, end));
		req.setAttribute("schType", schType);
		req.setAttribute("kwd", kwd);
		req.setAttribute("paging", myUtil.paging(page, totalPage, listUrl));
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("page", page);
		req.setAttribute("activeEmpCount", dao.countActiveEmp());
		req.getRequestDispatcher(viewPath).forward(req, resp);
	}

	// 사원 관리 목록 (인사부 전용)
	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doListPage(req, resp, "/WEB-INF/views/empinfoupdate.jsp", req.getContextPath() + "/emp/list.do");
	}

	// 사원 조회 (전 직원)
	private void search(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doListPage(req, resp, "/WEB-INF/views/search.jsp", req.getContextPath() + "/emp/search.do");
	}

	// 사원 등록 폼
	private void insertForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		req.setAttribute("deptList", new DeptDAO().listDept());
		req.setAttribute("positionList", new RankDAO().listRank());
		req.getRequestDispatcher("/WEB-INF/views/empAdd.jsp").forward(req, resp);
	}

	// 사원 등록 처리
	private void insert(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		EmpDTO dto = new EmpDTO();
		
		dto.setEmpName(req.getParameter("empName"));
		dto.setDeptCd(req.getParameter("deptCd"));
		dto.setPositionCd(req.getParameter("positionCd"));
		dto.setTel(req.getParameter("tel"));
		dto.setEmail(req.getParameter("email"));
		dto.setAddr(req.getParameter("addr"));
		dto.setPwd(req.getParameter("pwd"));
		dto.setHireDate(req.getParameter("hireDate")); 
		
		try
		{
			new EmpDAO().insertEmp(dto);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		resp.sendRedirect(req.getContextPath() + "/emp/list.do");
	}

	// 사원 수정 폼 (인사부 전용)
	private void editForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String empCd = req.getParameter("empCd");
		
		req.setAttribute("emp", new EmpDAO().findByEmpCd(empCd));
		req.setAttribute("deptList", new DeptDAO().listDept());
		req.setAttribute("positionList", new RankDAO().listRank());
		req.getRequestDispatcher("/WEB-INF/views/empEdit.jsp").forward(req, resp);
	}

	// 사원 수정 처리 (인사부 전용)
	private void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		EmpDAO dao = new EmpDAO();
		EmpDTO dto = new EmpDTO();
		dto.setEmpCd(req.getParameter("empCd"));
		dto.setEmpName(req.getParameter("empName"));
		dto.setDeptCd(req.getParameter("deptCd"));
		dto.setPositionCd(req.getParameter("positionCd"));
		dto.setTel(req.getParameter("tel"));
		dto.setEmail(req.getParameter("email"));
		dto.setAddr(req.getParameter("addr"));

		String newPwd = req.getParameter("pwd");
		
		if (newPwd == null || newPwd.isEmpty())
		{
			EmpDTO current = dao.findByEmpCd(dto.getEmpCd());
			dto.setPwd(current != null ? current.getPwd() : "");
		} 
		else
		{
			dto.setPwd(newPwd);
		}
		
		try
		{
			dao.updateEmpByHr(dto);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		resp.sendRedirect(req.getContextPath() + "/emp/list.do");
	}

	// 퇴사자 등록 폼 - 사원코드 입력
	private void exitForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		req.getRequestDispatcher("/WEB-INF/views/exitEmp.jsp").forward(req, resp);
	}

	// 퇴사자 확인 - 사원코드로 정보 조회 후 확인 화면 출력
	private void exitConfirm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String empCd = req.getParameter("empCd");
		EmpDAO dao  = new EmpDAO();
		EmpDTO emp  = dao.findByEmpCd(empCd);

		if (emp == null)
		{
			// VIEW_EMPLOYEE(재직자)에 없지만 EMP 원본에 있으면 이미 퇴사 처리된 사원
			String msg = dao.existsInEmpTable(empCd)
				? "이미 퇴사 처리된 사원입니다."
				: "존재하지 않는 사원코드입니다.";
			req.setAttribute("errorMsg", msg);
			req.getRequestDispatcher("/WEB-INF/views/exitEmp.jsp").forward(req, resp);
			return;
		}
		req.setAttribute("emp", emp);
		req.getRequestDispatcher("/WEB-INF/views/exitEmp.jsp").forward(req, resp);
	}

	// 퇴사 처리 - EMP_EXIT 프로시저 실행
	private void exit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String empCd = req.getParameter("empCd");
		EmpDAO dao   = new EmpDAO();

		// 이중 안전장치: 이미 퇴사됐거나 존재하지 않으면 처리 중단
		if (dao.findByEmpCd(empCd) == null)
		{
			resp.sendRedirect(req.getContextPath() + "/emp/list.do");
			return;
		}
		try
		{
			dao.exitEmp(empCd);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		resp.sendRedirect(req.getContextPath() + "/emp/list.do");
	}
	
	private void empListApi(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String schType = req.getParameter("schType");
		String kwd = req.getParameter("kwd");
		
		if (schType == null)
			schType = "";
		
		if (kwd == null)
			kwd = "";

		int pageSize = 5;
		int page = 1;
		
		String pageParam = req.getParameter("page");
		if (pageParam != null && !pageParam.isEmpty())
		{
			try
			{
				page = Integer.parseInt(pageParam);
			}
			catch (NumberFormatException e)
			{
				page = 1;
			}
		}
		if (page < 1)
			page = 1;

		String st = schType.isEmpty() ? null : schType;
		String kw = kwd.isEmpty() ? null : kwd;

		EmpDAO dao = new EmpDAO();
		int dataCount = dao.countEmp(st, kw);
		MyUtil myUtil = new MyUtil(pageSize);
		
		int totalPage = myUtil.pageCount(dataCount, pageSize);
		
		if (totalPage < 1)
			totalPage = 1;
		
		if (page > totalPage)
			page = totalPage;

		int start = (page - 1) * pageSize + 1;
		int end = start + pageSize - 1;

		List<EmpDTO> list = dao.listEmpPage(st, kw, start, end);
		
        StringBuffer sb = new StringBuffer();
        
        sb.append("{");
        sb.append("\"totalPage\":" + totalPage +",");
        sb.append("\"dataCount\":" + dataCount +",");
        sb.append("\"data\": [");
        for (int i = 0; i < list.size(); i++)
        {
              sb.append("{")
              .append("\"num\":\"" + (start+i) + "\",")
              .append("\"empCd\":\"" + list.get(i).getEmpCd() + "\",")
              .append("\"empName\":\"" + list.get(i).getEmpName() + "\",")
                .append("\"positionName\":\"" + list.get(i).getPositionName() + "\"")
                .append("}");
              
              if (i < list.size() - 1) {
                  sb.append(",");
              }
        }
        sb.append("]");
        sb.append("}");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(sb.toString());
		
	}
}
