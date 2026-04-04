# Scholar Cats Threat Model (DREAD)

## System understanding

This repository is a demo scholarship management portal called "Scholar Cats".

- Frontend: React single-page app in `FrontEnd/`
- Backend: Spring Boot API in `backend/`
- Data store: local CSV files, primarily `backend/student.csv` and `backend/scholarships.csv`

### Intended roles

- Student: browse matched scholarships, view requirements, draft profile edits, simulate application submission
- Advisor: view student list and demo reports, send demo notifications
- Scholarship provider: fill in a scholarship submission form

### Actual implementation notes

- Role selection is entirely client-side and driven by a dropdown in the React app.
- The backend exposes unauthenticated `GET /api/students` and `GET /api/scholarships`.
- The backend recalculates scholarship match scores on startup and writes them back into `student.csv`.
- The app currently behaves more like a prototype/demo than a production system.

## Assets to protect

- Student personal data in `student.csv`
- Scholarship records in `scholarships.csv`
- Match scores and recommendation integrity
- Availability of the backend API and CSV data files
- Trust in role-based views for students, advisors, and providers

## Trust boundaries

1. Browser to backend API
2. Backend process to local filesystem
3. User-selected role in frontend to privileged views
4. CSV data source to in-memory recommendation logic

## Applicable STRIDE categories

The following STRIDE categories are relevant to this system:

- Spoofing: users can impersonate roles or other students because identity is not verified server-side.
- Tampering: CSV-backed records and derived scores can be modified or corrupted.
- Repudiation: there is no audit trail for access, approvals, or data changes.
- Information Disclosure: student records and recommendation data are exposed through public API responses.
- Denial of Service: malformed CSV input or file issues can disrupt startup and API availability.
- Elevation of Privilege: advisor-like access is reachable through client-side navigation rather than enforced authorization.

## High-level data flow

1. User opens React SPA.
2. User picks a role from the client-side dropdown.
3. Student and advisor pages fetch data from `http://localhost:8080/api/...`.
4. Backend reads CSV files from disk and returns rows as JSON.
5. On backend startup, local scoring logic parses CSV rows, computes match scores, and rewrites `student.csv`.

## DREAD scale

Each category is scored from 1 to 3.

- Damage Potential
- Reproducibility
- Exploitability
- Affected Users
- Discoverability

Final score = average of the five values.

## Newly added in this revision

The following threats were added after comparing the model against the expanded list of candidate threats:

- T8. Secrets in repository exposure
- T9. Malicious CSV formula injection
- T10. XSS via CSV-backed content rendered in the frontend
- T11. Information disclosure through debug errors or stack traces
- T12. Denial of service via oversized CSV files

The following candidate threats were reviewed and marked not currently applicable to this repository:

- H2 console exposure
- File path manipulation through configurable CSV paths

## DREAD threat table

| ID | Threat | STRIDE | Damage | Repro | Exploit | Affected | Discover | Score | Priority |
|---|---|---|---:|---:|---:|---:|---:|---:|---|
| T1 | Missing authentication and authorization exposes all student data and advisor functionality | Spoofing, Information Disclosure, Elevation of Privilege | 3 | 3 | 3 | 3 | 3 | 3.0 | Critical |
| T2 | Wildcard CORS allows any origin to read API responses from a victim browser | Information Disclosure | 3 | 3 | 3 | 3 | 3 | 3.0 | High |
| T3 | Backend stores and mutates authoritative data in plain CSV files without protection | Tampering, Information Disclosure | 3 | 2 | 2 | 3 | 2 | 2.4 | High |
| T4 | Malformed CSV values can crash startup scoring and break availability | Denial of Service, Tampering | 2 | 3 | 2 | 3 | 2 | 2.4 | High |
| T5 | No transport security if deployed beyond localhost due hardcoded `http://` API usage | Information Disclosure, Tampering | 3 | 2 | 2 | 2 | 3 | 2.4 | High |
| T6 | Role spoofing and insecure direct object access in the UI let users inspect other student records | Spoofing, Information Disclosure, Elevation of Privilege | 2 | 3 | 3 | 2 | 3 | 2.6 | High |
| T7 | No audit logging or security monitoring limits detection and repudiation handling | Repudiation | 2 | 3 | 2 | 3 | 2 | 2.4 | High |
| T8 | [Added] Secrets committed to the repository could disclose sensitive credentials or configuration | Information Disclosure | 3 | 3 | 2 | 3 | 3 | 2.8 | High |
| T9 | [Added] CSV formula injection could execute spreadsheet formulas when exported or opened in Excel-like tools | Tampering, Elevation of Privilege | 2 | 3 | 2 | 2 | 2 | 2.2 | Medium |
| T10 | [Added] Untrusted CSV-backed content rendered in the frontend could create XSS risk if unsafe rendering is introduced later | Tampering, Information Disclosure | 2 | 2 | 2 | 2 | 2 | 2.0 | Medium |
| T11 | [Added] Debug errors or verbose stack traces could leak implementation details and data paths | Information Disclosure | 2 | 3 | 3 | 2 | 3 | 2.6 | High |
| T12 | [Added] Oversized CSV files can exhaust memory or degrade backend availability during full-file reads and startup scoring | Denial of Service | 2 | 3 | 2 | 3 | 2 | 2.4 | High |

