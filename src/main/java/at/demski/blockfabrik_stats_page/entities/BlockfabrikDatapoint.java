package at.demski.blockfabrik_stats_page.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class BlockfabrikDatapoint {
    @GeneratedValue
    @Id
    private Integer datapoint_id;
    private Integer day_id;
    private int hour;
    private int minute;
    private int value;
}
