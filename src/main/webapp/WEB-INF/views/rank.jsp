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
<title>직급 관리</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/emp.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/buseo-manage.css">
<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
</head>
<body>
	<div class="container">

		<div class="sidebar" id="sidebar" data-hr="true"
			data-ctx="${pageContext.request.contextPath}"></div>

		<div class="main-wrapper">
			<div class="header" id="header"
				data-name="${sessionScope.loginEmp.empName}"
				data-buseo="${sessionScope.loginEmp.deptName}"
				data-jikgup="${sessionScope.loginEmp.positionName}"
				data-ctx="${pageContext.request.contextPath}"></div>

			<div class="content">
				<div class="mgmt-header">
					<div class="mgmt-title">직급 관리</div>
					<div class="mgmt-exp">직급의 현황 확인과 직급을 추가할 수 있습니다.</div>
				</div>
				<div class="dept-stat-row">
					<div class="dept-stat-card">
						<div class="dept-stat-label">전체 직급 수</div>
						<div class="dept-stat-value">${totalCount}</div>
					</div>
					<div class="dept-stat-card">
						<div class="dept-stat-label">사용 중인 직급 수</div>
						<div class="dept-stat-value">${activeCount}</div>
					</div>
					<div class="dept-stat-card">
						<div class="dept-stat-label">최근 추가된 직급 수</div>
						<div class="dept-stat-value">${recentCount}</div>
					</div>
				</div>
				<div class="mgmt-container">

					<c:forEach var="rank" items="${rankList}">
						<div class="mgmt-item">
							<div>
								<span class="mgmt-code">${rank.positionCd}</span>
								<span class="mgmt-name">${rank.positionName}</span>
								<span class="mgmt-grade">(Grade: ${rank.grade})</span>
								<span class="mgmt-grade" style="margin-left:8px; color:#64748b; font-size:0.85em;">소속 인원: ${rank.empCount}명</span>
							</div>
							<div class="mgmt-btns">
								<button class="btn btn-sm" onclick="openEditModal('${rank.positionCd}', '${rank.positionName}', ${rank.grade})">수정</button>
								<button class="btn btn-danger btn-sm" onclick="deleteRank('${rank.positionCd}', '${rank.positionName}')">삭제</button>
							</div>
						</div>
					</c:forEach>

					<!-- 직급 추가 폼 -->
					<form action="${pageContext.request.contextPath}/rank/insert.do"
						method="post" class="add-form">
						<input type="text" name="positionName" placeholder="직급명 (예: 대리)" required>
						<input type="number" name="grade" placeholder="등급" min="10" step="10" required>
						<button type="submit" class="btn btn-primary">추가하기</button>
					</form>
					<c:if test="${not empty errorMsg}">
						<script>alert('${errorMsg}');</script>
					</c:if>
				</div>
			</div>
		</div>

	</div>

<!-- 직급 수정  -->
<div id="editModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.4); z-index:999; justify-content:center; align-items:center;">
	<div style="background:#fff; padding:32px; border-radius:12px; width:360px;">
		<h3 style="margin-bottom:20px;">직급 수정</h3>
		<form action="${pageContext.request.contextPath}/rank/update.do" method="post">
			<input type="hidden" id="modal-positionCd" name="positionCd">
			<div style="margin-bottom:16px;">
				<label style="display:block; margin-bottom:6px;">직급코드</label>
				<input type="text" id="modal-positionCdView" disabled style="width:100%; padding:8px; border:1px solid #e2e8f0; border-radius:6px;">
			</div>
			<div style="margin-bottom:16px;">
				<label style="display:block; margin-bottom:6px;">직급명</label>
				<input type="text" id="modal-positionName" name="positionName" required style="width:100%; padding:8px; border:1px solid #e2e8f0; border-radius:6px;">
			</div>
			<div style="margin-bottom:20px;">
				<label style="display:block; margin-bottom:6px;">등급</label>
				<input type="number" id="modal-grade" name="grade" min="10" step="10" required style="width:100%; padding:8px; border:1px solid #e2e8f0; border-radius:6px;">
			</div>
			<div style="display:flex; gap:8px; justify-content:flex-end;">
				<button type="button" class="btn" onclick="closeModal()">취소</button>
				<button type="submit" class="btn btn-primary">저장</button>
			</div>
		</form>
	</div>
</div>

<!-- 직급 삭제 폼 (hidden) -->
<form id="deleteForm" action="${pageContext.request.contextPath}/rank/delete.do" method="post">
	<input type="hidden" id="delete-positionCd" name="positionCd">
</form>



<script>
/* 값을 전달 받아서 수정하는 버튼 */
	function openEditModal(positionCd, positionName, grade) 
	{
		document.getElementById('modal-positionCd').value     = positionCd;
		document.getElementById('modal-positionCdView').value = positionCd;
		document.getElementById('modal-positionName').value   = positionName;
		document.getElementById('modal-grade').value          = grade;
		document.getElementById('editModal').style.display    = 'flex';
	}
	/* 취소 버튼 */
	function closeModal()
	{
		document.getElementById('editModal').style.display = 'none';
	}
	
	/*  삭제 버트 */
	function deleteRank(positionCd, positionName)
	{
		if (confirm('"' + positionName + '" 직급을 삭제하시겠습니까?')) {
			document.getElementById('delete-positionCd').value = positionCd;
			document.getElementById('deleteForm').submit();
		}
	}
	
</script>

</body>
</html>
