# ODA (행동 관리형 커리어 운영체계) - 상세 구현 계획서

## Context

**문제**: 25~35세 청년층이 취업, 정책, 자격증, 주거 등 파편화된 정보 속에서 "지금 당장 무엇을 해야 하는지" 모르는 **실행 격차(Action Gap)** 문제
**솔루션**: 개인 프로필 기반으로 정책/채용을 자동 매칭하고, 자체 캘린더와 할 일 관리로 실행을 관리하는 **커리어 OS**
**현재 상태**: Spring Boot 4.0.3 + Java 21 스켈레톤 프로젝트 (코드 미구현)

### 기술 결정 사항

| 항목 | 결정 |
|------|------|
| DB | **PostgreSQL** (MySQL에서 전환) |
| 프론트엔드 | 백엔드 REST API 우선 개발 (Swagger 문서화) |
| 아키텍처 | **헥사고날 아키텍처 (Ports & Adapters)** + **DDD** |
| 개발 방법론 | **TDD (Test-Driven Development)** - 테스트 먼저 작성 |
| AI 기능 | 제외 (LLM 대화, 스트리밍, RAG 없음) |

### 설계 원칙

- **TDD**: Red → Green → Refactor 사이클. 모든 비즈니스 로직은 테스트가 먼저
- **DDD**: Bounded Context별 모듈 분리, Aggregate Root, Value Object, Domain Event 활용
- **헥사고날 아키텍처**: Domain이 중심, 외부 의존성(DB, API, 알림)은 Port/Adapter로 분리
- **의존성 방향**: Adapter → Application → Domain (Domain은 외부를 모름)

---

## 아키텍처 개요: 헥사고날 (Ports & Adapters)

```
                    [REST API]        [Scheduler]
                        |                 |
                   ┌────▼─────────────────▼────┐
                   │    Adapter (Inbound)       │  ← Controller, SchedulerAdapter
                   └────────────┬───────────────┘
                                │
                   ┌────────────▼───────────────┐
                   │    Application (Use Case)   │  ← Service, Command/Query
                   └────────────┬───────────────┘
                                │
                   ┌────────────▼───────────────┐
                   │    Domain (Core)            │  ← Entity, VO, Domain Event, Port(Interface)
                   └────────────┬───────────────┘
                                │
                   ┌────────────▼───────────────┐
                   │    Adapter (Outbound)       │  ← JPA Repository, External API Client, EmailSender
                   └────────────────────────────┘
```

### 각 계층의 역할

| 계층 | 역할 | 예시 |
|------|------|------|
| **Domain** | 비즈니스 핵심 로직, 엔티티, VO, Port 인터페이스 정의 | `User`, `Policy`, `PolicyMatchingSpec`, `PolicyRepository`(interface) |
| **Application** | 유스케이스 오케스트레이션, 트랜잭션 경계 | `RegisterUserUseCase`, `MatchPoliciesUseCase` |
| **Adapter (In)** | 외부 요청 수신 (HTTP, 스케줄러) | `UserController`, `PolicySyncScheduler` |
| **Adapter (Out)** | 외부 시스템 연동 (DB, API, 알림) | `JpaUserRepository`, `PublicDataApiAdapter`, `EmailAdapter` |

---

## 패키지 구조 (헥사고날 + DDD)

