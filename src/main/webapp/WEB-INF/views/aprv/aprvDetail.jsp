<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>문서 상세 - SH Company</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/temp_common.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/aprvLine.css">
<style type="text/css">
	.worktarget
	{
		margin-left: 40%;
		margin-right: 40%;
	}
</style>
<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
<script type="text/javascript">
window.addEventListener("DOMContentLoaded", () =>
{
	scrollToCurrent();
});

//현재 결재선으로 자동 스크롤.
function scrollToCurrent() {
 const container = document.querySelector(".approval");
 const current = document.querySelector(".current-focus");

 if (!container || !current) return;

 const offsetTop = current.offsetTop;

 container.scrollTo({
     top: offsetTop - container.clientHeight / 2,
     behavior: "smooth"
 });
}

// 결재종류 선택시 코멘트 입력칸 보이기
function openComment(action, target) {
    document.getElementById("actionTitle").innerText = action + " 의견 작성";
    $("#commentArea").addClass("show");
    // 결재종류 선택값 저장
    document.getElementById("type_cd").value = target.value;
}

// 코멘트(결재) 등록
function submitComment() {
	const f = document.addAprvForm;
	
	const comments = f.comments.value;
	const type_cd = f.type_cd.value;

    if (!type_cd.trim()) {
        alert("결재 종류가 선택되지 않았습니다. 다시 선택해 주세요.");
        return;
    }
    else if (comments.length > 2000)
    {
    	alert("글자수가 2000을 넘을 수 없습니다.");
    }
    
    const queryString = window.location.search;
	const urlParams = new URLSearchParams(queryString);
	const doc_cd = urlParams.get("docCd");
    
    addAprvLog(doc_cd, type_cd, comments);
}

