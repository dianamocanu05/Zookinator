import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static Logger log = LoggerFactory.getLogger("Main.class");

    public static void main(String[] args) throws IOException, InterruptedException {
        DataPrep dataPrep = new DataPrep("data\\zoo-dataset.csv");
        dataPrep.init();
//
//        Model model = new Model(dataPrep.getTrainingData(),dataPrep.getTestingData(),dataPrep.getFEATURES_COUNT(),dataPrep.getCLASSES_COUNT());
//        model.run();
    }
}