```
com.oda/
├── OdaApplication.java
│
├── common/                                    # 공유 커널
│   ├── domain/
│   │   ├── BaseEntity.java                    # createdAt, updatedAt
│   │   └── DomainEvent.java                   # 도메인 이벤트 마커 인터페이스
│   ├── application/
│   │   └── EventPublisher.java                # 이벤트 발행 Port (interface)
│   ├── adapter/
│   │   ├── in/web/
│   │   │   ├── ApiResponse.java               # 통합 응답 래퍼
│   │   │   └── GlobalExceptionHandler.java    # 전역 예외 처리
│   │   └── out/
│   │       └── SpringEventPublisher.java      # EventPublisher 구현체
│   └── config/
│       ├── SecurityConfig.java
│       ├── AsyncConfig.java
│       ├── CacheConfig.java
│       └── WebMvcConfig.java
│
├── user/                                      # Bounded Context: 사용자
│   ├── domain/
│   │   ├── model/
│   │   │   ├── User.java                      # Aggregate Root
│   │   │   ├── UserProfile.java               # Entity
│   │   │   ├── Education.java                 # Value Object
│   │   │   ├── WorkExperience.java            # Value Object
│   │   │   ├── Certification.java             # Value Object
│   │   │   ├── IncomeInfo.java                # Value Object (암호화 대상)
│   │   │   ├── EmploymentStatus.java          # Enum
│   │   │   └── CareerGoalType.java            # Enum
│   │   ├── port/
│   │   │   ├── in/
│   │   │   │   ├── RegisterUserUseCase.java          # Input Port
│   │   │   │   ├── LoginUseCase.java                 # Input Port
│   │   │   │   ├── UpdateProfileUseCase.java         # Input Port
│   │   │   │   └── GetProfileUseCase.java            # Input Port
│   │   │   └── out/
│   │   │       ├── UserRepository.java               # Output Port (interface)
│   │   │       ├── UserProfileRepository.java        # Output Port (interface)
│   │   │       └── PasswordEncoder.java              # Output Port (interface)
│   │   └── exception/
│   │       ├── UserNotFoundException.java
│   │       └── DuplicateEmailException.java
│   ├── application/
│   │   ├── service/
│   │   │   ├── RegisterUserService.java       # UseCase 구현
│   │   │   ├── LoginService.java
│   │   │   ├── UpdateProfileService.java
│   │   │   └── GetProfileService.java
│   │   └── dto/
│   │       ├── RegisterCommand.java           # Command 객체
│   │       ├── LoginCommand.java
│   │       ├── UpdateProfileCommand.java
│   │       └── ProfileResult.java             # 결과 DTO
│   └── adapter/
│       ├── in/web/
│       │   ├── AuthController.java
│       │   ├── UserController.java
│       │   └── dto/                           # Request/Response DTO
│       │       ├── RegisterRequest.java
│       │       ├── LoginRequest.java
│       │       ├── LoginResponse.java
│       │       ├── ProfileRequest.java
│       │       └── ProfileResponse.java
│       └── out/persistence/
│           ├── JpaUserRepository.java         # Output Adapter
│           ├── JpaUserProfileRepository.java
│           ├── UserJpaEntity.java             # JPA 전용 엔티티 (도메인과 분리)
│           ├── UserProfileJpaEntity.java
│           ├── UserMapper.java                # Domain ↔ JPA 변환
│           └── SpringPasswordEncoder.java     # PasswordEncoder Adapter
│
├── policy/                                    # Bounded Context: 정책
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Policy.java                    # Aggregate Root
│   │   │   ├── EligibilityCriteria.java       # Value Object (구조화된 자격요건)
│   │   │   ├── PolicyCategory.java            # Enum
│   │   │   ├── MatchResult.java               # Value Object (해당/비해당 + 사유)
│   │   │   └── PolicyMatchingSpec.java        # 도메인 서비스: 매칭 규칙 집합
│   │   ├── port/
│   │   │   ├── in/
│   │   │   │   ├── GetPoliciesUseCase.java
│   │   │   │   ├── MatchPoliciesUseCase.java
│   │   │   │   └── CheckEligibilityUseCase.java
│   │   │   └── out/
│   │   │       ├── PolicyRepository.java      # Output Port
│   │   │       └── PolicyDataSource.java      # Output Port (외부 API 추상화)
│   │   └── exception/
│   │       └── PolicyNotFoundException.java
│   ├── application/
│   │   ├── service/
│   │   │   ├── GetPoliciesService.java
│   │   │   ├── MatchPoliciesService.java
│   │   │   └── PolicySyncService.java         # 데이터 동기화 유스케이스
│   │   └── dto/
│   │       ├── PolicySearchQuery.java
│   │       ├── MatchPoliciesCommand.java
│   │       └── PolicyMatchResult.java
│   └── adapter/
│       ├── in/
│       │   ├── web/PolicyController.java
│       │   └── scheduler/PolicySyncScheduler.java    # @Scheduled 어댑터
│       └── out/
│           ├── persistence/
│           │   ├── JpaPolicyRepository.java
│           │   ├── PolicyJpaEntity.java
│           │   └── PolicyMapper.java
│           └── external/
│               ├── PublicDataApiAdapter.java          # data.go.kr 연동
│               ├── OntongYouthApiAdapter.java         # 온통청년 연동
│               └── KoreanEligibilityParser.java       # 비정형 텍스트 파서
│
├── job/                                       # Bounded Context: 채용
│   ├── domain/
│   │   ├── model/
│   │   │   ├── JobPosting.java                # Aggregate Root
│   │   │   ├── Company.java                   # Entity
│   │   │   ├── SkillRequirement.java          # Value Object
│   │   │   ├── JobSource.java                 # Enum
│   │   │   ├── GapAnalysis.java               # Value Object (부족 요소 분석)
│   │   │   └── JobMatchingSpec.java           # 도메인 서비스: 매칭 규칙
│   │   ├── port/
│   │   │   ├── in/
│   │   │   │   ├── GetJobsUseCase.java
│   │   │   │   ├── MatchJobsUseCase.java
│   │   │   │   └── AnalyzeGapUseCase.java
│   │   │   └── out/
│   │   │       ├── JobPostingRepository.java
│   │   │       └── JobDataSource.java         # 외부 채용 API 추상화
│   │   └── exception/
│   │       └── JobNotFoundException.java
│   ├── application/
│   │   ├── service/
│   │   │   ├── GetJobsService.java
│   │   │   ├── MatchJobsService.java
│   │   │   ├── AnalyzeGapService.java
│   │   │   └── JobSyncService.java
│   │   └── dto/
│   │       ├── JobSearchQuery.java
│   │       ├── MatchJobsCommand.java
│   │       └── JobMatchResult.java
│   └── adapter/
│       ├── in/
│       │   ├── web/JobController.java
│       │   └── scheduler/JobSyncScheduler.java
│       └── out/
│           ├── persistence/
│           │   ├── JpaJobPostingRepository.java
│           │   ├── JobPostingJpaEntity.java
│           │   └── JobPostingMapper.java
│           └── external/
│               ├── SaraminApiAdapter.java
│               ├── WantedApiAdapter.java
│               └── JobKoreaApiAdapter.java
│
├── calendar/                                  # Bounded Context: 캘린더 + 할 일
│   ├── domain/
│   │   ├── model/
│   │   │   ├── CalendarEvent.java             # Aggregate Root (일정)
│   │   │   ├── Todo.java                      # Aggregate Root (할 일)
│   │   │   ├── EventType.java                 # Enum (POLICY, JOB, EXAM, CUSTOM)
│   │   │   ├── EventSource.java               # Value Object (원본 참조: policyId, jobId 등)
│   │   │   ├── RecurrenceRule.java            # Value Object (반복 규칙)
│   │   │   ├── TodoStatus.java                # Enum (PENDING, IN_PROGRESS, COMPLETED)
│   │   │   ├── TodoPriority.java              # Enum (HIGH, MEDIUM, LOW)
│   │   │   └── CalendarEventAddedEvent.java   # Domain Event
│   │   ├── port/
│   │   │   ├── in/
│   │   │   │   ├── GetCalendarEventsUseCase.java
│   │   │   │   ├── AddCalendarEventUseCase.java       # 사용자가 정책/채용 선택 시
│   │   │   │   ├── UpdateCalendarEventUseCase.java
│   │   │   │   ├── DeleteCalendarEventUseCase.java
│   │   │   │   ├── CreateTodoUseCase.java
│   │   │   │   ├── UpdateTodoUseCase.java
│   │   │   │   ├── CompleteTodoUseCase.java
│   │   │   │   └── GetTodosUseCase.java
│   │   │   └── out/
│   │   │       ├── CalendarEventRepository.java
│   │   │       └── TodoRepository.java
│   │   └── exception/
│   │       ├── CalendarEventNotFoundException.java
│   │       └── TodoNotFoundException.java
│   ├── application/
│   │   ├── service/
│   │   │   ├── CalendarEventService.java      # 캘린더 이벤트 CRUD
│   │   │   ├── TodoService.java               # 할 일 CRUD
│   │   │   └── AddFromPolicyService.java      # 정책/채용 → 캘린더 이벤트 변환
│   │   └── dto/
│   │       ├── AddEventCommand.java
│   │       ├── AddEventFromPolicyCommand.java # 정책 ID로 일정 추가
│   │       ├── AddEventFromJobCommand.java    # 채용 ID로 일정 추가
│   │       ├── UpdateEventCommand.java
│   │       ├── CreateTodoCommand.java
│   │       ├── UpdateTodoCommand.java
│   │       ├── CalendarEventResult.java
│   │       └── TodoResult.java
│   └── adapter/
│       ├── in/web/
│       │   ├── CalendarController.java
│       │   ├── TodoController.java
│       │   └── dto/
│       │       ├── AddEventRequest.java
│       │       ├── AddFromPolicyRequest.java  # { policyId: 123 }
│       │       ├── AddFromJobRequest.java     # { jobPostingId: 456 }
│       │       ├── EventResponse.java
│       │       ├── CreateTodoRequest.java
│       │       └── TodoResponse.java
│       └── out/persistence/
│           ├── JpaCalendarEventRepository.java
│           ├── JpaTodoRepository.java
│           ├── CalendarEventJpaEntity.java
│           ├── TodoJpaEntity.java
│           ├── CalendarEventMapper.java
│           └── TodoMapper.java
│
└── notification/                              # Bounded Context: 알림
    ├── domain/
    │   ├── model/
    │   │   ├── Notification.java              # Aggregate Root
    │   │   ├── NotificationType.java          # Enum (DEADLINE_REMINDER, TODO_REMINDER, POLICY_NEW, JOB_NEW)
    │   │   ├── NotificationChannel.java       # Enum (IN_APP, EMAIL, PUSH)
    │   │   ├── NotificationPreference.java    # Entity (사용자 알림 설정)
    │   │   └── NotificationTemplate.java      # Value Object (넛지 메시지 템플릿)
    │   ├── port/
    │   │   ├── in/
    │   │   │   ├── GetNotificationsUseCase.java
    │   │   │   ├── MarkAsReadUseCase.java
    │   │   │   ├── UpdatePreferencesUseCase.java
    │   │   │   └── SendNotificationUseCase.java
    │   │   └── out/
    │   │       ├── NotificationRepository.java
    │   │       ├── NotificationPreferenceRepository.java
    │   │       ├── EmailSender.java           # Output Port (이메일 발송 추상화)
    │   │       └── PushSender.java            # Output Port (푸시 발송 추상화)
    │   └── exception/
    │       └── NotificationNotFoundException.java
    ├── application/
    │   ├── service/
    │   │   ├── NotificationService.java
    │   │   ├── NotificationPreferenceService.java
    │   │   ├── DeadlineReminderService.java   # D-3, D-1, D-day 리마인더 생성
    │   │   └── NudgeMessageGenerator.java     # 행동경제학 기반 넛지 메시지
    │   ├── dto/
    │   │   ├── SendNotificationCommand.java
    │   │   ├── UpdatePreferencesCommand.java
    │   │   └── NotificationResult.java
    │   └── eventhandler/
    │       ├── CalendarEventHandler.java      # CalendarEventAddedEvent 수신 → 알림 예약
    │       └── DeadlineApproachingHandler.java # 마감 임박 이벤트 처리
    └── adapter/
        ├── in/
        │   ├── web/NotificationController.java
        │   └── scheduler/DeadlineCheckScheduler.java  # 매일 마감 임박 체크
        └── out/
            ├── persistence/
            │   ├── JpaNotificationRepository.java
            │   ├── JpaNotificationPreferenceRepository.java
            │   ├── NotificationJpaEntity.java
            │   └── NotificationMapper.java
            └── external/
                ├── SmtpEmailAdapter.java      # EmailSender 구현
                └── FcmPushAdapter.java        # PushSender 구현 (추후)
```

