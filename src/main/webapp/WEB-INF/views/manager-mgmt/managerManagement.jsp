<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!doctype html>
<html>
<head>
<meta charset="UTF-8" />
<title>담당 업무 - SH Company</title>
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
		grid-template-columns: 7% 12% 22% 12% 7% 12% 8% 8% 12%;
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
</style>
<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
<script type="text/javascript">
	window.addEventListener("DOMContentLoaded", () =>
	{
		const inputEL = document.querySelector("form input[name=kwd]");
		inputEL.addEventListener("keydown", function (evt)
		{
			if (evt.key==="Enter")
			{
				evt.preventDefault();
				searchList();
			}
		});
		
		getDeptList();
	});
	
	// 부서 목록 가져오기
	function getDeptList() {
		let url = "${pageContext.request.contextPath}/dept/api/list";
		
		fetch(url)
		.then(response => {
			if (!response.ok)
				throw new Error("에러발생");
			return response.json();
		})
		.then(data => {
			addOption("deptCd", data);
		})
		.catch(error => {
			console.error("에러", error);
            alert("데이터를 불러오는 중 오류가 발생했습니다.");
		});
	}
	
	// select 의 option 추가
	function addOption(name, data) {
		const select = document.getElementById(name);
		
		for (let dept of data) {
			const option = new Option(dept.deptName, dept.deptCd);
			
			const query = getSearchQuery(name);
			
			if (query == dept.deptCd)
				option.selected = true;
			
			select.add(option);
		}
	}
	
	// url query
	function getSearchQuery(name) {
		const urlParams = new URLSearchParams(window.location.search);
		const query = urlParams.get(name);
		
		return query;
	}

	// 검색
	function searchList() {
		const f = document.searchForm;
		
		//if ((f.deptCd.value=="none") && (f.schType.value=="none" || !f.kwd.value.trim()))
		// 선택안함이 아닌데 공백일 경우
		if (!f.schType.value=="none" && !f.kwd.value.trim())
			return;
		
		const formData = new FormData(f);
		let params = new URLSearchParams(formData);
		
		if (f.deptCd.value=="none")
			params.delete("deptCd");
			
		if (f.schType.value=="none" || !f.kwd.value.trim()) {
			params.delete("schType");
			params.delete("kwd");
		}
		params = params.toString();
		
		let url = "${pageContext.request.contextPath}/manager-mgmt/list";
		if (params.trim().length > 0)
			url += "?" + params;
		location.href = url;
	}

    // 초기화
	function resetSearch() {
		const f = document.searchForm;
		
		f.deptCd.value = "none";
		f.schType.value = "none";
		f.kwd.value = "";
	}

    // 모달 열기
    async function openModal(work_cd, emp_cd) {
        document.getElementById("work_cd").value = work_cd;
        document.getElementById("emp_cd").value = emp_cd;

        const success = await getManagerIssue(work_cd, emp_cd);

        // 모달 표시
        if (success) {
            document.getElementById("customModal").style.display = "flex";
        }
    }

    // 최소 날짜 가져와서 설정
    async function getManagerIssue(work_cd, emp_cd) {
        let url = "${pageContext.request.contextPath}/manager-mgmt/api/getManagerIssue";
        let result = false;

        await $.ajax({
            "type":"GET",
            "url":url,
            "data": {
                work_cd:work_cd,
                emp_cd:emp_cd,
            },
            "error":function(e) {
                console.error(e);
                alert("데이터를 불러오는 중 오류가 발생했습니다.");
            },
            "success":function(data) {
                const issueDate = data?.issueDate;
                const issueType = data?.issueType;

                if (issueType==="S")
                {
                    // 시작일 이후로 종료일 등록 가능.
                    document.getElementById("date").min = issueDate;
                    result = true;
                }
                else
                {
                    // 종료된 담당자는 새로 등록해야 함. 종료일 등록 불가.
                    alert("이미 종료된 담당자입니다. 새로 등록해 주세요.");
                    closeModal();
                }
            }
        });

        return result;
    }

    // 모달 닫기
    function closeModal() {
        document.getElementById("emp_cd").value = "";
        document.getElementById("work_cd").value = "";
        document.getElementById("date").value = "";
        document.getElementById("customModal").style.display = "none";
    }

    // 모달 바깥 영역 선택시 모달 닫기
    window.onclick = function(event) {
        const modal = document.getElementById('customModal');
        if (event.target == modal)
            closeModal();
    }

    // 기간 체크
    function addManagerCheck() {
        const f = document.managerForm;

        const work_cd = f.work_cd.value;
        const emp_cd = f.emp_cd.value;
        const date = f.date.value;
        const minDate = f.date.min;

        if (!date.trim())
            alert("종료 기간을 선택해 주세요.");
        else if (minDate && date < minDate) {
            alert("시작일보다 빠른 날짜로 종료할 수 없습니다.");
            f.date.value = "";
            f.date.focus();
        }
        else
            addManager(work_cd, emp_cd, date);
    }

    // 종료 등록
    function addManager(work_cd, emp_cd, date) {
        let url = "${pageContext.request.contextPath}/manager-mgmt/addManager";
        $.ajax({
            "type":"POST",
            "url":url,
            "data": {
                work_cd:work_cd,
                emp_cd:emp_cd,
                issue_type: "E",
                date:date
            },
            "error":function(e) {
                console.error(e);
                alert("종료일 등록에 실패했습니다.");
            },
            "success":function(data) {
                if (data?.success) {
                    alert("종료일을 등록했습니다.");
                    location.reload();
                } else {
                    const msg = data && data.message ? data.message : "등록에 실패했습나ㅑ";
                    alert(msg);
                }
            }
        });
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
					<span class="board-header">담당 업무</span>
					<div class="inner">
						<form name="searchForm" method="post">
							<!-- 부서 검색 -->
							<select class="search-type" id="deptCd" name="deptCd">
								<option value="none">전체</option>
							</select>
							<!-- 검색 타입(업무명/사원명) -->
							<select class="search-type" id="schType" name="schType">
								<option value="none">선택안함</option>
								<option value="work_name"${schType=="work_name" ? "selected" : ""}>업무명</option>
								<option value="manager_emp_name"${schType=="manager_emp_name" ? "selected" : ""}>사원명</option>
							</select>
							<input type="text" class="search-input" id="search-input" name="kwd" placeholder="검색어를 입력하세요" value="${kwd}" />
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
							<span class="board-title">업무종류</span>
							<span class="board-title">업무명</span>
							<span class="board-writer">부서</span>
							<span class="board-writer">사원코드</span>
							<span class="board-writer">담당자</span>
							<span class="board-writeday">시작일</span>
							<span class="board-writeday">종료일</span>
							<span class="board-writer">등록자</span>
						</div>

						<!-- 데이터 바인딩 -->
						<div id="board-items">
				            <c:forEach var="dto" items="${list }" varStatus="status">
					            <div class="board-row">
						        <%--<a href="${detailUrl }&managerNo=${dto.managerNo}"></a>--%>
                                    <%--<span class="board-number">${dto.rowNum }</span>--%>
                                    <fmt:parseNumber var="numVar" value="${page}" />
                                    <span class="board-number">${(numVar - 1) * 10 + status.count}</span>
                                    <span class="board-title">${dto.typeName }</span>
                                    <span class="board-title">${dto.workName }</span>
                                    <span class="board-writer">${dto.deptName }</span>
                                    <span class="board-writer">${dto.managerEmpCd }</span>
                                    <span class="board-writer">${dto.managerEmpName }</span>
                                    <span class="board-writeday">${dto.startIssueDate }</span>
                                    <span class="board-writeday">
                                        <c:choose>
                                            <c:when test="${not empty dto.endIssueDate }">
                                                ${dto.endIssueDate }
                                            </c:when>
                                            <c:otherwise>
                                                <input type="button" class="btn-write" value="등록"
                                                       onclick="openModal('${dto.workCd}', '${dto.managerEmpCd}')" />
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                    <span class="board-writer">${dto.regEmpName }</span>
					            </div>
				            </c:forEach>
						</div>
					</div>
				</div>

				<!-- 게시판의 페이지? -->
				<div class="pagination" id="pagination">
					<%-- ${dataCount == 0 ? "등록된 담당 업무가 없습니다." : paging } --%>
					${dataCount == 0 ? "등록된 담당 업무가 없습니다." : paging }
				</div>
				
				<!-- 등록하기 버튼 -->
				<div class="board-action-row">
				<c:choose>
					<c:when test="${not empty page && not empty query }">
						<input type="button" class="btn-write" value="등록하기"
						onclick="location.href='${pageContext.request.contextPath}/manager-mgmt/write?page=${page}&${query }';" />
					</c:when>
					<c:when test="${not empty page}">
						<input type="button" class="btn-write" value="등록하기"
						onclick="location.href='${pageContext.request.contextPath}/manager-mgmt/write?page=${page}';" />
					</c:when>
					<c:otherwise>
						<input type="button" class="btn-write" value="등록하기"
						onclick="location.href='${pageContext.request.contextPath}/manager-mgmt/write';" />
					</c:otherwise>
				</c:choose>
					
				</div>
			</div>
		</div>
	</div>

    <div id="customModal" class="modal-overlay" style="display: none;">
        <div class="modal-window">
            <div class="modal-header">
                <h3>종료일 등록</h3>
                <span class="modal-close" onclick="closeModal()">&times;</span>
            </div>
            <div class="modal-body">
                <form name="managerForm" id="managerForm">
                    <input type="hidden" id="work_cd">
                    <input type="hidden" id="emp_cd">
                    <div class="input-group">
                        <label for="date">종료 날짜</label>
                        <input type="date" id="date">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-cancel" onclick="closeModal()">취소</button>
                <button type="button" class="btn-save" onclick="addManagerCheck()">저장</button>
            </div>
        </div>
    </div>

</body>
</html>
