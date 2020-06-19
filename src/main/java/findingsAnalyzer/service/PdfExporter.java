package findingsAnalyzer.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfWriter;
import findingsAnalyzer.data.Finding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class PdfExporter {
    FindingsService findingsService = new FindingsService();
    private final Font titleFont = FontFactory.
            getFont(FontFactory.HELVETICA, 18, Font.NORMAL, new CMYKColor(0.7301f, 0.2086f, 0.0000f, 0.3608f));
    private final Font projectFont = FontFactory.
            getFont(FontFactory.HELVETICA, 14, Font.NORMAL, new CMYKColor(0.7301f, 0.2086f, 0.0000f, 0.3608f));
    private final Font textFont = FontFactory.
            getFont(FontFactory.HELVETICA, 10, Font.NORMAL);

    public byte[] generateMailReportWithFindings() {
       return null;
    }

    public byte[] generateMailReportWithoutFindings(List<Finding> findings, String projcetname, LocalDate startDate
            , LocalDate endDate, String pathImg1, String pathImg2, String pathImg3) {

        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try
        {
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            document.add(
                    new Paragraph(
                            projcetname + "Report - "+LocalDate.now(), titleFont));

            document.add(
                    new Paragraph(
                            startDate.toString() + "-" + endDate.toString(), projectFont));

            document.add(
                    new Paragraph("Most common findings", textFont));

            Image image = Image.getInstance(pathImg1);
            document.add(image);

            document.add(
                    new Paragraph("Classes with most findings", textFont));

            Image image2 = Image.getInstance(pathImg2);
            document.add(image2);

            document.add(
                    new Paragraph("Findings per Packages", textFont));

            Image image3 = Image.getInstance(pathImg2);
            document.add(image3);

            document.close();
            writer.close();
        } catch (DocumentException e)
        {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }

    public byte[] exportToPdf(String projectName, LocalDate startDate, LocalDate endDate, boolean allProjects) {
        List<Finding> findings = findingsService.getFindingsByProjectAndDate(projectName, startDate, endDate, allProjects);
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Map<String, List<Finding>> findingsGrouped =
                findings.stream().collect(Collectors.groupingBy(f -> f.getProject()));

        try
        {
            //PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("HelloWorld.pdf"));
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            document.add(
                    new Paragraph(
                            "Findings PDF Export. - "+LocalDate.now(), titleFont));

            for (Map.Entry<String, List<Finding>> entry  : findingsGrouped.entrySet()) {
                document.add(new Paragraph(entry.getKey(), projectFont));
                List<Finding> findingListForProject = entry.getValue();
                for(Finding finding : findingListForProject) {
                    document.add(new Paragraph(finding.toString(), textFont));
                }
            }

            document.close();
            writer.close();
        } catch (DocumentException e)
        {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }
}
