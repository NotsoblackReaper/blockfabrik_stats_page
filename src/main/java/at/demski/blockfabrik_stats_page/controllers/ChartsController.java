package at.demski.blockfabrik_stats_page.controllers;

import at.demski.blockfabrik_stats_page.entities.BlockfabrikDatapoint;
import at.demski.blockfabrik_stats_page.entities.Datapoint;
import at.demski.blockfabrik_stats_page.service.API.DatabaseConnector;
import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChartsController {
    final DatabaseConnector dbConnector;

    public ChartsController(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }
    String[] day_names = {"Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};

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
        int currentTime = 2400;

        List<List<Datapoint>> halfHourAverageList=new ArrayList<>();
        String[][] days = new String[7][4];
        days[0] = getDayData(currentDay, day_names);
        halfHourAverageList.add(dbConnector.getHalfHourAverages(currentDay));

        for (int i = 1, day = 1; i <= 7; ++i) {
            if (i == currentDay)
                continue;
            halfHourAverageList.add(dbConnector.getHalfHourAverages(day));
            days[day] = getDayData(i, day_names);
            day++;
        }

        model.addAttribute("data", halfHourAverageList.toArray());
        model.addAttribute("dayData", days);
        model.addAttribute("currentTime", currentTime);
        return "data_charts";
    }

    //@GetMapping("/data/charts/day/{dayNr}")
    public String dataChartForDay(Model model, @PathVariable("dayNr") int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int currentDay = day;
        int currentTime = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);

        String[] days;
        days = getDayData(currentDay, day_names);

        List<Datapoint> halfHourAverageList = dbConnector.getHalfHourAverages(currentDay);

        model.addAttribute("data", halfHourAverageList.toArray());
        model.addAttribute("dayData", days);
        model.addAttribute("currentTime", currentTime);
        return "data_chart_single";
    }

    @GetMapping("/data/charts/today")
    public String chartToday(Model model) {
        return "redirect:/data/charts/day/"+DateManager.day();
    }

    //@GetMapping("/data/charts/day/{dayNr}")
    public String chartForDay(Model model, @PathVariable("dayNr") int day){
        model.addAttribute("current",day);
        return "data_chart_single";
    }

    @GetMapping("/data/charts/experimental")
    public String dataChartsExperimental(Model model) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        int currentTime = 2400;

        String[][] days = new String[7][4];

        List<List<BlockfabrikDatapoint>> halfHourAverageList = new ArrayList<>();

        halfHourAverageList.add(dbConnector.getHalfHourAveragesNew(currentDay,20));
        days[0] = getDayData(currentDay, day_names);
        for (int i = 1, day = 1; i <= 7; ++i) {
            if (i == currentDay)
                continue;
            days[day] = getDayData(i, day_names);
            halfHourAverageList.add(dbConnector.getHalfHourAveragesNew(i,20));
            day++;
        }

        model.addAttribute("data", halfHourAverageList.toArray());
        model.addAttribute("dayData", days);
        model.addAttribute("currentTime", currentTime);
        return "data_charts_exp";
    }
}