---

## TDD 개발 프로세스

### Red → Green → Refactor 사이클

모든 기능 구현 시 아래 순서를 따름:

```
1. [RED]     실패하는 테스트 작성 (기대 동작 정의)
2. [GREEN]   테스트를 통과하는 최소한의 코드 작성
3. [REFACTOR] 코드 정리 (중복 제거, 네이밍 개선)
4. 반복
```

### 테스트 계층별 전략

| 계층 | 테스트 종류 | 도구 | 외부 의존성 |
|------|-----------|------|-----------|
| **Domain** | 단위 테스트 | JUnit 5 + AssertJ | **없음** (순수 자바) |
| **Application** | 단위 테스트 | JUnit 5 + Mockito | Port를 Mock으로 주입 |
| **Adapter (In)** | 슬라이스 테스트 | `@WebMvcTest` | MockMvc |
| **Adapter (Out/DB)** | 슬라이스 테스트 | `@DataJpaTest` + Testcontainers | PostgreSQL 컨테이너 |
| **Adapter (Out/API)** | 통합 테스트 | WireMock | Mock HTTP 서버 |
| **E2E** | 통합 테스트 | `@SpringBootTest` + Testcontainers | 전체 컨텍스트 |

### TDD 예시: 정책 매칭 도메인

```java
// 1. [RED] 도메인 테스트 먼저 작성
class PolicyMatchingSpecTest {

    @Test
    void 나이_범위_내_사용자는_매칭된다() {
        // given
        var criteria = EligibilityCriteria.builder()
            .minAge(19).maxAge(34).build();
        var policy = Policy.create("청년지원금", criteria);
        var userAge = 28;

        // when
        MatchResult result = PolicyMatchingSpec.evaluate(policy, userAge, ...);

        // then
        assertThat(result.isEligible()).isTrue();
    }

    @Test
    void 소득_초과_사용자는_비해당_사유와_함께_반환된다() {
        // given
        var criteria = EligibilityCriteria.builder()
            .maxPersonalIncome(30_000_000L).build();
        var policy = Policy.create("월세지원", criteria);
        var userIncome = 35_000_000L;

        // when
        MatchResult result = PolicyMatchingSpec.evaluate(policy, ..., userIncome, ...);

        // then
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("소득 기준 초과");
    }
}

// 2. [GREEN] 도메인 로직 구현
public class PolicyMatchingSpec {
    public static MatchResult evaluate(Policy policy, int age, long income, ...) {
        // 최소한의 구현
    }
}

// 3. [REFACTOR] 정리 후 다음 테스트로 이동
```

