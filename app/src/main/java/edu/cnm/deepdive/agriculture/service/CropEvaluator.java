package edu.cnm.deepdive.agriculture.service;

import edu.cnm.deepdive.agriculture.model.ClimateContext;
import edu.cnm.deepdive.agriculture.model.CropResult;
import edu.cnm.deepdive.agriculture.model.CropResult.Status;
import edu.cnm.deepdive.agriculture.model.CropType;
import edu.cnm.deepdive.agriculture.model.SoilContext;
import edu.cnm.deepdive.agriculture.model.WaterContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pure logic class — no Android dependencies. All thresholds are lower-inclusive,
 * upper-exclusive [low, high). Priority: NOT_RECOMMENDED > CAUTION > SUITABLE.
 * GW depth criterion is always skipped in MVP (toggle deferred to 003-groundwater-toggle).
 */
public class CropEvaluator {

    public List<CropResult> evaluate(SoilContext soil, WaterContext water, ClimateContext climate) {
        return Arrays.asList(
                evaluateChile(soil, water, climate),
                evaluateCorn(soil, water, climate),
                evaluateBeans(soil, water, climate),
                evaluateSquash(soil, water, climate)
        );
    }

    // ── Chile Pepper ─────────────────────────────────────────────────────────

    private CropResult evaluateChile(SoilContext soil, WaterContext water, ClimateContext climate) {
        Status status = determineStatusChile(soil, water, climate);
        return new CropResult(
                CropType.CHILE,
                status,
                generateHeadline(CropType.CHILE, status),
                generateDetailsChile(soil, water, climate, status)
        );
    }

    private Status determineStatusChile(SoilContext s, WaterContext w, ClimateContext c) {
        if (s.getPh() >= 8.0 || s.getSar() >= 12.0 || w.getAvgConductance() >= 2500 || w.getAvgStreamflow() < 10)
            return Status.NOT_RECOMMENDED;
        if (s.getPh() >= 7.5 || s.getSar() >= 8.0 || w.getAvgConductance() >= 1500 || w.getAvgStreamflow() <= 100)
            return Status.CAUTION;
        if (c.isAvailable() && c.getAvgJulyMaxTempF() > 100)
            return Status.CAUTION;
        return Status.SUITABLE;
    }

    private List<String> generateDetailsChile(SoilContext s, WaterContext w, ClimateContext c, Status status) {
        if (status == Status.SUITABLE) {
            List<String> details = new ArrayList<>();
            details.add("pH, salinity, streamflow, and soil sodium are all within ideal range for a productive harvest.");
            if (s.getAnnualPrecipIn() < 8)
                details.add(String.format("Annual precip %.1f in → fully irrigation-dependent; water availability is critical.", s.getAnnualPrecipIn()));
            return details;
        }
        List<String> details = new ArrayList<>();
        // NOT_RECOMMENDED violations first
        if (s.getPh() >= 8.0)
            details.add(String.format("pH %.1f is too alkaline → severe nutrient lock-out expected.", s.getPh()));
        if (s.getSar() >= 12.0)
            details.add(String.format("SAR %.1f is too high → root structure damage and reduced water uptake expected.", s.getSar()));
        if (w.getAvgConductance() >= 2500)
            details.add(String.format("Conductance %.0f uS/cm exceeds tolerance → germination failure likely.", w.getAvgConductance()));
        if (w.getAvgStreamflow() < 10)
            details.add(String.format("Streamflow %.1f cfs is critically low → insufficient surface water for irrigation.", w.getAvgStreamflow()));
        // CAUTION violations
        if (s.getPh() >= 7.5 && s.getPh() < 8.0)
            details.add(String.format("pH %.1f is slightly alkaline → may reduce nutrient uptake; consider sulfur amendment.", s.getPh()));
        if (s.getSar() >= 8.0 && s.getSar() < 12.0)
            details.add(String.format("SAR %.1f is elevated → sodium may cause soil crusting; gypsum amendment recommended.", s.getSar()));
        if (w.getAvgConductance() >= 1500 && w.getAvgConductance() < 2500)
            details.add(String.format("Conductance %.0f uS/cm is high → salt stress possible; extra irrigation leaching helps.", w.getAvgConductance()));
        if (w.getAvgStreamflow() >= 10 && w.getAvgStreamflow() <= 100)
            details.add(String.format("Streamflow %.1f cfs is low → irrigation water may be restricted this season.", w.getAvgStreamflow()));
        // Climate signals (CAUTION only, never NOT_RECOMMENDED)
        if (c.isAvailable() && c.getAvgJulyMaxTempF() > 100)
            details.add(String.format("July highs averaging %.1f°F → heat stress risk during fruit set.", c.getAvgJulyMaxTempF()));
        if (s.getAnnualPrecipIn() < 8)
            details.add(String.format("Annual precip %.1f in → fully irrigation-dependent; water availability is critical.", s.getAnnualPrecipIn()));
        return details;
    }

