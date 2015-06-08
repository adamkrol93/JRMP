/*Copyright 2015 AMG.net

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package net.amg.jira.plugins.jrmp.services;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.query.Query;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.velocity.DefaultVelocityManager;
import com.atlassian.velocity.VelocityManager;
import net.amg.jira.plugins.jrmp.listeners.PluginListener;
import net.amg.jira.plugins.jrmp.rest.ProjectOrFilter;
import net.amg.jira.plugins.jrmp.velocity.Cell;
import net.amg.jira.plugins.jrmp.velocity.Row;
import net.amg.jira.plugins.jrmp.velocity.Task;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MatrixGeneratorImpl implements MatrixGenerator{

	public static final String MATRIX_SIZE_STRING = "matrixSize";
	public static final String PROJECT_NAME_STRING = "projectName";
	public static final String PROJECT_URL_STRING = "projectURL";
	public static final String PROJECT_LABEL_STRING = "projectLabel";
	public static final String RISK_DATE_LABEL_STRING = "riskDateLabel";
	public static final String UPDATED_LABEL = "updatedLabel";
	public static final String RED_TASKS_LABEL_STRING = "redTasksLabel";
	public static final String GREEN_TASKS_LABEL_STRING = "greenTasksLabel";
	public static final String YELLOW_TASKS_LABEL_STRING = "yellowTasksLabel";
	public static final String RED_TASKS_VALUE_STRING = "redTasksValue";
	public static final String GREEN_TASKS_VALUE_STRING = "greenTasksValue";
	public static final String YELLOW_TASKS_VALUE_STRING = "yellowTasksValue";
	public static final String DATE_STRING = "date";
	public static final String UPDATE_DATE_STRING = "updateDate";
	public static final String UPDATED_TASK_STRING = "updatedTask";
	public static final String UPDATED_TASK_URL_STRING = "updatedTaskUrl";
	public static final String OVERLOAD_COMMENT_MULTI_STRING = "overloadCommentMulti";
	public static final String OVERLOAD_COMMENT_MULTI_2_STRING = "overloadCommentMulti_2";
	public static final String OVERLOAD_COMMENT_SINGLE_STRING = "overloadCommentSingle";
	public static final String MATRIX_STRING = "matrix";
	public static final String CONSEQUENCE_LABEL_STRING = "consequenceLabel";
	public static final String PROBABILITY_LABEL_STRING = "probabilityLabel";
	public static final String MATRIX_TITLE = "matrixTitle";
    public static final String MATRIX_TEMPLATE = "matrixTemplate";
	public static final String TITLE_LABEL_STRING = "titleLabel";
	public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("YYYY-MM-dd");
	public static final DateFormat UPDATE_DATE_FORMATTER = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

	private I18nResolver i18nResolver;
	private ImpactProbability impactProbability;
	private JRMPSearchService jrmpSearchService;
    private WebResourceUrlProvider webResourceUrlProvider;
    private CustomFieldManager customFieldManager;

	public void setImpactProbability(ImpactProbability impactProbability) {
		this.impactProbability = impactProbability;
	}

	public void setJrmpSearchService(JRMPSearchService jrmpSearchService) {
		this.jrmpSearchService = jrmpSearchService;
	}

	public void setI18nResolver(I18nResolver i18nResolver) {
		this.i18nResolver = i18nResolver;
	}

    public void setWebResourceUrlProvider(WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    public void setCustomFieldManager(CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }

    @Override
	public String generateMatrix(ProjectOrFilter projectOrFilter, String matrixTitle, String matrixTemplate){
		int size = getMaxProbability(projectOrFilter.getQuery());
		List<Issue> listOfIssues = jrmpSearchService.getAllQualifiedIssues(projectOrFilter.getQuery());

		if (listOfIssues.isEmpty()) {
			return i18nResolver.getText("risk.management.gadget.matrix.error.empty_list_of_issues");
		}

		List<Task> listOfTasks = getTasksFromIssues(listOfIssues);

	    List<Row> listOfRows = new ArrayList<Row>();
		for(int i = 0; i < size; i++){
			Row row = new Row();
			for(int j = 0; j< size; j++){
				Cell cell = new Cell((double)((size - i)), (double)(j + 1), (double)size);
				row.addCell(cell);
			}
			listOfRows.add(row);
		}
		
		int redTasks = 0;
	    int yellowTasks = 0;
	    int greenTasks = 0;

	    for(Task task : listOfTasks){
			listOfRows.get(size-(task.getProbability())).getCells().get(task.getConsequence()-1).addTask(task);
			switch (listOfRows.get(size-(task.getProbability())).getCells().get(task.getConsequence()-1).getRiskEnum()){
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
	    }

		Map<String, Object> params = new HashMap<String, Object>();

		String title = projectOrFilter.getName();
		String url = "";

		if(projectOrFilter.isFilter()){
			url = webResourceUrlProvider.getBaseUrl() + "/browse/?filter=" + projectOrFilter.getId();
		}

		if(projectOrFilter.isProject()){
			url = webResourceUrlProvider.getBaseUrl() + "/browse/?project=" + projectOrFilter.getId();
		}

		params.put(PROBABILITY_LABEL_STRING, i18nResolver.getText("risk.management.matrix.probability_label"));
		params.put(CONSEQUENCE_LABEL_STRING, i18nResolver.getText("risk.management.matrix.consequence_label"));
		params.put(MATRIX_SIZE_STRING, size);
		params.put(PROJECT_NAME_STRING, title);
		params.put(PROJECT_URL_STRING, url);
		params.put(UPDATED_LABEL, i18nResolver.getText("risk.management.matrix.updated_label"));
		params.put(RISK_DATE_LABEL_STRING, i18nResolver.getText("risk.management.matrix.risk_date_label"));
		params.put(PROJECT_LABEL_STRING, i18nResolver.getText("risk.management.matrix.project_label"));
		params.put(RED_TASKS_VALUE_STRING, redTasks);
		params.put(GREEN_TASKS_VALUE_STRING, greenTasks);
		params.put(YELLOW_TASKS_VALUE_STRING, yellowTasks);
		params.put(RED_TASKS_LABEL_STRING, i18nResolver.getText("risk.management.matrix.red_tasks_label"));
		params.put(GREEN_TASKS_LABEL_STRING, i18nResolver.getText("risk.management.matrix.green_tasks_label"));
		params.put(YELLOW_TASKS_LABEL_STRING, i18nResolver.getText("risk.management.matrix.yellow_tasks_label"));
		params.put(DATE_STRING, DATE_FORMATTER.format(new Date()));
		params.put(UPDATE_DATE_STRING, UPDATE_DATE_FORMATTER.format(new Date(getLastUpdatedIssue(listOfIssues).getUpdated().getTime())));
		params.put(UPDATED_TASK_STRING, getLastUpdatedIssue(listOfIssues).getKey());
		params.put(UPDATED_TASK_URL_STRING, webResourceUrlProvider.getBaseUrl() + "/browse/" + getLastUpdatedIssue(listOfIssues).getKey());
		params.put(OVERLOAD_COMMENT_MULTI_STRING, i18nResolver.getText("risk.management.matrix.overload_comment_multi"));
		params.put(OVERLOAD_COMMENT_MULTI_2_STRING, i18nResolver.getText("risk.management.matrix.overload_comment_multi_2"));
		params.put(OVERLOAD_COMMENT_SINGLE_STRING, i18nResolver.getText("risk.management.matrix.overload_comment_single"));
		params.put(MATRIX_STRING, listOfRows);
		params.put(MATRIX_TITLE,matrixTitle);
        params.put(MATRIX_TEMPLATE,matrixTemplate);
		params.put(TITLE_LABEL_STRING, i18nResolver.getText("risk.management.matrix.title_label"));
		VelocityManager velocityManager = new DefaultVelocityManager();
		return velocityManager.getBody("templates/", "matrixTemplate.vm", "UTF-8", params);
	}

	private int getMaxProbability(Query query){
		return impactProbability.getMaxProbability(query);
	}

	private List<Task> getTasksFromIssues(List<Issue> issues){
		CustomField probabilityField = customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_PROBABILITY_TEXT_CF);
		CustomField consequenceField = customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_CONSEQUENCE_TEXT_CF);

		List<Task> listOfTasks = new ArrayList<Task>();
		for (Issue issue : issues) {
			int probability;
			try {
				probability =  Integer.valueOf(issue.getCustomFieldValue(probabilityField).toString());
				if(probability > ImpactProbabilityImpl.MAX_PROBABILITY){
					probability = ImpactProbabilityImpl.MAX_PROBABILITY;
				}
			} catch (NullPointerException e){
				probability = 1;
			}
			int consequence;
			try {
				consequence = Integer.valueOf(issue.getCustomFieldValue(consequenceField).toString());
				if(consequence > ImpactProbabilityImpl.MAX_PROBABILITY){
					consequence = ImpactProbabilityImpl.MAX_PROBABILITY;
				}
			} catch (NullPointerException e){
				consequence = 1;
			}
					listOfTasks.add(new Task(issue.getKey(),
					webResourceUrlProvider.getBaseUrl() + "/browse/" + issue.getKey(),
					probability,
					consequence));
		}
		return listOfTasks;
	}

	private Issue getLastUpdatedIssue(List<Issue> issues){
		Issue lastUpdated = issues.get(0);
		for(Issue issue : issues){
			if (issue.getUpdated().getTime() > lastUpdated.getUpdated().getTime()){
				lastUpdated = issue;
			}
		}
		return lastUpdated;
	}
}
