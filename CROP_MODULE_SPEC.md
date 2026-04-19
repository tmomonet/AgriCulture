# CROP_MODULE_SPEC.md
## AgriCulture — Crop Recommendation Module
**Branch:** `002-crop-recommendation`  
**Base package:** `edu.cnm.deepdive.agriculture`  
**Date:** 2026-04-19  
**Last revised:** 2026-04-19 — API audit; USGS surface stations confirmed streamflow-only; groundwater split to well toggle; NOAA CDO added for climate context

---

## 1. Value Proposition

### Homeowner voice
> "I have a backyard garden and no idea what will actually grow in my area. I don't want to waste money on seeds that fail because my water is too salty or my soil's wrong. This tells me in plain English what to plant and what to avoid — no lab degree required."

### Farmer voice
> "I'm scouting land or planning next season. I need a quick sanity check on whether my region's water quality and soil profile can support the crops I'm considering, and I want to know what amendments I'd need if conditions are borderline."

---

## 2. User Stories

### Homeowner
| # | As a homeowner… | I want to… | So that… |
|---|---|---|---|
| H1 | with a garden in Central NM | pick my region from a list | the app uses data relevant to where I actually live |
| H2 | who doesn't have soil test results | see sensible defaults for my region | I can still get a recommendation without visiting a lab |
| H3 | looking at crop results | see clear green/amber/red cards for each crop | I instantly know what's likely to thrive vs. fail |
| H4 | who wants to understand why | read a one-sentence plain-English explanation per crop | I can make an informed decision without jargon |
| H5 | in a rural area with spotty connectivity | use the app offline | I still get region-average based guidance with no internet |

### Farmer
| # | As a farmer… | I want to… | So that… |
|---|---|---|---|
| F1 | planning next season's crop mix | fetch the past year of USGS water data for my region | my recommendation reflects actual recent conditions, not guesses |
| F2 | who has lab results | enter my actual pH, SAR, and organic matter values | the recommendation uses my real data, not regional averages |
| F3 | considering a field in a different NM region | switch between regions instantly | I can compare suitability across locations |
| F4 | who got a CAUTION result | read what specific amendment addresses the problem | I know what to fix before planting |
| F5 | building on the Three Sisters tradition | see Squash evaluated in its companion-planting context | I understand how it fits alongside corn and beans |

---

## 3. Data Models

### 3.1 `NmRegion` enum
**Package:** `edu.cnm.deepdive.agriculture.model`

Each region carries three distinct site IDs — surface flow gauge (confirmed streamflow-only), groundwater well (for the optional toggle), and NOAA station (for climate context).

```java
public enum NmRegion {
    //              display           USGS surface gauge   NOAA GHCND station *  pH   SAR   OM%  defaultGW(ft) defaultCond(uS/cm) defaultFlow(cfs) annualPrecip(in)
    NORTH  ("North NM",  "08313000", "GHCND:USW00023049", 6.8,  3.0, 2.1,  18,  320,  950, 12.0),
    CENTRAL("Central NM","08330000", "GHCND:USW00023050", 7.4,  8.0, 0.9,  35,  680, 1100,  9.5),
    SOUTH  ("South NM",  "08362500", "GHCND:USW00023044", 7.8, 14.0, 0.6,  55, 1400,  580,  8.0),
    EAST   ("East NM",   "08378500", "GHCND:USW00023009", 7.1,  5.0, 1.4,  42,  820,  140, 11.5),
    WEST   ("West NM",   "09386900", "GHCND:USW00023066", 7.6, 11.0, 0.8,  60, 1100,   55,  7.5);

    public final String displayName;
    public final String usgsSurfaceSiteId;   // surface flow gauge — param 00060 confirmed available
    public final String noaaStationId;       // * nearest GHCND station — verify before release
    public final double defaultPh;
    public final double defaultSar;
    public final double defaultOrganicMatter;  // percent
    public final double defaultGwDepth;        // ft — always used (GW toggle deferred post-demo)
    public final double defaultConductance;    // uS/cm — always a default; no live source confirmed
    public final double defaultStreamflow;     // cfs — offline fallback only
    public final double annualPrecipIn;        // hardcoded NM historical average — no live fetch needed

    NmRegion(String displayName, String usgsSurfaceSiteId, String noaaStationId,
             double defaultPh, double defaultSar, double defaultOrganicMatter,
             double defaultGwDepth, double defaultConductance,
             double defaultStreamflow, double annualPrecipIn) {
        this.displayName = displayName;
        this.usgsSurfaceSiteId = usgsSurfaceSiteId;
        this.noaaStationId = noaaStationId;
        this.defaultPh = defaultPh;
        this.defaultSar = defaultSar;
        this.defaultOrganicMatter = defaultOrganicMatter;
        this.defaultGwDepth = defaultGwDepth;
        this.defaultConductance = defaultConductance;
        this.defaultStreamflow = defaultStreamflow;
        this.annualPrecipIn = annualPrecipIn;
    }
}
```

> **Note — NOAA station IDs marked `*`:** Approximate placeholders — verify before release via `https://www.ncdc.noaa.gov/cdo-web/api/v2/stations?locationid=FIPS:35&datasetid=GHCND&limit=100` (token required). Wrong station ID causes `ClimateContext.unavailable()` — graceful fallback, not a crash.
>
> **GW well IDs removed:** Groundwater toggle deferred to post-demo branch `003-groundwater-toggle`. `defaultGwDepth` is always used; no live GW fetch in MVP.

