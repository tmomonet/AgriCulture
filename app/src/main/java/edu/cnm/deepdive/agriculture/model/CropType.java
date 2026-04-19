package edu.cnm.deepdive.agriculture.model;

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
