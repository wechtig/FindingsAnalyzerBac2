package findingsAnalyzer.service;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import findingsAnalyzer.converter.DBObjectConverter;
import findingsAnalyzer.data.Finding;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class FindingsService {
    public static final String DATABASE_NAME = "findings";
    public static final String COLLECTION_SONG = "checkstyleFindings";

    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> collection;
    private IgnoreMessageService ignoreMessageService;

    public FindingsService() {
        connect();
        ignoreMessageService = new IgnoreMessageService();
    }

    public List<String> getProjects() {
        BasicDBObject field = new BasicDBObject("project", "1");
        BasicDBObject id = new BasicDBObject("project", "1");
        DistinctIterable<String> cursor = collection.distinct("project", String.class);
        Iterator it = cursor.iterator();

        return getStringDataFromQuery(it);
    }

    public List<Finding> getFindingsByProjectAndDate(String projectName, LocalDate startDate, LocalDate endDate, boolean allProjects) {
        BasicDBObject dateRange = new BasicDBObject ("$gte", startDate);
        dateRange.put("$lt", endDate);

        BasicDBObject query = new BasicDBObject("date", dateRange);

        FindIterable<Document> cursor = collection.find(query);
        Iterator it = cursor.iterator();

        List<Finding> findings = getCheckstyleDataFromQuery(it);

        if (allProjects != true) {
            findings = findings.stream().filter(c -> c.getProject().equals(projectName)).collect(Collectors.toList());
        }

        List<Finding> filteredFindings = getFindingsWithoutIgnored(findings);

        return filteredFindings;
    }

    private void connect() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        db = mongoClient.getDatabase(DATABASE_NAME);
        collection = db.getCollection(COLLECTION_SONG);
    }


    private List<String> getStringDataFromQuery(Iterator iterator) {
        List<String> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add((String) iterator.next());
        }

        return list;
    }

    private List<Finding> getCheckstyleDataFromQuery(Iterator iterator) {
        List<Finding> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(DBObjectConverter.convertDocumentToFinding((Document) iterator.next()));
        }

        return list;
    }

    private List<Finding> getFindingsWithoutIgnored(List<Finding> findings) {
        List<String> ignoredMessages = ignoreMessageService.findAll();
        List<Finding> filtered = new ArrayList<>();

        if(ignoredMessages.size() == 0) {
            return findings;
        }

        for(Finding finding : findings) {
            boolean isIgnored = false;
            for (String message : ignoredMessages) {
                if(finding.getMessage().contains(message)) {
                    isIgnored = true;
                }
            }

            if(isIgnored == false) {
                filtered.add(finding);
            }
        }

        return filtered;
    }
}

