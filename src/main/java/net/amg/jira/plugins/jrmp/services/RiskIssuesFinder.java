/*
 * Licensed to AMG.net under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 *
 * AMG.net licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.amg.jira.plugins.jrmp.services;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.query.Query;
import net.amg.jira.plugins.jrmp.listeners.PluginListener;
import net.amg.jira.plugins.jrmp.services.model.DateModel;
import net.amg.jira.plugins.jrmp.services.model.RiskIssues;
import net.amg.jira.plugins.jrmp.velocity.Cell;
import net.amg.jira.plugins.jrmp.velocity.Row;
import net.amg.jira.plugins.jrmp.velocity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Król
 */
@Service
public class RiskIssuesFinder {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WebResourceUrlProvider webResourceUrlProvider;
    @Autowired
    private CustomFieldManager customFieldManager;
    @Autowired
    private QueryBuilder queryBuilder;
    @Autowired
    private SearchService searchService;

    public static final int MATRIX_SIZE = 5;

    public RiskIssues fillAllFields(List<Issue> issues, Query query, DateModel dateModel)
    {
        RiskIssues riskIssues = new RiskIssues();
        riskIssues.setIssues(issues);

        CustomField probabilityField = customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_PROBABILITY_TEXT_CF);
        CustomField consequenceField = customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_CONSEQUENCE_TEXT_CF);

        String baseUrl = webResourceUrlProvider.getBaseUrl();
        List<Row> listOfRows = fillRowsContent(query, dateModel, baseUrl);
        riskIssues.setListOfRows(listOfRows);

        List<Task> tasks = new ArrayList<Task>();
        riskIssues.setTasks(tasks);
        if (issues.isEmpty()) {
            return riskIssues; //Avoid NPE, return empty model.
        }
        Issue lastUpdatedIssue;
        lastUpdatedIssue = issues.get(0);

        int redTasks=0, greenTasks=0, yellowTasks=0;
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
                    baseUrl + "/browse/" + issue.getKey(),
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
        riskIssues.setGreenTasks(greenTasks);
        riskIssues.setRedTasks(redTasks);
        riskIssues.setYellowTasks(yellowTasks);
        riskIssues.setLastUpdatedIssue(lastUpdatedIssue);
        return riskIssues;
    }

    private List<Row> fillRowsContent(Query query, DateModel dateModel, String baseUrl) {
        List<Row> result = new ArrayList<Row>();
        for(int i = 0; i < MATRIX_SIZE; i++){
            Row row = new Row();
            for(int j = 0; j< MATRIX_SIZE; j++){
                int probability = MATRIX_SIZE - i;
                int consequence = j + 1;
                String jqlString = searchService.getJqlString(queryBuilder.buildFilterQuery(probability, consequence, query, dateModel));
                Cell cell = new Cell((double) probability, (double) consequence, (double) MATRIX_SIZE, baseUrl, jqlString);
                row.addCell(cell);
            }
            result.add(row);
        }
        return result;
    }

    public void setWebResourceUrlProvider(WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    public void setCustomFieldManager(CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
}
