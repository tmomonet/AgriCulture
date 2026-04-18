# AgriCulture — Module 1: Soil Testing Lab Directory

## 1. App Identity

**App name:** AgriCulture  
**Value proposition:** A native Android reference tool that connects New Mexico gardeners, farmers, and agricultural professionals to accredited soil testing laboratories so they can make data-driven decisions about soil amendments and crop inputs.

---

## 2. Target Personas

| Persona | Description | Primary Need |
|---|---|---|
| **Homeowner Gardener** | Suburban or rural resident managing a yard, raised bed, or small garden. Limited soil science background. | Find a nearby, affordable lab that offers a basic lawn/garden test with plain-language recommendations. |
| **Small-Scale Farmer** | Operates ≤500 acres of row crops, pasture, or specialty crops in the Southwest. Cost-conscious; needs crop-specific data. | Identify accredited labs offering complete soil profiles, SAR/salinity analysis, and crop-specific fertility recommendations. |
| **NM Agricultural Professional** | Extension agent, consultant, or agronomist advising multiple clients across New Mexico. | Quickly compare accreditation tiers (PAP vs NAPT), service offerings, and contact details. |

---

## 3. User Stories — Soil Lab Directory Module

| ID | As a… | I want to… | So that… | Priority |
|---|---|---|---|---|
| US-01 | Homeowner Gardener | see a scrollable list of all recommended soil labs | I can browse options at a glance | Must |
| US-02 | Homeowner Gardener | tap a lab card to view its full detail screen | I can read the homeowner-specific tests offered | Must |
| US-03 | Homeowner Gardener | tap a "Visit Website" button on the card or detail screen | I can navigate directly to the lab's order page | Must |
| US-04 | Small-Scale Farmer | see farmer-specific test packages clearly labeled | I know which labs serve my needs | Must |
| US-05 | Small-Scale Farmer | see the accreditation badge (PAP, NAPT, or both) prominently | I can trust the lab's analytical rigor | Must |
| US-06 | Agricultural Professional | call a lab directly from the detail screen | I can confirm turnaround times without leaving the app | Should |
| US-07 | Agricultural Professional | see both homeowner and farmer test packages on the detail screen | I can recommend the right package to a client | Must |
| US-08 | Any user | see the lab's city and state on the list card | I can judge shipping distance at a glance | Must |
| US-09 | Any user | tap the address on the detail screen to open it in Maps | I can get directions or verify the lab's location | Should |

---

## 4. Interaction Flows

### 4.1 Primary Flow — Browse to Contact

```
[MainActivity — Dashboard]
     │  User taps "Soil Testing Labs" card
     ▼
[LabListActivity]
  RecyclerView of SoilLab cards
  • Lab name (bold)
  • City, State
  • Accreditation badge chips (PAP / NAPT)
  • "Homeowner" / "Farmer" availability tags
  • "Visit Website" button  ──────────────────────► [External Browser]
     │
     │  User taps card body
     ▼
[LabDetailActivity]
  Full lab profile:
  • Name, full address (tap → Maps)
  • Phone (tap → ACTION_DIAL)
  • Accreditation string
  • Homeowner tests section
  • Farmer tests section (or "Same as homeowner" if null)
  • "Visit Website" button ───────────────────────► [External Browser]
```

### 4.2 Secondary Flow — Direct Phone Call

```
[LabDetailActivity]
     │  User taps phone TextView
     ▼
ACTION_DIAL Intent  →  System Phone Dialer
```

### 4.3 Secondary Flow — Open in Maps

```
[LabDetailActivity]
     │  User taps address TextView
     ▼
geo:0,0?q=<urlEncoded full address>  →  Maps app (or chooser)
```

Address is formatted as a single query string:  
`streetAddress + ", " + addressLine2 + ", " + city + ", " + state + " " + zip`  
(addressLine2 is omitted from the string when null/empty.)

### 4.4 Navigation

- `MainActivity` is the launcher Activity; it hosts a dashboard of module entry points.
- Up/Back on `LabListActivity` returns to `MainActivity`.
- Up/Back on `LabDetailActivity` returns to `LabListActivity`.
- No bottom nav or drawer needed for Module 1; future modules are added as additional dashboard cards in `MainActivity`.

