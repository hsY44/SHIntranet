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
<title>부서 관리</title>
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
			data-ctx="${pageContext.request.contextPath}">
		</div>

		<div class="content">
		<div class="mgmt-header">
			<div class="mgmt-title">부서 관리</div>
			<div class="mgmt-exp">부서의 인원수 확인과 부서를 추가할 수 있습니다.</div>
		</div>
		<div class="dept-stat-row">
			<div class="dept-stat-card">
				<div class="dept-stat-label">전체 부서 수</div>
				<div class="dept-stat-value">${totalCount}</div>
			</div>
			<div class="dept-stat-card">
				<div class="dept-stat-label">사용 중인 부서 수</div>
				<div class="dept-stat-value">${activeCount}</div>
			</div>
			<div class="dept-stat-card">
				<div class="dept-stat-label">최근 추가된 부서 수</div>
				<div class="dept-stat-value">${recentCount}</div>
			</div>
		</div>
			<div class="mgmt-container">


				<c:forEach var="dept" items="${deptList}">
					<div class="mgmt-item">
						<div>
							<span class="mgmt-code">${dept.deptCd}</span>
							<span class="mgmt-name">${dept.deptName}</span>
							<span class="mgmt-grade" style="margin-left:8px; color:#64748b; font-size:0.85em;">소속 인원: ${dept.empCount}명</span>
						</div>
						<div class="mgmt-btns">
							<button class="btn btn-sm" onclick="openEditModal('${dept.deptCd}', '${dept.deptName}')">수정</button>
							<button class="btn btn-danger btn-sm" onclick="deleteDept('${dept.deptCd}', '${dept.deptName}')">삭제</button>
						</div>
					</div>
				</c:forEach>

				<!-- 부서 추가 폼 -->
				<form action="${pageContext.request.contextPath}/dept/insert.do"
					method="post" class="add-form">
					<input type="text" name="deptName" placeholder="새 부서명을 입력하세요" required>
					<button type="submit" class="btn btn-primary">추가하기</button>
				</form>
				<c:if test="${not empty errorMsg}">
					<script>alert('${errorMsg}');</script>
				</c:if>
			</div>
		</div>
	</div>

</div>

<!-- 부서 수정  -->
<div id="editModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.4); z-index:999; justify-content:center; align-items:center;">
	<div style="background:#fff; padding:32px; border-radius:12px; width:360px;">
		<h3 style="margin-bottom:20px;">부서 수정</h3>
		<form action="${pageContext.request.contextPath}/dept/update.do" method="post">
			<input type="hidden" id="modal-deptCd" name="deptCd">
			<div style="margin-bottom:16px;">
				<label style="display:block; margin-bottom:6px;">부서코드</label>
				<input type="text" id="modal-deptCdView" disabled style="width:100%; padding:8px; border:1px solid #e2e8f0; border-radius:6px;">
			</div>
			<div style="margin-bottom:20px;">
				<label style="display:block; margin-bottom:6px;">부서명</label>
				<input type="text" id="modal-deptName" name="deptName" required style="width:100%; padding:8px; border:1px solid #e2e8f0; border-radius:6px;">
			</div>
			<div style="display:flex; gap:8px; justify-content:flex-end;">
				<button type="button" class="btn" onclick="closeModal()">취소</button>
				<button type="submit" class="btn btn-primary">저장</button>
			</div>
		</form>
	</div>
</div>

<!-- 부서 삭제 폼  -->
<form id="deleteForm" action="${pageContext.request.contextPath}/dept/delete.do" method="post">
	<input type="hidden" id="delete-deptCd" name="deptCd">
</form>

<script>

	function openEditModal(deptCd, deptName)
	{
		document.getElementById('modal-deptCd').value     = deptCd;
		document.getElementById('modal-deptCdView').value = deptCd;
		document.getElementById('modal-deptName').value   = deptName;
		document.getElementById('editModal').style.display = 'flex';
	}
	
	function closeModal()
	{
		document.getElementById('editModal').style.display = 'none';
	}
	
	function deleteDept(deptCd, deptName) 
	{
		if (confirm('"' + deptName + '" 부서를 삭제하시겠습니까?')) {
			document.getElementById('delete-deptCd').value = deptCd;
			document.getElementById('deleteForm').submit();
		}
	}

</script>

</body>
</html>
