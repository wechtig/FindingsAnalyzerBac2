package converter;

import data.Finding;
import data.Recommendation;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DBObjectConverter {
    public static Finding convertDocumentToFinding(Document e) {
        String project = (String) e.get("project");
        String file = (String) e.get("file");

        ObjectId id_d = (ObjectId) e.get("_id");
        String id = id_d.toString();
          String source = (String) e.get("source");
        String message = (String) e.get("message");
        String severity = (String) e.get("severity");
        Object lineO = (Object) e.get("line");
        String line = lineO.toString();
        Date date_d = (Date) e.get("date");
        LocalDate date = date_d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


        Finding finding = new Finding();
        finding.setDate(date);
        finding.setFile(file);
        finding.setLine(line);
        finding.setMessage(message);
        finding.setProject(project);
        finding.setSeverity(severity);
        finding.setId(id);
        finding.setSource(source);

        return finding;
    }

    public static Recommendation convertRecommendationToDocument(Document e) {
        String error = (String) e.get("error");
        String link = (String) e.get("link");

        return new Recommendation(error, link);
    }

    public static Document convertRecommendationToDocument(Recommendation recommendation) {
        Document recDoc = new Document();
        recDoc.append("error", recommendation.getError());
        recDoc.append("link", recommendation.getLink());
        return recDoc;
    }

    public static String convertDocumentToMessage(Document e) {
        String message = (String) e.get("message");
        return message;
    }

    public static Document convertMessageToDocument(String message) {
        Document doc = new Document();
        doc.append("message", message);
        return doc;
    }


}
