package findingsAnalyzer.controller;

import findingsAnalyzer.service.ReportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@RestController
public class ReportsController {
    ReportService reportService = new ReportService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @PostMapping("/reports/chart/findings/false")
    public void sendReportWithoutFindings(@RequestBody String data) {
        // to @PathVariable String project
        //            , @PathVariable String startDateStr, @PathVariable String  endDateStr

        // und body die bilder
        String[] chartData = data.split("&&");
        String project = chartData[0];
        String start = chartData[1];
        String end = chartData[2];
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        String chart1 = chartData[3];
        String pathImg1 = saveImages(chart1, project+"_chart1_"+ LocalDate.now().toString());

        String chart2 = chartData[4];
        String pathImg2 = saveImages(chart2, project+"_chart2_"+ LocalDate.now().toString());


        String chart3 = chartData[5];
        String pathImg3 = saveImages(chart3, project+"_chart3_"+ LocalDate.now().toString());

        reportService.sendReportWithoutFindings(project, startDate, endDate, pathImg1, pathImg2, pathImg3);
    }

    @PostMapping("/reports/chart/findings/true")
    public void sendReportWithFindings(@RequestBody String data) {
        // to @PathVariable String project
        //            , @PathVariable String startDateStr, @PathVariable String  endDateStr

        // und body die bilder
        String[] chartData = data.split("&&");
        String project = chartData[0];
        String start = chartData[1];
        String end = chartData[2];
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        String chart1 = chartData[3];
        String pathImg1 = saveImages(chart1, project+"_chart1_"+ LocalDate.now().toString());

        String chart2 = chartData[4];
        String pathImg2 = saveImages(chart2, project+"_chart2_"+ LocalDate.now().toString());

        String chart3 = chartData[5];
        String pathImg3 = saveImages(chart3, project+"_chart3_"+ LocalDate.now().toString());
        reportService.sendReportWithFindings(project, startDate, endDate, pathImg1, pathImg2, pathImg3);
    }

    @PostMapping("/reports/chart/findings/true/mail")
    public void sendReportWithFindingsManuel(@RequestBody String data) {
        // to @PathVariable String project
        //            , @PathVariable String startDateStr, @PathVariable String  endDateStr

        // und body die bilder
        String[] chartData = data.split("&&");
        String project = chartData[0];
        String start = chartData[1];
        String end = chartData[2];
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        String chart1 = chartData[3];
        String pathImg1 = saveImages(chart1, project+"_chart1_"+ LocalDate.now().toString());

        String chart2 = chartData[4];
        String pathImg2 = saveImages(chart2, project+"_chart2_"+ LocalDate.now().toString());

        String chart3 = chartData[5];
        String mail = chartData[6];
        String pathImg3 = saveImages(chart3, project+"_chart3_"+ LocalDate.now().toString());
        reportService.sendReportWithFindingsToMail(project, startDate, endDate, pathImg1, pathImg2, pathImg3, mail);
    }

    @PostMapping("/reports/chart/findings/false/mail")
    public void sendReportWithoutFindingsManuel(@RequestBody String data) {
        // to @PathVariable String project
        //            , @PathVariable String startDateStr, @PathVariable String  endDateStr

        // und body die bilder
        String[] chartData = data.split("&&");
        String project = chartData[0];
        String start = chartData[1];
        String end = chartData[2];
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        String chart1 = chartData[3];
        String pathImg1 = saveImages(chart1, project+"_chart1_"+ LocalDate.now().toString());

        String chart2 = chartData[4];
        String pathImg2 = saveImages(chart2, project+"_chart2_"+ LocalDate.now().toString());

        String chart3 = chartData[5];
        String mail = chartData[6];
        String pathImg3 = saveImages(chart3, project+"_chart3_"+ LocalDate.now().toString());
        reportService.sendReportWithoutFindingsToMail(project, startDate, endDate, pathImg1, pathImg2, pathImg3, mail);
    }


    private String saveImages(String data, String name) {
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
        String path = "images\\"+name+".gif";
        File file = new File(path);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            outputStream.write(dataimg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }
}
