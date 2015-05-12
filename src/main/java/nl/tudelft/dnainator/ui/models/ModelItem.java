package nl.tudelft.dnainator.ui.models;

import nl.tudelft.dnainator.graph.Graph;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;
import javafx.scene.Group;

public abstract class ModelItem extends Group {
	private Graph graph;
	
	public ModelItem() {
		this(Neo4jSingleton.getInstance().getDatabase());
	}
	
	public ModelItem(String DB_PATH) {
		this(Neo4jSingleton.getInstance().getDatabase(DB_PATH));
	}
	
	public ModelItem(Graph graph) {
		this.graph = graph;
	}
	
	
}
