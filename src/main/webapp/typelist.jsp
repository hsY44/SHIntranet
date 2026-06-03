<%@page import="com.sh.dto.WorkDTO"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<% 
	List<WorkDTO> list = (List<WorkDTO>)request.getAttribute("result");

	StringBuilder sb = new StringBuilder();
	
	sb.append("[");
	
	for(int i = 0; i < list.size(); i++)
	{
		sb.append("{");
		
		sb.append(String.format("\"code\":\"%s\",",list.get(i).getTypeCd()));
		sb.append(String.format("\"name\":\"%s\"",list.get(i).getTypeName()));
		
		sb.append("}");
		
		if(i != list.size()-1)
		{
			sb.append(",");
		}
	}
	
	sb.append("]");
	
	out.println(sb.toString());
%>