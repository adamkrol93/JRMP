package net.amg.jira.plugins.jrmp.rest;

/**
 * Created by jonatan on 30.05.15.
 */
public class MatrixRequest {

    private String filter;

    private String title;

    private String date;

    private String template;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
