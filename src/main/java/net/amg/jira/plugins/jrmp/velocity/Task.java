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

public class Task {
	private String name;
	private String url;
	private Double probability;
	private Double consequency;
	
	public Task(String name, String url, Double probability, Double consequency) {
		super();
		this.name = name;
		this.url = url;
		this.probability = probability;
		this.consequency = consequency;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Double getProbability() {
		return probability;
	}
	public void setProbability(Double probability) {
		this.probability = probability;
	}
	public Double getConsequency() {
		return consequency;
	}
	public void setConsequency(Double consequency) {
		this.consequency = consequency;
	}
	
}
