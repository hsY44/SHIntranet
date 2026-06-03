<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>댓글 관리</title>
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
		
		let url = "${pageContext.request.contextPath}/admin/comment/list.do";
		location.href = url + "?" + params;
	}
	
	// 댓글 삭제처리
	function deleteComment(num) 
	{
   	 	if(confirm("해당 댓글을 삭제 처리하시겠습니까?")) 
   	 	{
   	 	let url = "${pageContext.request.contextPath}/admin/comment/delete.do";
        url += "?num=" + num;
        url += "&page=${page}";
        url += "&schType=${schType}";
        url += "&kwd=${kwd}";
        
    	location.href = url;
    	}
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
						댓글 삭제</span>
					<form name="searchForm" action="${pageContext.request.contextPath}/admin/comment/list.do" method="get">
					<div class="inner">
						<select name="schType" class="search-type">
						    <option value="contents" ${schType == "contents" ? "selected" : ""}>내용</option>
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
						<div class="commentDelete-row board-list-header">
							<span class="comment-title">댓글 내용</span>
							<span class="board-writer">작성자</span> 
							<span class="board-writeday">작성일</span>
							<span class="board-admin">관리</span>
						</div>
						<c:choose>
							<c:when test="${not empty list}">
								<c:forEach var="dto" items="${list}" varStatus="status">
							        <div class="commentDelete-row">
							        	<span class="comment-title">${dto.contents }</span>
							            <span class="board-writer">${dto.name}</span>
							            <span class="board-writeday">${dto.regDate}</span>
							            <input type="button" class="board-delete" id="btn-delete" 
								 		value="삭제" onclick="deleteComment('${dto.num}')" style="background: #ef4444;">
							        </div>
							    </c:forEach>
							</c:when>
						    <c:otherwise>
				                <div class="board-row" style="display: flex; justify-content: center; align-items: center; padding: 50px 0; color: #999;">
        							<span style="width: 100%; text-align: center;">댓글이 존재하지 않습니다.</span>
    							</div>
				            </c:otherwise>
						</c:choose>
						
					</div>
				</div>
				
				<div class="board-action-row">
					<!-- 처음으로 버튼 -->
					<input type="button" class="btn-write" value="처음으로"
					onclick="location.href='${pageContext.request.contextPath }/admin/comment/list.do';">
				</div>
				
				<div class="pagination" id="pagination">
				${paging}
				</div>

			</div>
		</div>

	</div>
</body>
</html>