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
<title>사원 등록</title>
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
				data-ctx="${pageContext.request.contextPath}">
			</div>

			<div class="content">
				<div class="add-container">
					<div class="add-title">사원 등록</div>

					<div class="add-notice">사원코드는 시스템에서 자동 생성됩니다 (SW + 4자리 번호).</div>

					<form action="${pageContext.request.contextPath}/emp/insert.do"
						method="post">

						<div class="add-row">
							<div class="add-group">
								<label>이름 <span style="color: #ef4444;">*</span></label> <input
									type="text" name="empName" placeholder="이름을 입력하세요" required>
							</div>
							<div class="add-group">
								<label>초기 비밀번호 <span style="color: #ef4444;">*</span></label> <input
									type="password" name="pwd" placeholder="1234" required>
							</div>
						</div>

						<div class="add-row">
							<div class="add-group">
								<label>부서 <span style="color: #ef4444;">*</span></label> <select
									name="deptCd" required>
									<option value="">-- 부서 선택 --</option>
									<c:forEach var="d" items="${deptList}">
										<option value="${d.deptCd}">${d.deptName}</option>
									</c:forEach>
								</select>
							</div>
							<div class="add-group">
								<label>직급 <span style="color: #ef4444;">*</span></label> <select
									name="positionCd" required>
									<option value="">-- 직급 선택 --</option>
									<c:forEach var="p" items="${positionList}">
										<option value="${p.positionCd}">${p.positionName}</option>
									</c:forEach>
								</select>
							</div>
						</div>

						<div class="add-row">
							<div class="add-group">
								<label>입사일 <span style="color: #ef4444;">*</span></label>
								<input type="date" name="hireDate" required>
							</div>
							<div class="add-group">
								<label>연락처</label> <input type="text" name="tel"
									placeholder="01012345678" maxlength="11">
							</div>
						</div>

						<div class="add-row">
							<div class="add-group">
								<label>이메일</label> <input type="email" name="email"
									placeholder="example@email.com">
							</div>
							<div class="add-group">
								<label>주소</label> <input type="text" name="addr"
									placeholder="주소를 입력하세요">
							</div>
						</div>

						<div class="add-btns">
							<button type="submit" class="btn btn-primary">등록</button>
							<button type="button" class="btn"
								onclick="location.href='${pageContext.request.contextPath}/emp/list.do'">
								취소</button>
						</div>
					</form>
				</div>
			</div>
		</div>

	</div>
</body>
</html>
