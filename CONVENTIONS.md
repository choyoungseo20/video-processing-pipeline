# 프로젝트 컨벤션

## API 응답 양식

모든 응답은 `CommonResponse` 양식을 따릅니다.

```jsonc
// 성공
{ "code": "COMMON200", "message": "성공적으로 요청을 수행하였습니다.", "result": { ... } }

// 실패 (validation 실패 시 result에 필드별 에러)
{ "code": "COMMON400", "message": "잘못된 요청입니다.", "result": { "email": "올바른 이메일 형식이 아닙니다" } }
```

- 응답 코드는 `ErrorStatus` / `SuccessStatus` enum에서만 정의합니다
- 비즈니스 예외는 `GeneralException`으로 던지면 `ExceptionAdvice`가 공통 양식으로 변환합니다
- HTTP 상태 코드와 응답 `code`는 항상 일치합니다
- 서버 내부 정보(스택트레이스, 예외 메시지)는 응답에 노출하지 않습니다

## 환경 / 프로파일

| 프로파일 | 용도 | DB 접속 | ddl-auto |
|---|---|---|---|
| local | IDE 개발 | localhost 고정값 | update |
| dev | 도커/개발 서버 | 환경변수 주입 | update |
| prod | 운영 | 환경변수 주입 | validate |

- 시크릿은 `.env`로만 관리합니다 (`.env`는 커밋 금지, `.env.example`만 커밋)
- prod에서는 Swagger가 비활성화됩니다

## 이슈 / 브랜치 / 커밋

| 유형 | 이슈 접두사 | 브랜치 | 커밋 |
|---|---|---|---|
| 기능 | [FEAT] | `feat/#이슈번호-요약` | `feat: 내용` |
| 버그 | [FIX] | `fix/#이슈번호-요약` | `fix: 내용` |
| 리팩토링 | [REFACTOR] | `refactor/#이슈번호-요약` | `refactor: 내용` |
| 설정/문서 | [CHORE] | `chore/#이슈번호-요약` | `chore: 내용` |

## 작업 흐름

1. 이슈 생성 (템플릿 사용)
2. 이슈 번호로 브랜치 생성
3. 작업 후 PR 생성 (`Fixes #이슈번호`로 이슈 연결)
4. 리뷰 → 머지 → 이슈 자동 닫힘
