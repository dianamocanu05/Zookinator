import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.util.ClassPathResource;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;

import javax.xml.crypto.Data;
import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataPrep {
    private String path;
    private final List<String> features = new ArrayList<>();
    private final List<String> classes = new ArrayList<>();
    private List<String[]> entries = new ArrayList<>();

    private int BATCH_SIZE = 150;
    private int FEATURES_COUNT = -1;
    private int CLASSES_COUNT = -1;
    private double TRAIN_TEST_RATIO = 0.65;

    private RecordReader recordReader = null;
    private DataSet data = null;
    private DataSet trainingData = null;
    private DataSet testingData = null;

    public DataPrep(String path) {
        this.path = path;
    }

    public DataPrep(String path, int batchSize, double trainTestRatio) {
        this.path = path;
        this.BATCH_SIZE = batchSize;
        this.TRAIN_TEST_RATIO = trainTestRatio;
    }


    public void init() throws IOException, InterruptedException {
        readDataset();
        getFeaturesFromDataset();
        getClassesFromDataset();
        encodeLabels();
        setFeaturesCount(features.size());
        setClassesCount(classes.size());

        loadDataset();
        loadData();
        normalizeData();
        splitTestTrain();
    }

    private void loadDataset() throws IOException, InterruptedException {
        recordReader = new CSVRecordReader(1, ','); //don't read the header
        recordReader.initialize(new FileSplit(new File(path)));
    }

    private void loadData() throws IOException {
        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader,BATCH_SIZE);
        data = iterator.next();
        data.shuffle(7);
    }

    private void normalizeData() {
        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(data);
        normalizer.transform(data);
    }

    private void splitTestTrain() {
        SplitTestAndTrain testAndTrain = data.splitTestAndTrain(TRAIN_TEST_RATIO);
        trainingData = testAndTrain.getTrain();
        testingData = testAndTrain.getTest();
    }

    private void encodeLabels() throws IOException {
        String species;
        String[] newEntry = null;
        List<String[]> newEntries = new ArrayList<>();
        for (String[] entry : entries) {
            species = entry[0];
            newEntry = entry;
            newEntry[0] = String.valueOf(classes.indexOf(species));
            newEntries.add(newEntry);
        }
        createNewCSV("data\\zoo-dataset-transformed.csv", newEntries);
        this.path = "data\\zoo-dataset-transformed.csv";
    }

    private void createNewCSV(String path, List<String[]> entries) throws IOException {
        FileWriter fileWriter = new FileWriter(new File(path));
        CSVWriter writer = new CSVWriter(fileWriter,',', CSVWriter.NO_QUOTE_CHARACTER);
        writer.writeAll(entries);

        writer.close();
        fileWriter.close();

    }

    private void getFeaturesFromDataset() throws IOException {
        String[] header = entries.get(0); //first entry consists of labels
        features.addAll(Arrays.asList(header));
    }

    private void getClassesFromDataset() {
        for (String[] entry : entries) {
            classes.add(entry[0]); //first value of each entry is the class (i.e. animal species)
        }
        classes.remove(0); //first one is a label, not a class
    }

    private void readDataset() throws IOException {
        FileReader fileReader = new FileReader(new File(path));
        CSVReader reader = new CSVReader(fileReader);
        entries = reader.readAll();
        fileReader.close();
        reader.close();
    }

    public List<String> getFeatures() {
        return features;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setFeaturesCount(int count) {
        this.FEATURES_COUNT = count;
    }

    public void setClassesCount(int count) {
        this.CLASSES_COUNT = count;
    }

    public void setBatchSize(int batchSize) {
        this.BATCH_SIZE = batchSize;
    }

    public void setTrainTestRatio(double ratio) {
        this.TRAIN_TEST_RATIO = ratio;
    }

    public DataSet getTrainingData() {
        return trainingData;
    }

    public DataSet getTestingData() {
        return testingData;
    }


    public int getFEATURES_COUNT() {
        return FEATURES_COUNT;
    }

    public int getCLASSES_COUNT() {
        return CLASSES_COUNT;
    }


}
