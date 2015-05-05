package net.amg.jira.plugins.velocity;

public class Task {
	private String name;
	private String url;
	private int probability;
	private int consequency;
	
	public Task(String name, String url, int probability, int consequency) {
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
	public int getProbability() {
		return probability;
	}
	public void setProbability(int probability) {
		this.probability = probability;
	}
	public int getConsequency() {
		return consequency;
	}
	public void setConsequency(int consequency) {
		this.consequency = consequency;
	}
	
}
