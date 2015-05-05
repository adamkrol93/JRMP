package net.amg.jira.plugins.velocity;

import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class MatrixGenerator{
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	public String generateMatrix(int size){
	    List<Task> listOfTasks = new ArrayList<Task>();//TODO hard-coded list of tasks
	    listOfTasks.add(new Task("PIP-95", "https://jira.amg.net.pl/browse/PIP-95", 2, 2));
	    listOfTasks.add(new Task("PIP-86", "https://jira.amg.net.pl/browse/PIP-86", 1, 1));
	    listOfTasks.add(new Task("PIP-93", "https://jira.amg.net.pl/browse/PIP-93", 2, 2));
	    listOfTasks.add(new Task("PIP-92", "https://jira.amg.net.pl/browse/PIP-92", 5, 5));
	    listOfTasks.add(new Task("PIP-90", "https://jira.amg.net.pl/browse/PIP-90", 4, 2));
	    listOfTasks.add(new Task("PIP-91", "https://jira.amg.net.pl/browse/PIP-91", 4, 1));
		
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
	    
	    try {
	    	Velocity.init();
	    } catch (Exception e){
	    	logger.error("Cannot init velocity: " + e.getMessage());
	    }

	    VelocityContext velocityContext = new VelocityContext();
	    velocityContext.put("matrixSize", size);
	    velocityContext.put("projectName", "PŁ Implementacje Przemysłowe");//TODO hard-coded project name
	    velocityContext.put("projectURL", "https://confluence.amg.net.ple/pages/viewpage.action?pageId=244875576");//TODO hard-coded project URL
	    velocityContext.put("redTasks", redTasks);
	    velocityContext.put("greenTasks", greenTasks);
	    velocityContext.put("yellowTasks", yellowTasks);
	    DateFormat dateFormatter = new SimpleDateFormat("YYYY-MM-dd");
	    DateFormat updateDateFormatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	    velocityContext.put("date", dateFormatter.format(new Date()));
	    velocityContext.put("updateDate", updateDateFormatter.format(new Date()));
	    velocityContext.put("updatedTask", "PIP-9"); //TODO hard-coded
	    velocityContext.put("matrix", listOfRows);

	    Template t = null;
	    try{	    
                //t = Velocity.getTemplate("templates/matrixTemplate.vm");
                //t = Velocity.getTemplate("matrixTemplate.vm");
                //t = Velocity.getTemplate("resources/templates/matrixTemplate.vm");
                //t = Velocity.getTemplate("src/main/resources/templates/matrixTemplate.vm");
		t = Velocity.getTemplate("./src/main/resources/templates/matrixTemplate.vm");
            } catch (Exception e){
	        logger.error("Cannot load template: " + e.getMessage());
            }
            
	    Writer writer = new StringWriter();
	    try {
                //Velocity.evaluate( velocityContext, writer, "log tag name", templateStr);
	    	t.merge(velocityContext, writer);
	    } catch (Exception e){
	    	logger.error("Cannot merge template: " + e.getMessage());
	    }
	    return writer.toString();
	}
}
