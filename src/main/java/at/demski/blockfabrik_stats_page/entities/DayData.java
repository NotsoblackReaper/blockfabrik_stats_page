package at.demski.blockfabrik_stats_page.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class DayData {
    @Id
    Integer day_id;
    public Date date;
    public float temp;
    public float rain;
    public float wind;
    public boolean holiday;
    public String holiday_name;
}
