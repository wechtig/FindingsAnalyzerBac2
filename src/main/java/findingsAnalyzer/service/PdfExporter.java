package findingsAnalyzer.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfWriter;
import findingsAnalyzer.data.Finding;

import java.io.ByteArrayOutputStream;
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