    // ── Corn ─────────────────────────────────────────────────────────────────

    private CropResult evaluateCorn(SoilContext soil, WaterContext water, ClimateContext climate) {
        Status status = determineStatusCorn(soil, water, climate);
        return new CropResult(
                CropType.CORN,
                status,
                generateHeadline(CropType.CORN, status),
                generateDetailsCorn(soil, water, climate, status)
        );
    }

    private Status determineStatusCorn(SoilContext s, WaterContext w, ClimateContext c) {
        if (s.getPh() >= 7.8 || s.getSar() >= 10.0 || w.getAvgConductance() >= 2000 || w.getAvgStreamflow() < 10)
            return Status.NOT_RECOMMENDED;
        if (s.getPh() >= 7.0 || s.getSar() >= 6.0 || w.getAvgConductance() >= 1200 || w.getAvgStreamflow() <= 100)
            return Status.CAUTION;
        if (c.isAvailable() && (c.getAvgJulyMaxTempF() > 100 || c.getAvgLastFrostDoy() > 105)) // day 105 ≈ April 15
            return Status.CAUTION;
        return Status.SUITABLE;
    }

    private List<String> generateDetailsCorn(SoilContext s, WaterContext w, ClimateContext c, Status status) {
        if (status == Status.SUITABLE) {
            List<String> details = new ArrayList<>();
            details.add("Soil and water conditions match corn's preference for moderate pH, low salinity, and reliable flow.");
            if (s.getAnnualPrecipIn() < 8)
                details.add(String.format("Annual precip %.1f in → fully irrigation-dependent; water availability is critical.", s.getAnnualPrecipIn()));
            return details;
        }
        List<String> details = new ArrayList<>();
        if (s.getPh() >= 7.8)
            details.add(String.format("pH %.1f is too alkaline → severe nutrient lock-out expected.", s.getPh()));
        if (s.getSar() >= 10.0)
            details.add(String.format("SAR %.1f is too high → root structure damage and reduced water uptake expected.", s.getSar()));
        if (w.getAvgConductance() >= 2000)
            details.add(String.format("Conductance %.0f uS/cm exceeds tolerance → germination failure likely.", w.getAvgConductance()));
        if (w.getAvgStreamflow() < 10)
            details.add(String.format("Streamflow %.1f cfs is critically low → insufficient surface water for irrigation.", w.getAvgStreamflow()));
        if (s.getPh() >= 7.0 && s.getPh() < 7.8)
            details.add(String.format("pH %.1f is slightly alkaline → may reduce nutrient uptake; consider sulfur amendment.", s.getPh()));
        if (s.getSar() >= 6.0 && s.getSar() < 10.0)
            details.add(String.format("SAR %.1f is elevated → sodium may cause soil crusting; gypsum amendment recommended.", s.getSar()));
        if (w.getAvgConductance() >= 1200 && w.getAvgConductance() < 2000)
            details.add(String.format("Conductance %.0f uS/cm is high → salt stress possible; extra irrigation leaching helps.", w.getAvgConductance()));
        if (w.getAvgStreamflow() >= 10 && w.getAvgStreamflow() <= 100)
            details.add(String.format("Streamflow %.1f cfs is low → irrigation water may be restricted this season.", w.getAvgStreamflow()));
        if (c.isAvailable() && c.getAvgJulyMaxTempF() > 100)
            details.add(String.format("July highs averaging %.1f°F → heat stress lowers pollination success.", c.getAvgJulyMaxTempF()));
        if (c.isAvailable() && c.getAvgLastFrostDoy() > 105)
            details.add(String.format("Last frost around day %.0f → adjust planting date to avoid frost damage.", c.getAvgLastFrostDoy()));
        if (s.getAnnualPrecipIn() < 8)
            details.add(String.format("Annual precip %.1f in → fully irrigation-dependent; water availability is critical.", s.getAnnualPrecipIn()));
        return details;
    }