---

### 3.2 `CropType` enum
**Package:** `edu.cnm.deepdive.agriculture.model`

```java
public enum CropType {
    CHILE("Chile Pepper"),
    CORN("Corn"),
    BEANS("Pinto Beans"),
    SQUASH("Squash");

    public final String displayName;

    CropType(String displayName) {
        this.displayName = displayName;
    }
}
```

---

### 3.3 `CropResult` POJO
**Package:** `edu.cnm.deepdive.agriculture.model`

```java
public class CropResult {
    public enum Status { SUITABLE, CAUTION, NOT_RECOMMENDED }

    private final CropType cropType;
    private final Status status;
    private final String headline;          // e.g. "Good conditions for Chile Pepper."
    private final List<String> details;     // one entry per violated parameter + possible consequence

    public CropResult(CropType cropType, Status status, String headline, List<String> details) {
        this.cropType = cropType;
        this.status = status;
        this.headline = headline;
        this.details = Collections.unmodifiableList(details);
    }

    // getters only — immutable result object
    public CropType     getCropType() { return cropType; }
    public Status       getStatus()   { return status; }
    public String       getHeadline() { return headline; }
    public List<String> getDetails()  { return details; }
}
```

**SUITABLE case:** `details` contains exactly one entry describing what's going well (no violations to list).  
**CAUTION / NOT_RECOMMENDED:** one entry per violated parameter, each stating the violation and its agricultural consequence.

---

### 3.4 `SoilContext` POJO
**Package:** `edu.cnm.deepdive.agriculture.model`

```java
public class SoilContext {
    private double ph;
    private double sar;              // Sodium Adsorption Ratio
    private double organicMatter;   // percent

    // Built from NmRegion defaults or user override
    public static SoilContext fromRegion(NmRegion region) {
        return new SoilContext(
            region.defaultPh,
            region.defaultSar,
            region.defaultOrganicMatter
        );
    }

    public SoilContext(double ph, double sar, double organicMatter) {
        this.ph = ph;
        this.sar = sar;
        this.organicMatter = organicMatter;
    }

    // getters + setters (setters support stretch: manual override)
}
```

---

### 3.5 `WaterContext` POJO
**Package:** `edu.cnm.deepdive.agriculture.model`

> **API audit result (2026-04-19):** Live API calls to USGS confirmed that the five NM surface-flow gauges return **only param 00060 (streamflow)**. Params 00095 (conductance) and 72019 (groundwater depth) are not reported at these stations. The design below reflects confirmed data availability:

| Field | Source | Live? |
|-------|--------|-------|
| `avgStreamflow` | USGS surface gauge, param 00060 | Yes — always live |
| `avgGroundwaterDepth` | USGS GW well, param 72019 | Yes — only when toggle ON and well responds |
| `avgConductance` | Regional default table | No — no live source at these stations |
| `streamflowSource` | Enum label | — |
| `gwSource` | Enum label | — |

```java
public class WaterContext {

    public enum DataSource { USGS_LIVE, REGIONAL_DEFAULT, OFFLINE_DEFAULT }

    // Confirmed live: USGS surface gauge param 00060 (cfs)
    private final double avgStreamflow;
    private final DataSource streamflowSource;

    // Optional live: USGS groundwater well param 72019 (ft)
    // Populated only when groundwater toggle is ON and well fetch succeeds
    private final double avgGroundwaterDepth;
    private final DataSource gwSource;

    // Always a regional default — no live source at these surface stations
    private final double avgConductance;  // uS/cm

    public WaterContext(double avgStreamflow, DataSource streamflowSource,
                        double avgGroundwaterDepth, DataSource gwSource,
                        double avgConductance) {
        this.avgStreamflow = avgStreamflow;
        this.streamflowSource = streamflowSource;
        this.avgGroundwaterDepth = avgGroundwaterDepth;
        this.gwSource = gwSource;
        this.avgConductance = avgConductance;
    }

    /** Convenience factory: full offline / no-toggle path */
    public static WaterContext fromRegionDefaults(NmRegion region) {
        return new WaterContext(
            region.defaultStreamflow, DataSource.OFFLINE_DEFAULT,
            region.defaultGwDepth,   DataSource.REGIONAL_DEFAULT,
            region.defaultConductance
        );
    }

    // getters only — immutable
}
```

**Streamflow as surface water proxy:**  
Since streamflow (00060) is the only confirmed live parameter, `CropEvaluator` uses it as a **water availability index** with NM-appropriate thresholds:

| Streamflow (cfs) | Availability label | Crop impact |
|------------------|--------------------|-------------|
| > 500            | High               | Ample irrigation water |
| 100–500          | Moderate           | Normal season |
| 10–100           | Low                | Drought stress — CAUTION trigger |
| < 10             | Critical           | Near-dry — NOT_RECOMMENDED trigger for water-intensive crops |

These brackets apply relative to each station's long-run mean; for MVP, use absolute cfs values above.

---

### 3.6 `ClimateContext` POJO  
**Package:** `edu.cnm.deepdive.agriculture.model`

NOAA CDO data (requires user to obtain a free API token at `https://www.ncdc.noaa.gov/cdo-web/token`). Adds precipitation and temperature signals that complement water quality for crop suitability.

