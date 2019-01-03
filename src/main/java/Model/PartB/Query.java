package Model.PartB;

/**
 * Class that represent the given query from a file, it holds all the query properties
 */
public class Query {

    private String queryID;
    private String title;
    private String description;

    /**
     * C'tor create a query object
     * @param queryID - the query ID
     * @param title - the query itself
     * @param description - the query description
     */
    public Query(String queryID, String title, String description) {
        this.queryID = queryID;
        this.title = title;
        this.description = description;
    }

    /**
     * @return the query ID
     */
    public String getQueryID() {
        return queryID;
    }

    /**
     * @return the query Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the query Description
     */
    public String getDescription() {
        return description;
    }
}
