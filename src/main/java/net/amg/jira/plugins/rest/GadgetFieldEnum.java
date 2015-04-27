package net.amg.jira.plugins.rest;

/**
 * @author Adam Król
 */
public enum GadgetFieldEnum {
    FILTER("Filter"),DATE("Date"), TITLE("Title"), REFRESH("refresh");

    private String fieldName;

    GadgetFieldEnum(String fieldName) {
        this.fieldName = fieldName;
    }



    @Override
    public String toString() {
        return fieldName;
    }
}