```java
public class ClimateContext {

    public enum DataSource { NOAA_LIVE, NOT_AVAILABLE }

    // avgAnnualPrecipIn is NOT fetched — read from NmRegion.annualPrecipIn instead
    private final double avgJulyMaxTempF;  // °F, GHCND param TMAX for July (peak NM heat)
    private final double avgLastFrostDoy;  // day-of-year for last spring frost (GHCND TMIN < 32°F)
    private final DataSource source;

    public ClimateContext(double avgJulyMaxTempF, double avgLastFrostDoy, DataSource source) { ... }

    /** Returned when NOAA token is absent or fetch fails — evaluator skips climate checks */
    public static ClimateContext unavailable() {
        return new ClimateContext(0, 0, DataSource.NOT_AVAILABLE);
    }

    public boolean isAvailable() { return source == DataSource.NOAA_LIVE; }
    // getters only
}
```

**NOAA CDO fetch parameters:**
- Endpoint: `GET https://www.ncdc.noaa.gov/cdo-web/api/v2/data`
- Header: `Token: <user_token>`
- `datasetid=GHCND`, `stationid={region.noaaStationId}`
- `datatypeid=TMAX,TMIN` — PRCP dropped; annual precip read from `NmRegion.annualPrecipIn`
- `startdate={prior year}-01-01`, `enddate={prior year}-12-31`
- `limit=1000` — 365 days × 2 datatypes = 730 records max, safely under limit

**Aggregation in `NoaaClimateRepository`:**
- `avgJulyMaxTempF` — mean of TMAX values where month == July ÷ 10 × 1.8 + 32
- `avgLastFrostDoy` — latest day-of-year where TMIN ÷ 10 < 0°C

**NOAA + NmRegion crop relevance for CropEvaluator:**
| Climate signal | Source | Crop impact |
|----------------|--------|-------------|
| `region.annualPrecipIn` < 8 in | `NmRegion` hardcoded | Irrigation-dependent flag in detail text |
| `climate.avgJulyMaxTempF` > 100°F | NOAA live | Heat stress CAUTION for corn and beans |
| `climate.avgLastFrostDoy` after April 15 | NOAA live | Delayed planting note for chile and squash |

---

## 4. CropEvaluator Architecture

**File:** `app/src/main/java/edu/cnm/deepdive/agriculture/service/CropEvaluator.java`

### Public API
```java
public class CropEvaluator {
    /**
     * Evaluates all four crops. ClimateContext is optional — pass
     * ClimateContext.unavailable() when NOAA token is absent.
     */
    public List<CropResult> evaluate(SoilContext soil, WaterContext water,
                                     ClimateContext climate) {
        return List.of(
            evaluateChile(soil, water, climate),
            evaluateCorn(soil, water, climate),
            evaluateBeans(soil, water, climate),
            evaluateSquash(soil, water, climate)
        );
    }
}
```

### Per-crop methods

Each method follows the same pattern:

```java
private CropResult evaluateChile(SoilContext soil, WaterContext water,
                                  ClimateContext climate) {
    CropResult.Status status = determineStatusChile(soil, water, climate);
    return new CropResult(
        CropType.CHILE,
        status,
        generateHeadline(CropType.CHILE, status),
        generateDetail(CropType.CHILE, status, soil, water, climate)
    );
}
```

### `determineStatus*()` threshold rules

> **Data source reminder:**
> - `soil.ph`, `soil.sar` — regional defaults or user override
> - `water.avgConductance` — always a regional default (no live source)
> - `water.avgGroundwaterDepth` — live from GW well if toggle ON, else regional default
> - `water.avgStreamflow` — live from USGS 00060 (primary live signal)
> - `climate.*` — live from NOAA GHCND if token present, else skipped

*Priority across all crops: NOT_RECOMMENDED > CAUTION > SUITABLE. Evaluate worst condition first.*

**Boundary convention (applies to every threshold in every crop):**  
Suitable ranges are **lower-inclusive, upper-exclusive** — `[low, high)`.  
A value exactly at the upper edge of a SUITABLE range falls into CAUTION. A value exactly at the upper edge of a CAUTION range falls into NOT_RECOMMENDED.  
Example: pH 7.5 is CAUTION for Chile (not SUITABLE), pH 8.0 is NOT_RECOMMENDED (not CAUTION).

> **Toggle rule (applies to all crops):**
> - `streamflow` thresholds are **always evaluated** regardless of toggle state.
> - `groundwater` depth threshold is evaluated **only when the GW toggle is ON and a live or default value is available**. When toggle is OFF, the groundwater depth criterion is skipped (treated as passing) for all crops.

#### Chile Pepper
```
SUITABLE        pH 6.0–7.5  AND  SAR < 8    AND  conductance < 1500
                AND  streamflow > 100 cfs
                AND  (GW toggle OFF  OR  groundwater < 50 ft)
CAUTION         pH 7.5–8.0  OR   SAR 8–12   OR   conductance 1500–2500
                OR   streamflow 10–100 cfs
                OR   (GW toggle ON   AND  groundwater ≥ 50 ft)
NOT_RECOMMENDED pH > 8.0    OR   SAR > 12   OR   conductance > 2500
                OR   streamflow < 10 cfs
+ CLIMATE (when available):
  CAUTION+      July max temp > 100°F → append heat-stress sentence to detail; status stays CAUTION or worsens, never improves
```

