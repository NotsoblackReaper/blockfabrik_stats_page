package at.demski.blockfabrik_stats_page.service.utils;

import at.demski.blockfabrik_stats_page.entities.BlockfabrikDatapoint;

import java.util.ArrayList;
import java.util.List;

public class DatapointUtils {
    public static List<BlockfabrikDatapoint> averageDayValues(List<BlockfabrikDatapoint>values,int averageMinutes){
        List<BlockfabrikDatapoint>averages=new ArrayList<>();
        BlockfabrikDatapoint tmp=null;
        float tmpSum=0;
        for(int i=0,n=0;i<values.size();++i,++n){
            if(values.get(i).getMinute()%averageMinutes==0){
                if(tmp!=null) {
                    tmp.setValue((int) (tmpSum/n));
                    n=0;
                    tmpSum=0;
                    averages.add(tmp);
                    //System.out.println(minutes+" min Avg: \n"+tmp.getValue()+"\n");
                }
                tmp=new BlockfabrikDatapoint(values.get(i));
            }
            tmpSum+=values.get(i).getValue();
        }
        return averages;
    }


}
