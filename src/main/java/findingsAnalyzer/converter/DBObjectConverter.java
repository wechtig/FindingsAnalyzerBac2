package findingsAnalyzer.converter;

import findingsAnalyzer.data.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import findingsAnalyzer.service.UserService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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
        List<User> users  = new ArrayList<>();
        for(Document document : userLinesDoc) {
            String userMail = (String) document.get("email");
            User user = userService.findByEmail(userMail);
            users.add(user);
        }

        ProjectConfig projectConfig = new ProjectConfig();
        projectConfig.setDescription(description);
        projectConfig.setName(name);
        projectConfig.setVcsRepositoryLink(vcsRepositoryLink);
        projectConfig.setUsers(users);

        return projectConfig;
    }

    public static Document convertUserToDocument(User user) {
        org.bson.Document recDoc = new Document();
        recDoc.append("email", user.getEmail());
        recDoc.append("fullname", user.getFullname());
        recDoc.append("roles", user.getRoles());
        recDoc.append("password", user.getPassword());
        recDoc.append("roles", Arrays.asList("USER"));
        return recDoc;
    }

    public static User convertDocumentToUser(Document e) {
        User user = new User();
        String email = (String) e.get("email");
        String fullname = (String) e.get("fullname");
        String password = (String) e.get("password");
        user.setEmail(email);
        user.setEnabled(true);
        user.setFullname(fullname);
        user.setPassword(password);
        user.setRoles(new HashSet<>(Arrays.asList("USER")));
        return user;
    }
}
