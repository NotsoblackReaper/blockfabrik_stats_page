package at.demski.blockfabrik_stats_page.controllers;

import at.demski.blockfabrik_stats_page.BlockfabrikStatsPageApplication;
import at.demski.blockfabrik_stats_page.entities.Datapoint;
import at.demski.blockfabrik_stats_page.entities.DayData;
import at.demski.blockfabrik_stats_page.service.DatabaseConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import javax.xml.crypto.Data;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DataController {
    final DatabaseConnector dbConnector;

    public DataController(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    @GetMapping("/data/list")
    public String dataList(Model model) {
        List<DayData>data=dbConnector.getDayDataDesc(50);
        System.out.println("Accessing Data list: ");
        System.out.println("Got "+data.size()+" entries");
        model.addAttribute("dayList", data);
        model.addAttribute("data_scraping", BlockfabrikStatsPageApplication.data_scraping);
        return "data_list";
    }

    private String[] getDayData(int i, String[] day_names) {
        String[] res = new String[4];
        res[0] = String.valueOf(i);
        res[1] = "dataDay" + i;
        res[2] = day_names[i - 1];
        res[3] = "chart_day" + i;
        return res;
    }

    private float getMedian(List<Datapoint> datapointList, final int time) {
        List<Integer> data = datapointList.stream().filter(d -> d.getDatapoint_hour() * 60 + d.getDatapoint_minute() == time)
                .map(Datapoint::getDatapoint_act).sorted().collect(Collectors.toList());
        return (float) data.stream().mapToInt(value -> value).average().orElse(Double.NaN);
    }

    @GetMapping("/data/charts")
    public String dataCharts(Model model) {

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        int currentTime = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);


        String[][] days = new String[7][4];
        String[] day_names = {"Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};

        List<List<Datapoint>> rawlist = new ArrayList<>();
        List<List<Datapoint>> medianList = new ArrayList<>();
        List<List<Datapoint>> halfHourAverageList = new ArrayList<>();

        rawlist.add(dbConnector.getAllForDay(currentDay));
        days[0] = getDayData(currentDay, day_names);

        medianList.add(new ArrayList<>());
        for (int j = 7 * 60 + 30; j < 22 * 60; j += 2) {
            int finalJ = j;
            float median = getMedian(rawlist.get(rawlist.size() - 1), j);
            medianList.get(0).add(new Datapoint(Math.round(median), j / 60, j % 60, currentDay));
        }

        for (int i = 1, day = 1; i <= 7; ++i) {
            if (i == currentDay)
                continue;
            rawlist.add(dbConnector.getAllForDay(i));
            days[day] = getDayData(i, day_names);

            medianList.add(new ArrayList<>());
            for (int j = 7 * 60 + 30; j < 22 * 60; j += 2) {
                int finalJ = j;
                float median = getMedian(rawlist.get(rawlist.size() - 1), j);
                medianList.get(day).add(new Datapoint(Math.round(median), j / 60, j % 60, i));
            }
            day++;
        }

        for (int i = 0, day = 1; i < 7; ++i) {
            List<Datapoint> points = new ArrayList<>();
            float sum = 0;
            int n = 0;
            for (int j = 0; j < medianList.get(i).size(); ++j) {
                Datapoint p = medianList.get(i).get(j);
                sum += p.getDatapoint_act();
                ++n;
                if (p.getDatapoint_minute() == 28 || p.getDatapoint_minute() == 58) {
                    points.add(new Datapoint(
                            Math.round(sum / n),
                            p.getDatapoint_hour() - (p.getDatapoint_minute() == 28 ? 1 : 0),
                            p.getDatapoint_minute() == 28 ? 0 : 30,
                            p.getDatapoint_day()));
                    sum = 0;
                    n = 0;
                }
            }

            halfHourAverageList.add(points);
        }

        model.addAttribute("data", halfHourAverageList.toArray());
        model.addAttribute("dayData", days);
        model.addAttribute("currentTime", currentTime);
        return "data_charts";
    }

    @GetMapping("/data/charts/today")
    public String dataChartsToday(Model model) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        int currentTime = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);


        String[] days;
        String[] day_names = {"Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};

        List<Datapoint> rawlist;
        List<Datapoint> medianList = new ArrayList<>();
        List<Datapoint> halfHourAverageList = new ArrayList<>();

        rawlist = dbConnector.getAllForDay(currentDay);
        days = getDayData(currentDay, day_names);

        for (int j = 7 * 60 + 30; j < 22 * 60; j += 2) {
            float median = getMedian(rawlist, j);
            medianList.add(new Datapoint(Math.round(median), j / 60, j % 60, currentDay));
        }

        List<Datapoint> points = new ArrayList<>();
        float sum = 0;
        int n = 0;
        for (int j = 0; j < medianList.size(); ++j) {
            Datapoint p = medianList.get(j);
            sum += p.getDatapoint_act();
            ++n;
            if (p.getDatapoint_minute() == 28 || p.getDatapoint_minute() == 58) {
                points.add(new Datapoint(
                        Math.round(sum / n),
                        p.getDatapoint_hour() - (p.getDatapoint_minute() == 28 ? 1 : 0),
                        p.getDatapoint_minute() == 28 ? 0 : 30,
                        p.getDatapoint_day()));
                sum = 0;
                n = 0;
            }
        }

        halfHourAverageList = points;

        model.addAttribute("data", halfHourAverageList.toArray());
        model.addAttribute("dayData", days);
        model.addAttribute("currentTime", currentTime);
        return "data_chart_current";
    }
}
