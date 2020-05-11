package service;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import converter.DBObjectConverter;
import data.Recommendation;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecommendationService {
    public static final String DATABASE_NAME = "findings";
    public static final String COLLECTION_SONG = "recommendations";

    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> collection;

    public RecommendationService() {
        connect();
    }

    public void save(Recommendation recommendation) {
        collection.insertOne(DBObjectConverter.convertRecommendationToDocument(recommendation));
    }

    public List<Recommendation> findAll() {
        FindIterable<Document> cursor = collection.find();
        Iterator it = cursor.iterator();
        return GetRecommendationDataFromQuery(it);
    }

    private void connect() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        db = mongoClient.getDatabase(DATABASE_NAME);
        collection = db.getCollection(COLLECTION_SONG);
    }


    private List<Recommendation> GetRecommendationDataFromQuery(Iterator iterator) {
        List<Recommendation> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(DBObjectConverter.convertRecommendationToDocument((Document) iterator.next()));
        }

        return list;
    }
}

