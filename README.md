# 6YIX-android

An Android I Ching divination app built with **Kotlin** and **Jetpack Compose**.

## Features

### Screens

| Screen | Description |
|--------|-------------|
| **Home** | Choose divination mode: *Shake Coins* or *Tap Select Coins* |
| **Divination** | 6 rounds of 3 coins. Supports undo and reset. |
| **Result** | Shows the primary hexagram, changed hexagram (if any), and changing lines. |
| **History** | Locally stored past readings (Room database). |
| **AI Interpretation** | Generate an interpretation with a question + style (Short / Normal / Detailed). Uses a fake AI implementation (template-based, no backend needed). |

### Divination Modes

- **Shake mode** – detects device shakes via accelerometer (with debounce/cooldown). Each shake generates 3 random coin faces for the current round.
- **Tap mode** – tap each of 3 coin buttons to toggle HEADS/TAILS, then confirm the round.

## Architecture

- **MVVM + StateFlow** (no third-party DI framework)
- **Domain layer** with contracts:
  - `CoinFace` – enum (HEADS / TAILS)
  - `Throw` – 3 coin faces + computed value (6–9)
  - `SixThrows` – 6 rounds
  - `HexagramResult` – original hexagram, changed hexagram, changing lines
  - `HexagramEngine` – interface for computing results
  - `AiInterpreter` – interface for generating interpretations
  - `HistoryRepository` – interface for persistence
- **Data layer**:
  - `SimpleHexagramEngine` – King Wen sequence lookup
  - `FakeAiInterpreter` – template-based interpretation (no network)
  - `AppDatabase` (Room) + `LocalHistoryRepository`
- **UI layer** – Jetpack Compose screens with Navigation Compose

## Tech Stack

| Library | Purpose |
|---------|---------|
| Jetpack Compose + Material 3 | UI |
| Navigation Compose | Screen routing |
| ViewModel + StateFlow | State management |
| Room | Local database |
| Coroutines | Async/suspend functions |
| Gson | JSON serialization for Room |

## Building

Requirements: Android Studio Hedgehog+ or JDK 17+, Android SDK 34.

```bash
./gradlew assembleDebug
```

Min SDK: **26** · Target SDK: **34**
