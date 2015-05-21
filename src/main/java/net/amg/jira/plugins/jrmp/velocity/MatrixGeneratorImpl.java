package net.amg.jira.plugins.jrmp.velocity;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.query.Query;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.velocity.DefaultVelocityManager;
import com.atlassian.velocity.VelocityManager;
import net.amg.jira.plugins.jrmp.exceptions.NoIssuesFoundException;
import net.amg.jira.plugins.jrmp.listeners.PluginListener;
import net.amg.jira.plugins.jrmp.services.ImpactProbability;
import net.amg.jira.plugins.jrmp.services.JRMPSearchService;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MatrixGeneratorImpl implements MatrixGenerator{

	private I18nResolver i18nResolver;
	private ImpactProbability impactProbability;
	private JRMPSearchService jrmpSearchService;

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
	public String generateMatrix(int size, Query query){
		double maxProbability = getMaxProbability(query);
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

		DateFormat dateFormatter = new SimpleDateFormat("YYYY-MM-dd");
		DateFormat updateDateFormatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("matrixSize", size);
		params.put("projectName", "");
		params.put("projectURL", "");
		params.put("redTasks", redTasks);
		params.put("greenTasks", greenTasks);
		params.put("yellowTasks", yellowTasks);
		params.put("date", dateFormatter.format(new Date()));
		params.put("updateDate", updateDateFormatter.format(new Date(getLastUpdatedIssue(listOfIssues).getUpdated().getTime())));
		params.put("updatedTask", getLastUpdatedIssue(listOfIssues).getKey());
		params.put("matrix", listOfRows);

		VelocityManager velocityManager = new DefaultVelocityManager();
		return velocityManager.getBody("templates/", "matrixTemplate.vm", "UTF-8", params);
	}

	private Double getMaxProbability(Query query){
//		ImpactPropability impactPropability = new ImpactPropabilityImpl(searchService, authenticationContext, ComponentAccessor.getConstantsManager(), ComponentAccessor.getCustomFieldManager());
		return impactProbability.getMaxPropability(query);
	}

	private List<Issue> getIssues(Query query){
		List<Issue> issues = null;
//		JRMPSearchService jrmpSearchService = new JRMPSearchServiceImpl(searchService, authenticationContext, ComponentAccessor.getCustomFieldManager());
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