<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!doctype html>
<html>
<head>
<meta charset="UTF-8" />
<title>상신 내역 - SH Company</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/board.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/temp_common.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/modal.css"/>
<style type="text/css">
   .sidebar .board
   {
       background: none;
       border: none;
       border-radius: 0;
   }
	.board-row {
		grid-template-columns: 8% 12% 17% 24% 17% 9% 10%;
		position: relative;
        cursor: default !important;
	}
	.board-row>a {
		position: absolute;
	    top: 0;
	    left: 0;
	    width: 76%;
	    height: 100%;
	}
    /* 진행중 */
    .status
    {
        padding: 7px 10px;
        color: white;
        font-weight: bold;
        text-align: center;
        border: none;
        border-radius: 6px;
    }
    .status-progress
    {
        background: green;
    }
    /* 승인 */
    .status-approve
    {
        background: blue;
    }
    /* 반려 */
    .status-reject
    {
        background: red;
    }
    /* 조건부 승인 */
    .status-condi
    {
        background: orange;
    }
</style>
<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
<script type="text/javascript">
	window.addEventListener("DOMContentLoaded", () =>
	{
		const inputEL = document.querySelector("form input[name=title]");
		inputEL.addEventListener("keydown", function (evt)
		{
			if (evt.key==="Enter")
			{
				evt.preventDefault();
				searchList();
			}
		});
	});

	// 검색
	function searchList() {
		const f = document.searchForm;
		
		//if ((f.deptCd.value=="none") && (f.schType.value=="none" || !f.kwd.value.trim()))
		// 선택안함이 아닌데 공백일 경우
		if (!f.status.value=="none" && !f.title.value.trim())
			return;
		
		const formData = new FormData(f);
		let params = new URLSearchParams(formData);
		
		if (f.status.value=="none")
			params.delete("status");
			
		if (!f.title.value.trim()) {
			params.delete("title");
		}
		params = params.toString();
		
		let url = "${pageContext.request.contextPath}/aprv-submit/list";
		if (params.trim().length > 0)
			url += "?" + params;
		location.href = url;
	}

    // 초기화
	function resetSearch() {
		const f = document.searchForm;
		
		f.status.value = "none";
		f.title.value = "";
	}

</script>
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
					<span class="board-header">상신 내역</span>
					<div class="inner">
						<form name="searchForm" method="post">
							<!-- 상태 검색 -->
							<select class="search-type" id="status" name="status">
								<option value="none">전체</option>
								<option value="진행중"${status=="진행중" ? "selected" : ""}>진행중</option>
								<option value="승인"${status=="승인" ? "selected" : ""}>승인</option>
								<option value="반려"${status=="반려" ? "selected" : ""}>반려</option>
								<option value="조건부"${status=="조건부" ? "selected" : ""}>조건부</option>
							</select>
							<!-- 검색(제목) -->
							<input type="text" class="search-input" id="search-input" name="title" placeholder="검색어를 입력하세요" value="${title}" />
							<!-- 검색/초기화 버튼 -->
							<input type="button" class="board-search" id="btn-search" value="검색"  onclick="searchList()"/>
							<input type="button" class="board-deepsearch" id="btn-reset" value="초기화" onclick="resetSearch()" />
						</form>
					</div>
				</div>

				<!-- 리스트 -->
				${dataCount }개(${page }/${totalPage } 페이지)
				
				<div class="board">
					<div class="board-list" id="board-list">
						<!-- 헤더 행 -->
						<div class="board-row board-list-header">
							<span class="board-number">번호</span>
							<span class="board-title">문서코드</span>
							<span class="board-title">업무명</span>
							<span class="board-title">제목</span>
							<span class="board-writeday">기안일</span>
                            <span class="board-title">결재유형</span>
                            <span class="board-title">현재상태</span>
						</div>

						<!-- 데이터 바인딩 -->
						<div id="board-items">
				            <c:forEach var="dto" items="${list }" varStatus="status">
					            <div class="board-row">
						        <a href="${detailUrl }&docCd=${dto.docCd}"></a>
                                    <span class="board-number">${dto.rowNum }</span>
                                    <span class="board-title">${dto.docCd }</span>
                                    <span class="board-title">${dto.workName }</span>
                                    <span class="board-title">${dto.title }</span>
                                    <span class="board-writeday">${dto.regDt }</span>
                                    <span class="board-title">${dto.typeName }</span>
                                    <c:choose>
                                        <c:when test="${dto.status eq '진행중' }">
                                            <span class="board-title status status-progress">${dto.status }</span>
                                        </c:when>
                                        <c:when test="${dto.status eq '승인' }">
                                            <span class="board-title status status-approve">${dto.status }</span>
                                        </c:when>
                                        <c:when test="${dto.status eq '반려' }">
                                            <span class="board-title status status-reject">${dto.status }</span>
                                        </c:when>
                                        <c:when test="${dto.status eq '조건부' && dto.childCnt == 0 }">
                                            <input type="button" class="board-title status status-condi"
                                            style="cursor: pointer;"
                                            onclick="location.href='${pageContext.request.contextPath }/page/aprvrequest.do?parentCd=${dto.docCd}'" value="${dto.status }(재상신)">
                                        </c:when>
                                        <c:when test="${dto.status eq '조건부' && dto.childCnt > 0 }">
                                            <input type="button" class="board-title status status-condi" value="${dto.status }">
                                        </c:when>
                                    </c:choose>
					            </div>
				            </c:forEach>
						</div>
					</div>
				</div>

				<div class="pagination" id="pagination">
					${dataCount == 0 ? "상신 내역이 없습니다." : paging }
				</div>
			</div>
		</div>
	</div>

</body>
</html>
