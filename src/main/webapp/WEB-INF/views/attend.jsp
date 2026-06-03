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
<title>사원 출·퇴근 기록</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/emp.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/attend.css">
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
            <div class="contain">
                <div class="board-title">
                    <span class="board-header">사원 출·퇴근 기록</span>
                </div>
                <form action="${pageContext.request.contextPath}/attend/list.do" method="get" class="inner">
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
                <table class="attend-table">
                    <thead>
                        <tr>
                            <th>No</th>
                            <th>사원코드</th>
                            <th>이름</th>
                            <th>부서</th>
                            <th>직급</th>
                            <th>유형</th>
                            <th>기록일시</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="a" items="${attendList}" varStatus="s">
                        <tr>
                            <td>${dataCount - (page - 1) * 10 - (s.index)}</td>
                            <td>${a.empCd}</td>
                            <td>${a.empName}</td>
                            <td>${a.deptName}</td>
                            <td>${a.positionName}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${a.typeCd == '001'}">
                                        <span class="badge badge-in">${a.typeName}</span>
                                    </c:when>
                                    <c:when test="${a.typeCd == '002'}">
                                        <span class="badge badge-out">${a.typeName}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-etc">${a.typeName}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${a.regDt}</td>
                        </tr>
                        </c:forEach>
                        <c:if test="${empty attendList}">
                        <tr>
                            <td colspan="7" style="text-align:center; color:#94a3b8; padding:30px;">
                                기록이 없습니다.
                            </td>
                        </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div style="display:flex; justify-content:center; margin-top:16px;">
                ${paging}
            </div>
        </div>
    </div>

</div>
</body>
</html>
