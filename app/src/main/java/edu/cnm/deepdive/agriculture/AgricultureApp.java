package edu.cnm.deepdive.agriculture;

import android.app.Application;

import edu.cnm.deepdive.agriculture.data.ClimateRepository;
import edu.cnm.deepdive.agriculture.data.NoaaClimateRepository;
import edu.cnm.deepdive.agriculture.data.UsgsWaterRepository;
import edu.cnm.deepdive.agriculture.data.WaterRepository;

public class AgricultureApp extends Application {

    // TODO: before Play Store release, move to BuildConfig field in gradle.properties
    static final String NOAA_TOKEN = "YOUR_TOKEN_HERE";

    private WaterRepository waterRepository;
    private ClimateRepository climateRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        waterRepository   = new UsgsWaterRepository();
        climateRepository = new NoaaClimateRepository(NOAA_TOKEN);
    }

    public WaterRepository   getWaterRepository()   { return waterRepository; }
    public ClimateRepository getClimateRepository() { return climateRepository; }
}