#### Corn
```
SUITABLE        pH 6.0–7.0  AND  SAR < 6    AND  conductance < 1200
                AND  streamflow > 100 cfs
                AND  (GW toggle OFF  OR  groundwater < 60 ft)
CAUTION         pH 7.0–7.8  OR   SAR 6–10   OR   conductance 1200–2000
                OR   streamflow 10–100 cfs
                OR   (GW toggle ON   AND  groundwater ≥ 60 ft)
NOT_RECOMMENDED pH > 7.8    OR   SAR > 10   OR   conductance > 2000
                OR   streamflow < 10 cfs
+ CLIMATE (when available):
  CAUTION+      July max temp > 100°F → append heat-stress sentence; status stays CAUTION or worsens
  CAUTION+      last frost after April 15 → append delayed-planting sentence; status stays CAUTION or worsens
```

#### Pinto Beans
```
SUITABLE        pH 6.0–7.5  AND  SAR < 5    AND  conductance < 1000
                AND  streamflow > 100 cfs
                AND  (GW toggle OFF  OR  groundwater < 50 ft)
CAUTION         pH 7.5–8.0  OR   SAR 5–8    OR   conductance 1000–1800
                OR   streamflow 10–100 cfs
                OR   (GW toggle ON   AND  groundwater ≥ 50 ft)
NOT_RECOMMENDED pH > 8.0    OR   SAR > 8    OR   conductance > 1800
                OR   streamflow < 10 cfs
+ CLIMATE (when available):
  CAUTION+      July max temp > 100°F → append heat-stress sentence; status stays CAUTION or worsens
```

#### Squash
```
SUITABLE        pH 6.0–7.5  AND  SAR < 10   AND  conductance < 1800
                AND  streamflow > 50 cfs
                AND  (GW toggle OFF  OR  groundwater < 70 ft)
CAUTION         pH 7.5–8.2  OR   SAR 10–14  OR   conductance 1800–2800
                OR   streamflow 10–50 cfs
                OR   (GW toggle ON   AND  groundwater ≥ 70 ft)
NOT_RECOMMENDED pH > 8.2    OR   SAR > 14   OR   conductance > 2800
                OR   streamflow < 10 cfs
+ CLIMATE (when available):
  CAUTION+      last frost after May 1 → append frost-risk sentence; status stays CAUTION or worsens
```

> **Note — Squash streamflow:** SUITABLE threshold raised to `> 50 cfs` to close the overlap with CAUTION (`10–50 cfs`) that existed in the prior draft. Squash CAUTION is `10–50 cfs`, NOT_RECOMMENDED is `< 10 cfs`.

### `generateHeadline()` — plain English strings

```java
private String generateHeadline(CropType crop, CropResult.Status status) {
    switch (status) {
        case SUITABLE:         return "Good conditions for " + crop.displayName + ".";
        case CAUTION:          return "Possible with amendments for " + crop.displayName + ".";
        case NOT_RECOMMENDED:  return "Difficult conditions for " + crop.displayName + ".";
        default:               throw new IllegalArgumentException();
    }
}
```

### `generateDetails()` — per-violation bullet list

`generateDetails()` iterates every threshold for the crop, collects each violation into a `List<String>`, and returns it. All violated parameters appear regardless of severity — the list is ordered: NOT_RECOMMENDED triggers first, then CAUTION triggers, then CLIMATE signals.

**For SUITABLE:** return a single-element list with a positive summary.

**Violation sentence format:** `"[Parameter] is [value/condition] → [agricultural consequence]"`

**Per-parameter sentence templates (fill `[value]` at runtime):**

| Parameter | CAUTION sentence | NOT_RECOMMENDED sentence |
|-----------|-----------------|--------------------------|
| pH (high) | "pH [v] is slightly alkaline → may reduce nutrient uptake; consider sulfur amendment." | "pH [v] is too alkaline → severe nutrient lock-out expected." |
| pH (low) | "pH [v] is slightly acidic → monitor micronutrient availability." | "pH [v] is too acidic → toxic aluminum/manganese likely." |
| SAR | "SAR [v] is elevated → sodium may cause soil crusting; gypsum amendment recommended." | "SAR [v] is too high → root structure damage and reduced water uptake expected." |
| Conductance | "Conductance [v] uS/cm is high → salt stress possible; extra irrigation leaching helps." | "Conductance [v] uS/cm exceeds tolerance → germination failure likely." |
| Streamflow | "Streamflow [v] cfs is low → irrigation water may be restricted this season." | "Streamflow [v] cfs is critically low → insufficient surface water for irrigation." |
| Groundwater | "Groundwater at [v] ft is deeper than ideal → irrigation dependency increases." | "Groundwater at [v] ft is too deep to support crop without full irrigation." |
| July heat (climate) | "July highs averaging [v]°F → heat stress risk during pollination/pod set." | — (climate signals max out at CAUTION) |
| Last frost (climate) | "Last frost around day [v] → adjust planting date to avoid frost damage." | — |
| Low precip (climate) | "Annual precip [v] in → fully irrigation-dependent; water availability is critical." | — |

**SUITABLE positive summaries (single-element list):**

| Crop | Positive summary |
|------|-----------------|
| Chile | "pH, salinity, streamflow, and soil sodium are all within ideal range for a productive harvest." |
| Corn | "Soil and water conditions match corn's preference for moderate pH, low salinity, and reliable flow." |
| Beans | "Low salinity and near-neutral pH give pinto beans good conditions for nodule formation and yield." |
| Squash | "Squash is the most salt-tolerant of the Three Sisters and is well-matched to your current conditions." |