## Threat analysis and mitigations

### T1. Missing authentication and authorization

STRIDE:

- Spoofing
- Information Disclosure
- Elevation of Privilege

Evidence:

- `FrontEnd/src/App.js` routes users by dropdown selection only.
- `FrontEnd/src/pages/StudentPage.js` fetches all students and all scholarships.
- `FrontEnd/src/pages/AdvisorPage.js` fetches the full student list.
- `backend/src/main/java/com/example/backend/CsvController.java` exposes `/api/students` and `/api/scholarships` with no auth checks.

Impact:

- Any user who can reach the backend can retrieve student data.
- Advisor-only capabilities are not protected by the server.
- The role model is trust-by-UI only, so it can be bypassed trivially.

Mitigations:

- Add server-side authentication before exposing any student or advisor data.
- Enforce role-based authorization in backend controllers, not only in React routes.
- Change `/api/students` to scoped endpoints such as `/api/students/me` for students and privileged admin/advisor endpoints for staff.
- Remove client-side ability to impersonate arbitrary students.

### T2. Wildcard CORS

STRIDE:

- Information Disclosure

Evidence:

- `backend/src/main/java/com/example/backend/CsvController.java` uses `@CrossOrigin(origins = "*")`.

Impact:

- Any website can make browser requests to the API and read responses if the backend is reachable by the victim browser.
- This amplifies the impact of missing authentication and public data exposure.

Mitigations:

- Restrict CORS to the known frontend origin(s) only.
- Move CORS configuration to environment-specific backend config.
- Combine CORS restrictions with real authentication and authorization.

### T3. Plain CSV data store with local file mutation

STRIDE:

- Tampering
- Information Disclosure

Evidence:

- `backend/student.csv` contains student records.
- `backend/scholarships.csv` contains scholarship records.
- `backend/src/main/java/com/example/backend/BackendApplication.java` reads from and writes back to `student.csv`.

Impact:

- Confidentiality: student data is stored unencrypted and may be committed, copied, or exposed through file access.
- Integrity: match scores are recalculated and persisted directly into a flat file, making tampering hard to detect.
- Availability: corruption or partial edits to the CSV files can affect the whole application.

Mitigations:

- Move authoritative data to a database with access controls.
- Treat recommendation scores as derived data, not something rewritten into the source-of-truth file on startup.
- Apply file permissions and exclude real data from source control.
- Add backups and integrity validation for seed/demo data.

### T4. Malformed CSV can cause denial of service

STRIDE:

- Denial of Service
- Tampering

Evidence:

- `backend/src/main/java/com/example/backend/BackendApplication.java` calls `Double.parseDouble` on GPA values with no surrounding validation or recovery.
- Startup scoring runs in `main`, so a parsing failure can break application startup.

Impact:

- A bad GPA or malformed amount can stop scoring or crash the app during boot.
- Since startup logic rewrites data, file issues can have cascading effects.

Mitigations:

- Validate and sanitize CSV input before parsing.
- Fail gracefully per record instead of failing the application.
- Move scoring into a service with error handling, tests, and explicit validation.
- Add schema validation for imported or seeded data.

### T5. No transport security assumption

STRIDE:

- Information Disclosure
- Tampering

