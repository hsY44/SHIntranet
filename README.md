# SHIntranet

> 4인 팀 프로젝트 | 기여도 약 40%

직원·부서·직급 관리와 담당 업무 등록·조회를 포함한 사내 인트라넷 웹 시스템입니다.  
수기·구두로 처리하던 인사 관리 업무를 웹 기반으로 전환하여 데이터 일관성과 업무 효율을 높이는 것을 목표로 하였습니다.

---

## 목차

1. [스크린샷](#스크린샷)
2. [주요 기능](#주요-기능)
3. [담당 기능](#담당-기능)
4. [기술 스택](#기술-스택)
5. [아키텍처](#아키텍처)
6. [프로젝트 구조](#프로젝트-구조)
7. [실행 환경 설정](#실행-환경-설정)

---

## 스크린샷

| 로그인 | 사원 관리 |
|:---:|:---:|
| ![로그인](images/image.png) | ![사원관리](images/emp.png) |

| 부서 관리 | 담당 업무 | 직급 관리 |
|:---:|:---:|:---:|
| ![부서관리](images/dept.png) | ![담당업무](images/work.png) | ![직급관리](images/rank.png) |

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

### 직원 관리

- 직원 목록 조회 — Oracle `rownum` 기반 페이징 처리
- 신규 직원 등록 / 정보 수정 / 퇴직 처리 전체 구현

### 부서·직급 관리

- 부서 및 직급 생성·수정·삭제
- 직원과의 연관 관계(FK) 처리

### 담당 업무

- 직원별 업무 배정 및 조회 화면 구현

### UI

- 공통 헤더·사이드바 레이아웃 설계 및 구성
- 전체 페이지 CSS 스타일링

---

## 기술 스택

| 분류 | 내용 |
|---|---|
| Language | Java 21 |
| Web | Jakarta Servlet 6.0, JSP, JSTL 3.0 |
| WAS | Apache Tomcat 9.x |
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

---

## 프로젝트 구조

```
SHIntranet/
├── src/main/
│   ├── java/com/sh/
│   │   │
│   │   ├── [Controller]
│   │   ├── LoginController.java          # 로그인 / 로그아웃 ★
│   │   ├── EmpController.java            # 직원 목록·등록·수정·삭제 ★
│   │   ├── DeptController.java           # 부서 관리 ★
│   │   ├── RankController.java           # 직급 관리 ★
│   │   ├── WorkAddController.java        # 담당 업무 등록 ★
│   │   ├── WorkListController.java       # 담당 업무 목록 ★
│   │   ├── AttendController.java         # 출퇴근 관리
│   │   ├── BoardController.java          # 게시판
│   │   ├── BoardAdminController.java     # 게시판 관리자
│   │   ├── CommentController.java        # 댓글
│   │   ├── AprvRequestController.java    # 전자결재 요청
│   │   ├── AprvSubmitController.java     # 전자결재 제출
│   │   ├── AprvReceiveController.java    # 전자결재 수신
│   │   ├── MyInfoController.java         # 마이페이지
│   │   ├── ManagerController.java        # 권한 관리
│   │   └── ...
│   │   │
│   │   ├── [DAO]
│   │   ├── EmpDAO.java                   # 직원 DB — Stored Procedure ★
│   │   ├── DeptDAO.java                  # 부서 DB — Stored Procedure ★
│   │   ├── RankDAO.java                  # 직급 DB — Stored Procedure ★
│   │   ├── WorkDAO.java                  # 업무 DB ★
│   │   ├── AttendDAO.java                # 출퇴근 DB
│   │   ├── BoardDAO.java                 # 게시판 DB
│   │   ├── AprvDAO.java                  # 전자결재 DB
│   │   └── ...
│   │   │
│   │   ├── [DTO]
│   │   ├── EmpDTO.java                   # 직원 데이터 전송 객체 ★
│   │   ├── DeptDTO.java                  # 부서 DTO ★
│   │   ├── RankDTO.java                  # 직급 DTO ★
│   │   ├── WorkDTO.java                  # 업무 DTO ★
│   │   ├── AttendDTO.java                # 출퇴근 DTO
│   │   ├── BoardDTO.java                 # 게시판 DTO
│   │   └── ...
│   │   │
│   │   ├── [Filter]
│   │   └── ChareterEncodingFilter.java   # 문자 인코딩 필터
│   │   │
│   │   └── [Util]
│   │       ├── DBConn.java               # Oracle DB 연결 (Singleton)
│   │       ├── DBUtil.java               # 공통 DB 유틸
│   │       ├── JFunction.java            # 공통 함수
│   │       └── MyUtil.java               # 기타 유틸
│   │
│   └── webapp/
│       ├── css/                          # 전체 페이지 스타일시트 ★
│       ├── js/                           # 공통 스크립트
│       ├── sidebar.html                  # 공통 사이드바 ★
│       └── WEB-INF/
│           ├── views/                    # JSP 뷰
│           ├── lib/                      # JAR 라이브러리
│           └── web.xml
│
├── sql/
│   ├── SHIntranet_full.sql               # 전체 테이블 DDL + 초기 데이터
│   └── SHIntranet_attend_fix.sql         # 출퇴근 테이블 수정 스크립트
│
└── images/                               # README 스크린샷
```

> `★` 표시는 직접 담당한 파일입니다.

---

## 실행 환경 설정

### 1. DB 연결 설정

`src/main/java/com/sh/util/DBConn.java`의 아래 항목을 본인 환경에 맞게 수정합니다.

```java
private static final String URL = "jdbc:oracle:thin:@//YOUR_DB_HOST:1521/xe";
private static String USER = "YOUR_DB_USER";
private static String PASSWORD = "YOUR_DB_PASSWORD";
```

### 2. DB 초기화

Oracle SQL*Plus 또는 SQL Developer에서 아래 파일을 순서대로 실행합니다.

```sql
@sql/SHIntranet_full.sql
@sql/SHIntranet_attend_fix.sql
```

### 3. 서버 실행

STS(Eclipse)에서 프로젝트를 Tomcat에 배포 후 실행합니다.

접속 URL: `http://localhost:8080/SHIntranet/login.do`
