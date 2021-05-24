import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class Model {
    private DataSet trainingData;
    private DataSet testingData;
    private MultiLayerConfiguration configuration;
    private MultiLayerNetwork model;

    private Activation ACTIVATION_INITIAL = Activation.TANH;
    private Activation ACTIVATION = Activation.SOFTMAX;
    private WeightInit WEIGHT_INIT = WeightInit.XAVIER;
    private LossFunctions.LossFunction LOSS_FUNCTION = LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD;
    private double LEARNING_RATE = 0.1;
    private double MOMENTUM = 0.9;

    private int FEATURES_COUNT;
    private int CLASSES_COUNT;
    private int HIDDEN_LAYER_NEURONS;

    public Model(DataSet trainingData, DataSet testingData,int features, int classes){
        this.testingData = testingData;
        this.trainingData = testingData;
        this.FEATURES_COUNT = features;
        this.CLASSES_COUNT = classes;
    }

    public void run(){
        this.HIDDEN_LAYER_NEURONS = (CLASSES_COUNT + 1)/2;
        createNNArchitecture();
        runModel();
        evaluate();
    }

    private void createNNArchitecture(){
        configuration = new NeuralNetConfiguration.Builder()
                .activation(ACTIVATION_INITIAL)
                .weightInit(WEIGHT_INIT)
                .updater(new Nesterovs(LEARNING_RATE,MOMENTUM))
                .l2(0.0001)
                .list()
                .layer(0,new DenseLayer.Builder().nIn(FEATURES_COUNT).nOut(HIDDEN_LAYER_NEURONS).build())
                .layer(1,new DenseLayer.Builder().nIn(HIDDEN_LAYER_NEURONS).nOut(HIDDEN_LAYER_NEURONS).build())
                .layer(2,new OutputLayer.Builder(LOSS_FUNCTION)
                                            .activation(ACTIVATION)
                                            .nIn(HIDDEN_LAYER_NEURONS)
                                            .nOut(CLASSES_COUNT)
                                            .build())
                .backprop(true).pretrain(false)
                .build();
    }

    private void runModel(){
        model = new MultiLayerNetwork(configuration);
        model.init();
        model.fit(trainingData);
    }

    private void evaluate(){
        INDArray output = model.output(testingData.getFeatureMatrix());
        Evaluation evaluation = new Evaluation(CLASSES_COUNT);
        evaluation.eval(testingData.getLabels(),output);
        System.out.println(evaluation.stats());
    }
}
