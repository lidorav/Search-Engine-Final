package Model.PartB;

public class Query {

    private String queryID;
    private String title;
    private String description;

    public Query(String queryID, String title, String description) {
        this.queryID = queryID;
        this.title = title;
        this.description = description;
    }

    public String getQueryID() {
        return queryID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
