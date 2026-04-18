# AgriCulture — Code Conventions

**Date:** 2026-04-18  
**Generated from:** build.gradle.kts, settings.gradle.kts, SPEC.md

---

## Java Code Style

- **Indent:** 4 spaces (no tabs)
- **Braces:** Allman-adjacent — opening brace on same line as declaration
- **Line length:** 120 characters max
- **Imports:** No wildcard imports (`import java.util.*` is banned); order: Android → AndroidX → third-party → java.*
- **Annotations:** One per line, above the declaration

---

## Naming Conventions

| Element | Convention | Example |
|---|---|---|
| Package | all lowercase, reverse domain | `edu.cnm.deepdive.agriculture.model` |
| Activity | `PascalCase` + `Activity` suffix | `LabListActivity` |
| Adapter | `PascalCase` + `Adapter` suffix | `LabAdapter` |
| ViewHolder | inner class named `ViewHolder` or `LabViewHolder` | `LabAdapter.LabViewHolder` |
| Repository | `PascalCase` + `Repository` suffix | `LabRepository` |
| POJO/Model | `PascalCase`, noun | `SoilLab` |
| Interface | `PascalCase` + descriptive suffix | `OnLabClickListener` |
| Intent extra keys | `EXTRA_` + `UPPER_SNAKE` constant on the destination Activity | `LabDetailActivity.EXTRA_LAB_ID` |
| Layout files | snake_case, type prefix | `activity_lab_list.xml`, `item_lab_card.xml` |
| View ids | type prefix + `CamelCase` | `tvLabName`, `btnWebsite`, `chipPap` |
| Drawables | snake_case, type prefix | `bg_tag_green.xml`, `ic_lab.xml` |
| String resources | snake_case, module prefix | `lab_name`, `btn_visit_website` |
| Dimen resources | snake_case | `card_margin_horizontal`, `tag_padding` |

---

## Package Structure

```
edu.cnm.deepdive.agriculture
├── model/
│   └── SoilLab.java
├── data/
│   └── LabRepository.java
├── ui/
│   ├── MainActivity.java
│   ├── LabListActivity.java
│   ├── LabDetailActivity.java
│   └── LabAdapter.java
```

- `model/` — pure Java POJOs; zero Android imports
- `data/` — repositories and future DAOs; zero UI imports  
- `ui/` — Activities and Adapters; may import from `model/` and `data/`

---

## Gradle Conventions

- Dependencies use double-quoted strings: `implementation("group:artifact:version")`
- Versions pinned explicitly — no `+` or `latest.release`
- `build.gradle.kts` only — no Groovy DSL

---

## Git Conventions

- Branch naming: `feature/short-description`, `fix/short-description`
- Commit messages: imperative mood, present tense — `Add SoilLab POJO`, `Wire LabAdapter to RecyclerView`
- One logical change per commit; don't bundle unrelated layout and logic changes

---

## XML Layout Conventions

- Root layout is always a `CoordinatorLayout` for Activity layouts (supports `AppBarLayout` scroll behavior)
- Item layouts root is `MaterialCardView`
- Use `ConstraintLayout` inside cards for precise positioning
- All `android:id` values use `@+id/` prefix and match the naming table above
- `tools:` namespace attributes allowed freely for preview — they are stripped from the build
