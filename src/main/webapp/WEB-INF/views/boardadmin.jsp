<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 관리</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/boardlist.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/article.css"/>
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

	//검색 함수
	function searchList()
	{
		const f = document.searchForm;
		
		if (! f.kwd.value.trim()) {
			alert("검색어를 입력해 주세요.");
			return;
		}
		
		const formData = new FormData(f);
		let params = new URLSearchParams(formData).toString();
		
		let url = "${pageContext.request.contextPath}/admin/board/list.do";
		location.href = url + "?" + params;
	}
	
	// 게시글 삭제처리
	function deleteBoard(num) 
	{
   	 	if(confirm("해당 게시글을 삭제 처리하시겠습니까?")) 
   	 	{
	   	 	let url = "${pageContext.request.contextPath}/admin/board/delete.do";
	        url += "?num=" + num;
	        url += "&type=${type}";
	        url += "&page=${page}";
	        url += "&schType=${schType}";
	        url += "&kwd=${kwd}";
	        
        	location.href = url;
    	}
	}
    
    // 게시판 카테고리 변경 액션
    function changeType()
    {
    	const f = document.searchForm;
    	
    	const type = f.type.value;
    	
    	location.href = "${pageContext.request.contextPath}/admin/board/list.do?type=" + type;
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
             data-ctx="${pageContext.request.contextPath}"></div>

			<div class="content">

				<div class="contain">
						<span class="board-header" style="display: block; margin-bottom: 25px;">
						게시글 삭제</span>
					<form name="searchForm" action="${pageContext.request.contextPath}/admin/board/list.do" method="get">
					<div class="inner">
						<select name="type" class="search-type" onchange="changeType()">
						    <option value="002" ${type == '002' ? 'selected' : ''}>일반 게시판</option>
						    <option value="001" ${type == '001' ? 'selected' : ''}>공지사항</option>
						</select>
						<select name="schType" class="search-type">
						    <option value="all" ${schType == "all" ? "selected" : ""}>제목+내용</option>
						    <option value="title" ${schType == "title" ? "selected" : ""}>제목</option>
						    <option value="contents" ${schType == "contents" ? "selected" : ""}>내용</option>
						    <option value="board_no" ${schType == "board_no" ? "selected" : ""}>번호</option>
						    <option value="emp_name" ${schType == "emp_name" ? "selected" : ""}>작성자</option>
						</select>
						<input type="text" name="kwd"class="search-input" placeholder="검색어를 입력하세요" value="${kwd }"> 
						<input type="button" class="board-search" id="btn-search" 
						 value="검색" onclick="searchList()">
					</div>
					</form>
				</div>

				<div class="board">
					<div class="board-list" id="board-list">
						<div class="boardDelete-row board-list-header">
							<span class="board-number">번호</span> 
							<span class="board-number">게시판명</span> 
							<span class="board-title">제목</span>
							<span class="board-writer">작성자</span> 
							<span class="board-writeday">작성일</span>
							<span class="board-admin">관리</span>
						</div>
						<c:choose>
							<c:when test="${not empty list}">
								<c:forEach var="dto" items="${list}" varStatus="status">
							        <div class="boardDelete-row">
							            <span class="board-number">${dto.num}</span>
							            <span class="board-number">${dto.typeName}</span>
							            <span class="board-title">
							                <a href="${articleUrl}&num=${dto.num}" class="board-title">
							                	<c:out value="${dto.title}"/>
							            	</a>
							            </span>
							            <span class="board-writer">${dto.name}</span>
							            <span class="board-writeday">${dto.regDate}</span>
							            <input type="button" class="board-delete" id="btn-delete" 
								 		value="삭제" onclick="deleteBoard('${dto.num}')" style="background: #ef4444;">
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
					<input type="button" class="btn-write" value="처음으로"
					onclick="location.href='${pageContext.request.contextPath }/admin/board/list.do?type=${type }';">
				</div>
				
				<div class="pagination" id="pagination">
				${paging}
				</div>

			</div>
		</div>

	</div>
</body>
</html>