**UI rendering:** `item_crop_result.xml` renders `details` as a `TextView` with each entry on its own line prefixed with `•`. No separate bullet `View` needed — newline-joined string passed to a single multi-line `TextView`.

---

## 5. UI Architecture

### 5.1 Fragment Structure
**File:** `ui/crop/CropFragment.java`  
**Layout:** `res/layout/fragment_crop.xml`

```
fragment_crop.xml
├── Spinner (region_spinner)               — "Select your NM region"
│
├── Button (check_conditions_btn)          — "Check My Conditions"
├── ProgressBar (loading_indicator)        — VISIBLE during fetch, GONE otherwise
├── TextView (status_message)              — VISIBLE during fetch, GONE otherwise
│       Observes CropViewModel.getStatusMessage()
│       Text cycles: "Fetching streamflow data…"
│                  → "Fetching groundwater depth…" / "Using regional groundwater estimate…"
│                  → "Fetching climate data…"
│
├── LinearLayout (data_badges_row)         — horizontal row of small chips:
│       Chip: "Flow: USGS live" / "Flow: offline"
│       Chip: "GW: live" / "GW: estimate"  (shown only if toggle ON)
│       Chip: "Climate: NOAA" / "Climate: unavailable"
│
└── RecyclerView (crop_results_list)       — populated after fetch completes
```

### 5.2 Application class
**File:** `AgricultureApp.java` (registered in `AndroidManifest.xml` as `android:name=".AgricultureApp"`)

Repos are singletons created once at app start. ViewModel pulls them from here — no factory needed.

```java
public class AgricultureApp extends Application {
    private WaterRepository waterRepository;
    private ClimateRepository climateRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        waterRepository   = new UsgsWaterRepository();
        climateRepository = new NoaaClimateRepository(this); // needs Context for EncryptedSharedPreferences
    }

    public WaterRepository   getWaterRepository()   { return waterRepository; }
    public ClimateRepository getClimateRepository() { return climateRepository; }
}
```

---

### 5.3 ViewModel
**File:** `ui/crop/CropViewModel.java`

```java
public class CropViewModel extends AndroidViewModel {

    private final WaterRepository   waterRepo;
    private final ClimateRepository climateRepo;
    private final CropEvaluator     evaluator = new CropEvaluator();
    private final ExecutorService   executor  = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<CropResult>> results       = new MutableLiveData<>();
    private final MutableLiveData<Boolean>          loading       = new MutableLiveData<>(false);
    private final MutableLiveData<String>           statusMessage = new MutableLiveData<>();
    private final MutableLiveData<List<String>>     errors        = new MutableLiveData<>();

    // Cache keys: surface siteId → avg cfs, well siteId → avg ft, noaaId → ClimateContext
    private final Map<String, Double>         streamflowCache = new HashMap<>();
    private final Map<String, Double>         gwDepthCache    = new HashMap<>();
    private final Map<String, ClimateContext> climateCache    = new HashMap<>();

    public CropViewModel(Application app) {
        super(app);
        AgricultureApp agApp = (AgricultureApp) app;
        waterRepo   = agApp.getWaterRepository();
        climateRepo = agApp.getClimateRepository();
    }

    public void loadRecommendations(NmRegion region) {
        loading.postValue(true);
        executor.execute(() -> {
            // Each step returns null on failure — regional default used per step.
            // Errors collected into a list and posted once at the end.
            List<String> stepErrors = new ArrayList<>();

                // Step 1 — surface flow (always; check cache first)
                statusMessage.postValue("Fetching streamflow data…");
                Double streamflow = streamflowCache.containsKey(region.usgsSurfaceSiteId)
                    ? streamflowCache.get(region.usgsSurfaceSiteId)
                    : waterRepo.fetchStreamflowAvg(region.usgsSurfaceSiteId, 365);
                WaterContext.DataSource flowSrc;
                if (streamflow != null) {
                    streamflowCache.put(region.usgsSurfaceSiteId, streamflow);
                    flowSrc = WaterContext.DataSource.USGS_LIVE;
                } else {
                    streamflow = region.defaultStreamflow;
                    flowSrc    = WaterContext.DataSource.OFFLINE_DEFAULT;
                    stepErrors.add("Streamflow unavailable — using regional average.");
                }

                // Step 2 — groundwater always uses regional default (GW toggle deferred post-demo)
                statusMessage.postValue("Using regional groundwater estimate…");
                double gwDepth = region.defaultGwDepth;
                WaterContext.DataSource gwSrc = WaterContext.DataSource.REGIONAL_DEFAULT;

                // Step 3 — climate (check cache first; returns unavailable() on failure)
                statusMessage.postValue("Fetching climate data…");
                ClimateContext climate = climateCache.containsKey(region.noaaStationId)
                    ? climateCache.get(region.noaaStationId)
                    : climateRepo.fetch(region.noaaStationId);
                climateCache.put(region.noaaStationId, climate); // unavailable() is cacheable

                WaterContext water = new WaterContext(
                    streamflow, flowSrc, gwDepth, gwSrc, region.defaultConductance);
                SoilContext soil = SoilContext.fromRegion(region);
                results.postValue(evaluator.evaluate(soil, water, climate));
                if (!stepErrors.isEmpty()) {
                    errors.postValue(stepErrors);
                }

            } finally {
                loading.postValue(false);
            }
        });
    }

    public LiveData<List<CropResult>> getResults()       { return results; }
    public LiveData<Boolean>          isLoading()        { return loading; }
    public LiveData<String>           getStatusMessage() { return statusMessage; }
    public LiveData<List<String>>     getErrors()        { return errors; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
```

