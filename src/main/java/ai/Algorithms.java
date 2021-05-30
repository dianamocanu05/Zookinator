package ai;

import api.Prediction;
import api.Response;
import smile.classification.DecisionTree;
import smile.data.DataFrame;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.classifiers.trees.J48;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.io.IOException;

public class Algorithms {
    public static Prediction decisionTree(Response response) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("data\\zoo-dataset.arff");
        Instances data = source.getDataSet();
        System.out.println(data.numInstances() + " instances loaded.");

        String[] opts = new String[] { "-R", "17" };
        Remove remove = new Remove();
        remove.setOptions(opts);
        remove.setInputFormat(data);
        data = Filter.useFilter(data, remove);

        AttributeSelection attSelect = new AttributeSelection();
        InfoGainAttributeEval eval = new InfoGainAttributeEval();
        Ranker search = new Ranker();
        attSelect.setEvaluator(eval);
        attSelect.setSearch(search);
        attSelect.SelectAttributes(data);
        int[] indices = attSelect.selectedAttributes();

        String[] options = new String[1];
        options[0] = "-U";
        J48 tree = new J48();
        tree.setOptions(options);
        tree.buildClassifier(data);
        System.out.println(tree);



        Instance myUnicorn = new DenseInstance(1.0, parseResponse(response));
        myUnicorn.setDataset(data);

        double label = tree.classifyInstance(myUnicorn);
        System.out.println(data.classAttribute().value((int) label));
        return new Prediction(data.classAttribute().value((int) label), 100.0);
    }

    private static double[] parseResponse(Response response){
        double[] vals = new double[19];
        vals[0] = fromBoolean(response.hasHair);
        vals[1] = fromBoolean(response.hasFeathers);
        vals[2] = fromBoolean(response.laysEggs);
        vals[3] = fromBoolean(response.hasMilk);
        vals[4] = fromBoolean(response.isAirborne);
        vals[5] = fromBoolean(response.isAquatic);
        vals[6] = fromBoolean(response.isPredator);
        vals[7] = fromBoolean(response.isToothed);
        vals[8] = fromBoolean(response.hasBackbone);
        vals[9] = fromBoolean(response.breathes);
        vals[10] = fromBoolean(response.isVenomous);
        vals[11] = fromBoolean(response.hasFins);
        vals[12] = response.legs;
        vals[13] = fromBoolean(response.hasTail);
        vals[14] = fromBoolean(response.isDomestic);
        vals[15] = fromBoolean(response.isCatsized);

        return vals;

    }

    private static double fromBoolean(Boolean value){
        return value ? 1d : 0d;
    }
}
