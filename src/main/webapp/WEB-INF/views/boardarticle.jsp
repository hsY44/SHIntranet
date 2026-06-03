<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 상세보기 - SH Company</title>
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

	const cp = "${pageContext.request.contextPath}";
	
	// 게시글 번호
	const boardNum = "${dto.num}";
	// 로그인 아이디
	const loginEmpCd = "${sessionScope.loginEmp.empCd}";
	// 로그인한 사원의 부서
	const logindeptCd = "${sessionScope.loginEmp.deptCd}";
	// 게시판 타입
	const type = "${param.type}";
	
	$(function () {
		const writerEmpCd = "${dto.empCd}";
		
		// 로그인한 사람과 작성자가 같으면 수정 삭제 버튼 보이게
		if (loginEmpCd === writerEmpCd) {
			$(".right-actions").show();
		}
		
		// 인사부일 경우 공지사항의 수정, 삭제 버튼 보이게
		if (logindeptCd === "DE02" && type === "001") {
			$(".right-actions").show();
		}
		
		// 공지사항 게시글일 경우 첨부파일 영역 보이게
		if (type === "001") {
			$(".file-list-area").show();
		}
		
		// 페이지 로드 시 댓글 불러오기
		loadComments();
		
		// 댓글 등록
		$(".btn-comment-submit").click(function() {
			let contents = $(".comment-form textarea").val().trim();
			
			if (!contents) {
				alert("댓글을 입력해주세요.");
				return;
			}
			
			$.ajax({
				"type": "POST"
				, "url": cp + "/comment/insert.do"
				, "data": { num: boardNum, contents: contents }
				, "dataType": "json"
				, "success": function(response) {
					$(".comment-form textarea").val("");
					loadComments();
				}
				, "error":function(e) {
					console.log(e);
					alert("댓글 등록에 실패했습니다.");
				}
			});
		});
		
		// 댓글 삭제
		$(document).on("click", ".comment-del-btn", function() {
		    const commentNum = $(this).data("num");
		    
		    console.log("삭제할 댓글 번호:", commentNum);
		    
		    if(!confirm("댓글을 정말 삭제하시겠습니까?")) return;

		    $.ajax({
		        "type": "POST"
		        , "url": cp + "/comment/delete.do"
		        , "data": { num: commentNum }
		        , "dataType": "json"
		        , "success": function(res) {
		            if(res.result === "success") {
		                alert("삭제되었습니다.");
		                loadComments();
		        	}
		        }
		        , "error": function() {
		            alert("서버 통신 오류가 발생했습니다.");
		        }
		    });
		});
	});
	
	// 댓글 목록 가져오는 함수
	function loadComments() {
		
		$.ajax({
			
			"type": "POST"
			, "url": cp + "/comment/list.do"
			, "data": {num: boardNum}
			, "dataType": "json"
			, "cache": false
			, "success": function(jsonObj) {
				let out = "";
			
				for (let idx=0; idx<jsonObj.length; idx++)
				{
					let item = jsonObj[idx];
					
					out += "<div class='comment-item'>";	
					
					// 삭제된 댓글
					if (item.dropNum > 0) 
					{
						out += "<div class='comment-text' style='color: #bbb;'>삭제된 댓글입니다.</div>";
					}
					else
					{
						out += "	<div class='comment-info'>";			
						out += "		<span class='comment-name'>" + item.name + "</span>";			
						out += "		<span class='comment-date'>" + item.regDate + "</span>";			
						out += "	</div>";			
						out += "	<div class='comment-text'>" + item.contents + "</div>";
						
						// 작성자 일 때 버튼 활성화
						if (loginEmpCd === item.empCd) {
		                    out += "<button class='comment-del-btn' data-num='" + item.num + "'>삭제</button>";
		                }	
					}
					out += "</div>";			
				}
				
				$(".comment-list").empty();
				$(".comment-list").html(out);
				
				// 댓글 갯수
				$(".comment-count span").text("(" + jsonObj.length + ")");
				
			}
			, "error":function(e) {
				alert(e.responseText);
			}
		});
	}
	
	// 게시글 삭제 함수
	function deleteBoard()
	{
		let url = "${pageContext.request.contextPath}/board/delete.do?num=${dto.num}&${query }";
		
		if (confirm("게시물을 정말 삭제하시겠습니까?")) 
			location.href= url;
	}
	
</script>
</head>
<body>
    <div class="container">
        <div class="sidebar" id="sidebar" 
        data-hr="${sessionScope.loginEmp.deptName == '인사부' ? 'true' : 'false'}"
         data-ctx="${pageContext.request.contextPath}">
        </div>

        <div class="main-wrapper">
            <div class="header" id="header" 
            data-name="${sessionScope.loginEmp.empName}"
             data-buseo="${sessionScope.loginEmp.deptName}"
             data-jikgup="${sessionScope.loginEmp.positionName}"
             data-ctx="${pageContext.request.contextPath}">
            </div>

            <div class="content">
            	<span class="board-header" style="display: block; margin-bottom: 25px;">
						${param.type == '001' ? '공지사항' : '일반 게시판'} 
					</span>
                <div class="board">
                    <div class="detail-header">
                        <span class="detail-title">${dto.title}</span>
                        <div class="detail-info">
                            <span class="info-item"><b>작성자</b> ${dto.name}</span>
                            <span class="info-item"><b>작성일</b> ${dto.regDate}</span>
                        </div>
                    </div>

                    <div class="detail-content">
                        ${dto.contents}
                        </div>
                    
                    <c:if test="${not empty flist}">
					    <div class="file-list-area" style="display:none; margin: 20px 30px; padding: 15px; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0;">
					        <div style="font-weight: bold; color: #64748b; margin-bottom: 10px; font-size: 14px;">
					            첨부파일 (${flist.size()})
					        </div>
					        <ul style="list-style: none; padding: 0; margin: 0;">
					            <c:forEach var="fileDto" items="${flist}">
					                  <a href="${pageContext.request.contextPath}/download?fileNo=${fileDto.fileNo}" 
					                  style="text-decoration: none; color: #1e293b;">${fileDto.originName}</a>
					            </c:forEach>
					        </ul>
					    </div>
					</c:if>

                    <div class="detail-actions">
                        <div class="left-actions">
                            <input type="button" class="page-btn" value="목록으로" onclick="location.href='${pageContext.request.contextPath }/board/list.do?${query }'" />
                        </div>
                        <div class="right-actions" style="display: none;">
                            <input type="button" class="btn-write" value="수정하기" onclick="location.href='${pageContext.request.contextPath }/board/update.do?${query}&num=${dto.num}'" style="background: #64748b;"/>
                            <input type="button" class="btn-write" value="삭제하기" onclick="deleteBoard()" style="background: #ef4444;"/>
                        </div>
                    </div>

                    <div class="comment-section">
                        <h3 class="comment-count">댓글 <span>(0)</span></h3>
                        
                        <div class="comment-list">
                            </div>

                        <div class="comment-form">
                            <textarea id="contents" name="contents" placeholder="댓글을 입력해주세요."></textarea>
                            <button class="btn-comment-submit">등록</button>
                        </div>
                    </div>
                </div> <!-- board end -->
        	</div> <!-- content end -->
    	</div> <!-- main-wrapper end -->
    </div> <!-- container end -->

</body>
</html>