> `AndroidViewModel` (not `ViewModel`) is used because the Application reference is needed to reach the singletons. No `ViewModelProvider.Factory` required — the default factory handles `AndroidViewModel` automatically.

### 5.3 CropResultAdapter
**File:** `ui/crop/CropResultAdapter.java`  
**Item layout:** `res/layout/item_crop_result.xml`

Each card item:
```
item_crop_result.xml (MaterialCardView)
├── Left color strip (View, 6dp wide)    — green / amber / red via cardBackgroundColor or strokeColor
├── TextView (crop_name)                 — e.g. "Chile Pepper"
├── TextView (status_label)             — "SUITABLE" / "CAUTION" / "NOT RECOMMENDED"
├── TextView (headline_text)
└── TextView (detail_text)
```

Color mapping (Material Design color tokens):
| Status | Strip color | Status label color |
|--------|------------|-------------------|
| SUITABLE | `#2E7D32` (green-800) | `#2E7D32` |
| CAUTION | `#F57F17` (amber-900) | `#F57F17` |
| NOT_RECOMMENDED | `#C62828` (red-800) | `#C62828` |

### 5.5 Loading State

`CropFragment` observes `isLoading()` and applies this state table on every emission:

| `isLoading()` | `region_spinner` | `check_conditions_btn` | `loading_indicator` | `status_message` | `crop_results_list` |
|---|---|---|---|---|---|
| `true`  | disabled | disabled | VISIBLE | VISIBLE | GONE |
| `false` | enabled  | enabled  | GONE    | GONE    | VISIBLE (if results exist) |

Disabling the spinner during load prevents the user from changing inputs mid-fetch. No cancellation logic needed.

### 5.6 Error Display
`CropFragment` observes `getErrors()`. On each non-null emission, it renders one Snackbar whose message is all error strings joined by `"\n"`:

```java
viewModel.getErrors().observe(getViewLifecycleOwner(), errs -> {
    if (errs != null && !errs.isEmpty()) {
        String msg = String.join("\n", errs);
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show();
    }
});
```

A single Snackbar with a multi-line message avoids stacking or dismissal races. `LENGTH_LONG` gives ~3 s — enough to read two lines.

---

## 6. Data Fetch Strategy

### 6.0 Repository Interfaces

```java
// Package: edu.cnm.deepdive.agriculture.data

public interface WaterRepository {
    /**
     * Returns the mean daily streamflow (cfs) over the last {@code days} days
     * for the given USGS surface gauge site ID, or null if the fetch fails.
     */
    Double fetchStreamflowAvg(String siteId, int days);

    /**
     * Returns the mean groundwater depth (ft below surface) over the last
     * {@code days} days for the given USGS groundwater well site ID,
     * or null if the fetch fails or the well reports no data.
     */
    Double fetchGwDepthAvg(String wellId, int days);
}

public interface ClimateRepository {
    /**
     * Returns a populated ClimateContext for the given NOAA GHCND station ID,
     * or ClimateContext.unavailable() if the token is absent, the request fails,
     * or the station returns insufficient data. Never throws.
     */
    ClimateContext fetch(String noaaStationId);
}
```

Both `WaterRepository` methods block the calling thread and return `null` on any failure (network error, HTTP error, empty response, parse error). The ViewModel handles `null` with per-step fallback to regional defaults.

---

### 6.1 USGS Surface Flow (confirmed live — always fetched)
**API:** USGS NWIS Daily Values  
**Base URL:** `https://waterservices.usgs.gov/nwis/dv/`  
**Confirmed available:** param `00060` (discharge, cfs) at all five NM stations

```
GET https://waterservices.usgs.gov/nwis/dv/
    ?format=json
    &sites={region.usgsSurfaceSiteId}
    &parameterCd=00060
    &startDT={today minus 365 days}
    &endDT={today}
    &statCd=00003   (mean)
```

Average all non-null `value` entries. Store result in `WaterContext.avgStreamflow` with `DataSource.USGS_LIVE`.  
On failure: fall back to `region.defaultStreamflow` with `DataSource.OFFLINE_DEFAULT`.

---

### 6.2 USGS Groundwater Depth (optional — gated by toggle)
**API:** USGS NWIS Daily Values, same endpoint  
**Param:** `72019` (depth to water level, ft below land surface)  
**Site:** `region.usgsGwWellId` — a **different site ID** from the surface gauge

```
GET https://waterservices.usgs.gov/nwis/dv/
    ?format=json
    &sites={region.usgsGwWellId}
    &parameterCd=72019
    &startDT={today minus 365 days}
    &endDT={today}
    &statCd=00003
```

Only called when `gwToggleOn == true`. On success: `WaterContext.avgGroundwaterDepth` with `DataSource.USGS_LIVE`.  
On failure or toggle OFF: use `region.defaultGwDepth` with `DataSource.REGIONAL_DEFAULT`.

---

### 6.3 Conductance / Salinity (no live source — always regional default)
Specific conductance (param `00095`) is **not reported** at any of the five NM surface gauges confirmed in the API audit. `WaterContext.avgConductance` is always populated from `region.defaultConductance`.

