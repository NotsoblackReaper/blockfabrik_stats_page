package at.demski.blockfabrik_stats_page.controllers;

import at.demski.blockfabrik_stats_page.entities.Datapoint;
import at.demski.blockfabrik_stats_page.service.DatabaseConnector;
import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class IndexController {
    final DatabaseConnector dbConnector;

    public IndexController(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    private float getMedian(List<Datapoint> datapointList, final int time) {
        List<Integer> data = datapointList.stream().filter(d -> d.getDatapoint_hour() * 60 + d.getDatapoint_minute() == time)
                .map(Datapoint::getDatapoint_act).sorted().collect(Collectors.toList());
        return (float) data.stream().mapToInt(value -> value).average().orElse(Double.NaN);
    }

    @GetMapping("/")
    public String homeChart(Model model, @RequestParam(value = "day", required=false) Integer day){
        if(day==null)day=DateManager.day();
        model.addAttribute("current",day);

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int currentDay = day;
        int currentTime = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
        if(c.get(Calendar.HOUR_OF_DAY)<7||c.get(Calendar.HOUR_OF_DAY)<8&&c.get(Calendar.MINUTE)<30)
            currentTime=2400;

        String[] day_names = {"Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};

        List<Datapoint> rawlist;
        List<Datapoint> medianList = new ArrayList<>();
        List<Datapoint> halfHourAverageList = new ArrayList<>();

        rawlist = dbConnector.getAllForDay(currentDay+1);

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
        model.addAttribute("dayName", day_names[day]);
        model.addAttribute("currentTime", currentTime);

        return "index";
    }
}