Evidence:

- `FrontEnd/src/pages/StudentPage.js` and `FrontEnd/src/pages/AdvisorPage.js` call `http://localhost:8080/...` directly.
- `FrontEnd/package.json` includes a development proxy, but pages still bypass it with hardcoded absolute URLs.

Impact:

- Safe enough for local development, but unsafe if reused for shared or deployed environments.
- Traffic could be intercepted or modified if the app is exposed over a network without TLS.

Mitigations:

- Use relative API paths in the frontend and environment-based configuration.
- Terminate TLS in front of the backend for non-local deployments.
- Enforce HTTPS and secure deployment defaults.

### T6. Role spoofing and insecure record access in the UI

STRIDE:

- Spoofing
- Information Disclosure
- Elevation of Privilege

Evidence:

- `FrontEnd/src/App.js` lets a user self-select `Student`, `Advisor`, or `Scholarship Provider`.
- `FrontEnd/src/pages/StudentPage.js` loads the entire student list and exposes a selector for choosing any student profile.

Impact:

- A student can inspect other student names, majors, GPA values, and match-related data.
- The UI creates a false sense of role isolation while exposing shared records to any browser user.

Mitigations:

- Replace the role dropdown with authenticated session-based routing.
- Return only the current user’s record to the student view.
- Restrict advisor data access to authorized staff.

### T7. Lack of logging and monitoring

STRIDE:

- Repudiation

Evidence:

- No security event logging, auth logging, or audit trail is present.
- Console printing is used for file-read failures, with no structured monitoring.

Impact:

- Abuse, scraping, or repeated failed access would be difficult to detect.
- Data changes to local files are not auditable.

Mitigations:

- Add structured backend logging for authentication, authorization failures, and data access.
- Record administrative actions such as scholarship creation, approvals, and notifications.
- Add alerting for startup failures, repeated API access spikes, and data integrity issues.

### T8. [Added] Secrets in repository exposure

STRIDE:

- Information Disclosure

Evidence:

- I did not find obvious application secrets in the tracked files reviewed, but the repository currently contains data files and environment-free configuration, so secret scanning should still be part of the threat model.
- There is no documented secret management process in the repository.

Impact:

- If API keys, database credentials, tokens, or private student data are ever committed, they may be copied indefinitely through git history and forks.
- Even if secrets are removed later, previously exposed values usually must be rotated.

Mitigations:

- Run secret scanning on the repository and git history.
- Use environment variables or a secret manager for sensitive values.
- Add `.gitignore` rules and commit hooks for local secret files.
- Rotate any secrets found in history, not just current files.

### T9. [Added] CSV formula injection

STRIDE:

- Tampering
- Elevation of Privilege

Evidence:

- The system uses CSV files as a primary storage and interchange format in `backend/student.csv` and `backend/scholarships.csv`.
- Spreadsheet tools may interpret cells beginning with `=`, `+`, `-`, or `@` as formulas when opened manually or exported later.

Impact:

- A maliciously crafted value could trigger spreadsheet behavior when data is opened by staff in Excel or similar tools.
- That can lead to data tampering, misleading values, or spreadsheet-based command/exfiltration behavior depending on the environment.

Mitigations:

- Sanitize exported CSV fields that begin with spreadsheet formula characters.
- Prefix dangerous fields with a safe character such as `'` when generating analyst-facing CSV exports.
- Validate and reject suspicious leading characters in fields that do not need them.

### T10. [Added] XSS via CSV-backed content rendered in the frontend

STRIDE:

- Tampering
- Information Disclosure

Evidence:

- Scholarship and student values loaded from CSV are rendered into React components in `FrontEnd/src/pages/StudentPage.js` and `FrontEnd/src/pages/AdvisorPage.js`.
- React currently escapes rendered strings by default, so there is no direct XSS sink in the current code I reviewed.

Impact:

- Present risk is limited, but this becomes a real issue quickly if future changes add `dangerouslySetInnerHTML`, rich text rendering, markdown injection, or third-party widgets.
- CSV-backed content is untrusted input and should be modeled as such.

Mitigations:

- Keep rendering through React’s default escaping.
- Do not use `dangerouslySetInnerHTML` with CSV-backed values.
- Sanitize any future rich text or HTML-capable content paths.
- Add tests for script-like payloads in scholarship and student fields.

