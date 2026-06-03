    

    const PAGE_SIZE = 5;   // 페이지당 게시글 수
    const PAGE_BLOCK = 3;   // 한 번에 표시할 페이지 번호 개수

    let currentPage = 1;
    let filteredPosts = [];   // 검색 적용 후 대상
	let tagId = "";
	
	let paging = 0;
	let dataCount = 0;
	let totalPage = 0;
	let funcName = "";
	
	function setPaging(pg, cnt, totPage, fnName)
	{
		paging = pg;
		currentPage = pg;
		dataCount = cnt;
		totalPage = totPage;
		funcName = fnName;
	}
	
	function renderData(data, id)
	{
		console.log("da ", data);
		filteredPosts = [...data];
		console.log("filteredPosts ", filteredPosts);
		tagId = id;
		render();
	}

    /** 현재 페이지 게시글 목록 렌더링 */
    function renderList() {
        const container = document.getElementById(tagId+"-tbody-item");
		
		console.log("renderList");
		
        if (!container) return;


		console.log("renderList 랜더중", filteredPosts);
		
        const posts = filteredPosts;
		
		container.innerHTML = "";

        if (posts.length === 0) {
            return;
        }

		posts.forEach(p => {
		        const tr = document.createElement('tr');
					tr.innerHTML = `
						<td>${p.num}</td>                                                           
						<td>${p.empCd}</td>                                                         
						<td>${p.empName}</td>                                                       
						<td>${p.positionName}</td>                                                  
						<td>                                                                        
							<input type="radio" name="${tagId}_cd" value="${p.empCd }"                  
							onclick="onClickSelect('${tagId}', '${p.num}','${p.empCd}', '${p.empName} ${p.positionName}')">
						</td>
					`;
				
		        container.appendChild(tr);
		});
		
    }

	/** 이미 paging 되어 있는 api라면... */
    /** 페이지네이션 버튼 렌더링 */
    function renderPaginationPaging() {
        const container = document.getElementById(tagId+"-pagination");
        if (!container) return;

        const totalPages = totalPage;
        // 현재 페이지가 속한 블록의 시작/끝 페이지 계산
        const blockStart = Math.floor((currentPage - 1) / PAGE_BLOCK) * PAGE_BLOCK + 1;
        const blockEnd = Math.min(blockStart + PAGE_BLOCK - 1, totalPages);

        let html = "";

        // ◀◀ 처음
        html += `<button class="page-btn page-arrow" 
                    ${blockStart === 1 ? "disabled" : ""}
                    onclick="goPage(1)" title="처음">«</button>`;

        // ◀ 이전 블록
        html += `<button class="page-btn page-arrow"
                    ${blockStart === 1 ? "disabled" : ""}
                    onclick="goPage(${blockStart - 1})" title="이전">‹</button>`;

        // 번호 버튼
        for (let p = blockStart;p <= blockEnd;p++) {
            html += `<button class="page-btn ${p === currentPage ? "active" : ""}"
                        onclick="goPage(${p})">${p}</button>`;
        }

        // ▶ 다음 블록
        html += `<button class="page-btn page-arrow"
                    ${blockEnd === totalPages ? "disabled" : ""}
                    onclick="goPage(${blockEnd + 1})" title="다음">›</button>`;

        // ▶▶ 마지막
        html += `<button class="page-btn page-arrow"
                    ${blockEnd === totalPages ? "disabled" : ""}
                    onclick="goPage(${totalPages})" title="마지막">»</button>`;

        // 총 페이지 정보
        html += `<span class="page-info">${paging} / ${totalPage} 페이지 &nbsp;(총 ${dataCount}건)</span>`;

        container.innerHTML = html;
    }
	
    /** 목록  + 리스트 동시 갱신 */
    function render() {
		console.log("렌더해라");
        renderList();
		renderPaginationPaging();
    }
	
    //window.goPage = function(page) {
    function goPage(page) {
        const totalPages = totalPage ? totalPage : Math.ceil(filteredPosts.length / PAGE_SIZE) || 1;
        if (page < 1 || page > totalPages) return;
        currentPage = page;
			if (functions[funcName])
			  functions[funcName](page);
    };
	
	
	