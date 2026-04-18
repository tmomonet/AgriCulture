# AgriCulture — Quality Standards

**Date:** 2026-04-18  
**Quality Level:** Standard  
**Enforced by:** `/specswarm:ship`

---

## Quality Gates

| Gate | Threshold | Enforcement |
|---|---|---|
| Minimum quality score | 80 / 100 | Block merge |
| Unit test coverage (JUnit 5) | 80% line coverage | Block merge |
| Cyclomatic complexity per method | ≤ 10 | Block merge |
| Max lines per file | 300 | Warning |
| Max lines per method | 50 | Warning |
| Max parameters per method/constructor | 5 | Warning |
| Build must pass | `./gradlew build` exits 0 | Block merge |
| Tests must pass | `./gradlew test` exits 0 | Block merge |

---

## What Must Be Unit Tested (JUnit 5)

| Class | What to Test |
|---|---|
| `SoilLab` | `isPap()`, `isNapt()`, `getEffectiveFarmerTests()` fallback, `isFarmerSameAsHomeowner()`, `getFormattedAddress()` with/without `addressLine2` |
| `LabRepository` | `getAll()` returns 9 items; `getById()` returns correct lab; `getById(-1)` returns null |

Activities, Adapters, and ViewHolders are **not** required to have unit tests in Module 1 (they require Android instrumentation). Add Espresso tests in a future sprint if UI testing is prioritised.

---

## Code Quality Rules

### Naming
- Classes: `PascalCase` (`SoilLab`, `LabAdapter`)
- Methods/variables: `camelCase` (`getFormattedAddress`, `labId`)
- Constants: `UPPER_SNAKE_CASE` (`EXTRA_LAB_ID`)
- XML ids: `camelCase` prefixed by type (`tvLabName`, `btnWebsite`, `chipPap`)
- Layout files: `activity_*`, `item_*`, `fragment_*`

### Structure
- One top-level class per `.java` file.
- Inner classes allowed only for `ViewHolder` and anonymous functional interfaces.
- Static utility/helper methods belong in the class they serve, not a catch-all `Utils.java`.

### XML Layouts
- All dimensions via `@dimen/` or Material3 spacing tokens — no raw `dp` literals scattered across layouts.
- All strings via `@string/` — no hardcoded English text in XML.
- All colors via `?attr/` Material3 tokens or `@color/` from `res/values/colors.xml` — no hex literals.

---

## Performance Budgets (Android)

| Metric | Budget |
|---|---|
| Cold start to first frame (LabListActivity) | < 300 ms on mid-range device |
| RecyclerView scroll frame time | < 16 ms (60 fps) |
| APK size (debug) | < 10 MB |

---

## Exemptions

None currently. Request exemptions via a comment in the relevant PR.
