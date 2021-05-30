package api;

import ai.Algorithms;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResponseController{

    @PostMapping
    @RequestMapping("api/send")
    private Prediction sendResponse(@RequestBody Response response) throws Exception {
        System.out.println("Received the user responses...");
        Prediction prediction = Algorithms.decisionTree(response);
        System.out.println("Sending prediction...");
        return prediction;
    }
}
