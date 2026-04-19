package edu.cnm.deepdive.agriculture.data;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UsgsWaterRepository implements WaterRepository {

    private static final String BASE_URL = "https://waterservices.usgs.gov/nwis/dv/";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String NO_DATA = "-999999";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public Double fetchStreamflowAvg(String siteId, int days) {
        return fetchParamAvg(siteId, "00060", days);
    }

    @Override
    public Double fetchGwDepthAvg(String wellId, int days) {
        // TODO: implement live GW fetch in branch 003-groundwater-toggle
        return null;
    }

    private Double fetchParamAvg(String siteId, String paramCd, int days) {
        String startDT = LocalDate.now().minusDays(days).format(FMT);
        String endDT   = LocalDate.now().format(FMT);
        String url = BASE_URL
                + "?format=json"
                + "&sites=" + siteId
                + "&parameterCd=" + paramCd
                + "&startDT=" + startDT
                + "&endDT=" + endDT
                + "&statCd=00003";

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) return null;
            return parseAverage(response.body().string());
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    private Double parseAverage(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        JSONArray timeSeries = root.getJSONObject("value").getJSONArray("timeSeries");
        if (timeSeries.length() == 0) return null;

        JSONArray values = timeSeries.getJSONObject(0)
                .getJSONArray("values")
                .getJSONObject(0)
                .getJSONArray("value");

        double sum = 0;
        int count = 0;
        for (int i = 0; i < values.length(); i++) {
            String v = values.getJSONObject(i).getString("value");
            if (v == null || v.equals(NO_DATA)) continue;
            try {
                sum += Double.parseDouble(v);
                count++;
            } catch (NumberFormatException ignored) {}
        }
        return count > 0 ? sum / count : null;
    }
}