### 테스트 의존성

```groovy
// build.gradle에 추가
dependencies {
    testImplementation 'org.springframework.boot:spring-boot-starter-test'  // JUnit5, Mockito, AssertJ
    testImplementation 'org.springframework.security:spring-security-test'
    testImplementation 'org.testcontainers:postgresql'
    testImplementation 'org.testcontainers:junit-jupiter'
    testImplementation 'org.wiremock:wiremock-standalone:3.10.0'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
```

---

## Phase 1: 프로젝트 기반 구축 (1~2주차)

### 1-1. build.gradle 의존성 수정

**파일**: `build.gradle`

변경 사항:
- `mysql-connector-j` → `postgresql` 교체
- Validation, Cache, Caffeine, Flyway, JWT, SpringDoc 추가
- Testcontainers, WireMock 테스트 의존성 추가

### 1-2. application.yml 설정

**파일**: `src/main/resources/application.yml` (application.properties → yml 전환)

주요 설정:
- PostgreSQL 데이터소스 (환경변수로 외부화)
- JWT 시크릿 및 토큰 만료 시간
- 외부 API 키 (사람인, 공공데이터포털, 원티드)
- 스케줄링 cron 표현식
- 캐시 설정

### 1-3. 공통 인프라 (TDD로 구현)

| 구현 항목 | 테스트 | 비고 |
|----------|--------|------|
| `JwtTokenProvider` | 토큰 생성/검증/만료 단위 테스트 | Domain에 가까운 유틸 |
| `SecurityConfig` + Filter | `@WebMvcTest`로 인증/인가 검증 | Adapter(In) |
| `GlobalExceptionHandler` | 예외 → HTTP 응답 매핑 테스트 | Adapter(In) |
| `ApiResponse` | 직렬화 형태 테스트 | DTO |
| Flyway 마이그레이션 | `@DataJpaTest` + Testcontainers | Adapter(Out) |

---

## Phase 2: 사용자 도메인 (2~3주차)

### TDD 구현 순서

```
1. Domain 단위 테스트 → User, UserProfile 도메인 모델 구현
2. Application 단위 테스트 → RegisterUserService (Port mock) 구현
3. Adapter(Out) 테스트 → JpaUserRepository + Testcontainers 구현
4. Adapter(In) 테스트 → AuthController @WebMvcTest 구현
5. E2E 테스트 → 회원가입 → 로그인 → 프로필 CRUD 전체 플로우
```

### 인증 방식: OAuth 2.0 소셜 로그인 (Kakao + Google)

자체 회원가입/로그인 대신 **카카오, 구글 소셜 로그인**을 기본 인증 수단으로 사용한다.

#### 인증 플로우 (Authorization Code Grant)

```
1. 프론트엔드 → 카카오/구글 OAuth 인증 페이지로 리다이렉트
2. 사용자가 소셜 로그인 승인
3. 소셜 서버 → 프론트엔드에 Authorization Code 전달
4. 프론트엔드 → 백엔드로 Authorization Code 전송
   POST /api/v1/auth/oauth/{provider} { code: "...", redirectUri: "..." }
5. 백엔드 → 소셜 서버에 Access Token 요청 (code 교환)
6. 백엔드 → 소셜 서버에서 사용자 정보 조회 (이메일, 이름, 프로필)
7. 백엔드 → User 생성 또는 기존 User 조회 (이메일 기준)
8. 백엔드 → 자체 JWT 발급하여 프론트엔드에 응답
```

#### 헥사고날 구조에서의 소셜 로그인

**Domain 계층**:
- `User` 도메인에 `OAuthProvider` enum 추가 (KAKAO, GOOGLE)
- `User`에 `oauthProvider`, `oauthId` 필드 추가 (소셜 계정 식별)
- `passwordHash` 필드는 nullable (소셜 로그인 시 비밀번호 불필요)

**Port (interface)**:
- `OAuthPort` (Output Port): 소셜 서버와의 통신 추상화
  - `exchangeCodeForToken(provider, code, redirectUri)` → OAuthToken
  - `getUserInfo(provider, accessToken)` → OAuthUserInfo

**Adapter (Out)**:
- `KakaoOAuthAdapter`: Kakao OAuth API 구현
  - Token URL: `https://kauth.kakao.com/oauth/token`
  - UserInfo URL: `https://kapi.kakao.com/v2/user/me`
- `GoogleOAuthAdapter`: Google OAuth API 구현
  - Token URL: `https://oauth2.googleapis.com/token`
  - UserInfo URL: `https://www.googleapis.com/oauth2/v2/userinfo`