---

## 5. Data Model

### `SoilLab.java` — POJO

```java
public class SoilLab {

    private int id;                  // 1–9, used for Intent passing; stable across sessions
    private String name;             // "Ward Laboratories"
    private String websiteUrl;       // "http://www.wardlab.com"
    private String streetAddress;    // "4007 Cherry Ave"
    private String addressLine2;     // "PO Box 370" — null if not applicable
    private String city;             // "Kearney"
    private String state;            // "NE"
    private String zip;              // "68848"
    private String phone;            // "800-887-7645"
    private String accreditation;    // "NAPT Soil, Plant and Water"
    private String homeownerTests;   // full homeowner recommendation string; never null
    private String farmerTests;      // full farmer recommendation string; null → same as homeowner

    // --- Constructors ---

    public SoilLab() {}

    public SoilLab(int id, String name, String websiteUrl,
                   String streetAddress, String addressLine2,
                   String city, String state, String zip, String phone,
                   String accreditation,
                   String homeownerTests, String farmerTests) {
        this.id = id;
        this.name = name;
        this.websiteUrl = websiteUrl;
        this.streetAddress = streetAddress;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;
        this.accreditation = accreditation;
        this.homeownerTests = homeownerTests;
        this.farmerTests = farmerTests;
    }

    // --- Derived helpers (not stored fields) ---

    public boolean isPap() {
        return accreditation != null && accreditation.contains("PAP");
    }

    public boolean isNapt() {
        return accreditation != null && accreditation.contains("NAPT");
    }

    /** Returns farmerTests, or homeownerTests if farmerTests is null. */
    public String getEffectiveFarmerTests() {
        return farmerTests != null ? farmerTests : homeownerTests;
    }

    /** True when farmer package is identical to homeowner (null sentinel). */
    public boolean isFarmerSameAsHomeowner() {
        return farmerTests == null;
    }

    /**
     * Formats the full address as a single Maps query string.
     * addressLine2 is omitted when null or blank.
     */
    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder(streetAddress);
        if (addressLine2 != null && !addressLine2.isEmpty()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append(", ").append(city)
          .append(", ").append(state)
          .append(" ").append(zip);
        return sb.toString();
    }

    // --- Getters / Setters ---
    // getId(), getName(), getWebsiteUrl(), getStreetAddress(), getAddressLine2(),
    // getCity(), getState(), getZip(), getPhone(), getAccreditation(),
    // getHomeownerTests(), getFarmerTests()
    // + matching setters
}
```

### Seed Data Notes

- Lab 1 (American Agricultural): `addressLine2 = "PO Box 370"`
- Lab 2 (Inter Ag): `farmerTests = null` (packages are identical; display "Same as homeowner tests")
- Lab 4 (CSU): accreditation string stored as `"PAP/NAPT Soil Program"` — the `"4x/yr"` proficiency frequency qualifier is preserved in the raw `accreditation` field and will display verbatim on the detail screen
- All other labs: `addressLine2 = null`

### `LabRepository.java`

Plain Java class with `static List<SoilLab> getAll()` and `static SoilLab getById(int id)`.  
No database for Module 1. Swap `getAll()` for a Room DAO in a future sprint if favorites are added.

---

## 6. RecyclerView Architecture

### 6.1 Classes

| Class | Responsibility |
|---|---|
| `LabListActivity` | Hosts the `RecyclerView`; calls `LabRepository.getAll()`; constructs `LabAdapter`; handles `OnLabClickListener` callback by starting `LabDetailActivity` with the lab's `id` as an Intent extra |
| `LabAdapter` | Extends `RecyclerView.Adapter<LabAdapter.LabViewHolder>`; binds `SoilLab` to view holders; fires `OnLabClickListener` on card body clicks; handles website button clicks internally |
| `LabAdapter.LabViewHolder` | Extends `RecyclerView.ViewHolder`; holds typed references to all child views |
| `LabDiffCallback` | Extends `DiffUtil.ItemCallback<SoilLab>`; compares by `id` for identity, full field equality for content; required now so filtering (SG-01/02) can be added without restructuring the adapter |

