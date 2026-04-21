# FractionCalculator

Android fraction calculator app built with Kotlin + Jetpack Compose. Supports mixed-number arithmetic (whole + numerator/denominator), step-by-step breakdowns, decimal input, and calculation history.

## Tech Stack

- **Language**: Kotlin 2.0.20
- **UI**: Jetpack Compose (BOM 2024.09.03), Material3
- **Architecture**: ViewModel + Compose state (`mutableStateOf`)
- **Build**: Gradle 8.7, AGP 8.5.2
- **Min SDK**: 26 (Android 8.0), Target SDK: 34
- **Package**: `com.dq.fractioncalculator`

## Project Structure

```
fraction_calculator/
├── .envrc                          # Environment variables (source before building)
├── MANUAL_TESTS.md                 # 15 manual test cases
├── build.gradle.kts                # Root build file (plugin versions only)
├── settings.gradle.kts             # Project settings, repo config
├── gradle/
│   ├── libs.versions.toml          # Version catalog
│   └── wrapper/                    # Gradle 8.7 wrapper
└── app/
    ├── build.gradle.kts            # App module: SDK versions, dependencies
    └── src/main/kotlin/com/dq/fractioncalculator/
        ├── MainActivity.kt
        ├── math/
        │   ├── Fraction.kt         # Core arithmetic (Long-based, GCD/LCM)
        │   ├── Steps.kt            # Step-by-step reduction recording + Op enum
        │   └── Expression.kt       # (unused stub)
        ├── state/
        │   └── CalculatorViewModel.kt  # Input state, history, event handlers
        └── ui/
            ├── CalculatorScreen.kt # Root composable, system bar insets
            ├── Display.kt          # Expression display with scroll overflow
            ├── Keypad.kt           # Left column (whole) + dual numpad (num/den)
            ├── StepsSheet.kt       # Grid-paper modal bottom sheet
            ├── HistoryDrawer.kt    # History as ModalBottomSheet
            └── theme/              # Color.kt, Theme.kt, Type.kt
```

## Environment Setup (one-time)

```bash
brew install openjdk@17
brew install --cask android-commandlinetools

# Add to ~/.zshrc:
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

# Accept licenses and install SDK:
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

The `.envrc` file in the project root exports the same variables — run `source .envrc` or use `direnv` to load automatically.

## Building

```bash
source .envrc   # or: direnv allow

# Debug APK
./gradlew :app:assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Unit tests (JVM only, no device needed)
./gradlew :app:testDebugUnitTest
```

## Deploying to the Test Phone

The test phone has USB storage/data blocked (MDM policy). Use **wireless ADB** over WiFi.

Both Mac and phone must be on the **same WiFi network** (currently the phone's hotspot, subnet `192.168.42.x`).

### First-time pairing

1. On the phone: **Settings → Developer Options → Wireless debugging → Pair device with pairing code**
2. Note the IP:port and 6-digit code shown — act fast, they expire in ~30 seconds
3. Run:
   ```bash
   source .envrc
   adb pair <IP:port> <6-digit-code>
   # Example: adb pair 192.168.42.78:45461 088226
   ```

### Connecting after pairing

```bash
source .envrc
# Find the connect port (different from pairing port):
adb mdns services
# Look for: adb-RFCX606N94K-FXufkA  _adb-tls-connect._tcp  192.168.42.78:<port>

adb connect 192.168.42.78:<port>
adb devices   # should show "device"
```

### Installing

```bash
adb -s 192.168.42.78:<port> install -r app/build/outputs/apk/debug/app-debug.apk
```

The connect port changes on phone reboot or when wireless debugging is toggled. Re-pair if `adb connect` times out. The device GUID is `adb-RFCX606N94K-FXufkA`.

### One-liner build + install

```bash
source .envrc
PORT=$(adb mdns services 2>/dev/null | grep RFCX | awk '{print $3}' | cut -d: -f2)
./gradlew :app:assembleDebug && adb -s 192.168.42.78:$PORT install -r app/build/outputs/apk/debug/app-debug.apk
```

## Architecture Notes

### Input model
Each operand is a `MixedInput(whole: String, num: String, den: String)`. The left keypad column feeds `whole`; the right upper numpad feeds `num`; the right lower numpad feeds `den`. Decimal input (e.g. `1.5`) goes into the `whole` field and is parsed by `toFraction()` using string arithmetic to avoid float precision issues.

### Fraction arithmetic
All math uses `Long` integers. The `den > 0` invariant is enforced at construction; negatives live in `num`. `toMixed()` returns `Triple(whole, remainder_num, den)`.

### Step recording
`computeWithSteps()` in `Steps.kt` returns both the result fraction and a `List<Step>` capturing each symbolic reduction: mixed→improper conversion, common denominator scaling, numerator combination, and back-to-mixed conversion. The UI renders these in `StepsSheet`.

### Display scrolling
The expression row uses `BoxWithConstraints` to capture the bounded screen width, then a `Row` with `widthIn(min = screenWidth)` + `horizontalScroll` + `Arrangement.End`. A `LaunchedEffect` auto-scrolls to `maxValue` on each state change, keeping the current input visible at the right edge.

### Fraction bar width
Fraction columns use `Modifier.width(IntrinsicSize.Max)` so the horizontal bar matches the width of the widest digit (numerator or denominator), not the screen width.

## Known Limitations

- Single binary operation only (left op right); no chained expressions
- History is in-memory only (lost on app restart)
- Camera icon is a placeholder (no OCR)
- No settings screen
