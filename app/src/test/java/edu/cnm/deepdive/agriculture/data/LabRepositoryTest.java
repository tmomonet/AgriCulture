package edu.cnm.deepdive.agriculture.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.cnm.deepdive.agriculture.model.SoilLab;
import java.util.List;
import org.junit.jupiter.api.Test;

class LabRepositoryTest {

    @Test
    void getAll_returnsNineLabs() {
        assertEquals(9, LabRepository.getAll().size());
    }

    @Test
    void getAll_returnsUnmodifiableList() {
        List<SoilLab> labs = LabRepository.getAll();
        assertThrows(UnsupportedOperationException.class, () -> labs.add(null));
    }

    @Test
    void getById_returnsCorrectLabForId1() {
        SoilLab lab = LabRepository.getById(1);
        assertNotNull(lab);
        assertEquals("American Agricultural Laboratory", lab.getName());
    }

    @Test
    void getById_returnsCorrectLabForId6() {
        SoilLab lab = LabRepository.getById(6);
        assertNotNull(lab);
        assertEquals("Ward Laboratories", lab.getName());
    }

    @Test
    void getById_returnsNullForNegativeId() {
        assertNull(LabRepository.getById(-1));
    }

    @Test
    void getById_returnsNullForZero() {
        assertNull(LabRepository.getById(0));
    }

    @Test
    void getById_returnsNullForIdBeyondRange() {
        assertNull(LabRepository.getById(100));
    }

    @Test
    void lab1_hasAddressLine2() {
        SoilLab lab = LabRepository.getById(1);
        assertNotNull(lab);
        assertEquals("PO Box 370", lab.getAddressLine2());
    }

    @Test
    void lab2_farmerTestsIsNull_sameSentinel() {
        SoilLab lab = LabRepository.getById(2);
        assertNotNull(lab);
        assertNull(lab.getFarmerTests());
        assertTrue(lab.isFarmerSameAsHomeowner());
    }

    @Test
    void lab4_accreditationContainsFrequencyQualifier() {
        SoilLab lab = LabRepository.getById(4);
        assertNotNull(lab);
        assertTrue(lab.getAccreditation().contains("4x/yr"));
        assertTrue(lab.isPap());
        assertTrue(lab.isNapt());
    }
}
