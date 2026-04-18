# AgriCulture — Tech Stack

**Date:** 2026-04-18  
**Auto-generated:** Partial (Gradle detected; Android target from SPEC.md)

---

## Language & Platform

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 17 (source/target compatibility) |
| Platform | Android | API 26 min / API 35 target |
| Build | Gradle Kotlin DSL | `build.gradle.kts` |
| IDE | Android Studio | Hedgehog+ |

---

## Approved Dependencies

### Core AndroidX
```kotlin
implementation("androidx.appcompat:appcompat:1.7.0")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")
implementation("androidx.recyclerview:recyclerview:1.3.2")
```

### Material Design 3
```kotlin
implementation("com.google.android.material:material:1.12.0")
```
Provides: `MaterialCardView`, `MaterialToolbar`, `Chip`, `ChipGroup`, `Snackbar`  
Do not add `androidx.cardview:cardview` separately — MaterialCardView supersedes it.

### Testing
```kotlin
testImplementation(platform("org.junit:junit-bom:5.10.0"))
testImplementation("org.junit.jupiter:junit-jupiter")
testRuntimeOnly("org.junit.platform:junit-platform-launcher")
```

---

## Prohibited Patterns

| Pattern | Reason | Use Instead |
|---|---|---|
| `androidx.cardview:cardview` standalone | Superseded by Material3 | `MaterialCardView` |
| `android.graphics.Color` hex constants in XML | Breaks dark mode | `?attr/` color tokens |
| Anonymous `View.OnClickListener` inline | Clutters `onBindViewHolder` | Named interface callbacks |
| `Serializable` on POJO for Intent passing | Slow reflection; not needed | Pass primitive `int id` |
| `Parcelable` on POJO (Module 1) | Unnecessary for static in-memory data | Pass primitive `int id` |
| ViewModel / LiveData (Module 1) | Over-engineering synchronous static data | Direct repo call in `onCreate` |
| `getColor(int)` without context | Crashes on API < 23 | `ContextCompat.getColor(ctx, R.color.x)` |

---

## Future Modules — Pre-Approved (Do Not Add Yet)

| Library | Module | Trigger |
|---|---|---|
| `androidx.room:room-runtime` | Favorites | When user-saved data is introduced |
| `androidx.navigation:navigation-fragment` | Multi-module nav | When 3+ destination Activities exist |
| `com.google.android.gms:play-services-maps` | Map view | SG-03 stretch goal |

---

## Dependency Update Policy

- Pin to explicit versions (no `+` wildcards).
- Bump AndroidX / Material together — they share a release cadence.
- Run `./gradlew dependencies` and check for conflicts before adding any new library.