### 6.2 Intent Passing — List → Detail

```java
// In LabListActivity (OnLabClickListener implementation):
Intent intent = new Intent(this, LabDetailActivity.class);
intent.putExtra(LabDetailActivity.EXTRA_LAB_ID, lab.getId());
startActivity(intent);

// In LabDetailActivity.onCreate():
int labId = getIntent().getIntExtra(EXTRA_LAB_ID, -1);
SoilLab lab = LabRepository.getById(labId);
```

`SoilLab` stays in-memory in the repository; only the primitive `int` crosses the Activity boundary.

### 6.3 `LabViewHolder` — View References

```java
static class LabViewHolder extends RecyclerView.ViewHolder {
    TextView  tvLabName;
    TextView  tvCityState;
    Chip      chipPap;
    Chip      chipNapt;
    TextView  tvHomeownerTag;
    TextView  tvFarmerTag;
    Button    btnWebsite;
}
```

### 6.4 Adapter Interface

```java
public interface OnLabClickListener {
    void onLabClick(SoilLab lab);   // card body tap → navigate to detail
}
```

Website button clicks are handled inside `onBindViewHolder` directly (open browser); they do not bubble to `OnLabClickListener`.

---

## 7. XML Layout Structure

### 7.1 `activity_main.xml` — Dashboard

```
CoordinatorLayout
 └─ AppBarLayout
 │   └─ MaterialToolbar  (title: "AgriCulture")
 └─ ScrollView
     └─ LinearLayout  orientation="vertical"  padding="16dp"
         └─ MaterialCardView  id="cardSoilLabs"
              • Icon + "Soil Testing Labs" title
              • Subtitle: "Find accredited labs near you"
              • clickListener → startActivity(LabListActivity)
         (future module cards added here)
```

### 7.2 `activity_lab_list.xml`

```
CoordinatorLayout
 └─ AppBarLayout
 │   └─ MaterialToolbar  (title: "Soil Testing Labs", Up navigation → MainActivity)
 └─ RecyclerView
      android:id="@+id/recyclerViewLabs"
      LinearLayoutManager (vertical)
      8dp vertical item spacing via ItemDecoration
```

### 7.3 `item_lab_card.xml` — List Item Card

```
MaterialCardView
  cardElevation="2dp"  cornerRadius="8dp"  margin="8dp horizontal, 4dp vertical"
  clickListener: fires OnLabClickListener (card body → detail screen)
 └─ ConstraintLayout  padding="12dp"
     ├─ TextView  id="tvLabName"
     │    textAppearance="?attr/textAppearanceTitleMedium"
     │    constrain: top|start
     │
     ├─ TextView  id="tvCityState"
     │    textAppearance="?attr/textAppearanceBodySmall"
     │    textColor="?attr/colorOnSurfaceVariant"
     │    constrain: below tvLabName
     │
     ├─ ChipGroup  id="chipGroupAccreditation"
     │    constrain: below tvCityState
     │    ├─ Chip  id="chipPap"   text="PAP"   GONE when !isPap()
     │    └─ Chip  id="chipNapt"  text="NAPT"  GONE when !isNapt()
     │
     ├─ LinearLayout  id="llTags"  orientation="horizontal"  gap="4dp"
     │    constrain: below chipGroupAccreditation
     │    ├─ TextView  id="tvHomeownerTag"  text="Homeowner"
     │    │    background: @drawable/bg_tag_green  (rounded, filled)
     │    └─ TextView  id="tvFarmerTag"     text="Farmer"
     │         background: @drawable/bg_tag_amber  (rounded, filled)
     │         GONE when farmerTests == null (same-as-homeowner case has both services)
     │         Note: all 9 current labs offer both; GONE guard is defensive
     │
     └─ Button  id="btnWebsite"
          style="@style/Widget.Material3.Button.TextButton"
          text="Visit Website"
          constrain: end|bottom
          clickListener: ACTION_VIEW Intent with lab.getWebsiteUrl()
                         (handled in adapter, stops event propagation to card)
```

### 7.4 `activity_lab_detail.xml`

