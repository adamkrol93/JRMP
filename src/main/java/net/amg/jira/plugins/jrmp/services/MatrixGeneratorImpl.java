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

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.query.Query;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.velocity.DefaultVelocityManager;
import com.atlassian.velocity.VelocityManager;
import net.amg.jira.plugins.jrmp.exceptions.NoIssuesFoundException;
import net.amg.jira.plugins.jrmp.listeners.PluginListener;
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
	public static final String RED_TASKS_STRING = "redTasks";
	public static final String GREEN_TASKS_STRING = "greenTasks";
	public static final String YELLOW_TASKS_STRING = "yellowTasks";
	public static final String DATE_STRING = "date";
	public static final String UPDATE_DATE_STRING = "updateDate";
	public static final String UPDATED_TASK_STRING = "updatedTask";
	public static final String MATRIX_STRING = "matrix";
	private I18nResolver i18nResolver;
	private ImpactProbability impactProbability;
	private JRMPSearchService jrmpSearchService;
	public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("YYYY-MM-dd");

	public void setImpactProbability(ImpactProbability impactProbability) {
		this.impactProbability = impactProbability;
	}

	public void setJrmpSearchService(JRMPSearchService jrmpSearchService) {
		this.jrmpSearchService = jrmpSearchService;
	}

	public void setI18nResolver(I18nResolver i18nResolver) {
		this.i18nResolver = i18nResolver;
	}


	@Override
	public String generateMatrix(Query query){
		double maxProbability = getMaxProbability(query);
		int size = (int) maxProbability;
		List<Issue> listOfIssues = getIssues(query);

		if (listOfIssues.isEmpty()){
			i18nResolver.getText("risk.management.gadget.matrix.error.empty_list_of_issues");
		}

		List<Task> listOfTasks = getTasksFromIssues(listOfIssues);

	    List<Row> listOfRows = new ArrayList<Row>();
		for(int i = 0; i < size; i++){
			Row row = new Row();
			for(int j = 0; j< size; j++){
				Cell cell = new Cell((double)((size - i)*(j + 1)) / (size * size));
				row.addCell(cell);
			}
			listOfRows.add(row);
		}
		
		int redTasks = 0;
	    int yellowTasks = 0;
	    int greenTasks = 0;
		
	    for(Task task : listOfTasks){
			listOfRows.get(getRow(task.getProbability(),maxProbability,size)).getCells().get(getCell(task.getConsequency(),maxProbability,size)).addTask(task);
			switch (listOfRows.get(getRow(task.getProbability(), maxProbability,size)).getCells().get(getCell(task.getConsequency(),maxProbability,size)).getRiskEnum()){
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

		DateFormat updateDateFormatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		Map<String, Object> params = new HashMap<String, Object>();

		params.put(MATRIX_SIZE_STRING, size);
		params.put(PROJECT_NAME_STRING, "");
		params.put(PROJECT_URL_STRING, "");
		params.put(RED_TASKS_STRING, redTasks);
		params.put(GREEN_TASKS_STRING, greenTasks);
		params.put(YELLOW_TASKS_STRING, yellowTasks);
		params.put(DATE_STRING, DATE_FORMATTER.format(new Date()));
		params.put(UPDATE_DATE_STRING, updateDateFormatter.format(new Date(getLastUpdatedIssue(listOfIssues).getUpdated().getTime())));
		params.put(UPDATED_TASK_STRING, getLastUpdatedIssue(listOfIssues).getKey());
		params.put(MATRIX_STRING, listOfRows);

		VelocityManager velocityManager = new DefaultVelocityManager();
		return velocityManager.getBody("templates/", "matrixTemplate.vm", "UTF-8", params);
	}

	private Double getMaxProbability(Query query){
		return impactProbability.getMaxProbability(query);
	}

	private List<Issue> getIssues(Query query){
		List<Issue> issues = null;
		try {
			issues = jrmpSearchService.getAllQualifiedIssues(query);
		} catch (NoIssuesFoundException e) {
			return Collections.EMPTY_LIST;
		}
		return issues;
	}

	private List<Task> getTasksFromIssues(List<Issue> issues){
		CustomField probability = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName(PluginListener.RISK_PROBABILITY_TEXT_CF);
		CustomField consequence = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName(PluginListener.RISK_CONSEQUENCE_TEXT_CF);
		List<Task> listOfTasks = new ArrayList<Task>();
		for (Issue issue : issues){
			listOfTasks.add(new Task(issue.getKey(),
					ComponentAccessor.getWebResourceUrlProvider().getBaseUrl() + "/browse/" + issue.getKey(),
					(Double)issue.getCustomFieldValue(probability),
					(Double)issue.getCustomFieldValue(consequence)));
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

	private int getCell(double value, double max, int size){
		if (value == max) {
			return size-1;
		} else {
			return (int)Math.floor((value / max) * size);
		}
	}

	private int getRow(double value, double max, int size){
		if (value == max) {
			return 0;
		} else {
			return size - (int)Math.floor((value / max) * size) - 1;
		}
	}
}
