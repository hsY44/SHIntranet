-- ============================================================
-- SHIntranet - 출퇴근 프로시저 수정 SQL
-- 대상 DB 계정 : C##SH
-- 작성일       : 2026-05-16
-- 수정 목적    : 출근 중복 방지 / 퇴근 유효성 검증 (순서·중복 모두 차단)
-- attend.jsp 분석으로 확인된 TYPE_CD 규칙:
--   '001' = 출근 (badge-in)
--   '002' = 퇴근 (badge-out)
-- ============================================================
-- [주의] ATTEND_LOG 테이블의 실제 컬럼명·시퀀스명을 확인 후 실행하세요.
--   LOG_NO    : 기본키 (시퀀스 ATTEND_LOG_SEQ 사용 가정)
--   TYPE_CD   : CHAR(3), '001' = 출근 / '002' = 퇴근
--   EMP_CD    : CHAR, 사원코드
--   REG_DT    : DATE, 기록 일시
-- ============================================================


-- ① 출근 프로시저 수정 (당일 중복 출근 차단)
CREATE OR REPLACE PROCEDURE ATTEND_IN(P_EMP_CD IN CHAR) AS
    V_COUNT NUMBER;
BEGIN
    -- 오늘 이미 출근 기록이 있으면 에러
    SELECT COUNT(*) INTO V_COUNT
    FROM ATTEND_LOG
    WHERE TRIM(EMP_CD) = TRIM(P_EMP_CD)
      AND TYPE_CD      = '001'
      AND TRUNC(REG_DT) = TRUNC(SYSDATE);

    IF V_COUNT > 0 THEN
        RAISE_APPLICATION_ERROR(-20001, '이미 오늘 출근 처리되었습니다.');
    END IF;

    INSERT INTO ATTEND_LOG (LOG_NO, TYPE_CD, EMP_CD, REG_DT)
    VALUES (ATTEND_LOG_SEQ.NEXTVAL, '001', P_EMP_CD, SYSDATE);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END ATTEND_IN;
/


-- ② 퇴근 프로시저 수정 (출근 없는 퇴근 차단 + 당일 중복 퇴근 차단)
CREATE OR REPLACE PROCEDURE ATTEND_OUT(P_EMP_CD IN CHAR) AS
    V_IN_COUNT  NUMBER;
    V_OUT_COUNT NUMBER;
BEGIN
    -- 오늘 출근 기록이 없으면 퇴근 불가
    SELECT COUNT(*) INTO V_IN_COUNT
    FROM ATTEND_LOG
    WHERE TRIM(EMP_CD) = TRIM(P_EMP_CD)
      AND TYPE_CD      = '001'
      AND TRUNC(REG_DT) = TRUNC(SYSDATE);

    IF V_IN_COUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, '오늘 출근 기록이 없어 퇴근 처리할 수 없습니다.');
    END IF;

    -- 오늘 이미 퇴근 기록이 있으면 에러
    SELECT COUNT(*) INTO V_OUT_COUNT
    FROM ATTEND_LOG
    WHERE TRIM(EMP_CD) = TRIM(P_EMP_CD)
      AND TYPE_CD      = '002'
      AND TRUNC(REG_DT) = TRUNC(SYSDATE);

    IF V_OUT_COUNT > 0 THEN
        RAISE_APPLICATION_ERROR(-20003, '이미 오늘 퇴근 처리되었습니다.');
    END IF;

    INSERT INTO ATTEND_LOG (LOG_NO, TYPE_CD, EMP_CD, REG_DT)
    VALUES (ATTEND_LOG_SEQ.NEXTVAL, '002', P_EMP_CD, SYSDATE);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END ATTEND_OUT;
/


-- ③ 적용 확인 쿼리 (실행 후 검증용)
-- 오늘 출퇴근 로그 조회
SELECT AL.LOG_NO, AL.TYPE_CD, AT.TYPE_NAME, AL.EMP_CD, AL.REG_DT
FROM ATTEND_LOG AL
JOIN ATTEND_TYPE AT ON AL.TYPE_CD = AT.TYPE_CD
WHERE TRUNC(AL.REG_DT) = TRUNC(SYSDATE)
ORDER BY AL.REG_DT;