    // ── Pinto Beans ──────────────────────────────────────────────────────────

    private CropResult evaluateBeans(SoilContext soil, WaterContext water, ClimateContext climate) {
        Status status = determineStatusBeans(soil, water, climate);
        return new CropResult(
                CropType.BEANS,
                status,
                generateHeadline(CropType.BEANS, status),
                generateDetailsBeans(soil, water, climate, status)
        );
    }

    private Status determineStatusBeans(SoilContext s, WaterContext w, ClimateContext c) {
        if (s.getPh() >= 8.0 || s.getSar() >= 8.0 || w.getAvgConductance() >= 1800 || w.getAvgStreamflow() < 10)
            return Status.NOT_RECOMMENDED;
        if (s.getPh() >= 7.5 || s.getSar() >= 5.0 || w.getAvgConductance() >= 1000 || w.getAvgStreamflow() <= 100)
            return Status.CAUTION;
        if (c.isAvailable() && c.getAvgJulyMaxTempF() > 100)
            return Status.CAUTION;
        return Status.SUITABLE;
    }

    private List<String> generateDetailsBeans(SoilContext s, WaterContext w, ClimateContext c, Status status) {
        if (status == Status.SUITABLE) {
            List<String> details = new ArrayList<>();
            details.add("Low salinity and near-neutral pH give pinto beans good conditions for nodule formation and yield.");
            if (s.getAnnualPrecipIn() < 8)
                details.add(String.format("Annual precip %.1f in → fully irrigation-dependent; water availability is critical.", s.getAnnualPrecipIn()));
            return details;
        }
        List<String> details = new ArrayList<>();
        if (s.getPh() >= 8.0)
            details.add(String.format("pH %.1f is too alkaline → severe nutrient lock-out expected.", s.getPh()));
        if (s.getSar() >= 8.0)
            details.add(String.format("SAR %.1f is too high → root structure damage and reduced water uptake expected.", s.getSar()));
        if (w.getAvgConductance() >= 1800)
            details.add(String.format("Conductance %.0f uS/cm exceeds tolerance → germination failure likely.", w.getAvgConductance()));
        if (w.getAvgStreamflow() < 10)
            details.add(String.format("Streamflow %.1f cfs is critically low → insufficient surface water for irrigation.", w.getAvgStreamflow()));
        if (s.getPh() >= 7.5 && s.getPh() < 8.0)
            details.add(String.format("pH %.1f is slightly alkaline → may reduce nutrient uptake; consider sulfur amendment.", s.getPh()));
        if (s.getSar() >= 5.0 && s.getSar() < 8.0)
            details.add(String.format("SAR %.1f is elevated → sodium may affect nodule formation; inoculant and gypsum recommended.", s.getSar()));
        if (w.getAvgConductance() >= 1000 && w.getAvgConductance() < 1800)
            details.add(String.format("Conductance %.0f uS/cm is high → salt stress possible; extra irrigation leaching helps.", w.getAvgConductance()));
        if (w.getAvgStreamflow() >= 10 && w.getAvgStreamflow() <= 100)
            details.add(String.format("Streamflow %.1f cfs is low → irrigation water may be restricted this season.", w.getAvgStreamflow()));
        if (c.isAvailable() && c.getAvgJulyMaxTempF() > 100)
            details.add(String.format("July highs averaging %.1f°F → pod set affected above 95°F.", c.getAvgJulyMaxTempF()));
        if (s.getAnnualPrecipIn() < 8)
            details.add(String.format("Annual precip %.1f in → fully irrigation-dependent; water availability is critical.", s.getAnnualPrecipIn()));
        return details;
    }

