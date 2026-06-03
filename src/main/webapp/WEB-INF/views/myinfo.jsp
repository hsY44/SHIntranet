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
<title>내 정보</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/emp.css">
<script src="${pageContext.request.contextPath}/js/sidebar.js"></script>
<script src="${pageContext.request.contextPath}/js/header.js"></script>
</head>
<body>
	<div class="container">

		<div class="sidebar" id="sidebar"
			data-hr="${sessionScope.loginEmp.deptName == '인사부' ? 'true' : 'false'}"
			data-ctx="${pageContext.request.contextPath}"></div>

		<div class="main-wrapper">
			<div class="header" id="header" data-ctx="${pageContext.request.contextPath}"
				data-name="${sessionScope.loginEmp.empName}" data-buseo="${sessionScope.loginEmp.deptName}"
				data-jikgup="${sessionScope.loginEmp.positionName}"></div>

			<div class="content">
				<div class="attendance">
					<div class="att-in">
						<form id="attendInForm" action="${pageContext.request.contextPath}/attend/in.do" method="post">
							<input type="button" value="출근" onclick="checkIn()">
						</form>
					</div>
					<div class="att-out">
						<form action="${pageContext.request.contextPath}/attend/out.do" method="post">
							<input type="submit" value="퇴근" onclick="checkOut()">
						</form>
					</div>
				</div>

				<div class="emp-detail-container">

					<!-- 정보 조회 영역 -->
					<div class="info-view" id="info-view">
						<div class="info-view-title">
							<span>내 정보</span>
							<button class="btn btn-primary" onclick="toggleEdit()">수정</button>
						</div>
						<div class="info-grid">
							<div class="info-item">
								<span class="info-label">사원코드</span> <span class="info-value">${sessionScope.loginEmp.empCd}</span>
							</div>
							<div class="info-item">
								<span class="info-label">이름</span> <span class="info-value">${sessionScope.loginEmp.empName}</span>
							</div>
							<div class="info-item">
								<span class="info-label">부서</span> <span class="info-value">${sessionScope.loginEmp.deptName}</span>
							</div>
							<div class="info-item">
								<span class="info-label">직급</span> <span class="info-value">${sessionScope.loginEmp.positionName}</span>
							</div>
							<div class="info-item">
								<span class="info-label">연락처</span> <span class="info-value">${sessionScope.loginEmp.telFormatted}</span>
							</div>
							<div class="info-item">
								<span class="info-label">이메일</span> <span class="info-value">${sessionScope.loginEmp.email}</span>
							</div>
							<div class="info-item">
								<span class="info-label">주소</span> <span class="info-value">${sessionScope.loginEmp.addr}</span>
							</div>
							<div class="info-item">
								<span class="info-label">입사일</span> <span class="info-value">${sessionScope.loginEmp.hireDate}</span>
							</div>
						</div>
					</div>

					<!-- 정보 수정 영역 -->
					<div class="empinfo" id="edit-form" style="display: none;">
						<div class="empinfo-title">정보 수정</div>
						<form action="${pageContext.request.contextPath}/myinfo.do"
							method="post">
							<div class="form-row">
								<div class="form-group">
									<label>이름</label> <input type="text" name="empName"
										value="${sessionScope.loginEmp.empName}" required />
								</div>
								<div class="form-group">
									<label>부서</label> <input type="text"
										value="${sessionScope.loginEmp.deptName}" disabled />
								</div>
							</div>
							<div class="row1">
								<div class="form-group">
									<label>직급</label> <input type="text"
										value="${sessionScope.loginEmp.positionName}" disabled />
								</div>
								<div class="form-group">
									<label>연락처</label> <input type="tel" name="tel"
										value="${sessionScope.loginEmp.tel}" />
								</div>
							</div>
							<div class="row2">
								<div class="form-group">
									<label>이메일</label> <input type="text" name="email"
										value="${sessionScope.loginEmp.email}" />
								</div>
								<div class="form-group">
									<label>주소</label> <input type="text" name="addr"
										value="${sessionScope.loginEmp.addr}" />
								</div>
							</div>
							<div class="row3">
								<div class="form-group">
									<label>현재 비밀번호</label> <input type="text" name="npwd"
										 value="${sessionScope.loginEmp.pwd}" disabled/>
								</div>
								<div class="form-group">
									<label>새 비밀번호</label> <input type="text" name="pwd"
										 placeholder="변경할 경우만 입력" />
								</div>
							</div>
							<div class="btn-row">
								<button type="submit" class="btn btn-primary">저장</button>
								<button type="button" class="btn" onclick="toggleEdit()">취소</button>
							</div>
						</form>
					</div>

				</div>
			</div>
		</div>

	</div>

<!-- 정보 수정 후 결과 메세지를 띄우기 위해  -->
	<c:if test="${not empty updateMsg}">
		<script>
			alert('${updateMsg}');
		</script>
	</c:if>

<!-- 출퇴근 처리 오류 메시지 (세션) -->
	<c:if test="${not empty sessionScope.attendError}">
		<script>
			alert('${sessionScope.attendError}');
		</script>
		<% session.removeAttribute("attendError"); %>
	</c:if>
	
<!-- 출·퇴근 버튼 클릭 이벤트 -->
<script>
	function checkIn() {
		document.getElementById('attendInForm').submit();
	}

	function checkOut() {
		document.getElementById('attendInForm').submit();
	}

/* 전환을 위한 js */
	function toggleEdit()
	{
		const infoView = document.getElementById("info-view");
		const editForm = document.getElementById("edit-form");
		if (editForm.style.display === "none") {
			infoView.style.display = "none";
			editForm.style.display = "block";
		} 
		else 
		{
			editForm.style.display = "none";
			infoView.style.display = "block";
		}
	}
	
</script>
</body>
</html>
