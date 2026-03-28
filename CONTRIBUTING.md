# Contributing

## Branch rule
- Do NOT push to `main` directly.
- Create a feature branch for every task.

Branch name format (pick one):
- `feature/ui-xxx`
- `feature/engine-xxx`
- `feature/shake-xxx`
- `feature/api-xxx`
- `fix/xxx`

## PR rule
- Open a Pull Request into `main`.
- Keep PR small (prefer < 300 lines changed).
- In PR description, include:
    - What you changed
    - How to test (steps / screenshots)

## Project ownership (Android-only split)
- UI (Compose + Navigation + ViewModel/StateFlow): Person 1
- Engine (pure Kotlin + unit tests): Person 2
- Shake interaction (sensor + animation): Person 3
- API/AI + integration + release: Person 4

## Local setup
- Android Studio: open the project and run the `app` configuration.
- If build fails, try: `./gradlew clean` then rebuild.
