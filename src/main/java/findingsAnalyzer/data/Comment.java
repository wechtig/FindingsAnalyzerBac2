package findingsAnalyzer.data;

public class Comment {
    private String id;
    private String findingId;
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFindingId() {
        return findingId;
    }

    public void setFindingId(String findingId) {
        this.findingId = findingId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Comment(String id, String findingId, String text) {
        this.id = id;
        this.findingId = findingId;
        this.text = text;
    }

    public Comment() {

    }
}
