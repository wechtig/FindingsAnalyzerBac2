package findingsAnalyzer.data;

public class Recommendation {
    private String error;
    private String link;

    public Recommendation() {}

    public Recommendation(String error, String link) {
        this.error = error;
        this.link = link;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
