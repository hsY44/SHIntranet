<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!-- 디테일? write? form ? 고민해야함. -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>담당자 등록</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/temp_common.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/emp.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/paging.css">
<script src="${pageContext.request.contextPath}/js/sidebar.js" defer></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
<script src="${pageContext.request.contextPath}/js/paging.js" defer></script>
<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
<script type="text/javascript">
	window.addEventListener('DOMContentLoaded', async () => 
	{
		await getWorkList();
		await getEmpList(1);
        document.getElementById("date").onblur = function() {
            const selectedDate = this.value;
            const minDate = this.min;
            if (selectedDate && minDate && selectedDate < minDate) {
                alert("선택할 수 없는 날짜입니다. " + minDate + " 이후로 입력해주세요.");
                this.value = this.min;
                this.focus();
            }
        }
	});
	
	const functions = {
		getEmpList: (page) => getEmpList(page),
	};
	
	// 업무 목록 가져오기
	async function getWorkList() {
		let url = "${pageContext.request.contextPath}/worksearch.do?type=000";
		
		try {
			let response = await fetch(url);
			
			if (!response.ok)
				throw new Error("에러발생");
			
			let data = await response.json();
	        const container = document.getElementById("work-tbody-item");
			
	        data.forEach(p => {
	            const tr = document.createElement('tr');
	            
	            // 각 <td>를 동적으로 생성
	            const td1 = document.createElement('td');
	            td1.textContent = p.num;  // p.num 값을 넣음
	            
	            const td2 = document.createElement('td');
	            td2.textContent = p.workType;  // p.workType 값을 넣음
	            
	            const td3 = document.createElement('td');
	            td3.textContent = p.workName;  // p.workName 값을 넣음
	            
	            const td4 = document.createElement('td');
	            const radioBtn = document.createElement('input');
	            radioBtn.type = 'radio';
	            radioBtn.name = 'work_cd';
	            radioBtn.value = p.workCd;
	            
	            radioBtn.onclick = () => onClickSelect("work", p.num, p.workCd, p.workName);  // 클릭 이벤트
	            
	            td4.appendChild(radioBtn);  // <input>을 <td>에 추가
	            
	            // 각 <td>를 <tr>에 추가
	            tr.appendChild(td1);
	            tr.appendChild(td2);
	            tr.appendChild(td3);
	            tr.appendChild(td4);

	            // <tr>을 container에 추가
	            container.appendChild(tr);
	        });
		} catch (e) {
            console.error("에러", e);
            alert("데이터를 불러오는 중 오류가 발생했습니다.");
		}
	}
	
	// 사원 목록 가져오기
	async function getEmpList(page) {
        // 같은 부서 사원 조회
		let url = "${pageContext.request.contextPath}/emp/api/list?page="+page;
        url += "&schType=deptName&kwd=${sessionScope.loginEmp.deptName}";
		
		try {
			let response = await fetch(url);
			
			if (!response.ok)
				throw new Error("에러발생");
			
			let data = await response.json();
			if (data) {
				setPaging(page, data?.dataCount, data?.totalPage, "getEmpList");
				renderData(data?.data, "emp");
			}

		} catch (e) {
            console.error("에러", e);
            alert("데이터를 불러오는 중 오류가 발생했습니다.");
		}
	}
	
	// 업무/사원 선택
	function onClickSelect(tagId, num, cd, name) {
		document.getElementById("select-"+tagId+"-name").value = "[" + num + "] " + name;
		document.getElementById(tagId+"_cd").value = cd;
        activateDateLimit();
	}

    // 업무/사원 모두 선택하면 날짜 선택 활성화
    function activateDateLimit()
    {
        const f = document.managerForm;
        const work_cd = f.work_cd.value;
        const emp_cd = f.emp_cd.value;

        if (!f.work_cd.value.trim() || !f.emp_cd.value.trim())
            return;

        getManagerIssue(work_cd, emp_cd);
    }

    // 최소 날짜 가져와서 설정
    function getManagerIssue(work_cd, emp_cd) {
        let url = "${pageContext.request.contextPath}/manager-mgmt/api/getManagerIssue";

        $.ajax({
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

                const date = document.getElementById("date");

                /**
                 * 담당자 등록 규칙
                 * 1. [issueType]가 'S'(시작)인 경우:
                 *    - 아직 담당 업무가 종료되지 않았으므로 신규 등록 불가.
                 * 2. [issueType]가 'E'(종료)인 경우:
                 *    - 이전 담당 이력이 종료되었으므로 신규 등록(S) 가능.
                 *    - 단, 새로 등록하는 시작일은 이전 기록의 종료일보다 이후여야 함.
                 * * 3. 이력이 아예 없는 경우:
                 *    - 첫 등록이므로 자유롭게 시작(S) 등록 가능.
                 */
                if (issueType==="S")
                {
                    alert("활성화된 담당자는 신규 등록이 불가능합니다.\n종료 후 새로 등록해 주세요.");
                    document.getElementById("emp_cd").value = "";
                    const radios = document.getElementsByName("emp_cd");
                    radios.forEach((radio) => {
                        radio.checked = false;
                    });
                    document.getElementById("select-emp-name").value = "";
                    date.value = "";
                    date.disabled = true;

                    return;
                }

                // 날짜 등록 disabled 해제
                date.disabled = false;

                if (issueType==="E")
                {
                    let dateObj = new Date(issueDate);
                    dateObj.setDate(dateObj.getDate() + 1);
                    date.min = dateObj.toISOString().split('T')[0];
                }
                else
                    date.min = "";

            }
        });
    }

    // 업무, 사원, 기간 체크
    function addManagerCheck() {
        const f = document.managerForm;

        const work_cd = f.work_cd.value;
        const emp_cd = f.emp_cd.value;
        const date = f.date.value;
        const minDate = f.date.min;

        if (!work_cd.trim())
            alert("업무를 선택해 주세요.");
        else if (!emp_cd.trim())
            alert("업무를 담당할 사원을 선택해 주세요.");
        else if (!date.trim())
            alert("시작 기간을 선택해 주세요.");
        else if (minDate && date < minDate) {
            alert("이전의 종료일보다 같거나 빠른 날짜는 등록할 수 없습니다.");
            f.date.value = "";
            f.date.focus();
        }
        else
            addManager(work_cd, emp_cd, date);
    }

    // 담당자 등록
    function addManager(work_cd, emp_cd, date) {
        let url = "${pageContext.request.contextPath}/manager-mgmt/addManager";
        // '${issueType}'
        $.ajax({
            "type":"POST",
            "url":url,
            "data": {
                work_cd:work_cd,
                emp_cd:emp_cd,
                issue_type: "S",
                date:date
            },
            "error":function(e) {
                console.error(e);
                alert("담당자 등록에 실패했습니다.");
            },
            "success":function(data) {
                if (data?.success) {
                    alert(data?.message);
                    if (document.referrer && window.history.length > 1)
                        location.href = document.referrer;
                    else
                        location.href = "${pageContext.request.contextPath}/manager-mgmt/list";
                } else
                    alert(data?.message);
            }
        });
    }

    function onCancel() {
        if (confirm("변경사항이 저장되지 않을 수 있습니다. 돌아가시겠습니까?")) {
            if (document.referrer && window.history.length > 1)
                location.href = document.referrer;
            else
                location.href = "${pageContext.request.contextPath}/manager-mgmt/list";
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
            <div class="header" id="header" data-ctx="${pageContext.request.contextPath}"
                 data-name="${sessionScope.loginEmp.empName}" data-buseo="${sessionScope.loginEmp.deptName}"
                 data-jikgup="${sessionScope.loginEmp.positionName}"></div>
 
			<div class="content">
				<div class="contain">
					<span class="page-title">담당자 등록</span>
				</div>
				<div class="emp-detail-container">
					<!-- 입력 영역 -->
					<div class="empinfo" id="edit-form">
					
						<!-- 업무선택 -->
						<div class="form-row">
							<div class="form-group">
								<span>업무</span>
								<div class="table-border work-table-list">
								
									<table class="table table-list">
										<thead>
											<tr>
												<th>번호</th>
												<th>업무 종류</th>
												<th>업무명</th>
												<!-- 선택칸은 종료일 경우 X -->
												<th>선택</th>
											</tr>
										</thead>
										<tbody id="work-tbody-item">
										</tbody>
									</table>
								</div>
								<span>선택된 업무명</span>
								<input type="text" id="select-work-name" disabled>
							</div>
						</div>
						
						<!-- 사원선택 -->
						<div class="form-row">
							<div class="form-group">
								<span>사원</span>
								<div class="table-border emp-table-list">
								
									<table class="table table-list">
										<thead>
											<tr>
												<th>번호</th>
												<th>사원코드</th>
												<th>사원명</th>
												<th>직급</th>
												<th>선택</th>
											</tr>
										</thead>
										<tbody id="emp-tbody-item">
										</tbody>
										<tfoot>
											<tr>
												<td colspan="5">
													<div class="pagination" id="emp-pagination"></div>
												</td>
											</tr>
										</tfoot>
									</table>
								</div>
								<span>선택된 사원명</span>
								<input type="text" id="select-emp-name" disabled>
							</div>
						</div>
						
						<!-- 날짜선택 -->
						<div>
							<form name="managerForm" method="post">
								<input type="hidden" name="work_cd" id="work_cd" />
								<input type="hidden" name="emp_cd" id="emp_cd" />
								
								<div class="form-group">
									<span>시작 기간</span>
                                    <input type="date" id="date" name="date" min="" max="" disabled>
								</div>
							</form>
						</div>
						
						
						<div class="btn-row">
							<button class="btn btn-primary" onclick="addManagerCheck()">등록하기</button>
							<button class="btn" onclick="onCancel()">취소</button>
						</div>
					</div>
 
				</div>
			</div>
		</div>
 
	</div>

</body>
</html>