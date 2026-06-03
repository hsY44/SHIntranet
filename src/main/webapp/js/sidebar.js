function toggleMenu(el) {
    const menu = el.nextElementSibling;
    menu.classList.toggle("open");
    el.classList.toggle("open");
}

document.addEventListener("DOMContentLoaded", function() {

    const sidebar = document.getElementById("sidebar");
    const isHr = sidebar.dataset.hr === "true";
    const ctx = sidebar.dataset.ctx || "";

    sidebar.innerHTML = `
        <div class="companyName">
            <h1>SH Company</h1>
        </div>
        <div class="myinfo">
            <a href="${ctx}/myinfo.do">내정보</a>
        </div>
        <div class="board">
            <span class="menu-toggle" onclick="toggleMenu(this)">게시판</span>
            <div class="submenu">
                <a href="${ctx}/board/list.do?type=001">ㄴ공지사항</a>
                <a href="${ctx}/board/list.do?type=002">ㄴ일반</a>
            </div>
        </div>
        <div class="emp_search">
            <a href="${ctx}/emp/search.do">사원조회</a>
        </div>
        <div class="document">
            <span class="menu-toggle" onclick="toggleMenu(this)">결재</span>
            <div class="submenu">
                <a href="${ctx}/page/aprvrequest.do">ㄴ결재 요청</a>
                <a href="${ctx}/aprv-submit/list">ㄴ상신내역</a>
                <a href="${ctx}/aprv-receive/list">ㄴ수신내역</a>
            </div>
        </div>
        <div class="work">
            <span class="menu-toggle" onclick="toggleMenu(this)">업무지시</span>
            <div class="submenu">
                <a href="${ctx}/page/workrequest.do">ㄴ지시 등록</a>
                <a href="${ctx}/work-submit/list">ㄴ업무 지시 내역</a>
                <a href="${ctx}/work-receive/list">ㄴ업무 수신 내역</a>
            </div>
        </div>
        <div class="work_target">
            <span class="menu-toggle" onclick="toggleMenu(this)">업무관리</span>
            <div class="submenu">
                <a href="${ctx}/page/worklist.do">ㄴ회사 업무</a>
                <a href="${ctx}/page/deptworklist.do">ㄴ부서 업무</a>
                <a href="${ctx}/manager-mgmt/list">ㄴ담당 업무</a>
            </div>
        </div>
        ${isHr ? `
        <div>
            <h2>인사전용</h2>
            <div class="emp_manage">
                <span class="menu-toggle" onclick="toggleMenu(this)">사원 관리</span>
                <div class="submenu">
                    <a href="${ctx}/emp/list.do">ㄴ사원 관리</a>
					<a href="${ctx}/attend/list.do">ㄴ출퇴근 기록</a>
                </div>
            </div>
            <div class="dept_manage">
                <span class="menu-toggle" onclick="toggleMenu(this)">부서 관리</span>
                <div class="submenu">
                    <a href="${ctx}/dept/list.do">ㄴ부서 관리</a>
                </div>
            </div>
            <div>
                <span class="menu-toggle" onclick="toggleMenu(this)">직급 관리</span>
                <div class="submenu">
                    <a href="${ctx}/rank/list.do">ㄴ직급 관리</a>
                </div>
            </div>
            <div class="board_manage">
                <span class="menu-toggle" onclick="toggleMenu(this)">게시판 관리</span>
                <div class="submenu">
                    <a href="${ctx}/admin/board/list.do">게시글 삭제</a>
                    <a href="${ctx}/admin/comment/list.do">댓글 삭제</a>
                </div>
            </div>
            <div class="document_manage">
                <span class="menu-toggle" onclick="toggleMenu(this)">문서 관리</span>
                <div class="submenu">
                    <a href="${ctx}/adminList">문서 조회</a>
                </div>
            </div>
        </div>
        ` : ''}
    `;

    // 현재 URL과 일치하는 사이드바 링크에 active 클래스 적용
    const currentPath = location.pathname + location.search;
    sidebar.querySelectorAll("a").forEach(function(link) {
        if (!link.getAttribute("href")) return;
        const linkPath = link.getAttribute("href");
        if (linkPath && currentPath.startsWith(linkPath) && linkPath !== ctx + "/") {
            link.classList.add("active");
            // 서브메뉴 안에 있는 경우 부모 메뉴-토글도 열어줌
            const submenu = link.closest(".submenu");
            if (submenu) {
                submenu.classList.add("open");
                const toggle = submenu.previousElementSibling;
                if (toggle) toggle.classList.add("open");
            }
        }
    });
});