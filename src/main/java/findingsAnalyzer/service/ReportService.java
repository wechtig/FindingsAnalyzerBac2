package findingsAnalyzer.service;

import findingsAnalyzer.data.Finding;
import findingsAnalyzer.data.ProjectConfig;

import java.time.LocalDate;
import java.util.List;

public class ReportService {
    FindingsService findingsService = new FindingsService();
    PdfExporter pdfExporter = new PdfExporter();
    ConfigurationService configurationService = new ConfigurationService();
    MailService mailService = new MailService();

    public void sendReportWithFindings(String project, LocalDate startDate, LocalDate endDate, String chart1, String chart2, String chart3) {
        List<Finding> findings = findingsService.getFindingsByProjectAndDate(project, startDate, endDate, false);
        byte[] pdfReport = pdfExporter.generateMailReportWithFindings(findings, project, startDate, endDate, chart1, chart2, chart3);
        ProjectConfig projectConfig = configurationService.findConfigByProjectname(project);
        mailService.sendMail(projectConfig, pdfReport, startDate, endDate);
    }

    public void sendReportWithoutFindings(String project, LocalDate startDate, LocalDate endDate, String chart1, String chart2, String chart3) {
        byte[] pdfReport = pdfExporter.generateMailReportWithoutFindings(project, startDate, endDate, chart1, chart2, chart3);
        ProjectConfig projectConfig = configurationService.findConfigByProjectname(project);
        mailService.sendMail(projectConfig, pdfReport, startDate, endDate);
    }

    public void sendReportWithFindingsToMail(String project, LocalDate startDate, LocalDate endDate, String chart1, String chart2, String chart3, String mail) {
        List<Finding> findings = findingsService.getFindingsByProjectAndDate(project, startDate, endDate, false);
        byte[] pdfReport = pdfExporter.generateMailReportWithFindings(findings, project, startDate, endDate, chart1, chart2, chart3);
        ProjectConfig projectConfig = configurationService.findConfigByProjectname(project);
        mailService.sendMailToAdress(mail, pdfReport, startDate, endDate);
    }

    public void sendReportWithoutFindingsToMail(String project, LocalDate startDate, LocalDate endDate, String chart1, String chart2, String chart3, String mail) {
        byte[] pdfReport = pdfExporter.generateMailReportWithoutFindings(project, startDate, endDate, chart1, chart2, chart3);
        ProjectConfig projectConfig = configurationService.findConfigByProjectname(project);
        mailService.sendMailToAdress(mail, pdfReport, startDate, endDate);
    }
}
