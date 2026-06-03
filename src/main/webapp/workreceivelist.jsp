<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/board.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/temp_common.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/modal.css"/>
<style type="text/css">
.board-row {
	grid-template-columns: 20% 20% 40% 20%;
	position: relative;
       cursor: default !important;
}
.board-row>a {
	position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}

.listCount
{
	margin-left: 80%;
}

.pageIndex
{
	border: 0 auto;
	text-align: center;
}

</style>

<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>

<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>

<script type="text/javascript">
	
	$(function()
	{
		$("#listCount").on("change",function()
		{
			// alert("확인");
			
			window.location.href = "${pageContext.request.contextPath}/work-receive/list?page=1&listCount=" + $(this).val();
			
		});	
	})
	
</script>

<title>업무 지시 내역</title>
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
				<!-- 타이틀 + 검색 -->
				<div class="contain">
					<span class="board-header">업무 지시 내역</span>
					<span class="listCount">
						표시할 개수 
						<select name="listCount" id="listCount" class="search-type">
						 <option value="3" ${listCount == 3 ? "selected" : ""}>3</option>
						 <option value="5" ${listCount == 5 ? "selected" : ""}>5</option>
						 <option value="10" ${listCount == 10 ? "selected" : ""}>10</option>
						</select>
					</span>
				</div>
			
				<!-- 리스트 -->
				<%-- ${dataCount }개(${page }/${totalPage } 페이지) --%>
				
				<div class="board">
					<div class="board-list" id="board-list">
						<!-- 헤더 행 -->
						<div class="board-row board-list-header">
							<!-- <span class="board-number">번호</span> -->
							<span class="board-title">문서코드</span>
							<span class="board-title">업무명</span>
							<span class="board-title">제목</span>
							<span class="board-writeday">등록일</span>
							<span class="board-title">지시자</span>
						</div>

						<!-- 데이터 바인딩 -->
						<div id="board-items">
							<c:forEach var="item" items="${result }" varStatus="status">
								<div class="board-row">
									<a href="${pageContext.request.contextPath}/work-submit/detail?docCd=${item.docCd}"></a>
									<span class="board-title">${item.docCd }</span>
									<span class="board-title">${item.workCd }</span>
									<span class="board-title">${item.title }</span>
									<span class="board-writeday">${item.regDt }</span>
									<span class="board-writeday">${item.empCd }</span>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>
				
				<div class="pageIndex">
					${paging }
				</div>
				
			</div>
		</div>
	</div>

</body>
</html>