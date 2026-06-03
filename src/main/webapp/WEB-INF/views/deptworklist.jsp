<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/board.css"/>

<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>

<style type="text/css">
	
	.board-row 
	{
    	grid-template-columns: 25% 25% 25% 25%;
	}
	
	.sidebar .board
	{
		 background: none;
  		 border: none;
  	 	 border-radius: 0;
	}
	
</style>

<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>

<script type="text/javascript">
	
	$(function() 
	{
		const ctx = "${pageContext.request.contextPath}";
		
		$(".btn-write").click(function()
		{
			window.location.href = ctx + "/page/deptworkmanagement.do"; 
		});
		
		$("#btn-search").click(function() 
		{
			let type = $("#search-type").val();
			let keyword = $("#search-input").val();
			
			if(keyword === "")
			{
				keyword = "부";
			}
			
			if(type === "WORK_NAME" && keyword.length > 50)
			{
				alert("업무명은 50자를 넘길 수 없습니다.");
				return;
			}
			
			else if(type === "DEPT_NAME" && keyword.length > 20)
			{
				alert("부서명은 20자를 넘길 수 없습니다.");
				return;
			}
			
			else if(type === "TYPE_NAME" && keyword.length > 20)
			{
				alert("업무 종류명은 20자를 넘길 수 없습니다.");
				return;
			}
			
			// alert(type + keyword);
			
			let url = "deptworksearch.do";
			let params = "type=" + type + "&keyword=" + keyword;
			
			$.ajax(
			{
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
						result += "<div class='board-row'>";
						
						result += "<span class='board-writer'>" + jsonObj[i].deptName + "</span>\n";
						result += "<span class='board-writer'>" + jsonObj[i].workType + "</span>\n";
						result += "<span class='board-writer'>" + jsonObj[i].workName + "</span>\n";
						result += "<span class='board-number'>" + jsonObj[i].flow + "</span>\n";
						
						result += "</div>";
					}
					
					$("#board-items").html(result);
					
					$("#search-input").val("");
				}
			})
		})
		
		 $("#btn-search").click();
	})
	
</script>

<title>부서 업무 조회 - SH Company</title>
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
				
			<div class="contain">
					<span class="board-header">부서 업무 조회</span>
					<div class="inner">
						<select class="search-type" id="search-type">
							<option value="DEPT_NAME">부서명</option>
							<option value="TYPE_NAME">업무 종류</option>
							<option value="WORK_NAME">업무명</option>
						</select> 
						<input type="text" class="search-input" id="search-input"
							placeholder="키워드를 입력하세요" /> 
						<input type="button" class="board-search" id="btn-search" value="검색하기" />
					</div>
				</div>
			
			<div class="board">
					<div class="board-list" id="board-list">
						<div class="board-row board-list-header">
							<span class="board-number">부서명</span> 
							<span class="board-number">업무 종류</span>
							<span class="board-number">업무명</span> 
							<span class="board-number">결재 순서</span>
						</div>

						<div id="board-items"> 
							
						</div>
				</div>
			</div>
				
				<div class="board-action-row">
					<input type="button" class="btn-write" value="추가하기" />
				</div>
		</div>
	
	</div>
	
</div>

</body>
</html>