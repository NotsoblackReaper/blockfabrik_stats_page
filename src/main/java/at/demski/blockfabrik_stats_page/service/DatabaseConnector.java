package at.demski.blockfabrik_stats_page.service;

import at.demski.blockfabrik_stats_page.entities.Datapoint;
import at.demski.blockfabrik_stats_page.persistance.DatapointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;

@Component
public class DatabaseConnector {

    final DatapointRepository repository;

    public DatabaseConnector(DatapointRepository repository) {
        this.repository = repository;
    }

    public void insertDatapoint(Date date, Time time, VisitorCount count){
        Datapoint data=new Datapoint();

        data.setDatapoint_id(null);
        Calendar c=Calendar.getInstance();
        c.setTime(date);
        data.setDatapoint_date(date);
        data.setDatapoint_day(c.get(Calendar.DAY_OF_WEEK));
        c.setTime(time);
        data.setDatapoint_hour(c.get(Calendar.HOUR_OF_DAY));
        data.setDatapoint_minute(c.get(Calendar.MINUTE));
        data.setDatapoint_act(count.getCounter());
        data.setDatapoint_max(count.getMaxcount());

        repository.save(data);
    }

    public List<Datapoint> getAllDesc(){
        return repository.findAllDesc();
    }
}
