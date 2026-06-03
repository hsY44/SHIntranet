<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%
    if (session.getAttribute("loginEmp") == null) {
        response.sendRedirect(request.getContextPath() + "/login.do");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>퇴사자 등록</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/emp.css">
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
            <div class="contain">
                <div class="board-title">
                    <span class="board-header">퇴사자 등록</span>
                </div>
            </div>

            <div class="emp-detail-container exit-container">

                <%-- 1단계: 사원코드 입력 폼 --%>
                <c:if test="${empty emp}">
                    <p class="exit-guide">퇴사 처리할 사원코드를 입력하세요.</p>
                    <c:if test="${not empty errorMsg}">
                        <p class="exit-error">${errorMsg}</p>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/emp/exitConfirm.do" method="post"
                          class="exit-btn-row">
                        <input type="text" name="empCd" placeholder="사원코드 (예: SW0001)"
                               class="search-input exit-input" required>
                        <button type="submit" class="board-search">조회</button>
                        <button type="button" class="btn"
                            onclick="location.href='${pageContext.request.contextPath}/emp/list.do'">
                            취소
                        </button>
                    </form>
                </c:if>

                <%-- 2단계: 조회된 사원 정보 확인 및 최종 확인 --%>
                <c:if test="${not empty emp}">
                    <p class="exit-warn">아래 사원을 퇴사 처리하시겠습니까?</p>
                    <div class="info-view exit-confirm-view">
                        <div class="info-grid">
                            <div class="info-item">
                                <span class="info-label">사원코드</span>
                                <span class="info-value">${emp.empCd}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">이름</span>
                                <span class="info-value">${emp.empName}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">부서</span>
                                <span class="info-value">${emp.deptName}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">직급</span>
                                <span class="info-value">${emp.positionName}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">연락처</span>
                                <span class="info-value">${emp.tel}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">이메일</span>
                                <span class="info-value">${emp.email}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">입사일</span>
                                <span class="info-value">${emp.hireDate}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">주소</span>
                                <span class="info-value">${emp.addr}</span>
                            </div>
                        </div>
                    </div>

                    <div class="exit-btn-row">
                        <%-- 확인 → EMP_EXIT 프로시저 실행 --%>
                        <form action="${pageContext.request.contextPath}/emp/exit.do" method="post">
                            <input type="hidden" name="empCd" value="${emp.empCd}">
                            <button type="submit" class="btn btn-danger"
                                    onclick="return confirm('${emp.empName} 사원을 퇴사 처리합니다. 계속하시겠습니까?')">
                                퇴사 처리
                            </button>
                        </form>
                        <%-- 취소 → 사원코드 재입력 --%>
                        <button type="button" class="btn"
                            onclick="location.href='${pageContext.request.contextPath}/emp/exitForm.do'">
                            다시 검색
                        </button>
                        <button type="button" class="btn"
                            onclick="location.href='${pageContext.request.contextPath}/emp/list.do'">
                            목록으로
                        </button>
                    </div>
                </c:if>

            </div>
        </div>
    </div>

</div>
</body>
</html>
