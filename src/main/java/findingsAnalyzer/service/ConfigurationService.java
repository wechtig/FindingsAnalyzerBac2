package findingsAnalyzer.service;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import findingsAnalyzer.converter.DBObjectConverter;
import findingsAnalyzer.data.Finding;
import findingsAnalyzer.data.ProjectConfig;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConfigurationService {
    public static final String DATABASE_NAME = "findings";
    public static final String COLLECTION_SONG = "configurations";

    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> collection;
    private IgnoreMessageService ignoreMessageService;

    public ConfigurationService() {
        connect();
        ignoreMessageService = new IgnoreMessageService();
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
}
