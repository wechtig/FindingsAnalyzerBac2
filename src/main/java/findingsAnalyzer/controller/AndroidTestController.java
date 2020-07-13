package findingsAnalyzer.controller;

import findingsAnalyzer.data.Finding;
import findingsAnalyzer.data.ImageContainer;
import findingsAnalyzer.service.FindingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class AndroidTestController {
    FindingsService findingsService = new FindingsService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @GetMapping("/android/projects")
    public List<String> getProjectNames() {
        return findingsService.getProjects();
    }

    @GetMapping("/android/findings/{project}/{startDateStr}/{endDateStr}")
    public List<Finding> getFindingsByProjectAndDates(@PathVariable String project
            , @PathVariable String startDateStr, @PathVariable String  endDateStr) {
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        boolean allProjects = false;

        if(project.equals("alle")) {
            allProjects = true;
        }


        return findingsService.getFindingsForSchedulerByProjectAndDate(project, startDate, endDate, allProjects);
    }

    @GetMapping("/android/findings/images/{project}/{startDateStr}/{endDateStr}")
    public List<ImageContainer> getFindingsImagesByProjectAndDates(@PathVariable String project
            , @PathVariable String startDateStr, @PathVariable String  endDateStr) {
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        boolean allProjects = false;

        if(project.equals("alle")) {
            allProjects = true;
        }

        return findingsService.getFindingsForSchedulerImageByProjectAndDate(project, startDate, endDate, allProjects);
    }
}
