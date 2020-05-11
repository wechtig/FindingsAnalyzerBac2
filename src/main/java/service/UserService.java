package service;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import converter.DBObjectConverter;
import data.ProjectConfig;
import data.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public User findUserByUsername(String username) {
        FindIterable<Document> cursor = collection.find(eq("username",username));
        Iterator it = cursor.iterator();
        return getDataFromQuery(it).get(0);
    }

    private void connect() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        db = mongoClient.getDatabase(DATABASE_NAME);
        collection = db.getCollection(COLLECTION_SONG);
    }

    private List<User> getDataFromQuery(Iterator iterator) {
        List<User> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(DBObjectConverter.convertDocumentToUser((Document) iterator.next()));
        }

        return list;
    }
}
