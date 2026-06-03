<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%
	if (session.getAttribute("loginEmp") == null)
	{
		response.sendRedirect(request.getContextPath() + "/login.do");
		return;
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원 정보 수정</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/emp.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css">
<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
</head>
<body>
	<div class="container">

		<div class="sidebar" id="sidebar" data-hr="true"
			data-ctx="${pageContext.request.contextPath}"></div>

		<div class="main-wrapper">
			<div class="header" id="header" data-name="${sessionScope.loginEmp.empName}"
				data-buseo="${sessionScope.loginEmp.deptName}"
				data-jikgup="${sessionScope.loginEmp.positionName}"
				data-ctx="${pageContext.request.contextPath}"></div>

			<div class="content">
				<div class="edit-container">
					<div class="edit-title">사원 정보 수정 (인사부)</div>

					<form action="${pageContext.request.contextPath}/emp/update.do"
						method="post">
						<input type="hidden" name="empCd" value="${emp.empCd}">

						<div class="edit-group">
							<label>사원코드</label> <input type="text" value="${emp.empCd}"
								disabled>
						</div>

						<div class="edit-row">
							<div class="edit-group">
								<label>이름 <span style="color: #ef4444;">*</span></label> <input
									type="text" name="empName" value="${emp.empName}" required>
							</div>
							<div class="edit-group">
								<label>부서 <span style="color: #ef4444;">*</span></label> <select
									name="deptCd" required>
									<c:forEach var="d" items="${deptList}">
										<option value="${d.deptCd}"
											<c:if test="${d.deptCd == emp.deptCd}">selected</c:if>>
											${d.deptName}</option>
									</c:forEach>
								</select>
							</div>
						</div>

						<div class="edit-row">
							<div class="edit-group">
								<label>직급 <span style="color: #ef4444;">*</span></label> <select
									name="positionCd" required>
									<c:forEach var="p" items="${positionList}">
										<option value="${p.positionCd}"
											<c:if test="${p.positionCd == emp.positionCd}">selected</c:if>>${p.positionName}
										</option>
									</c:forEach>
								</select>
							</div>
							<div class="edit-group">
								<label>연락처</label> <input type="text" name="tel" value="${emp.tel}" maxlength="11">
							</div>
						</div>

						<div class="edit-row">
							<div class="edit-group">
								<label>이메일</label> <input type="email" name="email" value="${emp.email}">
							</div>
							<div class="edit-group">
								<label>주소</label> <input type="text" name="addr" value="${emp.addr}">
							</div>
						</div>
						<div class="edit-row">
							<div class="edit-group">
								<label>기존 비밀번호</label> <input type="text" name="opwd" value="${emp.pwd}" disabled="disabled">
							</div>
							<div class="edit-group">
								<label>새 비밀번호</label> <input type="password" name="pwd" placeholder="변경할 경우만 입력">
							</div>
						</div>

						<div class="edit-btns">
							<button type="submit" class="btn btn-primary">저장</button>
							<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/emp/list.do'">
								취소</button>
						</div>
					</form>
				</div>
			</div>
		</div>

	</div>
</body>
</html>
