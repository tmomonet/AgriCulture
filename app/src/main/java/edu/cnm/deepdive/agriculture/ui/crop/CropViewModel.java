package edu.cnm.deepdive.agriculture.ui.crop;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import edu.cnm.deepdive.agriculture.AgricultureApp;
import edu.cnm.deepdive.agriculture.data.ClimateRepository;
import edu.cnm.deepdive.agriculture.data.WaterRepository;
import edu.cnm.deepdive.agriculture.model.ClimateContext;
import edu.cnm.deepdive.agriculture.model.CropResult;
import edu.cnm.deepdive.agriculture.model.NmRegion;
import edu.cnm.deepdive.agriculture.model.SoilContext;
import edu.cnm.deepdive.agriculture.model.WaterContext;
import edu.cnm.deepdive.agriculture.service.CropEvaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CropViewModel extends AndroidViewModel {

    private final WaterRepository   waterRepo;
    private final ClimateRepository climateRepo;
    private final CropEvaluator     evaluator = new CropEvaluator();
    private final ExecutorService   executor  = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<CropResult>>     results          = new MutableLiveData<>();
    private final MutableLiveData<Boolean>              loading          = new MutableLiveData<>(false);
    private final MutableLiveData<String>               statusMessage    = new MutableLiveData<>();
    private final MutableLiveData<List<String>>         errors           = new MutableLiveData<>();
    private final MutableLiveData<WaterContext.DataSource> flowSource    = new MutableLiveData<>();
    private final MutableLiveData<Boolean>              climateAvailable = new MutableLiveData<>(false);

    private final Map<String, Double>         streamflowCache = new HashMap<>();
    private final Map<String, Double>         gwDepthCache    = new HashMap<>();
    private final Map<String, ClimateContext> climateCache    = new HashMap<>();

    public CropViewModel(Application app) {
        super(app);
        AgricultureApp agApp = (AgricultureApp) app;
        waterRepo   = agApp.getWaterRepository();
        climateRepo = agApp.getClimateRepository();
    }

    public void loadRecommendations(NmRegion region) {
        loading.postValue(true);
        executor.execute(() -> {
            List<String> stepErrors = new ArrayList<>();
            try {
                // Step 1 — surface flow
                statusMessage.postValue("Fetching streamflow data\u2026");
                Double streamflow = streamflowCache.containsKey(region.usgsSurfaceSiteId)
                        ? streamflowCache.get(region.usgsSurfaceSiteId)
                        : waterRepo.fetchStreamflowAvg(region.usgsSurfaceSiteId, 365);
                WaterContext.DataSource flowSrc;
                if (streamflow != null) {
                    streamflowCache.put(region.usgsSurfaceSiteId, streamflow);
                    flowSrc = WaterContext.DataSource.USGS_LIVE;
                } else {
                    streamflow = region.defaultStreamflow;
                    flowSrc    = WaterContext.DataSource.OFFLINE_DEFAULT;
                    stepErrors.add("Streamflow unavailable \u2014 using regional average.");
                }

                // Step 2 — groundwater (always regional default; GW toggle deferred to 003-groundwater-toggle)
                statusMessage.postValue("Using regional groundwater estimate\u2026");
                double gwDepth = region.defaultGwDepth;
                WaterContext.DataSource gwSrc = WaterContext.DataSource.REGIONAL_DEFAULT;

                // Step 3 — climate
                statusMessage.postValue("Fetching climate data\u2026");
                ClimateContext climate = climateCache.containsKey(region.noaaStationId)
                        ? climateCache.get(region.noaaStationId)
                        : climateRepo.fetch(region.noaaStationId);
                climateCache.put(region.noaaStationId, climate);

                WaterContext water = new WaterContext(
                        streamflow, flowSrc, gwDepth, gwSrc, region.defaultConductance);
                SoilContext soil = SoilContext.fromRegion(region);
                flowSource.postValue(flowSrc);
                climateAvailable.postValue(climate.isAvailable());
                results.postValue(evaluator.evaluate(soil, water, climate));

                if (!stepErrors.isEmpty()) {
                    errors.postValue(stepErrors);
                }
            } finally {
                loading.postValue(false);
            }
        });
    }

    public LiveData<List<CropResult>>     getResults()          { return results; }
    public LiveData<Boolean>              isLoading()           { return loading; }
    public LiveData<String>               getStatusMessage()    { return statusMessage; }
    public LiveData<List<String>>         getErrors()           { return errors; }
    public LiveData<WaterContext.DataSource> getFlowSource()    { return flowSource; }
    public LiveData<Boolean>              isClimateAvailable()  { return climateAvailable; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
