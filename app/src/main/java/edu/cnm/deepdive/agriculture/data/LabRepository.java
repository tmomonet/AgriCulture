package edu.cnm.deepdive.agriculture.data;

import edu.cnm.deepdive.agriculture.model.SoilLab;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LabRepository {

    private static final List<SoilLab> LABS = Collections.unmodifiableList(Arrays.asList(
        new SoilLab(1,
            "American Agricultural Laboratory",
            "http://www.amaglab.com",
            "700 West D Street", "PO Box 370",
            "McCook", "NE", "69001",
            "308-345-3670",
            "PAP/NAPT Soil, Plant and Water",
            "Lawn/Garden Test plus Sodium/Salinity (SAR), Olsen Bicarbonate P, recommendations supplied",
            "Complete plus Sodium/Salinity (SAR), Olsen Bicarbonate P, crop specific recommendations"),
        new SoilLab(2,
            "Inter Ag Services Laboratory",
            "https://iaslabs.com/pages/soil-test/",
            "2515 E. University Drive", null,
            "Phoenix", "AZ", "85034",
            "602-273-7248",
            "NAPT Soil and Plant Program",
            "Complete with Crop Specific Recommendations, Water soluble Ca/Mg/K/Na, Olsen P",
            null),
        new SoilLab(3,
            "Servi-Tech Laboratories-Amarillo",
            "http://www.servitechlabs.com",
            "6921 S. Bell Ave", null,
            "Amarillo", "TX", "79109",
            "806-677-0093",
            "NAPT Soil, Plant and Water",
            "Lawn & Garden, Golf Course/Athletic Turf, Soil Salinity Appraisal",
            "Soil Salinity Appraisal, Row Crop Test"),
        new SoilLab(4,
            "Colorado State University",
            "https://denver.extension.colostate.edu/csu-soil-testing/",
            "NESB Room A319", null,
            "Fort Collins", "CO", "80523",
            "970-491-5061",
            "PAP/NAPT Soil Program 4x/yr",
            "Routine and SAR",
            "Routine and SAR"),
        new SoilLab(5,
            "USUAL - Utah State University",
            "http://www.usual.usu.edu/forms/soilform.pdf",
            "9400 Old Main Hill", null,
            "Logan", "UT", "84322",
            "435-797-2217",
            "PAP/NAPT Soil, Plant, Water and Environmental",
            "Complete",
            "Complete"),
        new SoilLab(6,
            "Ward Laboratories",
            "http://www.wardlab.com",
            "4007 Cherry Ave", null,
            "Kearney", "NE", "68848",
            "800-887-7645",
            "NAPT Soil, Plant and Water",
            "S4, SAR, Walkley-Black OM, Olsen P",
            "S4, SAR, Walkley-Black OM, Olsen P"),
        new SoilLab(7,
            "Dellavalle Laboratory Inc.",
            "http://www.dellavallelab.com/",
            "1910 W. McKinley Ave Suite 110", null,
            "Fresno", "CA", "93728",
            "800-228-9896",
            "PAP/NAPT Soil, Plant and Water",
            "FA2 Sodium & Salinity Assay, Gypsum Requirement",
            "FA2 Sodium & Salinity Assay, Gypsum Requirement"),
        new SoilLab(8,
            "Western Laboratories",
            "https://westernlaboratories.com/",
            "211 HWY 95", null,
            "Parma", "ID", "83660",
            "208-722-6564",
            "PAP/NAPT Soil, Plant and Water",
            "Test 70 Garden Test",
            "Test 1 Complete Soil Test"),
        new SoilLab(9,
            "Analytical Sciences Laboratory - University of Idaho",
            "http://www.uidaho.edu/cals/analytical-sciences-laboratory",
            "2222 W. Sixth St", null,
            "Moscow", "ID", "83844",
            "208-885-7900",
            "PAP/NAPT Soil and Plant",
            "SNMP/SEFT, SOML, SELC, SGRQ, SNMS, SCCE",
            "SNMP/SEFT, SOML, SELC, SGRQ, SNMS, SCCE")
    ));

    private LabRepository() {}

    public static List<SoilLab> getAll() {
        return LABS;
    }

    public static SoilLab getById(int id) {
        for (SoilLab lab : LABS) {
            if (lab.getId() == id) {
                return lab;
            }
        }
        return null;
    }
}
