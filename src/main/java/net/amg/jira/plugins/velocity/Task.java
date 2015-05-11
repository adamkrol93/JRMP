package net.amg.jira.plugins.velocity;

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