#### 소셜 로그인 관련 패키지 구조 추가

```
user/
  domain/
    model/
      OAuthProvider.java             # Enum (KAKAO, GOOGLE)
      OAuthUserInfo.java             # Value Object (소셜에서 받은 사용자 정보)
    port/
      in/
        OAuthLoginUseCase.java       # Input Port: 소셜 로그인 유스케이스
      out/
        OAuthPort.java               # Output Port: 소셜 API 호출 추상화
  application/
    service/
      OAuthLoginService.java         # 소셜 로그인 → User 생성/조회 → JWT 발급
    dto/
      OAuthLoginCommand.java         # { provider, code, redirectUri }
      OAuthLoginResult.java          # { accessToken, refreshToken, isNewUser }
  adapter/
    in/web/
      OAuthController.java           # POST /api/v1/auth/oauth/{provider}
      dto/
        OAuthLoginRequest.java       # { code, redirectUri }
        OAuthLoginResponse.java      # { accessToken, refreshToken, isNewUser }
    out/oauth/
      KakaoOAuthAdapter.java         # OAuthPort 구현 (카카오)
      GoogleOAuthAdapter.java        # OAuthPort 구현 (구글)
      dto/
        KakaoTokenResponse.java      # 카카오 토큰 응답 DTO
        KakaoUserResponse.java       # 카카오 사용자 정보 응답 DTO
        GoogleTokenResponse.java     # 구글 토큰 응답 DTO
        GoogleUserResponse.java      # 구글 사용자 정보 응답 DTO
```

#### 설정 (application.yml)

```yaml
oda:
  oauth:
    kakao:
      client-id: ${KAKAO_CLIENT_ID}
      client-secret: ${KAKAO_CLIENT_SECRET}
    google:
      client-id: ${GOOGLE_CLIENT_ID}
      client-secret: ${GOOGLE_CLIENT_SECRET}
```

#### TDD 구현 순서 (소셜 로그인)

```
1. Domain 테스트 → User.createFromOAuth(provider, oauthId, email, name) 팩토리 메서드
   - 소셜 로그인으로 생성 시 passwordHash가 null 검증
   - oauthProvider, oauthId 세팅 검증
2. Application 테스트 → OAuthLoginService
   - 신규 사용자: OAuthPort mock → User 생성 → JWT 발급 → isNewUser=true
   - 기존 사용자: OAuthPort mock → User 조회 → JWT 발급 → isNewUser=false
   - 다른 Provider로 동일 이메일 로그인 시 기존 계정 연동 처리 검증
3. Adapter(Out/OAuth) 테스트 → KakaoOAuthAdapter + WireMock
   - Authorization Code → Access Token 교환 테스트
   - Access Token → 사용자 정보 조회 테스트
   - API 오류 응답 시 예외 처리 테스트
4. Adapter(Out/OAuth) 테스트 → GoogleOAuthAdapter + WireMock (동일 패턴)
5. Adapter(In) 테스트 → OAuthController @WebMvcTest
6. E2E 테스트 → 소셜 로그인 → JWT 발급 → 프로필 설정 전체 플로우
```

#### 신규 사용자 플로우

```
소셜 로그인 성공 (isNewUser=true)
    → 프론트엔드가 프로필 설정 화면으로 이동
    → PUT /api/v1/users/me/profile (나이, 거주지, 소득 등 입력)
    → 동의 플래그 설정 (개인정보 + 민감정보 별도 동의)
    → 서비스 이용 시작
```

### 도메인 모델 핵심

- `User`: Aggregate Root. 이메일, 이름, OAuth 정보(provider, oauthId), 동의 플래그
- `UserProfile`: 상세 프로필. 나이, 거주지, 소득(암호화), 학력, 경력, 자격증
- `IncomeInfo`: Value Object. 소득 데이터 캡슐화 (암호화/복호화 책임)
- 소득 필드는 JPA Adapter에서 `@Convert(converter = EncryptedLongConverter.class)` 처리

### 핵심 프로필 필드

| 필드 | 타입 | 용도 | 보안 |
|------|------|------|------|
| birthDate | LocalDate | 나이 기반 정책 매칭 | - |
| sido/sigungu | String | 지역 기반 정책 필터링 | - |
| personalIncome | Long | 소득 기준 정책 판별 | **AES-256 암호화** |
| householdIncome | Long | 가구소득 기준 판별 | **AES-256 암호화** |
| householdSize | Integer | 가구원 수 기준 | - |
| employmentStatus | Enum | 미취업/재직 기반 필터 | - |
| educations | List | 학력 조건 매칭 | - |
| certifications | List | 자격증 보유 현황 | - |
| skills | List<String> | 직무 스킬 매칭 | - |
| targetJobCategories | List<String> | 희망 직군 | - |

### 개인정보보호법 준수

- **별도 동의 플래그**: `consentPersonalInfo` (일반), `consentSensitiveInfo` (소득 등 민감정보)
- **필드 레벨 암호화**: 소득 관련 필드는 JPA `@Convert(converter = EncryptedLongConverter.class)` 적용
- **삭제 요청 지원**: `DELETE /api/v1/users/me` → 연관 데이터 전체 cascading 삭제
- **접근 로깅**: 민감 필드 조회 시 audit 로그 기록

### API 엔드포인트

```
# 인증 (소셜 로그인)
POST   /api/v1/auth/oauth/{provider}  # 소셜 로그인 (provider: kakao, google)
POST   /api/v1/auth/refresh           # JWT 토큰 갱신
POST   /api/v1/auth/logout            # 로그아웃 (리프레시 토큰 무효화)

# 사용자 정보
GET    /api/v1/users/me               # 내 정보 조회
PUT    /api/v1/users/me               # 정보 수정
GET    /api/v1/users/me/profile       # 상세 프로필 조회
PUT    /api/v1/users/me/profile       # 프로필 수정/입력 (최초 로그인 후 필수)
PUT    /api/v1/users/me/consent       # 동의 플래그 설정 (개인정보/민감정보)
DELETE /api/v1/users/me               # 회원 탈퇴 (cascading 삭제 + 소셜 연결 해제)
```

