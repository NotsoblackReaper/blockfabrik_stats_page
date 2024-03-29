package at.demski.blockfabrik_stats_page.service.utils;

import java.sql.Date;
import java.util.Calendar;

public class DateManager {
    public static Date today(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Date(calendar.getTimeInMillis());
    }

    public static int hour(){
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int minute(){
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public static int day(){
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    public static int weekday(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String dayName(int day){
        String[] day_names = {"Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
        return day_names[day];
    }
}
