	<%@ page contentType="text/html; charset=UTF-8"%>
	<!DOCTYPE html>
	<html>
	<head>
	<meta charset="UTF-8">
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/emp.css">
	
	<style type="text/css">
		
		.input-content, .txt-area
		{
			padding: 12px 16px;
		    border: 1px solid #e2e8f0;
		    border-radius: 6px;
		    font-size: 16px;
		    width: 500px;
		    font-family: 'Noto Sans KR', sans-serif;
		    outline: none;
		}
		
		select, button
		{
			width: 150px;
		    height: 43px;
		    font-size: 18px;
		    font-family: 'Noto Sans KR', sans-serif;
		    border: 1px solid #e2e8f0;
		    border-radius: 6px;
		    padding: 0 8px;
		}
		
		.menu
		{
			display: inline-block;
			width: 100px;
			text-align: center;
		}
		
		.main
		{
			display: flex;
			flex-direction: column;
			background-color: white;
			border: 1px solid #e2e8f0;
			border-radius: 6px;
			width: 40%;
			margin: 0 auto;
			max-width: 1000px;
			padding-bottom: 10px;
		}
		
		.btn-menu
		{
			margin: 0 auto;	
		}
		
		.reset
		{
			background-color: red;
		}
		
		.box
		{
			display: flex;
			align-items: center;
		}
		
		#add
		{
			font-size: 10px;
			width: 50px;
			height: 20px;
			margin-left: 10px;
			padding: 0px;
			text-align: center;
		}
		
		#drop-file
		{
			margin: 0 auto;
			border: 2px solid gray; 
			padding: 20px;
		}
		
		ul
		{
			margin: 0 auto;
			padding: 20px;
		}
		
		#target
		{
			padding: 12px 16px;
		    border: 1px solid #e2e8f0;
		    border-radius: 6px;
		    font-size: 16px;
		    width: 500px;
		    font-family: 'Noto Sans KR', sans-serif;
		    outline: none;
		
			/* display: flex;
			flex-direction: column;
			align-items: center;
			width: 100%;
			border: 1px solid black;
			margin: 0 auto; */
		}
		
		.targetItem
		{
			height: 30px;
			display: inline-block;
			text-align: center;
			width: 80%;
		}
		
	</style>
	
	<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
	<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
	
	
	<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
	
	<script type="text/javascript">
		
		$(function() 
		{
			const ctx = "${pageContext.request.contextPath}";
			
			$("#cancel").click(function()
			{
				window.location.href = ctx + "/myinfo.do";
			});
	
			$("#drop-file").on("dragover",function(e)
			{
				e.preventDefault();	
			});
			
			let fileList = new Array();
			
			$("#drop-file").on("drop",function(e)
			{
				e.preventDefault();
				
				let file = e.originalEvent.dataTransfer.files;
				
				for(let i = 0; i < file.length; i++)
				{
					fileList.push(file[i]);
					
					$("#file-list").append("<li>" + file[i].name + "</li>");
				}
			});
			
			$.ajax(
			{
				"type":"POST"
				, "url":ctx + "/worktargetlist.do"
				, "dataType":"json"
				, "error":function(e)
				{
					alert(e.responseText);
				}
				, "success":function(jsonObj)
				{
					let result = "";
					
					if(jsonObj.length === 0)
					{
						result += "업무 지시 가능한 인원이 없습니다.";
					}
					else
					{
						for(let i = 0; i < jsonObj.length; i++)
						{
							result += "<label class='targetItem'>";
							result += jsonObj[i].empCd + " ";
							result += jsonObj[i].empName + " ";
							result += jsonObj[i].positionName + " ";
							result += "<input type='checkbox' name='target' value='" + jsonObj[i].empCd + "'";
							result += "/></label>";
						}
					}
					
					$("#target").html(result);
				}
			});
			
			$.ajax(
			{
				"type":"POST"
				, "url":ctx + "/worklist.do"
				, "dataType":"json"
				, "error":function(e)
				{
					alert(e.responseText);
				}
				, "success":function(jsonObj)
				{
					let result = "";
					
					for(let i = 0; i < jsonObj.length; i++)
					{
						result += "<option value='";
						result += jsonObj[i].code;
						result += "'>";
						result += jsonObj[i].name;
						result += "</option>";
					}
					
					$("#type").html(result);
					$("#type").trigger("change");
				}
			});
		
			$("#type").change(function()
			{
				// alert($(this).val());
				
				let url = "worksearch.do";
				let params = "type=" + $(this).val();
				
				$.ajax({
					"type":"POST"
					, "url":ctx + "/" + url
					, "data":params
					, "dataType":"json"
					//, "beforeSend"
					,"error":function(e)
					{
						alert(e.responseText);
					}
					,"success":function(jsonObj)
					{
						let result = "";
						
						for(let i = 0; i < jsonObj.length; i++)
						{
							result += "<option value='"; 
							result += jsonObj[i].workCd;
							result += "'>";
							result += jsonObj[i].workName;
							result += "</option>"; 
						}
						
						$("#work-name").html(result);
					}
				})
			}); 
			
			$("#submit").click(function() 
			{
				let title = $("#title-content").val().trim();
				
				// alert(title);
				
				if(title === "")
				{
					alert("제목을 작성하세요");
					$("#title-content").focus();
					return;
				}
				
				if(title.length > 50)
				{
					alert("제목은 50자를 넘을 수 없습니다.");
					return;
				}
				
				let content = $(".txt-area").val();
				
				// alert(content);
				
				if(content.trim() === "")
				{
					alert("내용을 입력하세요");
					$(".txt-area").focus();
					return;
				}
				
				if(content.length > 2000)
				{
					alert("내용은 2000자를 넘을 수 없습니다.");
					return;
				}	
				
				let docType = $("#doc-type").val();
				
				let workCd = $("#work-name").val();
				
				if($("input[name='target']:checked").length === 0)
				{
					alert("대상자를 최소 한명은 선택해야 합니다.");
					return;
				}
				
				let formData = new FormData();
				
				formData.append("docType",docType);
				formData.append("workCd",workCd);
				formData.append("title",title);
				formData.append("content",content);
				
				$("input[name='target']:checked").each(function()
				{
					formData.append("target",$(this).val());
				});
				
				for(let i = 0; i < fileList.length; i++)
				{
					formData.append("files",fileList[i]);
				}
				
			/* 	let targetCds = $("input[name='target']:checked").map(function()
				{
					return 'target=' + $(this).val();				
				}).get().join("&"); */
				
			/* 	if(targetCds === "")
				{
					alert("대상자를 선택해야합니다.");
					return;
				} */
				
				// let params = "docType=" + docType + "&workCd=" + workCd + "&title=" + title + "&content=" + content + "&" + targetCds;
				
				$.ajax(
				{
					"type":"POST"
					,"url":ctx + "/aprvrequest.do"
					,"data":formData
					,"dataType":"json"
					,"processData":false
					,"contentType":false
					,"error":function(e)
					{
						alert(e.responseText);
					}
					,"success":function(jsonObj)
					{
						let result = jsonObj.result;
						
						if(result === "")
						{
							alert("등록 실패");
						}
						else
						{
							alert(result + "번 문서 등록 완료");
						}
						
						$("#title-content").val("");
						$(".txt-area").val("");		
						$("#file-list").html("");
						fileList = [];
						window.location.href = ctx + "/myinfo.do";
					}
				});
			});
		});
		
	</script>
	
	
	<title>업무 지시 작성</title>
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
				
				<div class="main">
					
					<h1 style="color: black">업무 지시 문서 작성</h1>
					
					<input type="hidden" name="" id="doc-type" value="001"/>
					
					<!-- 
					<div class="box">
						<span class="menu">문서 종류</span>
						
						<button id="doc-type" value="001">업무지시</button>
						
					</div> -->
					
					<div class="box">
						<span class="menu">업무 종류</span>
						<select name="" id="type">
							<!-- <option value="">근태</option>
							<option value="">보고</option>
							<option value="">업무</option>
							<option value="">복리후생</option> -->
						</select>
						<select name="" id="work-name">
							<!-- <option value="">출장</option>
							<option value="">주간보고</option>
							<option value="">협력요청</option>
							<option value="">지원금신청</option> -->
						</select>
					</div>
					
					<div class="box">
						<span class="menu">제목</span>
						<input type="text" name="" id="title-content" class="input-content"/>
					</div>
					
					<div class="box">
						<span class="menu">내용</span>
						<textarea name="txt-content" id="" cols="30" rows="10" class="txt-area"></textarea>
					</div>
	
					<div class="box">
						<span class="menu">지시 대상자</span>
						<div id="target">
							<!-- <label class="targetItem">SW0001 김길동 사원 <input type="checkbox" name="target" id="" value="SW0001"/></label><br>
							<label class="targetItem">SW0002 이길동 대리 <input type="checkbox" name="target" id="" value="SW0002"/></label><br>
							<label class="targetItem">SW0003 박길동 과장 <input type="checkbox" name="target" id="" value="SW0003"/></label><br>
							<label class="targetItem">SW0004 최길동 차장 <input type="checkbox" name="target" id="" value="SW0004"/></label><br> -->
						</div>
					</div>
					
					<br>
					
					<div id="drop-file">
						파일을 여기에 드래그 하세요	
					</div>
	
					<ul id="file-list"></ul>
	
					<br>
					
					<div class="btn-menu">
						<button class="btn emp-btn" id="submit">작성하기</button>
						<!-- <button class="emp-btn reset">초기화</button> -->
						<button class="btn" id="cancel">취소</button>
					</div>
					
				</div> <!-- End div class="main" -->
				
				
			</div>
			
		</div>
		
	</div>
	
	</body>
	</html>