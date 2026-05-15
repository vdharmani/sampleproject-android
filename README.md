# sampleproject-android

A working, runnable Android **template repo** in **Clean Architecture** for
your team. Clone, rename, point `BASE_URL` at your backend, start adding
features. Everything below ships pre-wired:

- Single-module Gradle (`:app`), organized **package-by-feature**
- Hilt DI, KSP-based code-gen (no kapt)
- Retrofit 2 + OkHttp 5 + kotlinx.serialization
- Auth interceptor + automatic refresh on 401
- Room + DataStore Preferences (auth tokens encrypted at rest via Android Keystore AES-256-GCM)
- Security hardening: HTTPS-only (network security config blocks cleartext), auth tokens excluded from cloud/device backups, bearer headers never logged in release
- Jetpack Compose + Material 3 + Navigation Compose (Kotlin-serialization type-safe routes)
- Edge-to-edge + `WindowInsets.safeDrawing` on `Scaffold` content
- **Release:** R8 minification + resource shrinking + `app/proguard-rules.pro`
- Mock backend wired to **reqres.in** so the app runs without a real backend

### UI state shape (team convention)

Screens use an **MVI-style** split: **`UiState`** (what to render), **`Intent`**
(sealed user/system actions the ViewModel handles), and **`Effect`** (one-shot
events such as navigation or snackbars). That is a **deliberate template choice**
so new contributors copy the same file set per screen.

Google's own samples often use a **lighter** pattern: a single `UiState` data
class plus ad-hoc events or callbacks without a named `Intent` hierarchy. Both
approaches are valid; this repo standardizes on explicit Intent/Effect types for
consistency across features, not because it is the only “correct” architecture.

### What ships in this repo

- **Navigation:** login, signup, forgot password, home hub, profile, change
  password, delete account, premium, terms, privacy (see `AppNavGraph.kt` and
  `AppDestinations.kt`).
- **Auth:** domain + data layers (use cases, repository, Retrofit API, token
  refresh) and **presentation** for login, signup, forgot, change password, and
  delete account.
- **Profile / premium / legal** feature packages with wired entry screens.
- **Core** packages: common types, network, datastore, database, shared Compose UI.

---

## Package map

Single module — everything lives in `:app`, organized **package-by-feature**.
Each feature keeps the Clean Architecture layer split (`domain` / `data` /
`presentation`) inside its own package.

```
sampleproject-android/
└── app/                                        ← the only Gradle module
    └── src/main/java/com/sample/app/
        ├── MainActivity.kt, SampleApplication.kt
        ├── navigation/                         ← AppNavGraph + type-safe routes
        ├── core/                               ← shared infrastructure
        │   ├── common/                         ← Resource<T>, AppException sealed types
        │   ├── network/                        ← OkHttp + Retrofit + interceptors
        │   ├── security/                       ← KeystoreCrypto (AES-256-GCM, Android Keystore)
        │   ├── datastore/                      ← TokenStore (tokens encrypted at rest)
        │   ├── database/                       ← Room + UserDao
        │   └── ui/                             ← theme, AppLoader, AppErrorDialog, EmailField, PasswordField
        └── feature/                            ← one package per feature
            ├── auth/                           ← one feature, three layers inside
            │   ├── domain/                     ← PURE Kotlin: models, repository interface, use cases
            │   ├── data/                       ← Retrofit DTOs, mappers, AuthRepositoryImpl, Hilt bindings
            │   └── presentation/               ← one subfolder per flow
            │       ├── login/                  ← Screen + ViewModel + UiState + Intent + Effect
            │       ├── signup/
            │       └── …                       ← same pattern for forgot, change password, etc.
            ├── profile/, premium/, legal/      ← wired entry screens
```

**Dependency flow (Clean Architecture):**

- `presentation` → `domain` (calls use cases such as `LoginUseCase`, never the repository directly)
- `data` → `domain` (implements `AuthRepository`)
- `domain` → nothing (pure Kotlin — no Android, no Retrofit, no Room)

Junior reads `feature/auth/` top-to-bottom and can build any other feature
(profile, premium, settings, …) by mimicking the three packages inside.

---

## Setup

