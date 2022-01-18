package at.demski.blockfabrik_stats_page.controllers;

import at.demski.blockfabrik_stats_page.entities.Datapoint;
import at.demski.blockfabrik_stats_page.service.DatabaseConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class DataController {
    final DatabaseConnector dbConnector;

    public DataController(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    @GetMapping("/data/list")
    public String dataList(Model model) {
        model.addAttribute("entryList",dbConnector.getAllDesc());
        return "data_list";
    }

    @GetMapping("/data/charts")
    public String dataCharts(Model model) {
        Calendar c=Calendar.getInstance();
        c.setTime(new Date());
        int currentDay=c.get(Calendar.DAY_OF_WEEK);
        String [][]days=new String[7][4];
        String[]day_names={"Sonntag","Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag"};
        List<List<Datapoint>> list=new ArrayList<>();

        list.add(dbConnector.getAllForDay(currentDay));
        days[0][0]= String.valueOf(currentDay);
        days[0][1]= "dataDay"+currentDay;
        days[0][2]= day_names[currentDay-1];
        days[0][3]="chart_day"+currentDay;

        //System.out.println("Current day "+currentDay);
        for(int i=1,day=1;i<=7;++i) {
            if (i == currentDay)
                continue;
            list.add(dbConnector.getAllForDay(i));
            days[day][0] = String.valueOf(i);
            days[day][1] = "dataDay" + i;
            days[day][2] = day_names[i - 1];
            days[day][3] = "chart_day" + i;
            //System.out.println("Day " + i + " " + day_names[i - 1]);
            ++day;
        }

        model.addAttribute("data",list.toArray());
        model.addAttribute("dayData",days);
        return "data_charts";
    }
}
