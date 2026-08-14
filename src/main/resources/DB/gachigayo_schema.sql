-- =====================================================
-- 같이가요 (Gachigayo) DDL — 최종 정리본
-- MariaDB 10.x / utf8mb4
-- 생성 순서: FK 의존성 순서대로 실행할 것
-- =====================================================

CREATE DATABASE IF NOT EXISTS gachigayo
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE gachigayo;

-- -----------------------------------------------------
-- 1. USERS : 이용자 / 보호자 공통 계정
-- -----------------------------------------------------
CREATE TABLE USERS (
    userId      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '사용자 고유 번호',
    userName    VARCHAR(50)  NOT NULL                COMMENT '사용자 이름',
    userRole    VARCHAR(20)  NOT NULL                COMMENT '사용자 역할 (USER, GUARDIAN)',
    loginId     VARCHAR(50)  NOT NULL                COMMENT '로그인 아이디',
    password    VARCHAR(255) NOT NULL                COMMENT 'BCrypt 암호화 비밀번호',
    phone       VARCHAR(20)  NULL                    COMMENT '보호자 연락처',
    email       VARCHAR(100) NOT NULL                COMMENT '이메일 주소 (인증/복구용)',
    linkCode    VARCHAR(9)   NULL                    COMMENT '보호자 연결코드',
    pushToken   VARCHAR(255) NULL                    COMMENT '푸시 알림 발송용 토큰',
    regDt       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입 일시',
    updDt       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    PRIMARY KEY (userId),
    UNIQUE KEY UK_USERS_loginId (loginId),
    UNIQUE KEY UK_USERS_email (email),
    UNIQUE KEY UK_USERS_linkCode (linkCode)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='사용자 계정';

-- -----------------------------------------------------
-- 2. GUARDIAN_LINKS : 이용자 - 보호자 1:1 연결
-- -----------------------------------------------------
CREATE TABLE GUARDIAN_LINKS (
    userId      BIGINT NOT NULL COMMENT '이용자 ID (USERS.userId)',
    guardianId  BIGINT NOT NULL COMMENT '보호자 ID (USERS.userId)',
    PRIMARY KEY (userId),
    UNIQUE KEY UK_GUARDIAN_LINKS_guardianId (guardianId),
    CONSTRAINT FK_GUARDIAN_LINKS_user
        FOREIGN KEY (userId) REFERENCES USERS (userId) ON DELETE CASCADE,
    CONSTRAINT FK_GUARDIAN_LINKS_guardian
        FOREIGN KEY (guardianId) REFERENCES USERS (userId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='보호자 연결';

-- -----------------------------------------------------
-- 3. USER_SETTINGS : 사용자 설정
-- -----------------------------------------------------
CREATE TABLE USER_SETTINGS (
    userId            BIGINT  NOT NULL COMMENT '설정 대상 사용자',
    shareLocationYn   CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '위치 공유 여부',
    deviationAlarmYn  CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '경로 이탈 알림 여부',
    arrivalAlarmYn    CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '도착 알림 여부',
    voiceGuideYn      CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '음성 안내 사용 여부',
    checkpointAlarmYn CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '체크포인트 알림 여부',
    PRIMARY KEY (userId),
    CONSTRAINT FK_USER_SETTINGS_user
        FOREIGN KEY (userId) REFERENCES USERS (userId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='사용자 설정';

-- -----------------------------------------------------
-- 4. FAVORITE_PLACES : 즐겨찾는 장소
-- -----------------------------------------------------
CREATE TABLE FAVORITE_PLACES (
    favoritePlaceId BIGINT        NOT NULL AUTO_INCREMENT COMMENT '즐겨찾는 장소 고유 번호',
    userId          BIGINT        NOT NULL COMMENT '등록한 이용자',
    category        VARCHAR(20)   NOT NULL COMMENT '장소 분류 (HOME, SCHOOL, WORK, WELFARE, OTHER)',
    placeName       VARCHAR(100)  NOT NULL COMMENT '장소 이름',
    address         VARCHAR(255)  NOT NULL COMMENT '장소 주소',
    lat             DECIMAL(10,7) NOT NULL COMMENT '장소 위도',
    lng             DECIMAL(10,7) NOT NULL COMMENT '장소 경도',
    regDt           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록 일시',
    PRIMARY KEY (favoritePlaceId),
    KEY IDX_FAVORITE_PLACES_userId (userId),
    CONSTRAINT FK_FAVORITE_PLACES_user
        FOREIGN KEY (userId) REFERENCES USERS (userId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='즐겨찾는 장소';

-- -----------------------------------------------------
-- 5. ROUTES : 저장된 경로
-- -----------------------------------------------------
CREATE TABLE ROUTES (
    routeId       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '경로 고유 번호',
    userId        BIGINT       NOT NULL COMMENT '경로를 사용하는 이용자',
    routeName     VARCHAR(100) NOT NULL COMMENT '경로 이름',
    routeType     VARCHAR(20)  NOT NULL COMMENT '경로 유형 (RECOMMENDED, LEARNING, GUARDIAN)',
    startName     VARCHAR(100) NOT NULL COMMENT '출발지 이름',
    endName       VARCHAR(100) NOT NULL COMMENT '목적지 이름',
    totalDistance INT          NOT NULL COMMENT '총 이동 거리(m)',
    totalDuration INT          NOT NULL COMMENT '총 예상 시간(초)',
    riskLevel     VARCHAR(20)  NOT NULL COMMENT '위험 등급 (LOW, MEDIUM, HIGH)',
    PRIMARY KEY (routeId),
    KEY IDX_ROUTES_userId (userId),
    CONSTRAINT FK_ROUTES_user
        FOREIGN KEY (userId) REFERENCES USERS (userId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='저장 경로';

-- -----------------------------------------------------
-- 6. ROUTE_STEPS : 경로의 단계별 안내
-- -----------------------------------------------------
CREATE TABLE ROUTE_STEPS (
    routeStepId    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '경로 단계 고유 번호',
    routeId        BIGINT        NOT NULL COMMENT '소속 경로',
    stepOrder      INT           NOT NULL COMMENT '단계 순서',
    stepType       VARCHAR(20)   NOT NULL COMMENT '단계 유형 (WALK, BUS, SUBWAY)',
    mainText       VARCHAR(160)  NOT NULL COMMENT '화면에 보여줄 핵심 안내 문구',
    landmark       VARCHAR(100)  NULL     COMMENT '길 안내 보조용 랜드마크 문구',
    lineName       VARCHAR(50)   NULL     COMMENT '버스 번호 또는 지하철 노선명',
    lat            DECIMAL(10,7) NOT NULL COMMENT '단계 기준 위치의 위도',
    lng            DECIMAL(10,7) NOT NULL COMMENT '단계 기준 위치의 경도',
    distance       INT           NOT NULL COMMENT '해당 구간 거리(m)',
    checkpointName VARCHAR(100)  NULL     COMMENT '확인해야 할 체크포인트 이름',
    PRIMARY KEY (routeStepId),
    KEY IDX_ROUTE_STEPS_routeId (routeId),
    CONSTRAINT FK_ROUTE_STEPS_route
        FOREIGN KEY (routeId) REFERENCES ROUTES (routeId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='경로 단계';

-- -----------------------------------------------------
-- 7. TRIPS : 실제 이동 기록
-- -----------------------------------------------------
CREATE TABLE TRIPS (
    tripId        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '이동 기록 번호',
    routeId       BIGINT      NOT NULL COMMENT '이동에 사용한 경로',
    startDt       DATETIME    NOT NULL COMMENT '이동 시작 시각',
    endDt         DATETIME    NULL     COMMENT '이동 종료 시각',
    currentStepId BIGINT      NULL     COMMENT '현재 진행 중 단계 (ROUTE_STEPS)',
    tripStatus    VARCHAR(20) NOT NULL COMMENT '이동 상태 (READY, MOVING, ARRIVED, CANCELLED)',
    PRIMARY KEY (tripId),
    KEY IDX_TRIPS_routeId (routeId),
    CONSTRAINT FK_TRIPS_route
        FOREIGN KEY (routeId) REFERENCES ROUTES (routeId) ON DELETE CASCADE,
    CONSTRAINT FK_TRIPS_currentStep
        FOREIGN KEY (currentStepId) REFERENCES ROUTE_STEPS (routeStepId) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='이동 기록';

-- -----------------------------------------------------
-- 8. TRIP_LOCATIONS : 이동 중 위치 로그
-- -----------------------------------------------------
CREATE TABLE TRIP_LOCATIONS (
    tripLocationId BIGINT        NOT NULL AUTO_INCREMENT COMMENT '위치 기록 번호',
    tripId         BIGINT        NOT NULL COMMENT '소속 이동',
    lat            DECIMAL(10,7) NOT NULL COMMENT '기록 시점 위도',
    lng            DECIMAL(10,7) NOT NULL COMMENT '기록 시점 경도',
    PRIMARY KEY (tripLocationId),
    KEY IDX_TRIP_LOCATIONS_tripId (tripId),
    CONSTRAINT FK_TRIP_LOCATIONS_trip
        FOREIGN KEY (tripId) REFERENCES TRIPS (tripId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='이동 위치 로그';

-- -----------------------------------------------------
-- 9. TRIP_EVENTS : 도움 요청 / 경로 이탈 등 이벤트
-- -----------------------------------------------------
CREATE TABLE TRIP_EVENTS (
    tripEventId  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '이벤트 번호',
    userId       BIGINT        NOT NULL COMMENT '이벤트를 발생시킨 이용자',
    tripId       BIGINT        NULL     COMMENT '관련 이동 (없을 수 있음)',
    routeStepId  BIGINT        NULL     COMMENT '관련 단계 (없을 수 있음)',
    eventType    VARCHAR(30)   NOT NULL COMMENT '이벤트 종류 (HELP_REQUEST, DEVIATION, SOS 등)',
    eventMessage VARCHAR(255)  NULL     COMMENT '이벤트 상세 내용',
    lat          DECIMAL(10,7) NOT NULL COMMENT '이벤트 발생 위치 위도',
    lng          DECIMAL(10,7) NOT NULL COMMENT '이벤트 발생 위치 경도',
    autoSentYn   CHAR(1)       NOT NULL DEFAULT 'N' COMMENT '자동 전송 여부',
    eventDt      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '이벤트 발생 시각',
    PRIMARY KEY (tripEventId),
    KEY IDX_TRIP_EVENTS_userId (userId),
    KEY IDX_TRIP_EVENTS_tripId (tripId),
    CONSTRAINT FK_TRIP_EVENTS_user
        FOREIGN KEY (userId) REFERENCES USERS (userId) ON DELETE CASCADE,
    CONSTRAINT FK_TRIP_EVENTS_trip
        FOREIGN KEY (tripId) REFERENCES TRIPS (tripId) ON DELETE SET NULL,
    CONSTRAINT FK_TRIP_EVENTS_routeStep
        FOREIGN KEY (routeStepId) REFERENCES ROUTE_STEPS (routeStepId) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='이동 이벤트';

-- -----------------------------------------------------
-- 10. NOTIFICATIONS : 보호자/이용자 알림
-- -----------------------------------------------------
CREATE TABLE NOTIFICATIONS (
    notificationId BIGINT       NOT NULL AUTO_INCREMENT COMMENT '알림 고유 번호',
    receiverId     BIGINT       NOT NULL COMMENT '알림을 받는 사용자',
    tripEventId    BIGINT       NULL     COMMENT '관련 이벤트 (없을 수 있음)',
    notifyType     VARCHAR(30)  NOT NULL COMMENT '알림 종류',
    content        VARCHAR(255) NOT NULL COMMENT '알림 본문',
    regDt          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '알림 생성 시각',
    PRIMARY KEY (notificationId),
    KEY IDX_NOTIFICATIONS_receiverId (receiverId),
    CONSTRAINT FK_NOTIFICATIONS_receiver
        FOREIGN KEY (receiverId) REFERENCES USERS (userId) ON DELETE CASCADE,
    CONSTRAINT FK_NOTIFICATIONS_tripEvent
        FOREIGN KEY (tripEventId) REFERENCES TRIP_EVENTS (tripEventId) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='알림';

-- =====================================================
-- 확인용 쿼리
-- =====================================================
-- SHOW TABLES;
-- SELECT TABLE_NAME, TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'gachigayo';
