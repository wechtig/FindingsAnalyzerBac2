package findingsAnalyzer.controller;

import findingsAnalyzer.service.ReportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

@RestController
public class ReportsController {
    ReportService reportService = new ReportService();
    @PostMapping("/reports/chart")
    public void sendReport(@RequestBody String data) {
        // to @PathVariable String project
        //            , @PathVariable String startDateStr, @PathVariable String  endDateStr

        // und body die bilder
        String[] strings = data.split(",");
        String extension;
        switch (strings[0]) {//check image's extension
            case "data:image/jpeg;base64":
                extension = "jpeg";
                break;
            case "data:image/png;base64":
                extension = "png";
                break;
            default://should write cases for more images types
                extension = "jpg";
                break;
        }
        //convert base64 string to binary data
        byte[] dataimg = Base64.getDecoder().decode(strings[1]);
        String path = "images\\test_image.gif";
        File file = new File(path);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            outputStream.write(dataimg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
