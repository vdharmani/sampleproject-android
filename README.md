# sampleproject-android

A working, runnable Android **template repo** in MVI + Clean Architecture for
your team. Clone, rename, point `BASE_URL` at your backend, start adding
features. Everything below ships pre-wired:

- Multi-module Gradle (`:app` + `:core:*` + `:feature:*`)
- Hilt DI, KSP-based code-gen (no kapt)
- Retrofit 2 + OkHttp 5 + kotlinx.serialization
- Auth interceptor + automatic refresh on 401
- Room + DataStore Preferences (for tokens)
- Jetpack Compose + Material 3 + Navigation Compose
- MVI for every screen: `State` + `Intent` + `Effect`
- Mock backend wired to **reqres.in** so the app runs without a real backend

> **Phase 1 (this version):** project skeleton + all `core/*` modules + the
> auth domain/data layers + the **Login** screen end-to-end. Signup, forgot
> password, reset, change password, logout, and delete account use cases are
> implemented in the domain layer — their UI screens land in subsequent
> phases (or you ship them, copying the Login pattern).

---

## Module map

```
sampleproject-android/
├── app/                                    ← Application + NavHost + theme entry
├── core/
│   ├── common/                             ← Resource<T>, AppException sealed types
│   ├── network/                            ← OkHttp + Retrofit + interceptors
│   ├── datastore/                          ← TokenStore (encrypted-ish prefs)
│   ├── database/                           ← Room + UserDao
│   └── ui/                                 ← theme, AppLoader, AppErrorDialog, EmailField, PasswordField
└── feature/
    └── auth/                               ← one feature, three layers inside
        ├── domain/                         ← PURE Kotlin: models, repository interface, use cases
        ├── data/                           ← Retrofit DTOs, mappers, AuthRepositoryImpl, Hilt bindings
        └── presentation/login/             ← Screen + ViewModel + UiState + Intent + Effect
```

**Dependency flow (Clean Architecture):**

- `presentation` → `domain` (uses `LoginUseCase`, never the repository directly)
- `data` → `domain` (implements `AuthRepository`)
- `domain` → nothing (pure Kotlin — no Android, no Retrofit, no Room)

Junior reads `feature/auth/` top-to-bottom and can build any other feature
(profile, premium, settings, …) by mimicking the three packages inside.

---

## Setup

1. Open in Android Studio Koala+ (anything that speaks AGP 8.13.x).
2. The first sync downloads dependencies — wait it out.
3. `./gradlew assembleDebug` confirms everything compiles.
4. Run on a device/emulator. The login screen accepts:
   - email: `eve.holt@reqres.in`
   - password: `cityslicka`
   - (these are the reqres.in mock credentials — see https://reqres.in/)

> **Note on JDK:** the project uses JVM 17 source/target. If `./gradlew sync`
> complains about JDK, point Android Studio at JDK 17+ in
> Preferences → Build Tools → Gradle.

---

## Renaming for your project

```bash
git clone https://github.com/vdharmani/sampleproject-android my-app
cd my-app
rm -rf .git && git init                       # fresh history
# Find/replace `com.vdharmani.starter` → `com.myteam.myapp` (in Studio: 
# Refactor → Rename Package, or via sed)
# Update `applicationId` in app/build.gradle.kts and `rootProject.name` 
# in settings.gradle.kts
# Update BASE_URL in core/network/build.gradle.kts (buildConfigField)
./gradlew assembleDebug
```

A `rename.sh` script automating this is on the roadmap (Phase 2).

---

## Adding a new auth screen (e.g. Signup)

The use case + repo method already exist. The work is only the presentation
layer — about **5 small files**, all in `feature/auth/presentation/signup/`:

1. `SignupUiState.kt` — the data class describing what the screen renders.
2. `SignupIntent.kt` — sealed interface of user actions
   (`EmailChanged`, `NameChanged`, `Submit`, `GoToLogin`).
3. `SignupEffect.kt` — sealed interface of one-shot events (`NavigateToHome`, `ShowError`).
4. `SignupViewModel.kt` — `@HiltViewModel`, injects `SignupUseCase`, handles
   intents, updates `MutableStateFlow<SignupUiState>`, emits effects.
5. `SignupScreen.kt` — `@Composable`, collects state via
   `collectAsStateWithLifecycle()`, renders fields + button.

Then wire one composable() in `app/navigation/AppNavGraph.kt`. Done.

> Copy `presentation/login/` as the starting point. Every auth screen follows
> the same shape — the boilerplate is intentional, juniors learn by mimicry.

---

## Adding a new feature module

```bash
# 1. Make folders
mkdir -p feature/profile/src/main/java/com/yourteam/yourapp/feature/profile/{domain/{model,repository,usecase},data/{remote/dto,mapper,repository,di},presentation}

# 2. Copy feature/auth/build.gradle.kts → feature/profile/build.gradle.kts,
#    change `namespace`, add only the deps the new feature needs.

# 3. Add to settings.gradle.kts:
#    include(":feature:profile")

# 4. Add an `implementation(project(":feature:profile"))` line to app/build.gradle.kts.
```

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

1. `core/network/build.gradle.kts` — change the
   `buildConfigField("String", "BASE_URL", "...")` line.
2. `feature/auth/data/remote/AuthApi.kt` — adjust endpoint paths if needed.
3. `feature/auth/data/mapper/AuthMappers.kt` — adjust DTO ↔ domain mapping
   if your backend's response field names differ from reqres.in.

That's it. The domain layer, ViewModels, and screens don't change.

---

## Tech stack snapshot

| | |
|---|---|
| AGP | 8.13.2 |
| Kotlin | 2.2.21 |
| KSP | 2.2.21-2.0.5 |
| compileSdk / minSdk / targetSdk | 36 / 24 / 36 |
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
