package at.demski.blockfabrik_stats_page.service.utils.tensorflow;

import at.demski.blockfabrik_stats_page.BlockfabrikStatsPageApplication;
import at.demski.blockfabrik_stats_page.entities.BlockfabrikDatapoint;
import at.demski.blockfabrik_stats_page.entities.WeatherData;
import at.demski.blockfabrik_stats_page.service.API.DatabaseConnector;
import at.demski.blockfabrik_stats_page.service.API.WeatherAPI;
import at.demski.blockfabrik_stats_page.service.utils.DatapointUtils;
import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.exceptions.TensorFlowException;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.types.TFloat32;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelHandler {
    Logger logger = LoggerFactory.getLogger(ModelHandler.class);
    SavedModelBundle model;

    final String DEFAULT_TYPE="complex";
    final String DEFAULT_NAME="model1";
    final int DEFAULT_VERSION=1;

    public ModelHandler(){
        if(!BlockfabrikStatsPageApplication.tf_support)return;
        logger.info("Creating Model Handler");
        logger.info("TF Version: "+TensorFlow.version());
        loadModel(DEFAULT_TYPE,DEFAULT_NAME,DEFAULT_VERSION);
    }

    public void loadModel(String type,String name,int version){
        try {
            Resource resource = new ClassPathResource("tf-models"+ File.separator+type+File.separator+name+File.separator+version+File.separator);
            model=SavedModelBundle.load(resource.getFile().getPath(), "serve");
            logger.info("Successfully loaded model!");
        }
        catch (TensorFlowException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public float predict(int weekday,float temp, float rain, float wind,int hour,int minute,int	hisVal5min,int	hisVal10min,int	hisVal15min){
        if(!BlockfabrikStatsPageApplication.tf_support)return 0;
        float []data={weekday,temp,rain,wind,hour,minute,hisVal5min,hisVal10min,hisVal15min};
        float prediction=predict(data)[0];
        //System.out.println("Values:\n["+weekday+", "+temp+", "+rain+", "+wind+", "+hour+", "+minute+", "+hisVal5min+", "+hisVal10min+", "+hisVal15min+"]\nPrediction: "+prediction);
        return prediction;
    }

    public float[] predict(float[]data){
        //float []data= {4.0f,-2.1f,0.0f,4.5f,9.0f,30.0f,29.0f,28.0f,27.0f};//target= 29.278
        try{
            Map<String, Tensor> inArgs=new HashMap<>();
            inArgs.put("normalization_input", TFloat32.tensorOf(NdArrays.vectorOf(data)));

            var out=model.call(inArgs);

            float[]res=new float[out.size()];
            int i=0;
            for(var t:out.values()){
                res[i++]=((TFloat32)t).getFloat();
            }
            return res;
        } catch (TensorFlowException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<BlockfabrikDatapoint> getPrediction(List<BlockfabrikDatapoint>facts,int currentHour,int currentMinute, int averageLength){
        currentMinute= currentMinute-currentMinute%5;
        if(currentHour<7||(currentHour==7&&currentMinute<30)){
            currentHour=7;
            currentMinute=25;
        }

        Collections.reverse(facts);

        WeatherData weatherData= WeatherAPI.getCurrentWeather();

        while(currentHour<22){
            currentMinute+=5;
            if(currentMinute==60){
                ++currentHour;
                currentMinute=0;
            }
            int his5=facts.size()>1?facts.get(facts.size()-1).getValue():0;
            int his10=facts.size()>2?facts.get(facts.size()-2).getValue():0;
            int his15=facts.size()>3?facts.get(facts.size()-3).getValue():0;
            float val=predict(DateManager.day(),weatherData.temperature, weatherData.downpour, weatherData.wind, currentHour,currentMinute,his5,his10,his15);
            int prediction= val<150? Math.round(val):(int)Math.floor(val);
            if(prediction>250)
                prediction=250;
            facts.add(new BlockfabrikDatapoint(currentHour,currentMinute,prediction));
        }

        return DatapointUtils.averageDayValues(facts,averageLength);
    }
}
