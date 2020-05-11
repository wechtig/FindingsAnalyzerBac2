package converter;

import data.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import service.UserService;

import javax.management.relation.Role;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    public static ProjectConfig convertDocumentToProjectConfig(Document e) {
        String name = (String) e.get("name");
        String description = (String) e.get("description");
        String vcsRepositoryLink = (String) e.get("vcsRepositoryLink");
        UserService userService = new UserService();

        List<Document> userLinesDoc  = (List<Document>) e.get("users");
        List<User> userLines  = new ArrayList<>();
        for(Document document : userLinesDoc) {
            String username = (String) document.get("username");
            userLines.add(userService.findUserByUsername(username));
        }

        ProjectConfig projectConfig = new ProjectConfig();
        projectConfig.setDescription(description);
        projectConfig.setName(name);
        projectConfig.setVcsRepositoryLink(vcsRepositoryLink);
        projectConfig.setUsers(userLines);

        return projectConfig;
    }

    public static User convertDocumentToUser(Document e) {
        String username = (String) e.get("username");
        String password = (String) e.get("password");
        String strRole = (String) e.get("role");
        RoleEnum role = RoleEnum.valueOf(strRole);
        User user = new User(username, password, role);
        return user;
    }
}
