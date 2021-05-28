package api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Prediction {
    public String prediction;
    public double confidence;

    public Prediction(String prediction, double confidence){
        this.prediction = prediction;
        this.confidence = confidence;
    }
}
