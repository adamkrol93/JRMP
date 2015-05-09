package net.amg.jira.plugins.velocity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.velocity.DefaultVelocityManager;
import com.atlassian.velocity.VelocityManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class MatrixGenerator{

	private I18nResolver i18nResolver;

	public MatrixGenerator(I18nResolver i18nResolver){
		this.i18nResolver = i18nResolver;
	}

	//Logger logger = LoggerFactory.getLogger(getClass());
	
	public String generateMatrix(int size){

		if (size == 0){
			return i18nResolver.getText("risk.management.gadget.matrix.error.wrong_size");
		}

	    List<Task> listOfTasks = getTasks();
	    List<Row> listOfRows = new ArrayList<Row>();
		for(int i = 0; i < size; i++){
			Row row = new Row();
			for(int j = 0; j< size; j++){
				Cell cell = new Cell((double)((size-i)*(j+1))/(size*size));
				row.addCell(cell);
			}
			listOfRows.add(row);
		}
		
		int redTasks = 0;
	    int yellowTasks = 0;
	    int greenTasks = 0;
		
	    for(Task task : listOfTasks){
	    	if (task.getProbability() <= size && task.getConsequency() <= size){
	    		listOfRows.get(size-task.getProbability()).getCells().get(task.getConsequency()-1).addTask(task);
		    	switch (listOfRows.get(task.getProbability()-1).getCells().get(task.getConsequency()-1).getRiskEnum()){
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
	    }

		DateFormat dateFormatter = new SimpleDateFormat("YYYY-MM-dd");
		DateFormat updateDateFormatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("matrixSize", size);
		params.put("projectName", "PŁ Implementacje Przemysłowe");//TODO hard-coded project name
		params.put("projectURL", "https://confluence.amg.net.pl/pages/viewpage.action?pageId=244875576");//TODO hard-coded project URL
		params.put("redTasks", redTasks);
		params.put("greenTasks", greenTasks);
		params.put("yellowTasks", yellowTasks);
		params.put("date", dateFormatter.format(new Date()));
		params.put("updateDate", updateDateFormatter.format(new Date()));
		params.put("updatedTask", "PIP-9"); //TODO hard-coded
		params.put("matrix", listOfRows);

		VelocityManager velocityManager = new DefaultVelocityManager();
		return velocityManager.getBody("templates/", "matrixTemplate.vm", "UTF-8", params);

	}

	private List<Task> getTasks(){
		List<Task> listOfTasks = new ArrayList<Task>();
		listOfTasks.add(new Task("PIP-95", "https://jira.amg.net.pl/browse/PIP-95", 2, 2));
		listOfTasks.add(new Task("PIP-86", "https://jira.amg.net.pl/browse/PIP-86", 1, 1));
		listOfTasks.add(new Task("PIP-93", "https://jira.amg.net.pl/browse/PIP-93", 2, 2));
		listOfTasks.add(new Task("PIP-92", "https://jira.amg.net.pl/browse/PIP-92", 5, 5));
		listOfTasks.add(new Task("PIP-90", "https://jira.amg.net.pl/browse/PIP-90", 4, 2));
		listOfTasks.add(new Task("PIP-91", "https://jira.amg.net.pl/browse/PIP-91", 4, 1));
		return listOfTasks;
	}
}
