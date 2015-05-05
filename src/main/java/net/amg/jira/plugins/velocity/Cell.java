package net.amg.jira.plugins.velocity;

import java.util.ArrayList;
import java.util.List;

public class Cell {
	private List<Task> tasks;
	private Colour colour;

	public Cell(double riskRate){
		this.tasks = new ArrayList<Task>();
		if (riskRate <= 0.25){
			colour = Colour.GREEN;
		} else if (riskRate <= 0.75){
			colour = Colour.YELLOW;
		} else {
			colour = Colour.RED;
		}
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
	
	public void addTask(Task task){
		tasks.add(task);
	}
	
	public String getRisk(){
		return colour.toString();
	}
	
	public Colour getRiskEnum(){
		return colour;
	}
}
