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
<title>사원 조회</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/emp.css">
<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
</head>
<body>
<div class="container">

    <div class="sidebar" id="sidebar"
         data-hr="${sessionScope.loginEmp.deptName == '인사부' ? 'true' : 'false'}"
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
                    <span class="board-header">사원 조회</span>
                </div>
                <form action="${pageContext.request.contextPath}/emp/search.do" method="get" class="inner">
                    <select name="schType" class="search-type">
                        <option value="empName"      <c:if test="${schType=='empName'}">selected</c:if>>이름</option>
                        <option value="empCd"        <c:if test="${schType=='empCd'}">selected</c:if>>사원코드</option>
                        <option value="deptName"     <c:if test="${schType=='deptName'}">selected</c:if>>부서</option>
                        <option value="positionName" <c:if test="${schType=='positionName'}">selected</c:if>>직급</option>
                    </select>
                    <input type="text" name="kwd" class="search-input"
                           placeholder="검색어를 입력하세요" value="${kwd}">
                    <button type="submit" class="board-search">검색</button>
                </form>
            </div>

            <div class="emp-detail-container">
                <div class="emp-container">
                    <c:forEach var="emp" items="${empList}">
                    <div class="emp-card">
                        <div class="emp-info">
                            <div>
                                <div class="emp-label">사원코드</div>
                                <div class="value">${emp.empCd}</div>
                            </div>
                            <div>
                                <div class="emp-label">이름</div>
                                <div class="value">${emp.empName}</div>
                            </div>
                            <div>
                                <div class="emp-label">부서</div>
                                <div class="value">${emp.deptName}</div>
                            </div>
                            <div>
                                <div class="emp-label">직급</div>
                                <div class="value">${emp.positionName}</div>
                            </div>
                            <div>
                                <div class="emp-label">연락처</div>
                                <div class="value">${emp.telFormatted}</div>
                            </div>
                            <div>
                                <div class="emp-label">이메일</div>
                                <div class="value">${emp.email}</div>
                            </div>
                        </div>
                    </div>
                    </c:forEach>

                    <c:if test="${empty empList}">
                        <p style="text-align:center; color:#94a3b8; padding:30px;">
                            검색 결과가 없습니다.
                        </p>
                    </c:if>
                </div>
            </div>

            <div class="paging-wrap">${paging}</div>
        </div>
    </div>

</div>
</body>
</html>
