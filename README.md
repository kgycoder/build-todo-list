# Todo — iOS 스타일 안드로이드 Todo 앱

블랙 & 화이트, iOS/macOS 감성의 미니멀 Todo 리스트 앱입니다. Jetpack Compose + Room으로 만들어졌고,
GitHub Actions에서 자동으로 디버그 APK가 빌드됩니다.

## 핵심 기능

- **Todo 작성/관리**: 앱 하단 입력창에서 바로 추가, 원형 버튼으로 완료 처리, 왼쪽으로 스와이프해서 삭제 (iOS 리스트 스와이프 제스처 동일 구현)
- **알림 동기화**: 완료되지 않은 모든 Todo는 각각 하나의 알림으로 표시되며, 알림 안의 원형 버튼을 누르면 앱을 열지 않고도 바로 완료 처리됩니다
- **접근성 버튼 빠른 작성**: 접근성 버튼(내비게이션 바의 원형 아이콘)을 켜두면 어떤 화면에서든 버튼 탭 한 번으로 현재 화면 위에 오버레이 카드가 뜨고, 앱을 열지 않고 바로 Todo를 추가할 수 있습니다
- **영속성**: 모든 데이터는 Room(SQLite)에 저장되어 앱을 완전히 종료해도 유지됩니다

## 프로젝트 구조

```
app/src/main/java/com/minimal/todolist/
├── data/                 # Entity, Dao, Database, Repository (Room)
├── notification/         # NotificationHelper, 완료 버튼 BroadcastReceiver
├── accessibility/        # 접근성 버튼 서비스 + 오버레이 창
├── ui/
│   ├── theme/            # 블랙&화이트 iOS 스타일 테마
│   ├── components/       # CircleCheckButton, TodoItem(스와이프), AddTodoBar, QuickAddOverlay
│   └── screens/          # TodoListScreen
├── MainActivity.kt
└── TodoApplication.kt
```

## 접근성 버튼 활성화 방법

1. 앱을 한 번 실행합니다 (알림 권한 허용).
2. 설정 > 접근성 > 설치된 앱 > **Todo 빠른 작성** 을 켭니다.
3. 내비게이션 바(또는 알림창 상단, 기기에 따라 위치가 다름)에 접근성 버튼 아이콘이 나타납니다.
4. 아무 화면에서나 이 버튼을 탭하면 현재 화면 위에 Todo 빠른 작성 카드가 오버레이로 표시됩니다.

> 기기 제조사(삼성 One UI 등)에 따라 접근성 버튼 대신 "플로팅 버튼" 형태로 표시될 수 있으나 동작은 동일합니다.

## 빌드 방법

### GitHub Actions (권장)
이 저장소를 GitHub에 push하면 `.github/workflows/android-build.yml` 워크플로우가 자동으로 실행되어
디버그 APK를 빌드합니다. Actions 탭 → 해당 워크플로우 실행 → **Artifacts** 에서
`todo-list-debug-apk` 를 다운로드하면 됩니다. (main 브랜치 push, PR, 수동 실행(workflow_dispatch) 모두 지원)

### 로컬 빌드
Android Studio(최신 버전)에서 프로젝트 폴더를 열고 Gradle Sync 후 Run 하면 됩니다.
JDK 17, Android SDK 34(minSdk 26)가 필요합니다.

## 기술 스택

- Kotlin, Jetpack Compose (Material3)
- Room (SQLite) — 로컬 영속성
- AccessibilityService + WindowManager Overlay — 접근성 버튼 오버레이
- NotificationCompat + RemoteViews — 커스텀 원형 완료 버튼이 있는 알림
- GitHub Actions — CI 빌드
