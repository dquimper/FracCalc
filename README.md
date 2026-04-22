# FractionCalculator

An Android calculator for mixed-number arithmetic. Enter fractions and mixed numbers directly — no decimal approximations. Results stay exact, and you can tap any result to see the step-by-step reduction.

## Features

- Addition, subtraction, multiplication, and division of fractions and mixed numbers
- Decimal input (e.g. `1.5`) parsed exactly without float rounding
- Step-by-step breakdown for every result
- Scrollable calculation history with tap-to-restore
- Sign toggle and backspace editing

## How to Install

### Prerequisites

- Android phone running Android 8.0+ (API 26+)
- USB or wireless ADB connection

### Build from source

```bash
# One-time environment setup
brew install openjdk@17
brew install --cask android-commandlinetools

# Add to ~/.zshrc:
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

```bash
# Build the APK
source .envrc
./gradlew :app:assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Install over wireless ADB

Both Mac and phone must be on the same WiFi network.

**First-time pairing:**
```bash
# On the phone: Settings → Developer Options → Wireless debugging → Pair device with pairing code
adb pair <IP:pairing-port> <6-digit-code>
```

**Connect and install:**
```bash
source .envrc
PORT=$(adb mdns services 2>/dev/null | grep RFCX | awk '{print $3}' | cut -d: -f2)
adb connect 192.168.42.78:$PORT
adb -s 192.168.42.78:$PORT install -r app/build/outputs/apk/debug/app-debug.apk
```

## How to Contribute

### Tech stack

- Kotlin 2.0.20, Jetpack Compose (Material3)
- ViewModel + `mutableStateOf` for state
- Gradle 8.7, AGP 8.5.2, min SDK 26

### Project layout

```
app/src/main/kotlin/com/dq/fractioncalculator/
├── math/
│   ├── Fraction.kt         # Core arithmetic (Long-based, GCD/LCM)
│   └── Steps.kt            # Step-by-step reduction recording
├── state/
│   └── CalculatorViewModel.kt  # Input state, history, event handlers
└── ui/
    ├── CalculatorScreen.kt
    ├── Display.kt
    ├── Keypad.kt
    ├── StepsSheet.kt
    └── HistoryDrawer.kt
```

### Running tests

```bash
./gradlew :app:testDebugUnitTest
```

Unit tests cover `Fraction.kt` arithmetic and `Steps.kt` reduction logic and run on the JVM — no device needed.

### Manual testing

`MANUAL_TESTS.md` contains 15 test cases covering arithmetic, display, sign toggle, steps, history, and layout. Run through the relevant cases before opening a PR.

### Guidelines

- All fraction arithmetic goes through `Fraction.kt`; keep the `den > 0` invariant and negatives in `num`
- New UI goes in `ui/`; keep composables stateless where possible and drive them from `CalculatorViewModel`
- Match the existing Material3 theme (`theme/`) rather than adding one-off colors
