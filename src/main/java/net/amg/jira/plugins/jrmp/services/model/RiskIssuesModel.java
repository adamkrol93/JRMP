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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Adam Król
 */
public class RiskIssuesModel {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
    public static final int MATRIX_SIZE = 5;
    private Query query;
    private DateModel dateModel;

    public RiskIssuesModel(List<Issue> issues, WebResourceUrlProvider webResourceUrlProvider, CustomFieldManager customFieldManager,
                           QueryBuilder queryBuilder, Query query, DateModel dateModel,SearchService searchService) {
        this.issues = issues;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.customFieldManager = customFieldManager;
        this.queryBuilder = queryBuilder;
        this.searchService = searchService;
        this.query = query;
        this.dateModel = dateModel;

    }

    public void fillAllFields()
    {
        CustomField probabilityField = customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_PROBABILITY_TEXT_CF);
        CustomField consequenceField = customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_CONSEQUENCE_TEXT_CF);

        listOfRows = new LinkedList<Row>();
        for(int i = 0; i < MATRIX_SIZE; i++){
            Row row = new Row();
            for(int j = 0; j< MATRIX_SIZE; j++){
                int probability = MATRIX_SIZE - i;
                int consequence = j + 1;
                Cell cell = new Cell((double) probability, (double) consequence, (double) MATRIX_SIZE,
                        webResourceUrlProvider.getBaseUrl(),
                        searchService.getJqlString(queryBuilder.buildFilterQuery(probability,consequence,query,dateModel)));
                row.addCell(cell);
            }
            listOfRows.add(row);
        }


        tasks = new ArrayList<Task>();
        if(!issues.isEmpty()) {
            lastUpdatedIssue = issues.get(0);
        }

        for (Issue issue : issues) {

            if (issue.getUpdated().getTime() > lastUpdatedIssue.getUpdated().getTime()){
                lastUpdatedIssue = issue;
			}
            int probability;
            try {
                probability =  Integer.valueOf(issue.getCustomFieldValue(probabilityField).toString());
                if(probability > MATRIX_SIZE)
                {
                    probability = MATRIX_SIZE;
                }
            } catch (Exception e){
                probability = 1;
                logger.info("There was a problem with obtaining probability. Setting Probability to 1. Error : " + e.getMessage(),e);
            }
            int consequence;
            try {
                consequence = Integer.valueOf(issue.getCustomFieldValue(consequenceField).toString());
                if(consequence > MATRIX_SIZE)
                {
                    consequence = MATRIX_SIZE;
                }
            } catch (Exception e){
                consequence = 1;
                logger.info("There was a problem with obtaining consequence. Setting Consequence to 1. Error : " + e.getMessage(),e);
            }

            Task task = new Task(issue.getKey(),
                    webResourceUrlProvider.getBaseUrl() + "/browse/" + issue.getKey(),
                    probability,
                    consequence);

            listOfRows.get(MATRIX_SIZE -(task.getProbability())).getCells().get(task.getConsequence()-1).addTask(task);
            switch (listOfRows.get(MATRIX_SIZE -(task.getProbability())).getCells().get(task.getConsequence()-1).getRiskEnum()){
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
