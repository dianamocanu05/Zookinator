package ai;
import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.BaseImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.ImageTransform;
import org.datavec.image.transform.MultiImageTransform;
import org.datavec.image.transform.ShowImageTransform;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class DataPrepImg {
    private final String folderPath;

    private static final String [] allowedExtensions = BaseImageLoader.ALLOWED_FORMATS;
    private final static Random random = new Random(123);
    private int outputNum;
    private FileSplit filesInDir;
    private BalancedPathFilter pathFilter;
    private ParentPathLabelGenerator labelMaker;
    private InputSplit trainData, testingData;
    private ImageRecordReader testReader, trainReader;
    private DataSetIterator trainIterator, testIterator;

    private final int height = 50;
    private final int width = 50;
    private final int channels = 1;
    private final int batchSize = 32;
    private final int labelIndex = 1;



    public DataPrepImg(String folderPath){
        this.folderPath = folderPath;
    }

    public void init() throws IOException {
        loadDataset();
        splitTestTrain();
        transformImages();
        trainIterator = new RecordReaderDataSetIterator(trainReader,batchSize,labelIndex,outputNum);
        trainIterator.next().shuffle();
        testIterator = new RecordReaderDataSetIterator(testReader, batchSize, labelIndex, outputNum);
        testIterator.next().shuffle();

    }

    private void loadDataset(){
        File parentDir = new File(folderPath);
        filesInDir = new FileSplit(parentDir, allowedExtensions, random);
        labelMaker = new ParentPathLabelGenerator();
        pathFilter = new BalancedPathFilter(random,allowedExtensions,labelMaker);
    }

    private void splitTestTrain(){
        InputSplit[] split = filesInDir.sample(pathFilter, 80, 20);
        trainData = split[0];
        testingData = split[1];
    }

    private void transformImages() throws IOException {
        trainReader = new ImageRecordReader(height,width,channels,labelMaker);
        ImageTransform imageTransform = new MultiImageTransform(random);
        trainReader.initialize(trainData,imageTransform);

        testReader = new ImageRecordReader(height,width,channels,labelMaker);
        ImageTransform imageTransformTest = new MultiImageTransform(random);
        testReader.initialize(testingData,imageTransform);

        outputNum = trainReader.numLabels();
    }


    public DataSetIterator getTrainIterator() {
        return trainIterator;
    }

    public DataSetIterator getTestIterator() {
        return testIterator;
    }

//    private List<String> getSubFolders() throws IOException {
//        return Files.walk(Paths.get(folderPath))
//                .filter(Files::isDirectory)
//                .map(p -> p.getFileName().toString())
//                .collect(Collectors.toList());
//    }
//
//    private void resizeImages() throws IOException {
//        for(String zooClass : classes){
//            String classPath = Path.of(folderPath,zooClass).toString();
//            List<String> images = getImagesPath(classPath);
//
//        }
//    }
//
//    private void resizeImage(String path) throws IOException {
//        File file = new File(path);
//        NativeImageLoader loader = new NativeImageLoader(height,width,channels);
//        INDArray image = loader.asMatrix(file);
//        DataNormalization scalar = new ImagePreProcessingScaler(0, 1);
//        scalar.transform(image);
//
//        //output = model.output(image);
//    }
//
//    private List<String> getImagesPath(String folderPath) throws IOException {
//        return Files.walk(Paths.get(folderPath))
//                .filter(Files::isRegularFile)
//                .map(p -> p.toString())
//                .collect(Collectors.toList());
//    }


}
