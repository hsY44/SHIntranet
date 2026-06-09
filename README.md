# SHIntranet

> Session-based HR Intranet: Employee & Org Management with Stored Procedure DAO

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Jakarta Servlet](https://img.shields.io/badge/Jakarta%20Servlet-6.0-blue)
![Oracle](https://img.shields.io/badge/Oracle-XE%2021C-F80000?logo=oracle&logoColor=white)
![JDBC](https://img.shields.io/badge/DB%20연동-JDBC%20%2B%20Stored%20Procedure-lightgrey)
![Team](https://img.shields.io/badge/프로젝트-팀-blue)

> 4인 팀 프로젝트 | 기여도 약 40%

직원·부서·직급 관리와 담당 업무 등록·조회를 포함한 사내 인트라넷 웹 시스템입니다.  
수기·구두로 처리하던 인사 관리 업무를 웹 기반으로 전환하여 데이터 일관성과 업무 효율을 높이는 것을 목표로 하였습니다.

---

## 목차

1. [주요 기능](#주요-기능)
2. [담당 기능](#담당-기능)
3. [기술 스택](#기술-스택)
4. [아키텍처](#아키텍처)

---

## 주요 기능

- **로그인 / 로그아웃** — 사원코드 기반 세션 인증
- **마이페이지** — 개인 정보 조회 및 수정, 출퇴근 처리
- **출퇴근 관리** — 출근·퇴근 기록, 인사부 전용 전체 목록 조회
- **게시판** — 글 작성·수정·삭제, 파일 첨부, 댓글, 관리자 기능
- **업무 관리** — 업무 등록·목록·배정·수신·검색, 부서별 업무 관리
- **전자결재** — 결재 요청·제출·수신·상세 조회
- **조직 관리** — 사원 등록·수정·퇴직 처리, 부서 관리, 직급 관리, 권한 관리
- **인사 문서** — 인사 문서 목록 조회 및 파일 다운로드

---

## 담당 기능

### 로그인

- 사원코드 기반 세션 인증 처리 및 로그인 상태 유지
- 미로그인 상태에서 보호 URL 접근 시 로그인 페이지로 리다이렉트
- 역할 기반 접근 제어 — 인사부 과장급 이상만 사원 관리 기능 진입 가능

![로그인 화면](images/image.png)

### 직원 관리

- 직원 목록 조회 — Oracle `rownum` 기반 페이징 처리
- 신규 직원 등록 / 정보 수정 / 퇴직 처리 전체 구현
- `@WebServlet` 와일드카드(`/emp/*`) 와 특정 URL 충돌 해결 및 라우팅 설계

![사원 관리 화면](images/emp.png)

### 부서·직급 관리

- 부서 및 직급 생성·수정·삭제
- 직원과의 연관 관계(FK) 처리

| 부서 관리 | 직급 관리 |
|:---:|:---:|
| ![부서 관리 화면](images/dept.png) | ![직급 관리 화면](images/rank.png) |

### 담당 업무

- 직원별 업무 배정 및 조회 화면 구현

![담당 업무 화면](images/work.png)

### UI

- 공통 헤더·사이드바 레이아웃 설계 및 구성
- 전체 페이지 CSS 스타일링

---

## 기술 스택

| 분류 | 내용 |
|---|---|
| Language | Java 21 |
| Web | Jakarta Servlet 6.0, JSP, JSTL 3.0 |
| WAS | Apache Tomcat 10.1+ |
| DB | Oracle XE 21C |
| DB 연동 | JDBC (Oracle Stored Procedure + CallableStatement) |
| 인증 | 세션 기반 인증 (HttpSession) |
| 아키텍처 | MVC — Controller / DAO / DTO 계층 분리 |
| Frontend | HTML5, CSS3, JavaScript, jQuery |
| IDE | Spring Tool Suite (Eclipse 기반) |
| 형상 관리 | GitHub |

---

## 아키텍처

### MVC 계층 구조

요청이 들어오면 Controller → DAO → Oracle DB 순으로 흐르고,  
결과를 DTO에 담아 JSP 뷰로 전달합니다.

```
Client (Browser)
    ↕ HTTP
  Controller (Servlet)
    ↕ DTO
  DAO (CallableStatement)
    ↕ Stored Procedure
  Oracle DB
    ↕
  JSP (View)
```

### Stored Procedure 기반 DAO

DB 로직을 Oracle Stored Procedure에 위임하고, DAO는 `CallableStatement`로 호출합니다.

```java
// EmpDAO 예시
CallableStatement cs = conn.prepareCall("{call EMP_SELECT(?,?,?,?)}");
cs.setInt(1, page);
cs.registerOutParameter(2, OracleTypes.CURSOR);
ResultSet rs = (ResultSet) cs.getObject(2);
```

이 구조로 DB 로직이 Procedure에 집중되고, Java 코드는 호출 및 결과 매핑만 담당합니다.

### 세션 기반 접근 제어

`LoginController`에서 로그인 성공 시 세션에 사원 정보를 저장하고,  
각 Controller에서 세션 존재 여부를 체크하여 미인증 접근을 차단합니다.

```
요청 도착
  → 세션에 empDTO 존재 여부 확인
  → 없으면 → login.do 리다이렉트
  → 있으면 → 권한(직급/부서) 확인 → 기능 진입
```

### VIEW_EMPLOYEE 뷰 기반 재직자 분리

재직 중인 사원만 포함하는 DB 뷰(`VIEW_EMPLOYEE`)를 통해 모든 사원 조회를 처리합니다.  
퇴직 처리(`EMP_EXIT` 프로시저) 후 해당 사원은 뷰에서 자동으로 제외되어,  
Java 코드에서 별도의 재직 상태 필터 없이 항상 재직자 데이터만 다룹니다.

```
일반 조회:  모든 DAO  →  VIEW_EMPLOYEE (재직자만 포함)
퇴직 여부:  existsInEmpTable()  →  EMP 원본 테이블 직접 조회
```

### 기술 선택 근거

**Stored Procedure + CallableStatement (JPA 대신):** 팀원 각자가 담당 모듈의 DB 로직을 Oracle Procedure로 독립적으로 작성하고, Java는 호출·결과 매핑만 담당하도록 역할을 명확히 분리했습니다. 퇴직 처리처럼 EMP + EMP_INFO 두 테이블을 연속으로 조작하는 절차형 로직에도 Procedure가 자연스럽게 맞았습니다.

**HttpSession (JWT 대신):** SPA가 아닌 JSP 서버 렌더링 환경에서 Tomcat이 세션 관리를 직접 담당하므로 별도 토큰 발급·검증 인프라 없이 적용 가능합니다. 로그인 상태를 서버가 보유하는 구조가 이 프로젝트 아키텍처에 더 단순하고 적합합니다.