---

## Phase 3: 정책 도메인 + 데이터 파이프라인 (3~5주차)

### TDD 구현 순서

```
1. Domain 테스트 → PolicyMatchingSpec (매칭 규칙) 구현
   - 나이, 소득, 거주지, 취업상태별 매칭/비매칭 케이스
   - MatchResult에 해당/비해당 사유 포함 검증
2. Domain 테스트 → KoreanEligibilityParser (정규식 파서) 구현
   - "1인 가구 2,564,238원" → 구조화 데이터 변환 테스트
3. Application 테스트 → MatchPoliciesService 구현 (Repository mock)
4. Adapter(Out/API) 테스트 → PublicDataApiAdapter + WireMock 구현
5. Adapter(Out/DB) 테스트 → JpaPolicyRepository + Testcontainers
6. Adapter(In) 테스트 → PolicyController @WebMvcTest
7. Adapter(In) 테스트 → PolicySyncScheduler 통합 테스트
```

### 정책 도메인 모델

```java
// Policy.java - Aggregate Root 핵심 필드
public class Policy {
    PolicyId id;
    String externalId;              // API 원본 ID
    String title;                   // 정책명
    String summary;                 // 요약
    String description;             // 전체 설명
    PolicyCategory category;        // EMPLOYMENT, HOUSING, FINANCE, EDUCATION, STARTUP
    String organizationName;        // 주관기관

    // 구조화된 자격요건 (파싱 결과)
    EligibilityCriteria eligibility;  // Value Object

    // 일정
    LocalDate applicationStartDate;
    LocalDate applicationEndDate;
    String applicationUrl;

    // 메타
    LocalDateTime lastSyncedAt;
    boolean isActive;
}

// EligibilityCriteria.java - Value Object
public record EligibilityCriteria(
    Integer minAge,
    Integer maxAge,
    Long maxPersonalIncome,
    Long maxHouseholdIncome,
    Integer maxMedianIncomePercent,
    List<String> requiredRegions,
    List<EmploymentStatus> targetEmploymentStatuses,
    List<String> excludeConditions
) {}
```

### 데이터 수집 파이프라인

| 어댑터 | 데이터소스 | 동기화 주기 |
|-------|-----------|------------|
| `PublicDataApiAdapter` | data.go.kr | 매일 03:00 |
| `OntongYouthApiAdapter` | 온통청년 | 매일 03:00 |

**파싱 전략**: `KoreanEligibilityParser`로 정규식 기반 구조화. 순수 자바 로직이므로 Domain 계층에 위치 가능 (외부 의존성 없음)

**핵심**: "1인 가구 2,564,238원, 2인 가구 4,199,292원 이하" 같은 비정형 텍스트를 `{householdSize: 1, maxIncome: 2564238}` 형태로 변환

### API 엔드포인트

```
GET    /api/v1/policies                   # 전체 정책 목록 (페이지네이션, 필터)
GET    /api/v1/policies/{id}              # 정책 상세
GET    /api/v1/policies/matched           # 내 프로필 기반 매칭 정책
GET    /api/v1/policies/{id}/eligibility  # 특정 정책 자격 판별 + 사유
```

---

## Phase 4: 채용 도메인 + 잡 매칭 (5~7주차)

### TDD 구현 순서

```
1. Domain 테스트 → JobMatchingSpec (스킬/학력/경력 매칭) 구현
2. Domain 테스트 → GapAnalysis (부족 요소 분석) 구현
3. Application 테스트 → MatchJobsService, AnalyzeGapService 구현
4. Adapter(Out/API) 테스트 → SaraminApiAdapter + WireMock
   - 일일 호출 한도(500건) 제한 로직 테스트
   - Caffeine 캐시 히트/미스 테스트
5. Adapter(Out/DB) 테스트 → JpaJobPostingRepository
6. Adapter(In) 테스트 → JobController
```

### 사람인 API 호출 제한 대응

- `SaraminApiAdapter` 내부에 `AtomicInteger` 일일 카운터
- 안전 마진: 480건까지만 호출
- Caffeine 캐시 (TTL 6시간) 적용
- 테스트: 481번째 호출 시 예외 발생 검증

### 외부 API 클라이언트

| 클라이언트 | 일일 한도 | 동기화 주기 | 특이사항 |
|-----------|----------|------------|---------|
| `SaraminApiAdapter` | **500건/일** | 12시간마다 | 출처 표기 필수, 재판매 금지 |
| `WantedApiAdapter` | 협의 | 12시간마다 | 사업자등록번호 필요 |
| `JobKoreaApiAdapter` | 협의 | 12시간마다 | 지원 시 본사이트 리다이렉트 필수 |

### API 엔드포인트

```
GET    /api/v1/jobs                       # 전체 채용 목록 (페이지네이션, 필터)
GET    /api/v1/jobs/{id}                  # 채용 상세
GET    /api/v1/jobs/matched               # 내 프로필 기반 매칭 채용
GET    /api/v1/jobs/{id}/gap              # 특정 직무 대비 갭 분석
```

---

## Phase 5: 캘린더 + 할 일 관리 (7~9주차)

### 핵심 컨셉

- **자체 캘린더**: 외부 캘린더(Google 등) 연동 없이 **서비스 자체 캘린더** 구현
- **사용자 선택 기반**: 매칭된 정책/채용이 자동으로 캘린더에 들어가지 않음. **사용자가 선택한 항목만** 캘린더에 추가
- **할 일 관리**: 캘린더와 연계된 Todo 기능으로 구체적 행동 항목 관리

### TDD 구현 순서