1. Open in Android Studio Koala+ (anything that speaks AGP 8.13.x).
2. The first sync downloads dependencies — wait it out.
3. `./gradlew assembleDebug` confirms everything compiles; `./gradlew assembleRelease` verifies R8 + shrink rules (unsigned release APK for CI smoke tests).
4. Run on a device/emulator. The login screen accepts:
   - email: `eve.holt@reqres.in`
   - password: `cityslicka`
   - (these are the reqres.in mock credentials — see https://reqres.in/)

> **Note on JDK:** the project uses JVM 17 source/target. If `./gradlew sync`
> complains about JDK, point Android Studio at JDK 17+ in
> Preferences → Build Tools → Gradle.

---

## Renaming for your project

Use the bundled `rename.sh` to rename the package + app ID in one shot:

```bash
git clone https://github.com/vdharmani/sampleproject-android my-app
cd my-app
rm -rf .git && git init                       # fresh history
./rename.sh com.myteam.myapp "My App"         # new package id, new app name
# Then update BASE_URL in app/build.gradle.kts (buildConfigField)
./gradlew assembleDebug
```

Prefer to do it by hand? In Studio: **Refactor → Rename Package** on
`com.sample.app`, then update `applicationId` in `app/build.gradle.kts`,
`rootProject.name` in `settings.gradle.kts`, and `app_name` in
`app/src/main/res/values/strings.xml`.

---

## Adding a new auth flow (presentation layer)

When the **domain + data** pieces already exist (use case + repository API), add
the UI the same way every other auth flow in this repo does: about **five
small files** under `feature/auth/presentation/<your-flow>/`.

Use **`presentation/login/`** or **`presentation/signup/`** as the copy
template (both are fully wired end-to-end). The usual file set:

1. `<Flow>UiState.kt` — data the screen renders.
2. `<Flow>Intent.kt` — sealed user/system actions.
3. `<Flow>Effect.kt` — one-shot events (navigation, errors).
4. `<Flow>ViewModel.kt` — `@HiltViewModel`, collects intents, drives `StateFlow`, emits effects.
5. `<Flow>Screen.kt` — `@Composable`; `collectAsStateWithLifecycle()` for state;
   `LaunchedEffect` for effects.

Then add a `@Serializable data object` route in `app/.../navigation/AppDestinations.kt`
and a matching `composable<YourRoute> { … }` in `app/navigation/AppNavGraph.kt`.

> The repeated **Intent / UiState / Effect** structure is intentional so the
> team shares one mental model; it is not required by the Android framework.

---

## Adding a new feature

No new Gradle module — a feature is just a package under `feature/`.

```bash
# 1. Make the package folders
mkdir -p app/src/main/java/com/sample/app/feature/profile/{domain/{model,repository,usecase},data/{remote/dto,mapper,repository,di},presentation}
```

2. Build the layers the same way `feature/auth/` does — `domain` (pure Kotlin
   use cases + repository interface), `data` (Retrofit + `RepositoryImpl` +
   Hilt bindings), `presentation` (one subfolder per screen).
3. Wire a `@Serializable` route in `navigation/AppDestinations.kt` and a
   matching `composable<…>` in `navigation/AppNavGraph.kt`.

Any new dependencies go straight into `app/build.gradle.kts`.

---

## Auth flow (how it all hangs together)

1. User submits the Login form → `LoginIntent.Submit`.
2. `LoginViewModel.submit()` calls `LoginUseCase(email, password)`.
3. `LoginUseCase` validates inputs (`InvalidEmailException`, etc.), then
   calls `repository.login(...)`.
4. `AuthRepositoryImpl` hits `AuthApi.login` (Retrofit). Retrofit goes
   through OkHttp, where `AuthInterceptor` skips token-attach (the request
   carries `No-Auth: true`).
5. On success, the response's `token` is mapped to a domain `AuthToken`
   and saved to `TokenStore` (DataStore). The user row is upserted into
   Room via `UserDao`.
6. ViewModel emits `LoginEffect.NavigateToHome`; the Screen routes via the
   `onAuthed` lambda.

When a future authenticated request returns 401:

1. OkHttp calls `TokenRefreshAuthenticator.authenticate`.
2. It pulls the current `refreshToken` from `TokenStore`, calls
   `AuthTokenRefresher.refresh(...)` (resolved via `dagger.Lazy` to break
   a dependency cycle).
3. New tokens are saved; the original request is retried with the new
   `Authorization: Bearer ...` header.
4. If refresh fails, tokens are cleared and the next read by any screen
   observing auth state sees `AuthState.SignedOut`.

---

## Swapping to your real backend

Three files touch:

1. `app/build.gradle.kts` — change the
   `buildConfigField("String", "BASE_URL", "...")` line.
2. `feature/auth/data/remote/AuthApi.kt` — adjust endpoint paths if needed.
3. `feature/auth/data/mapper/AuthMappers.kt` — adjust DTO ↔ domain mapping
   if your backend's response field names differ from reqres.in.

That's it. The domain layer, ViewModels, and screens don't change.

---

## Release builds (R8)

The **release** build type turns on **code shrinking / obfuscation (R8)** and
**resource shrinking** in `app/build.gradle.kts`. Custom keep rules live in
[`app/proguard-rules.pro`](app/proguard-rules.pro) (Kotlin Serialization,
Retrofit `AuthApi`, stack-trace attributes). Library AARs merge their own
consumer Proguard rules (Hilt, OkHttp, Retrofit, etc.).

- Smoke test: `./gradlew assembleRelease` (local release APK; configure signing
  in CI or Android Studio for Play uploads).
- If something breaks at runtime after an upgrade, add targeted `-keep` rules
  rather than disabling minify.

---

## Tech stack snapshot

| | |
|---|---|
| AGP | 8.13.2 |
| Kotlin | 2.2.21 |
| KSP | 2.2.21-2.0.5 |
| compileSdk / minSdk / targetSdk | 36 / 24 / 36 |
| Release | R8 + shrink resources + `proguard-rules.pro` |
| Compose runtime/UI | 1.11.1 |
| Material 3 (Compose) | 1.4.0 |
| Navigation Compose | 2.9.8 |
| Hilt | 2.56.2 |
| Retrofit | 2.12.0 |
| OkHttp | 5.3.2 |
| kotlinx.serialization | 1.11.0 |
| kotlinx.coroutines | 1.11.0 |
| Room | 2.8.4 |
| DataStore Preferences | 1.2.1 |

---

## License

MIT — see [`LICENSE`](LICENSE).
