package at.demski.blockfabrik_stats_page.service.utils.tensorflow;

import org.tensorflow.*;
import org.tensorflow.exceptions.TensorFlowException;
import org.tensorflow.ndarray.NdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.op.Ops;
import org.tensorflow.op.core.Placeholder;
import org.tensorflow.op.core.Shapes;
import org.tensorflow.op.math.Add;
import org.tensorflow.types.TFloat32;
import org.tensorflow.types.TInt32;

import java.util.HashMap;
import java.util.Map;

public class HelloTensorFlow {

    public static Session.Runner tryFeed(Session.Runner runner, String op, TFloat32 data){
        try{
            runner.feed(op,data);
            System.out.println(op);
            return runner;
        }catch (IllegalArgumentException e){
            System.err.println(e.getMessage());
        }
        return runner;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Hello TensorFlow " + TensorFlow.version());
        float []data= {4.0f,-2.1f,0.0f,4.5f,9.0f,30.0f,29.0f,28.0f,27.0f};//target= 29.278
        try (SavedModelBundle savedModelBundle = SavedModelBundle.load("./tf-models/complex/model1/1/", "serve")) {

            System.out.println("Operations: ");
            var it=savedModelBundle.graph().operations();
            Operation o;
            while((o=it.next())!=null){
                System.out.println(o+"\nName: "+o.name()+"\n#-Outputs: "+o.numOutputs()+"\nType: "+o.type()+"\n");
            }

            Map<String,Tensor> inArgs=new HashMap<>();
            inArgs.put("normalization_input",TFloat32.tensorOf(NdArrays.vectorOf(data)));

            var out=savedModelBundle.call(inArgs).get("dense_17");
            float res=((TFloat32)out).getFloat();

        } catch (TensorFlowException ex) {
            ex.printStackTrace();
        }
    }

    private static Signature dbl(Ops tf) {
        Placeholder<TInt32> x = tf.placeholder(TInt32.class);
        Add<TInt32> dblX = tf.math.add(x, x);
        return Signature.builder().input("x", x).output("dbl", dblX).build();
    }
}

