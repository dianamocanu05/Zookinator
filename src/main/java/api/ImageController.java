package api;


import org.springframework.hateoas.server.core.WebHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import ai.Model;
@RestController
public class ImageController {

    private static final String tmpPath = "./upload.jpg";
    @PostMapping
    @RequestMapping("api/image")
    public Prediction getImage(@ModelAttribute FormWrapper formWrapper) throws IOException {
        System.out.println("RECEIVED IMAGE!");
        Prediction prediction = null;
        try {
            saveImage(formWrapper.getImage());
            prediction = new Prediction("cleo",100.0);
            //prediction = Model.predict(tmpPath);
        }catch (Exception e){
            return null;
        }
        return prediction;
    }

    private void saveImage(MultipartFile file) throws IOException {
        if(!file.isEmpty()){
            byte[] bytes = file.getBytes();
            Files.write(java.nio.file.Path.of(tmpPath),bytes);
        }
    }
}
