package findingsAnalyzer.service;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import findingsAnalyzer.converter.DBObjectConverter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IgnoreMessageService {
    public static final String DATABASE_NAME = "findings";
    public static final String COLLECTION_SONG = "ignoredFindings";

    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> collection;

    public IgnoreMessageService() {
        connect();
    }

    private void connect() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        db = mongoClient.getDatabase(DATABASE_NAME);
        collection = db.getCollection(COLLECTION_SONG);
    }

    public List<String> save(String message) {
        collection.insertOne(DBObjectConverter.convertMessageToDocument(message));
        return findAll();
    }

    public List<String> delete(String message) {
        collection.deleteOne(new Document("message", message));
        return findAll();
    }

    public List<String> findAll() {
        FindIterable<Document> cursor = collection.find();
        Iterator it = cursor.iterator();
        return GetMessagesDataFromQuery(it);
    }

    private List<String> GetMessagesDataFromQuery(Iterator iterator) {
        List<String> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(DBObjectConverter.convertDocumentToMessage((Document) iterator.next()));
        }

        return list;
    }

}

