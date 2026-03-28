# Architecture (Android)

## Packages / Responsibilities

### `com.sixyix.android.ui`
Owned by: UI (Person 1)
- Compose screens
- Navigation
- ViewModel + StateFlow (UI state)
- UI-only models if needed (DO NOT put core calculation here)

### `com.sixyix.android.domain`
Owned by: Engine contract (shared)
- Pure Kotlin models & rules used across app
- No Android dependencies
- Must be stable for integration

### `com.sixyix.android.engine` (to be created)
Owned by: Engine (Person 2)
- Core calculation / 排盘算法 (pure Kotlin)
- Unit tests required

### `com.sixyix.android.shake` (to be created)
Owned by: Shake interaction (Person 3)
- Sensor detection / cooldown
- Emits events to UI layer (no UI screens here)

### `com.sixyix.android.data` (to be created)
Owned by: API/AI (Person 4)
- Network layer (Retrofit/Ktor etc.)
- DTOs / mappers
- Error handling / retries / timeouts

## Module rule
For now: single `app` module.
Later (optional): split into `:domain`, `:engine`, `:data` modules if needed.

## Testing rule
- Engine must have unit tests under `app/src/test`.
- Domain should stay Android-free to keep tests fast.
