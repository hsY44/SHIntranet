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
    	grid-template-columns: 10% 15% 30% 15% 20%;
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
				let result = "<option value='000'>전체</option>";
				
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
						result += "<option value="; 
						result += jsonObj[i].workCd;
						result += ">";
						result += jsonObj[i].workName;
						result += "</option>"; 
					}
					
					$("#work_cd").html(result);
				}
			})
		});
		
		$("#btn-add").click(function() 
		{
			if(!window.confirm("추가하시겠습니까?"))
			{
				//alert("확인");
				return;
			}
			
			//alert("확인");
			
			let url = "deptworkadd.do";
			
			let params = "workCd=" + $("#work_cd").val();
			
			$.ajax({
				
				"type":"POST"
				, "url" : ctx + "/" + url
				, "data" : params
				, "dataType":"json"
				, "error":function(e)
				{
					alert(e.responseText);
				}
				, "success":function(jsonObj)
				{
					let result = jsonObj.result;
					
					if(result === -1)
					{
						alert("이미 등록된 업무 입니다.");
					}
					else if(result === 0)
					{
						alert("등록 실패");
					}
					else if(result === 1)
					{
						alert("등록 완료");
					}
				}
			});
		});
	})
	
</script>

<title>부서 업무 등록 - SH Company</title>
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
				<span class="board-header">부서 업무 등록</span>
					<div class="inner">
						<select class="search-type" id="type">
							<!-- <option value="000">전체</option>
							<option value="001">근태</option>
							<option value="002">보고</option>
							<option value="003">업무</option>
							<option value="004">복리후생</option> -->
						</select> 
						<select name="" id="work_cd" class="search-input">
						</select>
						<input type="button" class="btn-write" id="btn-add" value="추가하기" />
					</div>
			</div>
		</div>
	
	</div>
	
</div>

</body>
</html>