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
