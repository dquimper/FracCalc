# Manual Test Plan — Fraction Calculator

Each test is independent. Clear the calculator (C) before each one.

---

## T01 — Basic addition
1. Left: whole=2, num=1, den=2
2. Tap +
3. Right: whole=3, num=2, den=3
4. Tap =

**Expected:** display shows `2 1/2 + 3 2/3 = 6 1/6`, decimal reads `(6.16667)`

---

## T02 — Basic subtraction
1. Left: whole=3, num=3, den=4
2. Tap −
3. Right: num=1, den=2
4. Tap =

**Expected:** `3 3/4 − 1/2 = 3 1/4`

---

## T03 — Multiplication
1. Left: num=1, den=2
2. Tap ×
3. Right: num=2, den=3
4. Tap =

**Expected:** `1/2 × 2/3 = 1/3`

---

## T04 — Division
1. Left: num=1, den=2
2. Tap ÷
3. Right: num=1, den=4
4. Tap =

**Expected:** `1/2 ÷ 1/4 = 2`

---

## T05 — Negative result
1. Left: num=1, den=2
2. Tap −
3. Right: num=3, den=4
4. Tap =

**Expected:** `1/2 − 3/4 = −1/4`

---

## T06 — Whole number result
1. Left: num=3, den=4
2. Tap +
3. Right: num=1, den=4
4. Tap =

**Expected:** `3/4 + 1/4 = 1` (no fraction part shown)

---

## T07 — Sign toggle
1. Type whole=5
2. Tap +/−

**Expected:** display shows `−5`

---

## T08 — Backspace
1. Type whole=1, then 2 (shows 12)
2. Tap ⌫ on the left keypad

**Expected:** display shows `1` (last digit removed)

---

## T09 — Clear
1. Type whole=9, num=8, den=7
2. Tap C

**Expected:** display resets to `0`, all fields empty

---

## T10 — Display during input (second operand visible)
1. Type left whole=5, num=3, den=5
2. Tap +
3. Type right num=1, den=5

**Expected:** display shows `5 3/5 + 1/5` — both operands fully visible, no overflow off-screen

---

## T11 — Fraction bar width
1. Type left num=1, den=2 (no whole number)

**Expected:** the horizontal fraction bar is only as wide as the digits above/below it, not full-screen width

---

## T12 — Steps view
1. Perform T01 (2 1/2 + 3 2/3 = 6 1/6)
2. Tap the result

**Expected:** steps sheet opens showing:
- Equation: `2 1/2 + 3 2/3`
- Steps: convert to improper → common denominator → combine → back to mixed
- Final result: `6 1/6`
- Close button dismisses the sheet

---

## T13 — History
1. Perform T01, then T03
2. Tap the history icon (top-left)

**Expected:** history sheet shows both calculations, most recent first. Tap one — it restores that expression to the display.

---

## T14 — Layout: no overlap with system bars
1. Open the app

**Expected:**
- Top bar (FRACTIONPLUS) is fully below the status bar (clock/icons)
- Operator row (+, −, ×, ÷, =) is fully above the navigation bar (home/back buttons)

---

## T15 — Keypad proportions
1. Open the app

**Expected:** the left column (whole number pad) takes roughly 1/3 of the keypad width; the right dual pad takes 2/3
