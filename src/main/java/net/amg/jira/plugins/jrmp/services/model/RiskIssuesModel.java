package net.amg.jira.plugins.jrmp.services.model;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.query.Query;
import net.amg.jira.plugins.jrmp.listeners.PluginListener;
import net.amg.jira.plugins.jrmp.services.QueryBuilder;
import net.amg.jira.plugins.jrmp.velocity.Cell;
import net.amg.jira.plugins.jrmp.velocity.Row;
import net.amg.jira.plugins.jrmp.velocity.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Adam Król
 */
public class RiskIssuesModel {

    private List<Issue> issues;
    private WebResourceUrlProvider webResourceUrlProvider;
    private CustomFieldManager customFieldManager;
    private QueryBuilder queryBuilder;
    private List<Task> tasks;
    private List<Row> listOfRows;
    private Issue lastUpdatedIssue;
    private SearchService searchService;

    private int redTasks = 0;
    private int yellowTasks = 0;
    private int greenTasks = 0;
    private int matrxSize = 0;
    private Query query;
    private DateModel dateModel;

    public RiskIssuesModel(List<Issue> issues, WebResourceUrlProvider webResourceUrlProvider, CustomFieldManager customFieldManager,
                           QueryBuilder queryBuilder, int matrixSize, Query query, DateModel dateModel,SearchService searchService) {
        this.issues = issues;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.customFieldManager = customFieldManager;
        this.queryBuilder = queryBuilder;
        this.searchService = searchService;
        this.matrxSize = matrixSize;
        this.query = query;
        this.dateModel = dateModel;
        fillAllFields();
    }


    private void fillAllFields()
    {
        CustomField probabilityField = customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_PROBABILITY_TEXT_CF);
        CustomField consequenceField = customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_CONSEQUENCE_TEXT_CF);

        listOfRows = new LinkedList<Row>();
        for(int i = 0; i < matrxSize; i++){
            Row row = new Row();
            for(int j = 0; j< matrxSize; j++){
                int probability = matrxSize - i;
                int consequence = j + 1;
                Cell cell = new Cell((double) probability, (double) consequence, (double)matrxSize,
                        webResourceUrlProvider.getBaseUrl(),
                        searchService.getJqlString(queryBuilder.buildFilterQuery(probability,consequence,query,dateModel)));
                row.addCell(cell);
            }
            listOfRows.add(row);
        }


        tasks = new ArrayList<Task>();
        lastUpdatedIssue = issues.get(0);

        for (Issue issue : issues) {

            if (issue.getUpdated().getTime() > lastUpdatedIssue.getUpdated().getTime()){
                lastUpdatedIssue = issue;
			}
            int probability;
            try {
                probability =  Integer.valueOf(issue.getCustomFieldValue(probabilityField).toString());
                if(probability > matrxSize)
                {
                    probability = matrxSize;
                }
            } catch (NullPointerException e){
                probability = 1;
            }
            int consequence;
            try {
                consequence = Integer.valueOf(issue.getCustomFieldValue(consequenceField).toString());
                if(consequence >matrxSize )
                {
                    consequence = matrxSize;
                }
            } catch (NullPointerException e){
                consequence = 1;
            }

            Task task = new Task(issue.getKey(),
                    webResourceUrlProvider.getBaseUrl() + "/browse/" + issue.getKey(),
                    probability,
                    consequence);

            listOfRows.get(matrxSize-(task.getProbability())).getCells().get(task.getConsequence()-1).addTask(task);
            switch (listOfRows.get(matrxSize-(task.getProbability())).getCells().get(task.getConsequence()-1).getRiskEnum()){
                case RED:
                    redTasks++;
                    break;
                case YELLOW:
                    yellowTasks++;
                    break;
                case GREEN:
                    greenTasks++;
                    break;
            }


            tasks.add(task);
        }


    }

    public List<Task> getTasks() {
        return tasks;
    }

    public int getRedTasks() {
        return redTasks;
    }

    public int getYellowTasks() {
        return yellowTasks;
    }

    public int getGreenTasks() {
        return greenTasks;
    }

    public List<Row> getListOfRows() {
        return listOfRows;
    }

    public Issue getLastUpdatedIssue() {
        return lastUpdatedIssue;
    }
}