If future expansion requires live salinity:
- USGS Water Quality Portal: `https://www.waterqualitydata.us/data/Result/search?siteid={site}&characteristicName=Specific+conductance`
- But WQP returns discrete grab samples, not daily averages — requires additional averaging logic

---

### 6.4 NOAA CDO Climate Context (optional — requires user API token)
**API:** NOAA Climate Data Online v2  
**Base URL:** `https://www.ncdc.noaa.gov/cdo-web/api/v2/data`  
**Auth:** Header `Token: <user_supplied_token>` (free registration at ncdc.noaa.gov/cdo-web/token)

```
GET https://www.ncdc.noaa.gov/cdo-web/api/v2/data
    ?datasetid=GHCND
    &stationid={region.noaaStationId}
    &datatypeid=PRCP,TMAX,TMIN
    &startdate={prior year}-01-01
    &enddate={prior year}-12-31
    &limit=1000
    &units=standard
```

**Token storage (MVP):** Token is hardcoded as a constant in `AgricultureApp` for development and portfolio use:

```java
// AgricultureApp.java
static final String NOAA_TOKEN = "your_token_here"; // register free at ncdc.noaa.gov/cdo-web/token
```

`NoaaClimateRepository` receives this string in its constructor and passes it as the `Token` header on every request. If the string is empty or the request returns 400/401, the repo returns `ClimateContext.unavailable()` — no crash.

> **Pre-release note:** Hardcoded token ships in the APK binary. Acceptable for a course/portfolio project. Before any public Play Store release, move to `BuildConfig` field injected from `gradle.properties` (excluded from git via `.gitignore`).

**Aggregation in ClimateRepository:**
- `avgAnnualPrecipIn` — sum all PRCP values ÷ 10 (GHCND stores tenths of mm) × 0.0394 → inches
- `avgJulyMaxTempF` — mean of TMAX values where month == July ÷ 10 × 1.8 + 32 → °F
- `avgLastFrostDoy` — latest day-of-year where TMIN ÷ 10 < 0°C (below 32°F)

---

### 6.5 In-Memory Cache
All three fetches are cached in `CropViewModel` keyed by site ID:

```java
Map<String, Double>         streamflowCache = new HashMap<>();  // siteId → avg cfs
Map<String, Double>         gwDepthCache    = new HashMap<>();  // wellId → avg ft
Map<String, ClimateContext> climateCache    = new HashMap<>();  // noaaId → ClimateContext
```

Cache lives for the ViewModel's lifetime (survives rotation). Re-selecting the same region within a session skips all matching network calls.

---

## 7. Navigation — Dashboard Cards (MVP)

**Bottom nav deferred to post-demo branch `003-bottom-nav-refactor`.** For the Sunday demo, `MainActivity` uses its existing `MaterialCardView` dashboard pattern — zero risk to working code.

Add two cards alongside the existing Soil Labs card:

```
activity_main.xml  (existing ScrollView/LinearLayout)
├── MaterialCardView → LabListActivity       (existing, untouched)
├── MaterialCardView → WaterActivity         (new — wraps WaterFragment)
└── MaterialCardView → CropActivity          (new — wraps CropFragment)
```

**Each new card:**
- Title TextView: "Water Quality" / "Crop Recommendations"
- Subtitle TextView: "USGS streamflow + NOAA climate" / "Suitability for NM crops"
- Icon: `ic_water_drop` / `ic_grass` (vector drawables)
- `setOnClickListener` → `startActivity(new Intent(this, WaterActivity.class))`

**`CropActivity`** and **`WaterActivity`** are thin wrappers:
```java
public class CropActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);  // contains FragmentContainerView hosting CropFragment
    }
}
```

**Post-demo:** Migrate all three to a `BottomNavigationView` in `003-bottom-nav-refactor`. The Fragments themselves need no changes — only the container moves.

---

## 8. Stretch Features (priority order)

### S1 — Manual Soil Input Override
Add a collapsible `TextInputLayout` section above the region spinner:
- Fields: pH, SAR, Organic Matter %
- When populated, `SoilContext` is built from user values instead of regional defaults
- Validation: pH must be 0–14, SAR must be ≥ 0, OM must be 0–100
- Label: "Override with your soil test results (optional)"

### S2 — Claude API Integration
After `CropEvaluator` produces results, pass the `SoilContext`, `WaterContext`, and `List<CropResult>` to a Claude API call:
- Prompt: "Given these NM soil and water conditions [X], provide a 2–3 sentence plain-English explanation for a home gardener about the crop recommendations."
- Display in an expandable card below each crop result labeled "AI Insight"
- Gate behind a "Get AI explanation" button to avoid unnecessary API calls
- Use `claude-haiku-4-5-20251001` for cost efficiency on per-tap calls

### S3 — Seasonal Adjustment
Map `LocalDate.now().getMonth()` to NM planting windows:
- Chile: plant March–May, harvest August–October
- Corn: plant April–June
- Beans: plant May–July
- Squash: plant April–June
Add a banner on each card: "Planting window: [date range]" and a note if currently outside the window.

### S4 — Expand Crop List
Add to `CropType` enum: `ALFALFA`, `ONIONS`, `PECANS`  
Add threshold rules to `CropEvaluator` — requires research on NM-specific salinity/pH tolerance for each crop.

---

## 9. Gaps and Decisions Needed

