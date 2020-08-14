package findingsAnalyzer.controller;

import findingsAnalyzer.data.Comment;
import findingsAnalyzer.data.Finding;
import findingsAnalyzer.data.Recommendation;
import findingsAnalyzer.service.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class FindingsController {
    FindingsService findingsService = new FindingsService();
    RecommendationService recommendationService = new RecommendationService();
    PdfExporter pdfExporter = new PdfExporter();
    IgnoreMessageService ignoreMessageService= new IgnoreMessageService();
    CommentService commentService = new CommentService();

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

    @GetMapping("/findings/user/{project}/{startDateStr}/{endDateStr}")
    public List<Finding> getFindingsInformationForUser(@PathVariable String project
            , @PathVariable String startDateStr, @PathVariable String  endDateStr) {
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);

        return findingsService.getFindingsByProjectAndDateForUser(project, startDate, endDate);
    }

    @GetMapping("/findings/comment/{project}/{dateStr}/{file}/{lineStr}/{message}")
    public List<Comment> getComments(@PathVariable String project
            , @PathVariable String dateStr, @PathVariable String  file,  @PathVariable String  lineStr,  @PathVariable String  message) {
        final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, formatter2);
        int line = Integer.parseInt(lineStr);

        List<Finding> findings = findingsService.getFindingByProjectDateMessageFile(project, date, message, file, line);
        return  commentService.findCommentsByMessageAndDate(findings);
    }

    @PostMapping("/findings/comment/{project}/{date}/{file}/{line}/{message}/{text")
    public void saveComment(@PathVariable String project
            , @PathVariable String dateStr, @PathVariable String  file,  @PathVariable String  lineStr,  @PathVariable String  message, String text) {
        LocalDate date = LocalDate.parse(dateStr, formatter);
        int line = Integer.parseInt(lineStr);

        List<Finding> findings = findingsService.getFindingByProjectDateMessageFile(project, date, message, file, line);
        commentService.saveComment(findings.get(0), text);
    }

    @PostMapping("/findings/comment")
    public void saveComment2(@RequestBody String valueOne) {
        final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String[] parts = valueOne.split("&&");
        String project = parts[0];
        String dateStr = parts[1];
        String file = parts[2];
        String lineStr = parts[3];
        int line = Integer.parseInt(lineStr);
        String message = parts[4];
        String commment = parts[5];
        LocalDate date = LocalDate.parse(dateStr, formatter2);

        List<Finding> findings = findingsService.getFindingByProjectDateMessageFile(project, date, message, file, line);
        if(findings == null || findings.isEmpty()) {
            return;
        }
        commentService.saveComment(findings.get(0), commment);
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
