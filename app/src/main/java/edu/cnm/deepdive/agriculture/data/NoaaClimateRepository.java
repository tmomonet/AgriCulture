package edu.cnm.deepdive.agriculture.data;

import edu.cnm.deepdive.agriculture.model.ClimateContext;

import java.io.IOException;
import java.time.LocalDate;
import java.time.MonthDay;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NoaaClimateRepository implements ClimateRepository {

    private static final String BASE_URL = "https://www.ncdc.noaa.gov/cdo-web/api/v2/data";

    private final OkHttpClient client = new OkHttpClient();
    private final String token;

    public NoaaClimateRepository(String token) {
        this.token = token;
    }

    @Override
    public ClimateContext fetch(String noaaStationId) {
        if (token == null || token.isEmpty() || token.equals("YOUR_TOKEN_HERE")) {
            return ClimateContext.unavailable();
        }

        int priorYear = LocalDate.now().getYear() - 1;
        String url = BASE_URL
                + "?datasetid=GHCND"
                + "&stationid=" + noaaStationId
                + "&datatypeid=TMAX,TMIN"
                + "&startdate=" + priorYear + "-01-01"
                + "&enddate=" + priorYear + "-12-31"
                + "&limit=1000"
                + "&units=standard";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Token", token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return ClimateContext.unavailable();
            }
            return parse(response.body().string());
        } catch (IOException | JSONException e) {
            return ClimateContext.unavailable();
        }
    }

    private ClimateContext parse(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        JSONArray results = root.optJSONArray("results");
        if (results == null || results.length() == 0) return ClimateContext.unavailable();

        double julyTmaxSum = 0;
        int    julyTmaxCount = 0;
        int    lastFrostDoy = 0;

        for (int i = 0; i < results.length(); i++) {
            JSONObject rec = results.getJSONObject(i);
            String datatype = rec.getString("datatype");
            String date     = rec.getString("date");     // "2024-07-15T00:00:00"
            double value    = rec.getDouble("value");    // tenths of °C

            int month = Integer.parseInt(date.substring(5, 7));
            int doy   = LocalDate.parse(date.substring(0, 10)).getDayOfYear();

            if ("TMAX".equals(datatype) && month == 7) {
                double tempF = (value / 10.0) * 1.8 + 32;
                julyTmaxSum += tempF;
                julyTmaxCount++;
            }

            if ("TMIN".equals(datatype)) {
                double tempC = value / 10.0;
                if (tempC < 0 && doy > lastFrostDoy) {
                    lastFrostDoy = doy;
                }
            }
        }

        if (julyTmaxCount == 0) return ClimateContext.unavailable();

        double avgJulyMax = julyTmaxSum / julyTmaxCount;
        return new ClimateContext(avgJulyMax, lastFrostDoy, ClimateContext.DataSource.NOAA_LIVE);
    }
}
