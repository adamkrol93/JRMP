package net.amg.jira.plugins.jrmp.velocity;

import java.util.ArrayList;
import java.util.List;

public class Row {

	private List<Cell> cells;

	public Row(){
		this.cells = new ArrayList<Cell>();
	}

	public List<Cell> getCells() {
		return cells;
	}

	public void addCell(Cell cell){
		cells.add(cell);
	}
}
