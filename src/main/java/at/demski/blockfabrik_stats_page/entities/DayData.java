package at.demski.blockfabrik_stats_page.entities;

import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class DayData {
    @GeneratedValue
    @Id
    Integer day_id;
    public Date date;
    public int weekday;
    public float temp;
    public float rain;
    public float wind;
    public boolean holiday;
    public String holiday_name;
    public boolean historic;

    public DayData(Date date, float temp, float rain, float wind, boolean holiday, String holiday_name, boolean historic) {
        this.date = date;
        this.weekday= DateManager.weekday(date);
        this.temp = temp;
        this.rain = rain;
        this.wind = wind;
        this.holiday = holiday;
        this.holiday_name = holiday_name;
        this.historic = historic;
    }
}
