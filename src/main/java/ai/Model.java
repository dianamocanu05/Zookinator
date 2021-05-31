package ai;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.modelimport.keras.layers.core.KerasFlatten;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.FileStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.deeplearning4j.ui.api.UIServer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Model {
    private final DataSetIterator trainingData;
    private final DataSetIterator testingData;
    private MultiLayerConfiguration configuration;
    private MultiLayerNetwork model;

    //CONFIG: TO MOVE IN A MODEL.PROPERTIES FILE
    private final Activation ACTIVATION_INITIAL = Activation.RELU;
    private final Activation ACTIVATION = Activation.SOFTMAX;
    private final WeightInit WEIGHT_INIT = WeightInit.RELU;
    private final LossFunctions.LossFunction LOSS_FUNCTION = LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD;
    private final double LEARNING_RATE = 1e-6;
    private final double MOMENTUM = 0.9;
    private final int INPUT_NEURONS = 10;
    private final int HIDDEN_NEURONS = 10;
    private final int OUTPUT_NEURONS = 10;
    private final int WIDTH = 50;
    private final int HEIGHT = 50;
    private final static String[] classes = {"butterfly","cats","cow","dogs","elephant","hen","horse","monkey","panda","sheep","spider","squirrel"};
    private int EPOCHS = 10;


    private int HIDDEN_LAYER_NEURONS;


    public Model(DataSetIterator trainingData, DataSetIterator testingData, int epochs){
        this.testingData = testingData;
        this.trainingData = testingData;
        this.EPOCHS = epochs;
    }

    public void run() throws IOException {
        //this.HIDDEN_LAYER_NEURONS = (CLASSES_COUNT + 1)/2;
        createNNArchitecture();
        runModel();
        evaluate();
    }
    public void createNNArchitecture()  {
        int seed = 4;
        int channels = 1;
        int numLabels = 12;
        configuration = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .updater(new Adam(0.01))
                .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT)
                .weightInit(WeightInit.RELU)
                .list()
                .layer(new ConvolutionLayer.Builder().kernelSize(3,3).stride(1,1).padding(1,1).activation(Activation.RELU)
                        .nIn(numLabels).nOut(16).build())
                .layer(new BatchNormalization())
                .layer(new SubsamplingLayer.Builder().kernelSize(2,2).stride(1,1).poolingType(SubsamplingLayer.PoolingType.MAX).build())
                .layer(new ConvolutionLayer.Builder().kernelSize(3,3).stride(1,1).padding(1,1).activation(Activation.RELU)
                        .nOut(32).build())
                .layer(new BatchNormalization())
                .layer(new SubsamplingLayer.Builder().kernelSize(2,2).stride(1,1).poolingType(SubsamplingLayer.PoolingType.MAX).build())
                .layer(new ConvolutionLayer.Builder().kernelSize(3,3).stride(1,1).padding(1,1).activation(Activation.RELU)
                        .nOut(64).build())
                .layer(new BatchNormalization())
                .layer(new SubsamplingLayer.Builder().kernelSize(2,2).stride(2,2).poolingType(SubsamplingLayer.PoolingType.MAX).build())
                .layer(new DenseLayer.Builder().nOut(64).activation(Activation.RELU).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .name("output")
                        .nOut(numLabels)
                        .dropOut(0.2)
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutional(HEIGHT, WIDTH, channels))
                .build();

    }

    private void runModel() throws IOException {
        model = new MultiLayerNetwork(configuration);
        model.init();
        //model.setListeners(new ScoreIterationListener(1));
        UIServer uiServer = UIServer.getInstance();

        //Configure where the network information (gradients, activations, score vs. time etc) is to be stored
        //Then add the StatsListener to collect this information from the network, as it trains
        StatsStorage statsStorage = new FileStatsStorage(new File(System.getProperty("java.io.tmpdir"), "ui-stats.dl4j"));
        int listenerFrequency = 1;
        model.setListeners(new StatsListener(statsStorage, listenerFrequency));

        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);
        for(int epoch=0; epoch<EPOCHS; epoch++) {
            model.fit(trainingData);
        }
        model.save(new File("./model.h5"));
    }

    private void evaluate(){
        Evaluation evaluation = model.evaluate(testingData);
        System.out.println(evaluation.stats());
    }

    public static String predict(String path) throws IOException {
        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(new File("./model.h5"),true);
        File f=new File(path);
        NativeImageLoader loader = new NativeImageLoader(50, 50, 1);
        INDArray image = loader.asMatrix(f);
        DataNormalization scalar = new ImagePreProcessingScaler(0, 1);
        scalar.transform(image);
        INDArray output = model.output(image);
        return explainPrediction(output);
    }

    private static String explainPrediction(INDArray array){
        double[] predictions = array.toDoubleVector();
        double maximum = -1;
        int maxI = -1;
        for(int i=0;i<11;i++){
            if(predictions[i] > maximum){
                maximum = predictions[i];
                maxI = i;
            }
        }

        return classes[maxI];
    }
}
