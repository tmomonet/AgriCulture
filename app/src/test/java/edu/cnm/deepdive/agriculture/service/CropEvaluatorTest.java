package edu.cnm.deepdive.agriculture.service;

import static org.junit.jupiter.api.Assertions.*;

import edu.cnm.deepdive.agriculture.model.ClimateContext;
import edu.cnm.deepdive.agriculture.model.CropResult;
import edu.cnm.deepdive.agriculture.model.CropResult.Status;
import edu.cnm.deepdive.agriculture.model.CropType;
import edu.cnm.deepdive.agriculture.model.SoilContext;
import edu.cnm.deepdive.agriculture.model.WaterContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class CropEvaluatorTest {

    private CropEvaluator evaluator;
    private ClimateContext noClimate;

    @BeforeEach
    void setUp() {
        evaluator = new CropEvaluator();
        noClimate = ClimateContext.unavailable();
    }

    // ── Helper builders ───────────────────────────────────────────────────────

    private SoilContext soil(double ph, double sar, double om, double precip) {
        return new SoilContext(ph, sar, om, precip);
    }

    private WaterContext water(double flow, double conductance) {
        return new WaterContext(flow, WaterContext.DataSource.USGS_LIVE,
                30, WaterContext.DataSource.REGIONAL_DEFAULT, conductance);
    }

    // ── Chile Pepper ──────────────────────────────────────────────────────────

    @Test
    void chile_suitable_idealConditions() {
        CropResult r = eval(CropType.CHILE, soil(6.5, 4.0, 1.5, 10.0), water(500, 800));
        assertEquals(Status.SUITABLE, r.getStatus());
        assertEquals(1, r.getDetails().size());
    }

    @Test
    void chile_caution_phAtUpperSuitableBoundary() {
        // pH 7.5 is upper-exclusive of SUITABLE → CAUTION
        CropResult r = eval(CropType.CHILE, soil(7.5, 4.0, 1.5, 10.0), water(500, 800));
        assertEquals(Status.CAUTION, r.getStatus());
    }

    @Test
    void chile_notRecommended_phTooHigh() {
        CropResult r = eval(CropType.CHILE, soil(8.1, 4.0, 1.5, 10.0), water(500, 800));
        assertEquals(Status.NOT_RECOMMENDED, r.getStatus());
    }

    @Test
    void chile_caution_streamflowAtBoundary() {
        // 100 cfs is upper-exclusive of CAUTION lower bound → CAUTION
        CropResult r = eval(CropType.CHILE, soil(7.0, 4.0, 1.5, 10.0), water(100, 800));
        assertEquals(Status.CAUTION, r.getStatus());
    }

    @Test
    void chile_notRecommended_criticalStreamflow() {
        CropResult r = eval(CropType.CHILE, soil(7.0, 4.0, 1.5, 10.0), water(5, 800));
        assertEquals(Status.NOT_RECOMMENDED, r.getStatus());
    }

    // ── Corn ─────────────────────────────────────────────────────────────────

    @Test
    void corn_suitable_idealConditions() {
        CropResult r = eval(CropType.CORN, soil(6.5, 3.0, 1.5, 10.0), water(500, 800));
        assertEquals(Status.SUITABLE, r.getStatus());
        assertEquals(1, r.getDetails().size());
    }

    @Test
    void corn_caution_phAtUpperSuitableBoundary() {
        CropResult r = eval(CropType.CORN, soil(7.0, 3.0, 1.5, 10.0), water(500, 800));
        assertEquals(Status.CAUTION, r.getStatus());
    }

    @Test
    void corn_notRecommended_highSar() {
        CropResult r = eval(CropType.CORN, soil(6.5, 11.0, 1.5, 10.0), water(500, 800));
        assertEquals(Status.NOT_RECOMMENDED, r.getStatus());
    }

    @Test
    void corn_caution_streamflowAtBoundary() {
        CropResult r = eval(CropType.CORN, soil(6.5, 3.0, 1.5, 10.0), water(100, 800));
        assertEquals(Status.CAUTION, r.getStatus());
    }

    // ── Pinto Beans ──────────────────────────────────────────────────────────

    @Test
    void beans_suitable_idealConditions() {
        CropResult r = eval(CropType.BEANS, soil(6.5, 3.0, 1.5, 10.0), water(500, 600));
        assertEquals(Status.SUITABLE, r.getStatus());
        assertEquals(1, r.getDetails().size());
    }

    @Test
    void beans_caution_phAtUpperSuitableBoundary() {
        CropResult r = eval(CropType.BEANS, soil(7.5, 3.0, 1.5, 10.0), water(500, 600));
        assertEquals(Status.CAUTION, r.getStatus());
    }

    @Test
    void beans_notRecommended_highConductance() {
        CropResult r = eval(CropType.BEANS, soil(7.0, 3.0, 1.5, 10.0), water(500, 2000));
        assertEquals(Status.NOT_RECOMMENDED, r.getStatus());
    }

    @Test
    void beans_caution_streamflowAtBoundary() {
        CropResult r = eval(CropType.BEANS, soil(7.0, 3.0, 1.5, 10.0), water(100, 600));
        assertEquals(Status.CAUTION, r.getStatus());
    }

    // ── Squash ───────────────────────────────────────────────────────────────

    @Test
    void squash_suitable_idealConditions() {
        CropResult r = eval(CropType.SQUASH, soil(7.0, 5.0, 1.5, 10.0), water(500, 1000));
        assertEquals(Status.SUITABLE, r.getStatus());
        assertEquals(1, r.getDetails().size());
    }

    @Test
    void squash_caution_streamflowAtBoundary() {
        // 50 cfs is upper-exclusive of SUITABLE (> 50) for squash → CAUTION
        CropResult r = eval(CropType.SQUASH, soil(7.0, 5.0, 1.5, 10.0), water(50, 1000));
        assertEquals(Status.CAUTION, r.getStatus());
    }

    @Test
    void squash_notRecommended_highSar() {
        CropResult r = eval(CropType.SQUASH, soil(7.0, 15.0, 1.5, 10.0), water(500, 1000));
        assertEquals(Status.NOT_RECOMMENDED, r.getStatus());
    }

    // ── Multi-violation ───────────────────────────────────────────────────────

    @Test
    void chile_multipleViolations_allAppearInDetails() {
        // pH CAUTION + SAR CAUTION + low streamflow CAUTION
        SoilContext soil = soil(7.6, 9.0, 1.0, 10.0);
        WaterContext water = water(50, 800);
        CropResult r = eval(CropType.CHILE, soil, water);
        assertEquals(Status.CAUTION, r.getStatus());
        assertTrue(r.getDetails().size() >= 3, "Expected at least 3 detail entries, got " + r.getDetails().size());
    }

    @Test
    void chile_notRecommended_multipleViolations() {
        SoilContext soil = soil(8.2, 13.0, 1.0, 10.0);
        WaterContext water = water(5, 3000);
        CropResult r = eval(CropType.CHILE, soil, water);
        assertEquals(Status.NOT_RECOMMENDED, r.getStatus());
        assertTrue(r.getDetails().size() >= 3);
    }

    // ── Climate signals ───────────────────────────────────────────────────────

    @Test
    void climate_unavailable_doesNotAffectStatus() {
        // Ideal conditions + unavailable climate → SUITABLE unchanged
        CropResult r = eval(CropType.CORN, soil(6.5, 3.0, 1.5, 10.0), water(500, 800));
        assertEquals(Status.SUITABLE, r.getStatus());
        assertEquals(1, r.getDetails().size()); // no climate entries added
    }

    @Test
    void climate_highJulyTemp_raisesStatusToCaution() {
        ClimateContext hotClimate = new ClimateContext(105, 90, ClimateContext.DataSource.NOAA_LIVE);
        SoilContext soil = soil(6.5, 3.0, 1.5, 10.0);
        WaterContext water = water(500, 800);
        List<CropResult> results = evaluator.evaluate(soil, water, hotClimate);
        CropResult corn = results.stream().filter(r -> r.getCropType() == CropType.CORN).findFirst().orElseThrow();
        assertEquals(Status.CAUTION, corn.getStatus());
        assertTrue(corn.getDetails().stream().anyMatch(d -> d.contains("July")));
    }

    @Test
    void climate_lowPrecip_appendsIrrigationNote() {
        // annualPrecipIn < 8 → irrigation note in details
        SoilContext dryRegion = soil(7.0, 4.0, 1.0, 7.0);
        WaterContext water = water(500, 800);
        CropResult r = eval(CropType.CHILE, dryRegion, water);
        // Status may be SUITABLE but details should mention irrigation
        boolean hasPrecipNote = r.getDetails().stream().anyMatch(d -> d.contains("precip") || d.contains("irrigation"));
        assertTrue(hasPrecipNote);
    }

    // ── Suitable positive summary ─────────────────────────────────────────────

    @Test
    void suitable_hasExactlyOnePositiveDetail() {
        List<CropResult> results = evaluator.evaluate(
                soil(6.5, 3.0, 1.5, 10.0),
                water(500, 800),
                noClimate);
        for (CropResult r : results) {
            if (r.getStatus() == Status.SUITABLE) {
                assertEquals(1, r.getDetails().size(),
                        r.getCropType() + " SUITABLE should have exactly 1 detail");
            }
        }
    }

    // ── Private helper ────────────────────────────────────────────────────────

    private CropResult eval(CropType type, SoilContext soil, WaterContext water) {
        return evaluator.evaluate(soil, water, noClimate)
                .stream()
                .filter(r -> r.getCropType() == type)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No result for " + type));
    }
}
