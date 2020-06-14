package findingsAnalyzer.service;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import findingsAnalyzer.converter.DBObjectConverter;
import findingsAnalyzer.data.Finding;
import findingsAnalyzer.data.ProjectConfig;
import findingsAnalyzer.data.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurationService {
    public static final String DATABASE_NAME = "findings";
    public static final String COLLECTION_SONG = "configurations";

    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> collection;
    private IgnoreMessageService ignoreMessageService;
    private UserSecurityService userSecurityService;

    public ConfigurationService() {
        connect();
        ignoreMessageService = new IgnoreMessageService();
        userSecurityService = new UserSecurityService();
    }

    public List<ProjectConfig> getProjectConfigs() {
        FindIterable<Document> cursor = collection.find();
        Iterator it = cursor.iterator();
        return getDataFromQuery(it);
    }

    private void connect() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        db = mongoClient.getDatabase(DATABASE_NAME);
        collection = db.getCollection(COLLECTION_SONG);
    }

    private List<ProjectConfig> getDataFromQuery(Iterator iterator) {
        List<ProjectConfig> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(DBObjectConverter.convertDocumentToProjectConfig((Document) iterator.next()));
        }

        return list;
    }

    public void saveConfig(String name, String description, String vcsLink) {
        Document query = new Document();
        query.append("name",name);
        Document data = new Document();
        data.append("description", description).append("vcsRepositoryLink", vcsLink);
        Document updateQuery = new Document();
        updateQuery.append("$set", data);
        collection.updateOne(query, updateQuery);
    }

    public void addUserToProject(String usermail, String project) {
        User user = userSecurityService.findUserByEmail(usermail);
        ProjectConfig projectConfig = findConfigByProjectname(project);

        if(user == null || projectConfig == null) {
            return;
        }

        List<User> users = projectConfig.getUsers();
        users.add(user);

        List<String> userDocuments = users.stream()
                .map(u-> u.getEmail()).collect(Collectors.toList());

        Document query = new Document();
        query.append("name",project);
        Document data = new Document();
        data.append("users", userDocuments);
        Document updateQuery = new Document();
        updateQuery.append("$set", data);
        collection.updateOne(query, updateQuery);
    }

    public ProjectConfig findConfigByProjectname(String projectname) {
        BasicDBObject query = new BasicDBObject();
        query.put("name", projectname);
        Document document = collection.find(query).first();

        if(document == null || document.isEmpty()) {
            return null;
        }

        return DBObjectConverter.convertDocumentToProjectConfig(document);
    }
}
