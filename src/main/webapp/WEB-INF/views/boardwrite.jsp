<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
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
<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
<script type="text/javascript">

	const dt = new DataTransfer();
	
	$(function() {
		
		const $fileInput = $("#hidden-file-input");
		
		
	    $("#drop-file").on("dragover", function(e) {
	        e.preventDefault();
	        $(this).css("background-color", "#f8fafc");
	    });
	
	    $("#drop-file").on("dragleave", function(e) {
	        e.preventDefault();
	        $(this).css("background-color", "white");
	    });
	
	    // 파일 드롭 시
	    $("#drop-file").on("drop", function(e) {
	        e.preventDefault();
	        $(this).css("background-color", "white");
	
	        let files = e.originalEvent.dataTransfer.files;

	        for (let i = 0; i < files.length; i++) {
	            dt.items.add(files[i]);
	        }

	        $fileInput[0].files = dt.files;
	        
	        // 화면 다시 그리기
	        renderNewFiles();
	    });
	}); 
	
	// 삭제처리한 첨부파일 
	function removeFileStep(fileNo)
	{
		const item = document.getElementById("f_"+fileNo);
		item.style.display = "none";
		
		const input = document.createElement("input");
        input.type = "hidden";
        input.name = "delFiles";
        input.value = fileNo;
        document.forms["boardForm"].appendChild(input);
		
	}
	
	// 새로 추가한 파일 x 클릭 시
	window.removeNew = function(index) {
        dt.items.remove(index); 
        const fileInput = document.getElementById("hidden-file-input");
        if (fileInput) 
        	fileInput.files = dt.files;
        
        renderNewFiles();
    };
	
	function renderNewFiles() {
	    let html = "";
	    const files = dt.files;
	    
	    for (let i = 0; i < files.length; i++) {
	        html += `
	                <span style="font-size: 14px; color: #1e293b;">
	                    \${files[i].name}
	                </span>

                	<span onclick="removeNew(\${i})" 
                          style="cursor:pointer; color: #ef4444; font-weight: bold; margin-left: 8px;">[X]
            		</span>`;
	    }
	    document.getElementById("new-files-area").innerHTML = html;
	}

	function sendOk()
	{
		let f = document.boardForm;
		
		if (!f.title.value.trim()) 
		{
			alert("제목을 입력해야 합니다.");
			f.title.focus();
			return;
		}
		
		if (!f.contents.value.trim()) 
		{
			alert("내용을 입력해야 합니다.");
			f.contents.focus();
			return;
		}
		
		
		f.action = "${pageContext.request.contextPath}/board/${mode }.do";
			
		f.submit();
		
	}
	
</script>
</head>
<body>
    <div class="container">
        <div class="sidebar" id="sidebar"data-hr="${sessionScope.loginEmp.deptName == '인사부' ? 'true' : 'false'}"
			data-ctx="${pageContext.request.contextPath}"></div>

        <div class="main-wrapper">
            <div class="header" id="header"data-name="${sessionScope.loginEmp.empName}"
             data-buseo="${sessionScope.loginEmp.deptName}"
             data-jikgup="${sessionScope.loginEmp.positionName}"
             data-ctx="${pageContext.request.contextPath}"></div>
			
			<form name="boardForm" method="post" enctype="multipart/form-data">
            <div class="content">
            	<span class="board-header" style="display: block; margin-bottom: 25px;">
						${param.type == '001' ? '공지사항' : '일반 게시판'} 
				</span>
                <div class="board">
                	<input type="hidden" name="type" value="${type}">
                    <div class="detail-header" style="padding: 20px 30px;">
                       <input type="text" name="title" value="${dto.title}" placeholder="제목을 입력해 주세요."
                       class="write-title-input">
                        <div class="detail-info" style="margin-top: 15px; border-top: 1px solid #f1f5f9; padding-top: 15px;">
                            <span class="info-item">
                            	<b style="color: #64748b; margin-right: 10px;">작성자</b> 
                            	<span style="color: #1e293b;">${mode=="update" ? dto.name : loginEmp.empName }</span>
                            </span>
                        </div>
                    </div>

                    <div class="detail-content" style="padding: 0 30px 30px 30px;">
                        <textarea name="contents" class="write-content-textarea" 
                        placeholder="내용을 입력해 주세요.">${dto.contents }</textarea>
                    </div>
                    <c:if test="${type == '001'}">
					    <div class="file-list-area" style=" margin: 20px 30px; padding: 15px; background: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0;">
					        <div style="font-weight: bold; color: #64748b; margin-bottom: 10px; font-size: 14px;">
					            첨부파일
    						</div>
    
							    <ul id="existingFileList" style="list-style: none; padding: 0; margin: 0;">
							        <c:if test="${mode == 'update' && not empty flist}">
							            <c:forEach var="fileDto" items="${flist}">
							                <span id="f_${fileDto.fileNo}" style="margin-bottom: 5px;">
							                    <a style="font-size: 14px; color: #1e293b;">${fileDto.originName}</a>
							                    <span onclick="removeFileStep('${fileDto.fileNo}')" 
							                          style="cursor:pointer; color: #ef4444; font-weight: bold; margin-left: 8px;">[X]</span>
							                </span>
							            </c:forEach>
							        </c:if>
							    </ul>
							    
							    <span id="new-files-area"></span>
					    </div>
                    
	                    <div class="file-upload-section" style="padding: 0 30px 30px 30px;">
						    <div id="drop-file" style="border: 2px dashed #cbd5e1; padding: 30px; text-align: center; color: #64748b; border-radius: 8px; cursor: pointer;">
						        파일을 여기에 드래그 해주세요
						        <input type="file" name="selectFile" id="hidden-file-input" multiple style="display: none;">
						    </div>
						    
						</div>
					</c:if>
                    
                </div> <!-- board end -->
                <div class="write-form-actions">
                	<button type="button" class="btn-cancel"
                	onclick="location.href='${pageContext.request.contextPath}/board/list.do?${mode=='update' ? query : 'type='.concat(type)}';">
                	취소</button>
                	<button type="button" class="btn-write" onclick="sendOk()">
					${mode=="update" ? "수정" : "등록" }
					</button>
				</div>
                 
				<c:if test="${mode=='update' }">
					<input type="hidden" name="num" value="${dto.num }">
					<input type="hidden" name="page" value="${page }">
					<input type="hidden" name="schType" value="${schType }">
					<input type="hidden" name="kwd" value="${kwd }">
				</c:if>
        	</div> <!-- content end -->
        	</form>
    	</div> <!-- main-wrapper end -->
    </div> <!-- container end -->

</body>
</html>