```
1. Domain 테스트 → CalendarEvent 생성/수정/삭제 도메인 로직
   - 정책에서 이벤트 생성 시 마감일, 제목 자동 세팅 검증
   - 채용에서 이벤트 생성 시 마감일, 출처 포함 검증
2. Domain 테스트 → Todo 생성/상태변경 도메인 로직
   - PENDING → IN_PROGRESS → COMPLETED 상태 전이 검증
3. Domain 테스트 → CalendarEventAddedEvent (도메인 이벤트) 발행 검증
4. Application 테스트 → AddFromPolicyService
   - 정책 ID → Policy 조회 → CalendarEvent 생성 플로우
5. Application 테스트 → CalendarEventService, TodoService
6. Adapter 테스트 → JpaCalendarEventRepository, JpaTodoRepository
7. Adapter 테스트 → CalendarController, TodoController
```

### CalendarEvent 도메인 모델

```java
// CalendarEvent.java - Aggregate Root
public class CalendarEvent {
    CalendarEventId id;
    UserId userId;
    String title;
    String description;
    LocalDate startDate;
    LocalDate endDate;              // nullable (단일 날짜 이벤트)
    LocalTime startTime;            // nullable (종일 이벤트)
    LocalTime endTime;              // nullable
    EventType type;                 // POLICY, JOB, EXAM, CUSTOM
    EventSource source;             // 원본 참조 (policyId or jobPostingId)
    String actionUrl;               // 신청/접수 링크
    boolean allDay;                 // 종일 이벤트 여부
    RecurrenceRule recurrence;      // nullable (반복 규칙)

    // 팩토리 메서드: 정책에서 캘린더 이벤트 생성
    public static CalendarEvent fromPolicy(Policy policy, UserId userId) {
        return new CalendarEvent(
            userId,
            policy.getTitle() + " 마감",
            "신청 마감일: " + policy.getApplicationEndDate(),
            policy.getApplicationEndDate(),
            null,
            EventType.POLICY,
            EventSource.policy(policy.getId()),
            policy.getApplicationUrl(),
            true  // 종일 이벤트
        );
        // CalendarEventAddedEvent 도메인 이벤트 발행
    }

    // 팩토리 메서드: 채용에서 캘린더 이벤트 생성
    public static CalendarEvent fromJobPosting(JobPosting job, UserId userId) { ... }
}
```

### Todo 도메인 모델

```java
// Todo.java - Aggregate Root
public class Todo {
    TodoId id;
    UserId userId;
    CalendarEventId calendarEventId;  // nullable (캘린더 이벤트와 연결 가능)
    String title;
    String description;
    TodoPriority priority;            // HIGH, MEDIUM, LOW
    TodoStatus status;                // PENDING, IN_PROGRESS, COMPLETED
    LocalDate dueDate;                // nullable
    LocalDateTime completedAt;        // nullable

    public void start() {
        if (this.status != TodoStatus.PENDING) throw new IllegalStateException();
        this.status = TodoStatus.IN_PROGRESS;
    }

    public void complete() {
        this.status = TodoStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
```

### API 엔드포인트

**캘린더**:
```
GET    /api/v1/calendar/events                # 캘린더 이벤트 목록 (기간 필터)
GET    /api/v1/calendar/events/{id}           # 이벤트 상세
POST   /api/v1/calendar/events                # 커스텀 이벤트 추가
POST   /api/v1/calendar/events/from-policy    # 정책 → 캘린더 추가 (사용자 선택)
POST   /api/v1/calendar/events/from-job       # 채용 → 캘린더 추가 (사용자 선택)
PUT    /api/v1/calendar/events/{id}           # 이벤트 수정
DELETE /api/v1/calendar/events/{id}           # 이벤트 삭제
```

**할 일**:
```
GET    /api/v1/todos                          # 할 일 목록 (상태/우선순위 필터)
GET    /api/v1/todos/{id}                     # 할 일 상세
POST   /api/v1/todos                          # 할 일 생성
PUT    /api/v1/todos/{id}                     # 할 일 수정
PATCH  /api/v1/todos/{id}/start               # 진행 중으로 변경
PATCH  /api/v1/todos/{id}/complete            # 완료 처리
DELETE /api/v1/todos/{id}                     # 삭제
```

### 핵심 플로우

```
사용자가 매칭 정책 목록 조회 (/api/v1/policies/matched)
    → "청년월세지원" 선택
    → POST /api/v1/calendar/events/from-policy { policyId: 123 }
    → 캘린더에 "청년월세지원 마감" 이벤트 자동 생성
    → 사용자가 연관 할 일 추가: "주민등록등본 발급", "소득금액증명원 준비"
    → 알림 시스템이 D-3, D-1에 리마인더 전송
```

---

## Phase 6: 알림 시스템 (9~11주차)

### TDD 구현 순서

```
1. Domain 테스트 → NotificationTemplate (넛지 메시지 생성) 구현
   - 손실 회피 메시지 포맷 검증
   - D-day 카운트다운 메시지 검증
2. Domain 테스트 → Notification 생성/읽음처리 도메인 로직
3. Application 테스트 → DeadlineReminderService
   - 캘린더 이벤트 D-3 → 알림 생성 검증
   - 캘린더 이벤트 D-1 → 긴급 알림 검증
   - 만료된 이벤트 → 알림 없음 검증
4. Application 테스트 → CalendarEventHandler (도메인 이벤트 수신)
   - CalendarEventAddedEvent → 알림 예약 검증
5. Adapter 테스트 → SmtpEmailAdapter (이메일 발송)
6. Adapter 테스트 → DeadlineCheckScheduler 통합 테스트
7. Adapter 테스트 → NotificationController
```

### 알림 트리거

