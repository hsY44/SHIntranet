<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 - SH Company</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/boardlist.css"/>
<style type="text/css">
   .sidebar .board
   {
       background: none;
         border: none;
          border-radius: 0;
   }
</style>
<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
<script type="text/javascript">
	
	// 로그인한 사원의 부서
	const logindeptCd = "${sessionScope.loginEmp.deptCd}";
	
	const boardType = "${type}";
	
	// 공지사항의 글쓰기는 인사부만 할 수 있도록
	$(function() {
		
		// 공지사항
		if (boardType === "001") 
		{
			if (logindeptCd === "DE02") 
				$(".btn-write").show();
			else
				$(".btn-write").hide();
		}
		// 일반 게시판
		else
			$(".btn-write").show();
	});
	// 검색 함수
	function searchList()
	{
		const f = document.searchForm;
		
		if (! f.kwd.value.trim()) {
			alert("검색어를 입력해 주세요.");
			return;
		}
		
		const formData = new FormData(f);
		let params = new URLSearchParams(formData).toString();
		
		let url = "${pageContext.request.contextPath}/board/list.do";
		location.href = url + "?" + params;
	}
</script>

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
             data-ctx="${pageContext.request.contextPath}">
			</div>

			<div class="content">
				<!-- 타이틀 + 검색 -->
				<div class="contain">
					<span class="board-header">
						${param.type == '001' ? '공지사항' : '일반 게시판'} 
					</span>
					<div class="inner">
						<form name="searchForm" method="GET" action="${pageContext.request.contextPath}/board/list.do">
							<input type="hidden" name="type" value="${type}">
							<select class="search-type" id="search-type" name="schType">
								<option value="all" ${schType == "all" ? "selected" : ""}>제목+내용</option>
								<option value="title" ${schType == "title" ? "selected" : ""}>제목</option>
								<option value="contents" ${schType == "contents" ? "selected" : ""}>내용</option>
								<option value="emp_name" ${schType == "emp_name" ? "selected" : ""}>작성자</option>
							</select> 
							<input type="text" name="kwd" class="search-input" id="search-input"
								placeholder="검색어를 입력하세요" value="${kwd }"> <input type="button"
								class="board-search" id="btn-search" value="검색" onclick="searchList()">
						</form>
					</div>
				</div>

				<!-- 게시판 리스트 -->
				<div class="board">
					<div class="board-list">
						<!-- 헤더 행 -->
						<div class="board-row board-list-header">
							<span class="board-number">번호</span> <span class="board-title">제목</span>
							<span class="board-writer">작성자</span> 
							<span class="board-writeday">작성일</span>
						</div>
						<c:choose>
							<c:when test="${not empty list}">
								<c:forEach var="dto" items="${list}" varStatus="status">
							        <div class="board-row">
							            <span class="board-number">${dto.num}</span>
							            <span class="board-title">
							            	<a href="${articleUrl}&num=${dto.num}" class="board-title">
							                	<c:out value="${dto.title}"/>
							            	</a>
							            </span>
							            <span class="board-writer">${dto.name}</span>
							            <span class="board-writeday">${dto.regDate}</span>
							        </div>
							    </c:forEach>
							</c:when>
						    <c:otherwise>
				                <div class="board-row" style="display: flex; justify-content: center; align-items: center; padding: 50px 0; color: #999;">
        							<span style="width: 100%; text-align: center;">게시글이 존재하지 않습니다.</span>
    							</div>
				            </c:otherwise>
						</c:choose>
					    
					</div>
				</div>

				<div class="board-action-row">
					<!-- 처음으로 버튼 -->
					<input type="button" class="btn-home" value="처음으로"
					onclick="location.href='${pageContext.request.contextPath }/board/list.do?type=${param.type }';">
					<!-- 글쓰기 버튼 -->
					<input type="button" class="btn-write" value="글쓰기" style="display:none"
					onclick="location.href='${pageContext.request.contextPath }/board/write.do?type=${param.type }';">
				</div>
				

				<!-- 게시판의 페이지? -->
				<div class="page-navigation">
					${paging}
				</div>
			</div>
		</div>
	</div>
</body>
</html>