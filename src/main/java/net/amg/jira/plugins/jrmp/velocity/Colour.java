package net.amg.jira.plugins.jrmp.velocity;

public enum Colour {
	RED("red"), YELLOW("yellow"), GREEN("green");
    
	private String text;

	Colour(String text){
		this.text = text;
	}
	
	@Override
	public String toString(){
		return text;
	}
};  

