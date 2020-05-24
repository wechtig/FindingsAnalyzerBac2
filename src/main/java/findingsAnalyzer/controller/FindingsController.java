package findingsAnalyzer.controller;

import findingsAnalyzer.data.Finding;
import findingsAnalyzer.data.Recommendation;
import org.springframework.web.bind.annotation.*;
import findingsAnalyzer.service.FindingsService;
import findingsAnalyzer.service.IgnoreMessageService;
import findingsAnalyzer.service.PdfExporter;
import findingsAnalyzer.service.RecommendationService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class FindingsController {
    FindingsService findingsService = new FindingsService();
    RecommendationService recommendationService = new RecommendationService();
    PdfExporter pdfExporter = new PdfExporter();
    IgnoreMessageService ignoreMessageService= new IgnoreMessageService();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    @GetMapping("/test")
    public String test() {
        LocalDate date;
        return "test";
    }

    @GetMapping("/projects")
    public List<String> getProjectNames() {
        return findingsService.getProjects();
    }

    @GetMapping("/findings/{project}/{startDateStr}/{endDateStr}")
    public List<Finding> getFindingsByProjectAndDates(@PathVariable String project
            , @PathVariable String startDateStr, @PathVariable String  endDateStr) {
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        boolean allProjects = false;

        if(project.equals("alle")) {
            allProjects = true;
        }


        return findingsService.getFindingsByProjectAndDate(project, startDate, endDate, allProjects);
    }


    @PostMapping("/export/{project}/{startDateStr}/{endDateStr}")
    public byte[] exportToPdf(@PathVariable String project
            , @PathVariable String startDateStr, @PathVariable String  endDateStr) {
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        boolean allProjects = false;

        if(project.equals("alle")) {
            allProjects = true;
        }

        byte[] contents = pdfExporter.exportToPdf(project, startDate, endDate, allProjects);
/*
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Here you have to set the actual filename of your pdf
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;*/

        return contents;
    }

    @PostMapping("/recommendation")
    public void saveRecommendation(@RequestBody String valueOne) {

        if(valueOne==null) {
            return;
        }
        String[] parts = valueOne.split("&&");
        String recError = parts[0];
        String recLink = parts[1];

        if(recError == null || recLink == null || recError.isEmpty() || recLink.isEmpty()) {
            return;
        }

        recError.replaceAll("\"", "");
        recLink.replaceAll("\"", "");

        recommendationService.save(new Recommendation(recError, recLink));
    }


    @GetMapping("/recommendations")
    public List<Recommendation> getRecommendations() {
        return recommendationService.findAll();
    }

    @GetMapping("/ignored")
    public List<String> getIgnoredMessages() {
        return ignoreMessageService.findAll();
    }

    @PostMapping("/removeignored")
    public List<String> getIgnoredMessages(@RequestBody String message) {
        return ignoreMessageService.delete(message);
    }

    @PostMapping("/addignored")
    public List<String> addIgnoreMessage(@RequestBody String message) {
        return ignoreMessageService.save(message);
    }
}
