package at.demski.blockfabrik_stats_page.service.utils.tensorflow;

import org.tensorflow.SavedModelBundle;

public class ModelHandler {
    SavedModelBundle model;
    
    public void loadModel(String name){
        model=SavedModelBundle.load("/model",name);
    }
}
