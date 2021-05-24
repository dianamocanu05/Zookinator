import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.EmbeddingLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class Model {
    private DataSet trainingData;
    private DataSet testingData;
    private MultiLayerConfiguration configuration;
    private MultiLayerNetwork model;

    private Activation ACTIVATION_INITIAL = Activation.RELU;
    private Activation ACTIVATION = Activation.SOFTMAX;
    private WeightInit WEIGHT_INIT = WeightInit.RELU;
    private LossFunctions.LossFunction LOSS_FUNCTION = LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD;
    private double LEARNING_RATE = 1e-6;
    private double MOMENTUM = 0.9;

    private int FEATURES_COUNT;
    private int CLASSES_COUNT;
    private int HIDDEN_LAYER_NEURONS;
    private int EPOCHS = 10;

    public Model(DataSet trainingData, DataSet testingData,int features, int classes, int epochs){
        this.testingData = testingData;
        this.trainingData = testingData;
        this.FEATURES_COUNT = features;
        this.CLASSES_COUNT = classes;
        this.EPOCHS = epochs;
    }

    public void run(){
        this.HIDDEN_LAYER_NEURONS = (CLASSES_COUNT + 1)/2;
        createNNArchitecture();
        runModel();
        evaluate();
    }

    private void createNNArchitecture(){
    configuration = new NeuralNetConfiguration.Builder()
            .seed(123)
            .weightInit(WeightInit.XAVIER)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .updater(new Nesterovs(0.001,0.9))
            .l2(1e-4)
            .list()
            .layer(0, new DenseLayer.Builder()
                    .nIn(INPUT_NEURONS)
                    .nOut(HIDDEN_NEURONS)
                    .activation(Activation.RELU)
                    .build())
            .layer(1, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nIn(HIDDEN_NEURONS)
                    .nOut(OUTPUT_NEURONS)
                    .activation(Activation.SOFTMAX)
                    .build())
            .pretrain(false).backprop(true).build();
//        configuration = new NeuralNetConfiguration.Builder()
//                .list()
//                .layer(0,new DenseLayer.Builder().nIn(FEATURES_COUNT).nOut(HIDDEN_LAYER_NEURONS).build())
//                .layer(1, new DenseLayer.Builder().nIn(HIDDEN_LAYER_NEURONS).nOut(HIDDEN_LAYER_NEURONS).build())
//                .layer(2, new DenseLayer.Builder().nIn(HIDDEN_LAYER_NEURONS).nOut(CLASSES_COUNT).build())

    }

    private void runModel(){
        model = new MultiLayerNetwork(configuration);
        model.init();
        //model.setListeners(new ScoreIterationListener(10));
        for(int epoch=0; epoch<EPOCHS; epoch++) {
            model.fit(trainingData);
        }
    }

    private void evaluate(){
        INDArray output = model.output(testingData.getFeatureMatrix());
        Evaluation evaluation = new Evaluation(CLASSES_COUNT);
        evaluation.eval(testingData.getLabels(),output);
        System.out.println(evaluation.stats());
    }
}
