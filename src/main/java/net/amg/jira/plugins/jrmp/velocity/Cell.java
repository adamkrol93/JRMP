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
package net.amg.jira.plugins.jrmp.velocity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Cell {
	private List<Task> tasks;
	private Colour colour;
	private int overload;
	private String baseUrl;
	private String jql;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public Cell(double probability, double consequence, double matrixSize, String baseUrl, String jql){
		this.tasks = new ArrayList<Task>();
		this.baseUrl = baseUrl;
		this.jql = jql;
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
		if (tasks.size()>2) {
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

	public String getJqlQuery() {
		try {
			jql = URLEncoder.encode(jql, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Couldn't create UTF-8 String from jql: " + jql + " with message: " + e.getMessage(), e);
			jql = jql.replaceAll("\\u007b","").replaceAll("\\u007d","").replaceAll(" ","%20")
					.replaceAll("=", "%3D").replaceAll("\"",""); // Głupie ale może pomoże jak coś pójdzie nie tak
		}

		String jqlQuery = baseUrl + "/issues/?jql=" + jql;
		return jqlQuery;
	}

}
