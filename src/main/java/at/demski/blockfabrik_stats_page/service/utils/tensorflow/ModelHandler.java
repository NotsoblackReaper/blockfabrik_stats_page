package at.demski.blockfabrik_stats_page.service.utils.tensorflow;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.exceptions.TensorFlowException;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.types.TFloat32;

import java.util.HashMap;
import java.util.Map;

public class ModelHandler {
    SavedModelBundle model;

    final String DEFAULT_TYPE="complex";
    final String DEFAULT_NAME="model1";
    final int DEFAULT_VERSION=1;

    public ModelHandler(){
        loadModel(DEFAULT_TYPE,DEFAULT_NAME,DEFAULT_VERSION);
    }

    public void loadModel(String type,String name,int version){
        try {
            model=SavedModelBundle.load("./tf-models/"+type+"/"+name+"/"+version+"/", "serve");
        }
        catch (TensorFlowException ex) {
            ex.printStackTrace();
        }
    }

    public float predict(int weekday,float temp, float rain, float wind,int hour,int minute,int	hisVal5min,int	hisVal10min,int	hisVal15min){
        float []data={weekday,temp,rain,wind,hour,minute,hisVal5min,hisVal10min,hisVal15min};
        float prediction=predict(data)[0];
        System.out.println("Values:\n["+weekday+", "+temp+", "+rain+", "+wind+", "+hour+", "+minute+", "+hisVal5min+", "+hisVal10min+", "+hisVal15min+"]\nPrediction: "+prediction);
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
}
