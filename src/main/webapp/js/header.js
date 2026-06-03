document.addEventListener("DOMContentLoaded", function () {

    const header = document.getElementById("header");
    const name   = header.dataset.name    || "";
    const buseo  = header.dataset.buseo   || "";
    const jikgup = header.dataset.jikgup  || "";
    const ctx    = header.dataset.ctx     || "";

    header.innerHTML = `
        <div class="headerinfo">
            <span class="headerBuseo">${buseo}</span>
            <span class="headerRank">${jikgup}</span>
            <span class="headerName">${name}</span>
        </div>
        <div>
            <form action="${ctx}/logout.do" method="post" style="margin:0">
                <input type="button" class="btn btn-primary" onclick="this.form.submit()" value="로그아웃"></input>
            </form>
        </div>
    `;
});