### T11. [Added] Information disclosure via debug errors or stack traces

STRIDE:

- Information Disclosure

Evidence:

- Backend endpoints declare `throws Exception` in `backend/src/main/java/com/example/backend/CsvController.java`.
- File-read or parse failures could bubble up into default framework error responses depending on runtime configuration.
- The code also logs raw exception messages to stdout in startup logic.

Impact:

- Error responses may reveal file names, internal class names, stack traces, or parsing behavior useful to an attacker.
- This can make follow-on attacks easier even when direct data access is limited.

Mitigations:

- Add centralized exception handling with generic client-facing error messages.
- Disable verbose stack traces in production error responses.
- Log detailed diagnostics server-side only.

### T12. [Added] DoS via oversized CSV files

STRIDE:

- Denial of Service

Evidence:

- `backend/src/main/java/com/example/backend/BackendApplication.java` reads entire files and rewrites `student.csv`.
- `writeScoresToCsv` uses `Files.readAllLines`, which loads the full file into memory.
- Startup scoring loops over every student and every scholarship, so time cost grows with dataset size.

Impact:

- Very large CSV files can increase memory usage, startup time, and request latency.
- In constrained environments, this can make the API unavailable.

Mitigations:

- Enforce reasonable CSV size limits for seed or imported data.
- Stream records where possible instead of loading complete files into memory.
- Move startup scoring out of application boot and process data incrementally.
- Add timeouts and operational limits for large datasets.

## Reviewed but not currently applicable

### H2 console exposure

Status:

- Not currently applicable

Reason:

- I did not find H2 dependencies, database console settings, or Spring Data/JPA configuration in this repository.
- The backend uses CSV files rather than an H2 database.

### File path manipulation through configurable CSV paths

Status:

- Not currently applicable

Reason:

- The backend uses hardcoded relative paths such as `Paths.get("student.csv")` and `Paths.get("scholarships.csv")`.
- I did not find a user-controlled file path parameter, upload path, or config property that would let an attacker choose which file is read.
- This would become applicable if future versions allow runtime path configuration or user-supplied file imports.

## Alternative table formats

This section mirrors the two table formats you shared, but filled in for the current Scholar Cats codebase.

### Table 1. Threat summary with STRIDE and DREAD

| # | Threat | STRIDE Category | Damage | Repro | Exploit | Affected | Discover | Avg | Risk |
|---|---|---|---:|---:|---:|---:|---:|---:|---|
| 1 | Broken Access Control on API | S / E / I | 3 | 3 | 3 | 3 | 3 | 3.0 | Critical |
| 2 | Wildcard CORS Exposure | I | 3 | 3 | 3 | 3 | 3 | 3.0 | High |
| 3 | Unauthorized Student Data Exposure | I / E | 3 | 3 | 3 | 3 | 3 | 3.0 | Critical |
| 4 | CSV Data Tampering | T | 3 | 2 | 2 | 3 | 2 | 2.4 | High |
| 5 | Malformed CSV Causes Startup Failure | D / T | 2 | 3 | 2 | 3 | 2 | 2.4 | High |
| 6 | DoS via Large CSV File | D | 2 | 3 | 2 | 3 | 2 | 2.4 | High |
| 7 | Secrets in Repository | I | 3 | 3 | 2 | 3 | 3 | 2.8 | High |
| 8 | Malicious CSV Injection (Formula Injection) | T / E | 2 | 3 | 2 | 2 | 2 | 2.2 | Medium |
| 9 | XSS via CSV Content | T / I | 2 | 2 | 2 | 2 | 2 | 2.0 | Medium |
| 10 | Information Disclosure (Debug Errors / Stack Traces) | I | 2 | 3 | 3 | 2 | 3 | 2.6 | High |
| 11 | No Transport Security Beyond Localhost | I / T | 3 | 2 | 2 | 2 | 3 | 2.4 | High |
| 12 | Lack of Audit Logging / Monitoring | R | 2 | 3 | 2 | 3 | 2 | 2.4 | High |

Reviewed and not currently applicable:

