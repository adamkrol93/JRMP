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
package net.amg.jira.plugins.jrmp.velocity;

import java.util.ArrayList;
import java.util.List;

public class Cell {
	private List<Task> tasks;
	private Colour colour;
	private int overload;

	public Cell(double probability, double consequence, double matrixSize){
		this.tasks = new ArrayList<Task>();
		double length = Math.sqrt(((matrixSize - (probability - 0.5)) * (matrixSize - (probability - 0.5)))+((matrixSize - (consequence - 0.5)) * (matrixSize - (consequence - 0.5))));
		double lengthToExtreme = Math.sqrt(((matrixSize-0.5) * (matrixSize-0.5)) + (0.5 * 0.5));
		if (0.6 * matrixSize >= length){
			colour = Colour.RED;
		} else if (lengthToExtreme >= length){
			colour = Colour.YELLOW;
		} else {
			colour = Colour.GREEN;
		}
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
	
	public void addTask(Task task){
		tasks.add(task);
		if (tasks.size()>2){
			overload++;
		}
	}
	
	public String getRisk(){
		return colour.toString();
	}
	
	public Colour getRiskEnum(){
		return colour;
	}

	public int getOverload() {
		return overload;
	}

	public void setOverload(int overload) {
		this.overload = overload;
	}
}
