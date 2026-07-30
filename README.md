# TaskFlow · 일정 & 업무관리 안드로이드 앱

Kotlin + Jetpack Compose 로 만든 로컬 저장 기반의 일정·할 일 관리 앱입니다.
서버 없이 기기 안에서 모든 데이터가 처리됩니다.

## 주요 기능

| 화면 | 설명 |
|------|------|
| **대시보드** | 오늘 완료율(원형 그래프), 최근 7일 완료 현황(막대 그래프), 카테고리별 진행 분포, 기한 지난 항목 경고, 오늘 일정 요약 |
| **할 일** | 추가 / 편집 / 완료 토글 / 삭제, 카테고리 필터, 마감일·우선순위·등록순 정렬, 완료 항목 숨기기 |
| **캘린더** | 월 단위 달력, 날짜별 일정 개수 표시(완료 여부에 따라 색상), 날짜 선택 시 해당 일정 목록 |
| **알림** | 마감 날짜/시간 기준 푸시 알림 예약, 재부팅 후 자동 재예약 |

- **카테고리**: 업무 / 개인 / 공부 / 건강 / 기타
- **우선순위**: 높음 / 보통 / 낮음
- 다크 모드 자동 대응

## 기술 스택

- **언어**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **아키텍처**: MVVM (ViewModel + StateFlow)
- **로컬 DB**: Room (SQLite)
- **알림**: AlarmManager + BroadcastReceiver + NotificationCompat
- **네비게이션**: Navigation Compose (하단 탭)
- **최소 SDK**: API 26 (Android 8.0) / **타깃 SDK**: 35

## 프로젝트 구조

```
app/src/main/java/com/taskflow/
├── TaskFlowApp.kt          # Application, 간이 DI(서비스 로케이터)
├── MainActivity.kt         # 진입점, 알림 권한 요청, Compose 호스트
├── data/
│   ├── model/              # Category, Priority
│   ├── local/              # Task 엔티티, DAO, Room DB, TypeConverters
│   └── repository/         # TaskRepository
├── notification/           # 알림 채널/표시, 알람 예약, 부팅 재예약
└── ui/
    ├── theme/              # 색상, 타이포, 테마
    ├── navigation/         # 라우트 정의, NavHost + 하단 네비게이션
    ├── components/         # 공용 컴포저블 (TaskRow 등)
    ├── dashboard/          # 대시보드 화면 + 차트 + 뷰모델
    ├── tasks/              # 목록/편집 화면 + 뷰모델
    └── calendar/           # 캘린더 화면 + 뷰모델
```

## 빌드 & 실행

Android Studio(Ladybug 이상 권장)에서 프로젝트를 열면 됩니다.

1. Android Studio에서 이 디렉터리를 **Open**
2. Gradle Sync 완료까지 대기 (필요한 SDK 자동 설치)
3. 기기/에뮬레이터 선택 후 **Run ▶**

명령행 빌드:

```bash
# 디버그 APK 빌드 (Android SDK 필요, local.properties 에 sdk.dir 설정)
./gradlew assembleDebug

# 단위 테스트
./gradlew testDebugUnitTest
```

> 빌드에는 Android SDK 가 필요합니다. Android Studio 가 자동으로 `local.properties`
> 에 SDK 경로를 설정합니다. 직접 설정하려면 `local.properties` 에
> `sdk.dir=/path/to/Android/sdk` 를 추가하세요.

## 알림 권한 안내

- **Android 13(API 33)+**: 앱 첫 실행 시 알림 권한을 요청합니다.
- **정확한 알람**: 마감 시간에 정확히 알리기 위해 `USE_EXACT_ALARM`/`SCHEDULE_EXACT_ALARM`
  권한을 사용하며, 권한이 없으면 자동으로 비정확 알람으로 대체됩니다.

## 다음 단계 아이디어

- 반복 일정(매일/매주) 지원
- 위젯 / 홈 화면 오늘 요약
- 데이터 내보내기(백업) & 클라우드 동기화
- 하위 작업(체크리스트) 및 태그
