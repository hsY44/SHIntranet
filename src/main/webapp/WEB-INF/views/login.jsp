<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%
	if (session.getAttribute("loginEmp") != null)
	{
		response.sendRedirect(request.getContextPath() + "/myinfo.do");
		return;
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SH Company 로그인</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
	<div class="login-wrap">
		<div class="login-box">
			<div class="login-title">SH Company</div>
			<form action="${pageContext.request.contextPath}/login.do"
				method="post">
				<div class="login-group">
					<label>사원코드</label> <input type="text" name="empid"
						placeholder="사원코드를 입력하세요 (예: SW0001)" required>
				</div>
				<div class="login-group">
					<label>비밀번호</label> <input type="password" name="emppw"
						placeholder="비밀번호를 입력하세요" required>
				</div>
				<button type="submit" class="login-btn">로그인</button>
			</form>
			<c:if test="${not empty errorMsg}">
				<div class="error-msg">${errorMsg}</div>
			</c:if>
		</div>
	</div>
</body>
</html>
