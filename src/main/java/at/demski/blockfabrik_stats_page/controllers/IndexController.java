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

    @GetMapping("/")
    public String homeChart(Model model, @RequestParam(value = "day", required=false) Integer day){
        if(day==null)day=DateManager.day()-1;
        model.addAttribute("current",day);

        int currentTime = DateManager.hour() * 100 + DateManager.minute();
        if(DateManager.hour()<7||DateManager.hour()<8&&DateManager.minute()<30)
            currentTime=2400;

        model.addAttribute("data", dbConnector.getHalfHourAverages(day+1).toArray());
        model.addAttribute("dayName", DateManager.dayName(day));
        model.addAttribute("currentTime", currentTime);

        return "index";
    }
}