| # | Threat | Status | Reason |
|---|---|---|---|
| 13 | H2 Console Exposure | Not applicable | No H2 dependency, console config, or embedded H2 usage was found in this repository. |
| 14 | File Path Manipulation (if CSV path configurable) | Not applicable | CSV paths are hardcoded and not user-configurable in the current implementation. |

### Table 2. Asset-focused DREAD analysis with mitigations

| Asset | Threat | Damage Potential | Reproducibility | Exploitability | Affected Users | Discoverability | Risk | Rating | Mitigation |
|---|---|---:|---:|---:|---:|---:|---:|---|---|
| Spring Boot API | Broken access control allows any user to call student and advisor data endpoints | 3 | 3 | 3 | 3 | 3 | 15 | Critical | Add backend authentication and role-based authorization; expose student-specific endpoints such as `/me` instead of full datasets. |
| Spring Boot API | Wildcard CORS allows any origin to read API responses from a victim browser | 3 | 3 | 3 | 3 | 3 | 15 | High | Restrict CORS to trusted frontend origins and pair it with server-side auth. |
| Student records in `backend/student.csv` | Unauthorized API access reveals names, GPA, major, year, and match information | 3 | 3 | 3 | 3 | 3 | 15 | Critical | Limit API responses by role, stop returning all student records to the browser, and protect the backing data source. |
| Student records in `backend/student.csv` | CSV tampering could alter student details or recommendation scores | 3 | 2 | 2 | 3 | 2 | 12 | High | Restrict file permissions, validate imported data, and move authoritative records to a database with access control. |
| Recommendation scoring process | Malformed GPA or scholarship values can crash scoring during startup | 2 | 3 | 2 | 3 | 2 | 12 | High | Add validation, fail safely per record, and move scoring logic out of `main` into a tested service layer. |
| Recommendation scoring process | Oversized CSV files can increase startup time and memory usage | 2 | 3 | 2 | 3 | 2 | 12 | High | Stream file processing, add size limits, and avoid `Files.readAllLines` for large inputs. |
| Git repository / source history | Secrets accidentally committed to the repo could expose credentials or sensitive data | 3 | 3 | 2 | 3 | 3 | 14 | High | Run secret scanning, use environment variables or a secret manager, and rotate any exposed secrets. |
| Scholarship and student CSV content | Malicious formula injection could execute when opened in spreadsheet software | 2 | 3 | 2 | 2 | 2 | 11 | Medium | Escape or neutralize fields beginning with `=`, `+`, `-`, or `@` before export or spreadsheet use. |
| React frontend | CSV-backed content could become an XSS vector if unsafe HTML rendering is introduced later | 2 | 2 | 2 | 2 | 2 | 10 | Medium | Keep React escaping in place, avoid `dangerouslySetInnerHTML`, and sanitize future rich text fields. |
| Spring Boot API | Debug errors or stack traces could expose internal file paths and implementation details | 2 | 3 | 3 | 2 | 3 | 13 | High | Add centralized exception handling and return generic production error responses. |
| Frontend to backend transport | Hardcoded `http://localhost:8080` usage is unsafe if reused outside local development | 3 | 2 | 2 | 2 | 3 | 12 | High | Use environment-based API URLs, relative paths where appropriate, and enforce HTTPS in deployed environments. |
| Spring Boot API and admin workflows | Missing audit logs make abusive access and data changes hard to detect or prove | 2 | 3 | 2 | 3 | 2 | 12 | High | Add structured access logs, admin action logs, and alerting for suspicious activity. |

## Recommended mitigation roadmap

### Phase 1: Immediate risk reduction

- Add backend authentication and role-based authorization.
- Remove `@CrossOrigin(origins = "*")`.
- Stop exposing the full student list to the student-facing UI.
- Replace hardcoded absolute API URLs with environment-based configuration.

### Phase 2: Integrity and resilience

- Remove CSV rewrite-on-startup behavior from `BackendApplication`.
- Validate all CSV input and handle bad records safely.
- Add backend tests for scoring, parsing, and authorization behavior.

### Phase 3: Production hardening

- Migrate from CSV files to a database with proper access control.
- Add audit logging and monitoring.
- Define secure deployment settings for HTTPS, secrets, and environment separation.

## Residual risk note

As currently written, this codebase should be treated as a classroom prototype and not a production system handling real student records. The biggest theme is broken access control: the browser decides who the user is, while the backend trusts everyone.
