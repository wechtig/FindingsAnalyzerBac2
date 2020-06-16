package findingsAnalyzer.controller;

import findingsAnalyzer.service.ReportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportsController {
    ReportService reportService = new ReportService();
    @PostMapping("/reports/chart")
    public void sendReport(@RequestBody String data) {
        // to @PathVariable String project
        //            , @PathVariable String startDateStr, @PathVariable String  endDateStr

        // und body die bilder
        reportService.sendReport(data);
    }
}
