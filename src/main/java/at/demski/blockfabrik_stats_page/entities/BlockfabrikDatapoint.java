package at.demski.blockfabrik_stats_page.entities;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Entity
public class BlockfabrikDatapoint {
    @GeneratedValue
    @Id
    private Integer datapoint_id;
    @ManyToOne
    @JoinColumn(name = "day_id", nullable = true)
    private DayData day;
    private int hour;
    private int minute;
    private int value;

    public int compareTime(BlockfabrikDatapoint other){
        if(other.getHour()>hour)return 1;
        if(other.getHour()<hour)return -1;
        if(other.getMinute()>minute)return 1;
        if(other.getMinute()<minute)return -1;
        return 0;
    }
}
