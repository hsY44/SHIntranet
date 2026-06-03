(function() {
    const WRITERS = ["조세빈   ", "정세찬", "강명철", "윤주열", "유현선",
        "임유훤", "대대대", "이수빈", "엄준식", "양상국"];

    const TITLES = [
        "2분기 개발 일정 공유드립니다",
        "코드 리뷰 요청드립니다",
        "신규 API 명세서 배포",
        "팀 회의 일정 안내",
        "서버 점검 공지",
        "휴가 신청 관련 안내",
        "배포 완료 보고",
        "버그 리포트 공유",
        "인수인계 문서 등록",
        "업무 협조 요청",
        "프로젝트 진행 현황 공유",
        "신규 입사자 소개",
        "보안 패치 적용 안내",
        "데이터베이스 마이그레이션 계획",
        "UI 개선안 검토 요청"
    ];

    function padZero(n) { return n < 10 ? "0" + n : String(n); }

    function randomDate(startYear, endYear) {
        const y = startYear + Math.floor(Math.random() * (endYear - startYear + 1));
        const m = padZero(Math.floor(Math.random() * 12) + 1);
        const d = padZero(Math.floor(Math.random() * 28) + 1);
        return `${y}-${m}-${d}`;
    }

    const ALL_POSTS = [];
    for (let i = 200;i >= 1;i--) {
        ALL_POSTS.push({
            no: i,
            title: TITLES[(200 - i) % TITLES.length] + (i <= TITLES.length ? "" : ` (${i})`),
            writer: WRITERS[(200 - i) % WRITERS.length],
            date: randomDate(2024, 2025)
        });
    }

    const PAGE_SIZE = 20;   // 페이지당 게시글 수
    const PAGE_BLOCK = 3;   // 한 번에 표시할 페이지 번호 개수

    let currentPage = 1;
    let filteredPosts = [...ALL_POSTS];   // 검색 적용 후 대상


    /** 현재 페이지 게시글 목록 렌더링 */
    function renderList() {
        const container = document.getElementById("board-items");
        if (!container) return;

        const start = (currentPage - 1) * PAGE_SIZE;
        const end = start + PAGE_SIZE;
        const posts = filteredPosts.slice(start, end);

        if (posts.length === 0) {
            container.innerHTML = `
                <div class="board-row board-empty">
                    <span style="grid-column:1/-1; text-align:center; color:#94a3b8; font-size:16px; padding:40px 0;">
                        검색 결과가 없습니다.
                    </span>
                </div>`;
            return;
        }

        container.innerHTML = posts.map(p => `
            <div class="board-row" onclick="alert('${p.no}번 게시글: ${p.title}')">
                <span class="board-number">${p.no}</span>
                <span class="board-title">${p.title}</span>
                <span class="board-writer">${p.writer}</span>
                <span class="board-writeday">${p.date}</span>
            </div>
        `).join("");
    }

    /** 페이지네이션 버튼 렌더링 */
    function renderPagination() {
        const container = document.getElementById("pagination");
        if (!container) return;

        const totalPages = Math.ceil(filteredPosts.length / PAGE_SIZE) || 1;
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
        html += `<span class="page-info">${currentPage} / ${totalPages} 페이지 &nbsp;(총 ${filteredPosts.length}건)</span>`;

        container.innerHTML = html;
    }

    /** 목록  + 리스트 동시 갱신 */
    function render() {
        renderList();
        renderPagination();
    }
    window.goPage = function(page) {
        const totalPages = Math.ceil(filteredPosts.length / PAGE_SIZE) || 1;
        if (page < 1 || page > totalPages) return;
        currentPage = page;
        render();
        document.getElementById("board-list").scrollIntoView({ behavior: "smooth", block: "start" });
    };

    function doSearch() {
        const type = document.getElementById("search-type").value || "all";
        const keyword = (document.getElementById("search-input").value || "").trim().toLowerCase();

        if (!keyword) {
            filteredPosts = [...ALL_POSTS];
        } else {
            filteredPosts = ALL_POSTS.filter(p => {
                if (type === "title") return p.title.toLowerCase().includes(keyword);
                if (type === "writer") return p.writer.toLowerCase().includes(keyword);
                /* all */              return p.title.toLowerCase().includes(keyword)
                    || p.writer.toLowerCase().includes(keyword);
            });
        }

        currentPage = 1;
        render();
    }

    function doReset() {
        document.getElementById("search-input").value = "";
        document.getElementById("search-type").value = "title";
        filteredPosts = [...ALL_POSTS];
        currentPage = 1;
        render();
    }
    document.addEventListener("DOMContentLoaded", function() {
        document.getElementById("btn-search").addEventListener("click", doSearch);
        document.getElementById("btn-reset").addEventListener("click", doReset);

        // Enter 키 검색
        document.getElementById("search-input").addEventListener("keydown", function(e) {
            if (e.key === "Enter") doSearch();
        });

        render();
    });

})();
