package edu.cnm.deepdive.agriculture.model;

import java.util.Collections;
import java.util.List;

public class CropResult {

    public enum Status {
        SUITABLE,
        CAUTION,
        NOT_RECOMMENDED
    }

    private final CropType cropType;
    private final Status status;
    private final String headline;
    private final List<String> details;

    public CropResult(CropType cropType, Status status, String headline, List<String> details) {
        this.cropType = cropType;
        this.status = status;
        this.headline = headline;
        this.details = Collections.unmodifiableList(details);
    }

    public CropType getCropType() { return cropType; }
    public Status   getStatus()   { return status; }
    public String   getHeadline() { return headline; }
    public List<String> getDetails() { return details; }
}
