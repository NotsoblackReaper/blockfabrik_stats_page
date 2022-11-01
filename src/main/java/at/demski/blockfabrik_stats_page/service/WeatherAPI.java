package at.demski.blockfabrik_stats_page.service;

import at.demski.blockfabrik_stats_page.entities.WeatherData;
import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherAPI {

    public static WeatherData getWeather(Date date){
        if(date.compareTo(DateManager.today())!=0)
            return getHistoricWeather(date);
        return getCurrentWeather();
    }

    public static WeatherData getHistoricWeather(Date date) {
        String dateString = date.toString();
        try {
            URL request = new URL(
                    "https://dataset.api.hub.zamg.ac.at/v1/station/historical/klima-v1-1d?parameters=t,vv,nied&start="
                            + dateString + "&end=" + dateString + "&station_ids=5925");
            JSONObject root = new JSONObject(new JSONTokener(request.openStream()));

            return new WeatherData(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WeatherData getCurrentWeather() {
        HttpURLConnection con = null;
        try {
            URL url = new URL("https://wetter.orf.at/wien/innerestadt/");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            String regex = "Temperatur(<[^>]+>)+:(<[^>]+>)+\\s*(\\d+,?\\d+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            if(matcher.find())
                return new WeatherData(Float.parseFloat(matcher.group(3).replace(',', '.')), 0, 0, new Date(System.currentTimeMillis()));
            return new WeatherData(0,0,0,new Date(System.currentTimeMillis()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}