    // ── Squash ───────────────────────────────────────────────────────────────

    private CropResult evaluateSquash(SoilContext soil, WaterContext water, ClimateContext climate) {
        Status status = determineStatusSquash(soil, water, climate);
        return new CropResult(
                CropType.SQUASH,
                status,
                generateHeadline(CropType.SQUASH, status),
                generateDetailsSquash(soil, water, climate, status)
        );
    }

    private Status determineStatusSquash(SoilContext s, WaterContext w, ClimateContext c) {
        if (s.getPh() >= 8.2 || s.getSar() >= 14.0 || w.getAvgConductance() >= 2800 || w.getAvgStreamflow() < 10)
            return Status.NOT_RECOMMENDED;
        if (s.getPh() >= 7.5 || s.getSar() >= 10.0 || w.getAvgConductance() >= 1800 || w.getAvgStreamflow() <= 50)
            return Status.CAUTION;
        if (c.isAvailable() && c.getAvgLastFrostDoy() > 121) // day 121 ≈ May 1
            return Status.CAUTION;
        return Status.SUITABLE;
    }

    private List<String> generateDetailsSquash(SoilContext s, WaterContext w, ClimateContext c, Status status) {
        if (status == Status.SUITABLE) {
            List<String> details = new ArrayList<>();
            details.add("Squash is the most salt-tolerant of the Three Sisters and is well-matched to your current conditions.");
            if (s.getAnnualPrecipIn() < 8)
                details.add(String.format("Annual precip %.1f in → fully irrigation-dependent; water availability is critical.", s.getAnnualPrecipIn()));
            return details;
        }
        List<String> details = new ArrayList<>();
        if (s.getPh() >= 8.2)
            details.add(String.format("pH %.1f is too alkaline → severe nutrient lock-out expected.", s.getPh()));
        if (s.getSar() >= 14.0)
            details.add(String.format("SAR %.1f is too high → root structure damage and reduced water uptake expected.", s.getSar()));
        if (w.getAvgConductance() >= 2800)
            details.add(String.format("Conductance %.0f uS/cm exceeds tolerance → germination failure likely.", w.getAvgConductance()));
        if (w.getAvgStreamflow() < 10)
            details.add(String.format("Streamflow %.1f cfs is critically low → insufficient surface water for irrigation.", w.getAvgStreamflow()));
        if (s.getPh() >= 7.5 && s.getPh() < 8.2)
            details.add(String.format("pH %.1f is slightly alkaline → may reduce nutrient uptake; consider sulfur amendment.", s.getPh()));
        if (s.getSar() >= 10.0 && s.getSar() < 14.0)
            details.add(String.format("SAR %.1f is elevated → high sodium may cause tip burn; mulching and drip irrigation help.", s.getSar()));
        if (w.getAvgConductance() >= 1800 && w.getAvgConductance() < 2800)
            details.add(String.format("Conductance %.0f uS/cm is high → salt stress possible; extra irrigation leaching helps.", w.getAvgConductance()));
        if (w.getAvgStreamflow() >= 10 && w.getAvgStreamflow() <= 50)
            details.add(String.format("Streamflow %.1f cfs is low → irrigation water may be restricted this season.", w.getAvgStreamflow()));
        if (c.isAvailable() && c.getAvgLastFrostDoy() > 121)
            details.add(String.format("Last frost around day %.0f → squash is very frost-sensitive; delay transplant accordingly.", c.getAvgLastFrostDoy()));
        if (s.getAnnualPrecipIn() < 8)
            details.add(String.format("Annual precip %.1f in → fully irrigation-dependent; water availability is critical.", s.getAnnualPrecipIn()));
        return details;
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    private String generateHeadline(CropType crop, Status status) {
        switch (status) {
            case SUITABLE:         return "Good conditions for " + crop.displayName + ".";
            case CAUTION:          return "Possible with amendments for " + crop.displayName + ".";
            case NOT_RECOMMENDED:  return "Difficult conditions for " + crop.displayName + ".";
            default: throw new IllegalArgumentException("Unknown status: " + status);
        }
    }

    private List<String> singletonList(String s) {
        List<String> list = new ArrayList<>();
        list.add(s);
        return list;
    }
}
