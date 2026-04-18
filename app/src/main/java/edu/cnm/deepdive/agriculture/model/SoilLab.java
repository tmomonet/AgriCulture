package edu.cnm.deepdive.agriculture.model;

public class SoilLab {

    private int id;
    private String name;
    private String websiteUrl;
    private String streetAddress;
    private String addressLine2;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private String accreditation;
    private String homeownerTests;
    private String farmerTests;

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

    public boolean isPap() {
        return accreditation != null && accreditation.contains("PAP");
    }

    public boolean isNapt() {
        return accreditation != null && accreditation.contains("NAPT");
    }

    /** Returns farmerTests, or homeownerTests when farmerTests is null (same-package sentinel). */
    public String getEffectiveFarmerTests() {
        return farmerTests != null ? farmerTests : homeownerTests;
    }

    public boolean isFarmerSameAsHomeowner() {
        return farmerTests == null;
    }

    /**
     * Comma-separated full address suitable for display and as a Maps geo: query string.
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAccreditation() { return accreditation; }
    public void setAccreditation(String accreditation) { this.accreditation = accreditation; }

    public String getHomeownerTests() { return homeownerTests; }
    public void setHomeownerTests(String homeownerTests) { this.homeownerTests = homeownerTests; }

    public String getFarmerTests() { return farmerTests; }
    public void setFarmerTests(String farmerTests) { this.farmerTests = farmerTests; }
}
