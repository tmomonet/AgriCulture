# AgriCulture — Project Constitution

**Date:** 2026-04-18  
**Project:** AgriCulture  
**Platform:** Native Android (Java + XML)  
**Group:** edu.cnm.deepdive

---

## Governing Principles

### 1. Single Responsibility
Every class does one thing. Activities orchestrate; Adapters bind; Repositories supply data. A class that fetches, formats, and displays data is three classes waiting to be split.

### 2. Explicit Over Clever
Prefer readable Java over terse tricks. A clear `if`/`else` beats a chained ternary. Descriptive variable names beat comments explaining a cryptic one-liner.

### 3. No Premature Abstraction
Three similar lines of code is not a pattern to extract. Extract only when a fourth use case arrives or when the duplication creates a real maintenance burden. Don't design for hypothetical modules.

### 4. Data at the Boundary
Validate and sanitize at system boundaries only: user input, external URLs, Intent extras. Trust internal data passed between your own classes without defensive null-checks on every access.

### 5. Dependency Direction
Dependencies point inward:  
`Activity → Adapter → ViewHolder`  
`Activity → Repository → POJO`  
Nothing in the data layer (POJO, Repository) may import Android UI classes.

---

## Architectural Constraints

- **No ViewModel or LiveData for Module 1.** The data is static and synchronous. Introduce ViewModel only when async data loading (Room, network) is added in a future module.
- **No anonymous inner classes for click listeners.** Use named interface callbacks or method references to keep `onBindViewHolder` readable.
- **Activity → Detail navigation via primitive extras only.** Pass `int id` across Activity boundaries; never pass a POJO directly (no Parcelable in Module 1).
- **XML layouts use Material3 components** (`MaterialCardView`, `MaterialToolbar`, `Chip`) and `?attr/` color tokens. No hardcoded color hex values in layout files.

---

## Enforcement

These principles are enforced during `/specswarm:ship` quality gates and code review. Violations must be resolved before merge.