| 트리거 | 시점 | 메시지 예시 |
|--------|------|-----------|
| 캘린더 이벤트 추가 | 즉시 | "청년월세지원 마감일이 캘린더에 추가되었습니다" |
| D-7 리마인더 | 매일 09:00 체크 | "청년지원금 신청 마감 7일 전입니다" |
| D-3 리마인더 | 매일 09:00 체크 | "청년지원금 마감 3일 전! 필요 서류를 확인하세요" |
| D-1 긴급 | 매일 09:00 체크 | "내일 마감! 놓치면 다음 기회는 6개월 뒤입니다" |
| D-day | 당일 09:00 | "오늘이 마감일입니다. 지금 바로 신청하세요" |
| 할 일 미완료 | 매일 20:00 체크 | "오늘 완료해야 할 항목이 2개 남아있습니다" |
| 새 정책 매칭 | 데이터 동기화 후 | "새로운 정책이 매칭되었습니다: 청년창업지원금" |

### 넛지 메시지 전략 (행동경제학)

- **손실 회피**: "놓치면 다음 기회는 N개월 뒤", "이미 마감된 정책 N건을 놓쳤습니다"
- **진행률 시각화**: "이번 주 목표 5개 중 3개 완료 (60%)"
- **사회적 비교**: "같은 조건의 사용자 73%가 이 정책에 신청했습니다" (추후)

### 알림 채널

| 채널 | 구현 시기 | 어댑터 |
|------|----------|--------|
| **인앱 알림** | Phase 6 | DB 저장 + REST API 조회 |
| **이메일** | Phase 6 | `SmtpEmailAdapter` (Spring Mail) |
| **푸시 알림** | Phase 8+ | `FcmPushAdapter` (FCM) |

### API 엔드포인트

```
GET    /api/v1/notifications                      # 알림 목록 (읽음/안읽음 필터)
GET    /api/v1/notifications/unread-count          # 안 읽은 알림 수
PATCH  /api/v1/notifications/{id}/read             # 읽음 처리
PATCH  /api/v1/notifications/read-all              # 전체 읽음 처리
GET    /api/v1/notifications/preferences           # 알림 설정 조회
PUT    /api/v1/notifications/preferences           # 알림 설정 수정
```

---

## Phase 7: D-Day 대시보드 + 통합 (11~12주차)

기존 캘린더/할일/알림을 종합한 D-Day 중심 통합 뷰 API

```
GET    /api/v1/dashboard                  # 대시보드 (오늘의 할 일 + 임박 마감 + 알림 요약)
GET    /api/v1/dashboard/dday             # D-day 카운트다운 목록 (D-3, D-2, D-1 형식)
```

**응답 예시**:
```json
{
  "success": true,
  "data": {
    "todaySummary": {
      "pendingTodos": 3,
      "completedTodos": 1,
      "upcomingEvents": 2
    },
    "dDayItems": [
      {"dDay": -1, "title": "한국사능력검정시험 접수", "type": "EXAM", "actionUrl": "..."},
      {"dDay": -2, "title": "청년지원금 마감", "type": "POLICY", "actionUrl": "..."},
      {"dDay": -5, "title": "○○공기업 서류 마감", "type": "JOB", "actionUrl": "..."}
    ],
    "unreadNotifications": 4
  }
}
```

---

## Phase 8: 고도화 (13주차~)

- 커리어 경로 시뮬레이션
- 게이미피케이션 (달성 뱃지, 연속 실행 스트릭)
- Google Calendar 외부 연동 (OAuth 2.0)
- 푸시 알림 (FCM)
- 성능 최적화, 보안 감사

---

## 법적 컴플라이언스 체크리스트

| 항목 | 구현 방법 |
|------|----------|
| 개인정보 수집 동의 | 회원가입 시 `consentPersonalInfo` 필수 체크 |
| 민감정보 별도 동의 | 소득 입력 시 `consentSensitiveInfo` 별도 동의 |
| 소득 정보 암호화 | AES-256 필드 레벨 암호화 (JPA Converter) |
| 비밀번호 해싱 | BCrypt (Spring Security 기본) |
| 사람인 출처 표기 | 채용 데이터 노출 시 "출처: 사람인" 필수 표시 |
| 잡코리아 지원 리다이렉트 | 실제 지원은 잡코리아 사이트로 이동 |
| 무단 크롤링 금지 | 모든 데이터는 공식 API로만 수집 |
| 데이터 삭제 권리 | 회원 탈퇴 시 모든 연관 데이터 cascading 삭제 |

---

## 검증 계획 (Verification)

### Phase별 테스트 검증

**Phase 1-2**:
- `./gradlew test` → 모든 단위/슬라이스 테스트 통과
- Testcontainers PostgreSQL → DB 마이그레이션 정상 확인
- Swagger UI에서 Auth/User API 수동 테스트

**Phase 3**:
- `PolicyMatchingSpec` 도메인 테스트 100% 통과
- `KoreanEligibilityParser` 파서 테스트 (10개 이상 실제 정책 텍스트 케이스)
- WireMock으로 data.go.kr 응답 시뮬레이션 테스트
- `/api/v1/policies/matched` E2E 테스트

**Phase 4**:
- `SaraminApiAdapter` 호출 한도 테스트 (481번째 호출 예외)
- `/api/v1/jobs/matched` + `/api/v1/jobs/{id}/gap` E2E 테스트

**Phase 5**:
- `POST /api/v1/calendar/events/from-policy` → 정책 ID로 캘린더 이벤트 생성 확인
- Todo CRUD + 상태 전이 (PENDING → IN_PROGRESS → COMPLETED) 확인
- 도메인 이벤트 발행/수신 E2E 테스트

**Phase 6**:
- `DeadlineReminderService` → D-3, D-1 리마인더 생성 검증
- 이메일 발송 통합 테스트 (GreenMail 테스트 서버)
- `/api/v1/notifications` API 테스트

**전체**:
- `./gradlew test` → 전체 테스트 스위트 통과
- 테스트 커버리지: Domain 계층 90%+, Application 계층 80%+
