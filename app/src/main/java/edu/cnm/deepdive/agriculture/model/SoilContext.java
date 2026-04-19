package edu.cnm.deepdive.agriculture.model;

public class SoilContext {

    private double ph;
    private double sar;
    private double organicMatter;
    private double annualPrecipIn;

    public static SoilContext fromRegion(NmRegion region) {
        return new SoilContext(
                region.defaultPh,
                region.defaultSar,
                region.defaultOrganicMatter,
                region.annualPrecipIn
        );
    }

    public SoilContext(double ph, double sar, double organicMatter, double annualPrecipIn) {
        this.ph = ph;
        this.sar = sar;
        this.organicMatter = organicMatter;
        this.annualPrecipIn = annualPrecipIn;
    }

    public double getPh()             { return ph; }
    public double getSar()            { return sar; }
    public double getOrganicMatter()  { return organicMatter; }
    public double getAnnualPrecipIn() { return annualPrecipIn; }

    public void setPh(double ph)                       { this.ph = ph; }
    public void setSar(double sar)                     { this.sar = sar; }
    public void setOrganicMatter(double organicMatter) { this.organicMatter = organicMatter; }
    public void setAnnualPrecipIn(double v)            { this.annualPrecipIn = v; }
}
