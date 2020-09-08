package findingsAnalyzer.service;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import findingsAnalyzer.converter.DBObjectConverter;
import findingsAnalyzer.data.Comment;
import findingsAnalyzer.data.Finding;
import org.bson.Document;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommentService {
    public static final String DATABASE_NAME = "findings";
    public static final String COLLECTION_SONG = "comments";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> collection;
    private FindingsService findingsService;
    private UserService userService;

    public CommentService() {
        connect();
        userService = new UserService();
        findingsService = new FindingsService();
    }

    public List<Comment> findCommentsByMessageAndDate(List<Finding> findings) {
        if(findings == null ||findings.isEmpty()) {
            return new ArrayList<>();
        }
        Finding finding = findings.get(0);

        BasicDBObject criteria = new BasicDBObject();
        criteria.append("findingId", finding.getId());

        FindIterable<Document> cursor = collection.find(criteria);
        Iterator it = cursor.iterator();
        List<Comment> comments = getComment(it);
        return comments;
    }

    public void saveComment(Finding finding, String text) {
        org.springframework.security.core.userdetails.User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        findingsAnalyzer.data.User user = userService.findByEmail(loggedInUser.getUsername());

        Comment comment = new Comment();
        comment.setFindingId(finding.getId());
        comment.setText(user.getFullname() + ":" + text);
        collection.insertOne(DBObjectConverter.convertCommentToDocument(comment));

    }

    private void connect() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        db = mongoClient.getDatabase(DATABASE_NAME);
        collection = db.getCollection(COLLECTION_SONG);
    }

    private List<Comment> getComment(Iterator iterator) {
        List<Comment> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(DBObjectConverter.convertDocumentToComment((Document) iterator.next()));
        }

        return list;
    }
}
