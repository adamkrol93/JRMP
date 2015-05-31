package net.amg.jira.plugins.jrmp.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.query.Query;
import org.ofbiz.core.entity.GenericValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jonatan on 31.05.15.
 */
public class ProjectOrFilter {
    public static final String PROJECT = "project";
    public static final String FILTER = "filter";
    private Query query;
    private String name;
    private String id;
    private boolean isFilter = false;
    private boolean isProject = false;

    public ProjectOrFilter(String projectOrFilter){
        String type = projectOrFilter.split("-")[0];
        id = projectOrFilter.split("-")[1];
        List<GenericValue> result = null;
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("id", id);

        if (FILTER.equals(type)) {
            query = getQueryFilter(id);
            result = ComponentAccessor.getOfBizDelegator().findByAnd("SearchRequest", hashMap);
            isFilter = true;
        }
        if(PROJECT.equals(type)){
            query = getQueryProject(id);
            result = ComponentAccessor.getOfBizDelegator().findByAnd("Project", hashMap);
            isProject = true;
        }

        if (!result.isEmpty()){
            name = result.get(0).getString("name");
        } else {
            name = "";
        }
    }

    private Query getQueryFilter(String filter) {
        JqlClauseBuilder subjectBuilder = JqlQueryBuilder.newClauseBuilder().savedFilter(filter);
        return subjectBuilder.buildQuery();
    }

    private Query getQueryProject(String project) {
        JqlClauseBuilder subjectBuilder = JqlQueryBuilder.newClauseBuilder().project(project);
        return subjectBuilder.buildQuery();
    }

    public Query getQuery() {
        return query;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isProject() {
        return isProject;
    }

    public boolean isFilter() {
        return isFilter;
    }
}
