package at.demski.blockfabrik_stats_page.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;

public class WeatherData {
    public float temperature;
    public float downpour;
    public float wind;
    public Date date;

    public WeatherData(JSONObject root) {
        try {
            date = Date.valueOf(root.getJSONArray("timestamps").getString(0).split("T")[0]);
            JSONObject params = root.getJSONArray("features").getJSONObject(0).getJSONObject("properties").getJSONObject("parameters");
            if (!params.getJSONObject("t").getJSONArray("data").isNull(0))
                temperature = params.getJSONObject("t").getJSONArray("data").getFloat(0);
            if (!params.getJSONObject("vv").getJSONArray("data").isNull(0))
                wind = params.getJSONObject("vv").getJSONArray("data").getFloat(0);
            if (!params.getJSONObject("nied").getJSONArray("data").isNull(0))
                downpour = params.getJSONObject("nied").getJSONArray("data").getFloat(0);
            if (downpour < 0) downpour = 0;
        } catch (JSONException e) {
            System.err.println(root);
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public WeatherData(float temperature, float downpour, float wind, Date date) {
        this.temperature = temperature;
        this.downpour = downpour;
        this.wind = wind;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Datum: " + date + " (Temp.: " + temperature + "°C, Nied.: " + downpour + "mm, Windgeschw.: " + wind + "m/s)";
    }
}