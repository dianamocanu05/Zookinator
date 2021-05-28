package api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResponseController{

    @PostMapping
    @RequestMapping("api/send")
    private Prediction sendResponse(@RequestBody Response response){
        System.out.println("Received the user responses...");
        //predict
        System.out.println(response.breathes + " " + response.legs);
        System.out.println("Sending prediction...");
        return new Prediction("caine cleo",100.0);
    }
}
