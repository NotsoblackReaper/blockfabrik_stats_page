package at.demski.blockfabrik_stats_page.controllers;

import at.demski.blockfabrik_stats_page.BlockfabrikStatsPageApplication;
import at.demski.blockfabrik_stats_page.service.API.DatabaseConnector;
import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import at.demski.blockfabrik_stats_page.service.utils.tensorflow.ModelHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class IndexController {
    final DatabaseConnector dbConnector;
    final ModelHandler modelHandler;

    public IndexController(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
        this.modelHandler=new ModelHandler();
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

    @GetMapping("/experimental")
    public String homeChart(Model model,
                            @RequestParam(value = "day", required=false) Integer day,
                            @RequestParam(value = "hour", required=false) Integer hour,
                            @RequestParam(value = "minute", required=false) Integer minute){
        if(day==null)day= DateManager.day()-1;
        model.addAttribute("current",day);
        model.addAttribute("dayName", DateManager.dayName(day));

        int currentTime = DateManager.hour() * 100 + DateManager.minute();
        //If it is before opening hours, set the time to midnight, as to show everything in colour
        if(DateManager.hour()<7||DateManager.hour()<8&&DateManager.minute()<30)currentTime=2400;
        model.addAttribute("currentTime", currentTime);

        //If the selected day is the current day, show predicted values, otherwise show averages
        if(BlockfabrikStatsPageApplication.tf_support&&DateManager.day()-1==day){
            int usedHour=hour==null? DateManager.hour():hour;
            int usedMinute=hour==null? DateManager.minute():minute;
            model.addAttribute("data", modelHandler.getPrediction(dbConnector.getValuesForDay(DateManager.today(),usedHour,usedMinute),usedHour,usedMinute,20));
        }else{
            model.addAttribute("data", dbConnector.getHalfHourAveragesNew(day+1,20));
        }

        return "index_exp";
    }
}
