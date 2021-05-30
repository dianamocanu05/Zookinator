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
@RestController
public class ImageController {


    @PostMapping
    @RequestMapping("api/image")
    public ResponseEntity<?> getImage(@ModelAttribute FormWrapper formWrapper) throws IOException {
        System.out.println("RECEIVED IMAGE!");
        try {
            saveImage(formWrapper.getImage());
        }catch (Exception e){
            System.out.println("NEY");
            return new ResponseEntity("NASPAAA", HttpStatus.I_AM_A_TEAPOT);
        }
        return new ResponseEntity("Successfully saved!", HttpStatus.OK);
    }

    private void saveImage(MultipartFile file) throws IOException {
        if(!file.isEmpty()){
            byte[] bytes = file.getBytes();
            Files.write(java.nio.file.Path.of("./upload.jpg"),bytes);
        }
    }
}
