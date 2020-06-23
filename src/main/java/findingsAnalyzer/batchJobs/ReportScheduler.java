package findingsAnalyzer.batchJobs;

import com.itextpdf.text.pdf.BadPdfFormatException;
import findingsAnalyzer.data.ProjectConfig;
import findingsAnalyzer.service.ConfigurationService;
import findingsAnalyzer.service.MailService;
import findingsAnalyzer.service.PdfExporter;
import findingsAnalyzer.service.ReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportScheduler {
    private ConfigurationService configurationService;
    private PdfExporter pdfExporter;
    private MailService mailService;

    private static final int DAYS = 7;

    public ReportScheduler() {
        this.configurationService = new ConfigurationService();
        mailService = new MailService();
        pdfExporter = new PdfExporter();
    }

    @Scheduled(cron = "${findings.check.cron}")
    public void generateReport() {
        List<ProjectConfig> projectConfigList = configurationService.getProjectConfigs();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(DAYS);

        for (ProjectConfig projectConfig : projectConfigList) {
            byte[] pdf = pdfExporter.exportToPdfScheduler(projectConfig.getName(), startDate, endDate, false);
            mailService.sendMail(projectConfig, pdf, startDate, endDate);
        }
    }
}
