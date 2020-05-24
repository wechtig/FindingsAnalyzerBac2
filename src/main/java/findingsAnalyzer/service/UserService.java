package findingsAnalyzer.service;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import findingsAnalyzer.converter.DBObjectConverter;
import findingsAnalyzer.data.ProjectConfig;
import findingsAnalyzer.data.User;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class UserService {
    public static final String DATABASE_NAME = "findings";
    public static final String COLLECTION_SONG = "users";

    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> collection;

    public UserService() {
        connect();
    }

    public void saveUser(User user) {
        collection.insertOne(DBObjectConverter.convertUserToDocument(user));
    }

    private void connect() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        db = mongoClient.getDatabase(DATABASE_NAME);
        collection = db.getCollection(COLLECTION_SONG);
    }

    public User findByEmail(String email) {
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        Document document = collection.find(query).first();

        if(document == null || document.isEmpty()) {
            return null;
        }

        return DBObjectConverter.convertDocumentToUser(document);

    }
}

