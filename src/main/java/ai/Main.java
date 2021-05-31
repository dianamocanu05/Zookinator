package ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger("Main.class");

    public static void main(String[] args) throws IOException, InterruptedException {
        DataPrepImg dataPrepImg = new DataPrepImg("data\\images");
        dataPrepImg.init();

    }
}
