package api;


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

public class ImageController {


    @PostMapping(value = "api/image")
    public ResponseEntity<?> getImage(@ModelAttribute MultipartFile file) throws IOException {
        System.out.println("RECEIVED IMAGE!");
        saveImage(file);
        return new ResponseEntity("Successfully saved!", HttpStatus.OK);
    }

    private void saveImage(MultipartFile file) throws IOException {
        if(!file.isEmpty()){
            byte[] bytes = file.getBytes();
            Files.write(java.nio.file.Path.of("./upload.jpg"),bytes);
        }
    }
}
