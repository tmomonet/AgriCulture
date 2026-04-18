package edu.cnm.deepdive.agriculture.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SoilLabTest {

    private SoilLab labPapNapt;
    private SoilLab labNaptOnly;
    private SoilLab labWithAddressLine2;
    private SoilLab labNullFarmer;

    @BeforeEach
    void setUp() {
        labPapNapt = new SoilLab(1, "Lab A", "http://a.com",
                "123 Main St", null,
                "Anytown", "NM", "87001", "505-555-0100",
                "PAP/NAPT Soil, Plant and Water",
                "Homeowner package", "Farmer package");

        labNaptOnly = new SoilLab(2, "Lab B", "http://b.com",
                "456 Oak Ave", null,
                "Las Cruces", "NM", "88001", "575-555-0200",
                "NAPT Soil and Plant Program",
                "Homeowner package", "Farmer package");

        labWithAddressLine2 = new SoilLab(3, "Lab C", "http://c.com",
                "700 West D Street", "PO Box 370",
                "McCook", "NE", "69001", "308-345-3670",
                "PAP/NAPT Soil, Plant and Water",
                "Homeowner tests", "Farmer tests");

        labNullFarmer = new SoilLab(4, "Lab D", "http://d.com",
                "999 University Dr", null,
                "Phoenix", "AZ", "85034", "602-555-0300",
                "NAPT Soil and Plant Program",
                "Complete with Crop Specific Recommendations", null);
    }

    @Test
    void isPap_trueWhenAccreditationContainsPap() {
        assertTrue(labPapNapt.isPap());
    }

    @Test
    void isPap_falseWhenAccreditationContainsOnlyNapt() {
        assertFalse(labNaptOnly.isPap());
    }

    @Test
    void isNapt_trueForPapNaptAccreditation() {
        assertTrue(labPapNapt.isNapt());
    }

    @Test
    void isNapt_trueForNaptOnlyAccreditation() {
        assertTrue(labNaptOnly.isNapt());
    }

    @Test
    void getEffectiveFarmerTests_returnsFarmerTestsWhenNotNull() {
        assertEquals("Farmer package", labPapNapt.getEffectiveFarmerTests());
    }

    @Test
    void getEffectiveFarmerTests_fallsBackToHomeownerTestsWhenFarmerIsNull() {
        assertEquals("Complete with Crop Specific Recommendations",
                labNullFarmer.getEffectiveFarmerTests());
    }

    @Test
    void isFarmerSameAsHomeowner_trueWhenFarmerTestsIsNull() {
        assertTrue(labNullFarmer.isFarmerSameAsHomeowner());
    }

    @Test
    void isFarmerSameAsHomeowner_falseWhenFarmerTestsPresent() {
        assertFalse(labPapNapt.isFarmerSameAsHomeowner());
    }

    @Test
    void getFormattedAddress_noAddressLine2() {
        assertEquals("123 Main St, Anytown, NM 87001",
                labPapNapt.getFormattedAddress());
    }

    @Test
    void getFormattedAddress_withAddressLine2() {
        assertEquals("700 West D Street, PO Box 370, McCook, NE 69001",
                labWithAddressLine2.getFormattedAddress());
    }

    @Test
    void getFormattedAddress_blankAddressLine2IsOmitted() {
        SoilLab lab = new SoilLab(5, "Lab E", "http://e.com",
                "100 Main St", "",
                "Albuquerque", "NM", "87102", "505-000-0000",
                "NAPT", "Tests", "Tests");
        assertEquals("100 Main St, Albuquerque, NM 87102", lab.getFormattedAddress());
    }

    @Test
    void getFarmerTests_returnsNullForSameSentinel() {
        assertNull(labNullFarmer.getFarmerTests());
    }
}