```
CoordinatorLayout
 └─ AppBarLayout
 │   └─ MaterialToolbar  (title = lab name; Up navigation → LabListActivity)
 └─ ScrollView
     └─ LinearLayout  orientation="vertical"  padding="16dp"  gap="8dp"
         ├─ TextView  id="tvDetailAddress"
         │    Body Medium
         │    text = lab.getFormattedAddress()  (multi-line: street / line2 if present / city, state zip)
         │    clickListener: geo:0,0?q=<urlEncoded address> → Maps chooser
         │
         ├─ TextView  id="tvDetailPhone"
         │    Body Medium
         │    textColor="?attr/colorPrimary"  (looks tappable)
         │    clickListener: ACTION_DIAL Intent
         │
         ├─ TextView  id="tvDetailAccred"
         │    Body Small, colorOnSurfaceVariant
         │    text = "Accreditation: " + lab.getAccreditation()
         │
         ├─ View  (horizontal divider)
         │
         ├─ TextView  "Homeowner Tests"  textAppearance TitleSmall
         ├─ TextView  id="tvHomeownerTests"  Body Medium
         │
         ├─ View  (horizontal divider)
         │
         ├─ TextView  "Farmer Tests"  textAppearance TitleSmall
         ├─ TextView  id="tvFarmerTests"
         │    Body Medium
         │    text = farmerTests != null ? farmerTests : "Same as homeowner tests"
         │    textColor = colorOnSurfaceVariant when showing fallback
         │
         └─ Button  id="btnVisitWebsite"
              style filled, layout_width="match_parent"
              text="Visit Website"
              clickListener: ACTION_VIEW Intent
```

---

## 8. Tech Stack & Dependencies

### Language & SDK

| Setting | Value |
|---|---|
| Language | Java |
| Minimum SDK | API 26 (Android 8.0) |
| Target SDK | API 35 |
| Build system | Gradle with Kotlin DSL (`build.gradle.kts`) |

### `build.gradle.kts` — Dependencies

```kotlin
dependencies {
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.12.0")  // MaterialCardView, Chip, Toolbar
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
```

`CardView` is not listed separately — `MaterialCardView` from the Material library supersedes it.

### `AndroidManifest.xml` Notes

- No `INTERNET` permission needed — `ACTION_VIEW` and `ACTION_DIAL` delegate to external apps.
- `MainActivity` is the launcher Activity (`intent-filter` with `MAIN` + `LAUNCHER`).
- `LabListActivity` and `LabDetailActivity` declared with `android:parentActivityName` for Up navigation.

### Intents Used

| Trigger | Intent | Notes |
|---|---|---|
| "Visit Website" button | `ACTION_VIEW`, `Uri.parse(websiteUrl)` | Both list card and detail screen |
| Phone tap | `ACTION_DIAL`, `Uri.parse("tel:" + phone)` | Opens dialer prefilled; no CALL_PHONE permission |
| Address tap | `ACTION_VIEW`, `Uri.parse("geo:0,0?q=" + Uri.encode(getFormattedAddress()))` | Falls back to chooser if no Maps app installed |

---

## 9. Stretch Goals

| ID | Feature | Notes |
|---|---|---|
| SG-01 | **Search bar** | `SearchView` in toolbar; filters adapter in real-time via `LabDiffCallback` (already specced) |
| SG-02 | **Filter chips** | "PAP only" / "NAPT only" / "Homeowner" / "Farmer" row above RecyclerView |
| SG-03 | **Map pin on detail** | Google Maps Static API thumbnail; requires INTERNET permission and API key |
| SG-04 | **Favorites** | Room database; star FAB on detail screen; "Favorites" section in dashboard |
| SG-05 | **Share lab** | `ACTION_SEND` exports name + phone + URL as plain text |
| SG-06 | **Dark mode** | Material3 dynamic color; no extra code if `?attr/` tokens used throughout |
| SG-07 | **Offline graceful degradation** | Detect no network; disable/gray website button; show snackbar |
| SG-08 | **Additional modules** | Pest ID, weather, market prices — each gets a new dashboard card in `MainActivity` |