| # | Gap | Status | Recommendation |
|---|-----|--------|----------------|
| G1 | **WaterRepository doesn't exist yet** — `WaterRepository` is not in the codebase; being built on water branch | Confirmed absent | Build `StubWaterRepository` implementing the same interface for this branch; swap when water branch merges |
| G2 | **Conductance (00095) has no live source** — Confirmed by API audit: none of the five NM surface gauges report 00095 | Confirmed gap | Use regional defaults permanently for MVP; add note in UI badge. Flag in §3.5. |
| G3 | **USGS GW well IDs are placeholders** — IDs in `NmRegion` need verification via USGS NWIS site search before build | Unresolved | Run USGS site query: `waterservices.usgs.gov/nwis/site/?stateCd=NM&siteType=GW&parameterCd=72019` filtered by county; replace placeholders before implementation |
| G4 | **NOAA station IDs are approximate** — GHCND station IDs in `NmRegion` need verification | Unresolved | Query `ncdc.noaa.gov/cdo-web/api/v2/stations?locationid=FIPS:35&datasetid=GHCND` with token; pick nearest station to each region capital |
| G5 | **NOAA token UX** — Where does the user enter/store the NOAA token? | Decision needed | For MVP: skip NOAA entirely if token absent; add Settings screen in stretch S2. Never store token in plaintext — use `EncryptedSharedPreferences` |
| G6 | **Streamflow threshold calibration** — Absolute cfs values (10/100/500) may not be meaningful across all five stations (Little Colorado at 55 cfs is "normal", not "low drought") | Design gap | Station-relative percentiles are more accurate but require historical baseline. For MVP use absolute brackets; add station-specific normal ranges in stretch |
| G7 | **Retrofit not yet in build.gradle.kts** — Project has no Retrofit dependency | Confirmed absent | Add `implementation("com.squareup.retrofit2:retrofit:2.9.0")` and `implementation("com.squareup.retrofit2:converter-gson:2.9.0")` before any network code |
| G8 | **LabListActivity → Fragment migration** — Needed for clean bottom nav but is a significant refactor | Deferred | Keep Activities for MVP; use Intents from bottom nav items. Migrate in separate branch `003-bottom-nav-refactor` |
| G9 | **Groundwater toggle default state** — Should toggle default ON or OFF? | Decision needed | Default OFF: avoids a surprise extra network call and a potentially confusing "well near your region" concept for homeowner persona. Power users (farmer persona) can enable it |
| G10 | **USGS DV API vs OGC API** — The Water tab uses OGC endpoint (`labs.waterdata.usgs.gov/api/observations`); confirmed available data uses classic NWIS (`waterservices.usgs.gov/nwis/dv`) | Confirmed split | Use classic NWIS for crop module (simpler JSON, confirmed working). Reconcile with Water tab's OGC approach when WaterRepository is shared |

---

## Implementation Checklist (ordered)

**Pre-build verification (do before writing any code)**
- [ ] Confirm USGS NWIS DV endpoint response shape: run one request against 08330000 param 00060 and note exact JSON field paths
- [ ] Obtain NOAA CDO token; confirm one TMAX/TMIN request against GHCND:USW00023050 returns data (wrong station ID = graceful fallback, not a blocker)

**Data models**
- [ ] Add Retrofit + OkHttp + Gson to `build.gradle.kts`
- [ ] Create `NmRegion` enum (surface ID, GW well ID, NOAA station ID, all defaults)
- [ ] Create `CropType`, `CropResult`, `SoilContext`, `WaterContext`, `ClimateContext`

**Business logic**
- [ ] Implement `CropEvaluator.java` — all four crops with streamflow + GW + soil + climate rules
- [ ] Write unit tests for `CropEvaluator` — construct `SoilContext`/`WaterContext`/`ClimateContext` directly, no stub needed:
  - Boundary values at each threshold edge (e.g. pH exactly 7.5, streamflow exactly 100)
  - Multiple simultaneous violations → correct status + all violations appear in `details` list
  - GW toggle OFF → groundwater depth criterion ignored for all crops
  - Climate unavailable → no climate entries in `details`, status unaffected
  - SUITABLE path → single positive entry in `details`

**Repositories**
- [ ] Create `WaterRepository` interface + `UsgsWaterRepository`: NWIS DV fetch for 00060 (surface), 72019 (GW well)
- [ ] Create `ClimateRepository` interface + `NoaaClimateRepository`: GHCND fetch, graceful absent-token path

**ViewModel + UI**
- [ ] Implement `CropViewModel` with parallel fetch logic + three-cache design
- [ ] Build `fragment_crop.xml` (spinner, GW toggle + hint, data badges row, RecyclerView)
- [ ] Build `item_crop_result.xml` color-coded card
- [ ] Implement `CropResultAdapter`
- [ ] Implement `CropFragment` (spinner, toggle, button, badge updates, observer wiring)
- [ ] Add Water and Crop `MaterialCardView` entries to existing `MainActivity` dashboard
- [ ] Create thin `CropActivity` and `WaterActivity` wrappers hosting their respective Fragments

**Integration + QA**
- [ ] Wire real `UsgsWaterRepository` and verify against live USGS data
- [ ] Manual end-to-end: all 5 regions × 4 crops × toggle ON/OFF × online/offline
- [ ] Verify NOAA path: with token (climate chips show) and without (evaluator skips climate rules)