// 결재 등록
function addAprvLog(doc_cd, type_cd, comments) {
	let url = "${pageContext.request.contextPath}/aprv/detail/addAprvLog";
	
    $.ajax({
        "type":"POST",
        "url":url,
        "data": {
        	doc_cd:doc_cd,
        	type_cd:type_cd,
        	comments:comments
        },
        "error":function(e) {
            console.error(e);
            alert("결재 등록에 실패했습니다.");
        },
        "success":function(data) {
            if (data?.success) {
                alert(data?.message);
                location.reload();
            } else
                alert(data?.message);
        }
    });
	
    $("#commentArea").removeClass("show");
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
				data-ctx="${pageContext.request.contextPath}"
				data-name="${sessionScope.loginEmp.empName}"
				data-buseo="${sessionScope.loginEmp.deptName}"
				data-jikgup="${sessionScope.loginEmp.positionName}"></div>

			<div class="content">
				<div class="contain">
					<span class="page-title">문서 상세</span>
				</div>
				<div class="detail-container">
					<!-- 입력 영역 -->
					<div class="detail-info" id="edit-form">

						<div class="aprv-container">

							<!-- ===== 문서 | 결재선 ===== -->
							<div class="top">
							
								<!-- 문서 -->
								<div class="document-area">
								<c:if test="${not empty data }">
									<table class="table table-border table-form">
										<tbody>
											<tr>
												<td>제목</td>
												<td>
													<p type="text" name="subject" maxlength="70" class="form-control">
														${data.title }
													</p>
												</td>
											</tr>
											<tr>
												<td>기안일</td>
												<td>
													<p type="text" name="name" maxlength="10" class="form-control">
														${data.regDt }
													</p>
												</td>
											</tr>
											<tr>
												<td>기안자</td>
												<td>
													<p type="text" name="name" maxlength="10" class="form-control">
														${data.empName }
													</p>
												</td>
											</tr>
											<tr>
												<td>업무명</td>
												<td>
													<p type="text" name="name" maxlength="10" class="form-control">
														${data.workName }
													</p>
												</td>
											</tr>
											<tr>
												<td>문서종류</td>
												<td>
													<p type="text" name="name" maxlength="10" class="form-control">
														${data.typeName }
													</p>
												</td>
											</tr>
											<tr>
												<td>참조문서</td>
												<td>
													<p type="text" name="name" maxlength="10" class="form-control" style="cursor: pointer;"
													   onclick="location.href='${pageContext.request.contextPath }/aprv/detail?docCd=${data.parentDocCd}'">
														${data.parentDocTitle }
													</p>
												</td>
											</tr>
											<tr>
												<td valign="top">내용</td>
												<td><textarea name="content" class="form-control" readonly="readonly">${data.contents }</textarea></td>
											</tr>
										</tbody>
									</table>
								</c:if>
								</div>

								<!-- 결재선 -->
								<div class="approval">
								
								<!-- 결재선 리스트 -->
								<c:set var="aprvActiv" value="false" />
								<c:if test="${not empty lineList && typeCd != '001' }">
									<c:forEach var="dto" items="${lineList }" varStatus="status">
										<c:choose>
	                                        <c:when test="${not empty dto.typeCd }">
		                                        <c:choose>
		                                        	<c:when test="${dto.typeCd eq '001' }">
														<div class="step approved">
															<div class="name">${dto.empName }</div>
															<div class="role">${dto.regDt }</div>
															<div class="badge">${dto.typeName }</div>
															<div class="step-comment">
																<textarea rows="" cols="" id="txt-${dto.flow }" readonly="readonly">${dto.comments }</textarea>
															</div>
														</div>
		                                        	</c:when>
		                                        	<c:when test="${dto.typeCd eq '002' }">
														<div class="step rejected current-focus">
															<div class="name">${dto.empName }</div>
															<div class="role">${dto.regDt }</div>
															<div class="badge">${dto.typeName }</div>
															<div class="step-comment">
																<textarea rows="" cols="" id="txt-${dto.flow }" readonly="readonly">${dto.comments }</textarea>
															</div>
														</div>
		                                        	</c:when>
		                                        	<c:when test="${dto.typeCd eq '003' }">
														<div class="step partial current-focus">
															<div class="name">${dto.empName }</div>
															<div class="role">${dto.regDt }</div>
															<div class="badge">${dto.typeName }</div>
															<div class="step-comment">
																<textarea rows="" cols="" id="txt-${dto.flow }" readonly="readonly">${dto.comments }</textarea>
															</div>
														</div>
		                                        	</c:when>
												</c:choose>
	                                        </c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${empty pageScope.firstCurrent && currentFlow != -1 && dto.flow > currentFlow }">
													    <c:if test="${dto.empCd eq sessionEmpCd }">
													        <c:set var="aprvActiv" value="true" />
													    </c:if>
														<div class="step current current-focus">
															<div class="name">${dto.empName }</div>
															<div class="date">-</div>
															<div class="badge">진행중</div>
														</div>
													    <c:set var="firstCurrent" value="true" scope="page"/>
													</c:when>
													<c:when test="${empty pageScope.firstCurrent && currentFlow == -1 }">
														<div class="step close">
															<div class="name">${dto.empName }</div>
															<div class="date">-</div>
															<div class="badge">종료</div>
														</div>
													</c:when>
													<c:otherwise>
														<div class="step pending">
															<div class="name">${dto.empName }</div>
															<div class="date">-</div>
															<div class="badge">대기중</div>
														</div>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</c:if>
								<c:if test="${not empty target && typeCd == '001'}">
									<div class="worktarget">
										업무 대상자
									</div>
									<br>
									<c:forEach var="dto" items="${target }" varStatus="status">
										<div class="step approved">
											<div class="name">${dto }</div>
										</div>
									</c:forEach>
								</c:if>
								<!-- 결재선 리스트 종료 -->
								
								</div>
							</div>
							
							<c:if test="${not empty fileList }">
							<div>
								<table>
								<c:forEach var="dto" items="${fileList }" varStatus="status">
									<tr>
										<td>
											<%-- <a href="${dto.path }" download="${dto.originName }">${dto.originName }</a> --%>
											<a href="${pageContext.request.contextPath}/download?docFileNo=${dto.fileNo}">${dto.originName }</a>
										</td>
									</tr>
								</c:forEach>
								</table>
							</div>
							
							</c:if>
							
							<!-- aprvActiv가 true로 설정되면 출력 -->
							<c:if test="${aprvActiv}">
								<!-- ===== 버튼 ===== -->
								<div class="actions">
									<button class="approve" onclick="openComment('승인', this)" value="001">승인</button>
									<button class="reject" onclick="openComment('반려', this)" value="002">반려</button>
									<button class="partial" onclick="openComment('조건부 승인', this)" value="003">조건부
										승인</button>
								</div>
	
								<!-- ===== 코멘트 ===== -->
								<div class="commentArea" id="commentArea">
									<h4 id="actionTitle">결재 의견</h4>
									<form name="addAprvForm" method="post">
										<input type="hidden" name="type_cd" id="type_cd">
										<div class="comment" id="commentBox">
											<textarea name="comments" placeholder="결재 의견을 입력하세요..." maxlength="2000"></textarea>
											<input type="button" class="btn-write" onclick="submitComment()" value="등록"></input>
										</div>
									</form>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>

		</div>
	</div>

</body>
</html>