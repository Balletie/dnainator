package nl.tudelft.dnainator.ui.models;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.scene.layout.Region;
import nl.tudelft.dnainator.graph.Graph;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;

public abstract class ModelItem extends Region {
	private Graph graph;
	List<ModelItem> children;
	
	public ModelItem() {
		this(Neo4jSingleton.getInstance().getDatabase());
	}
	
	public ModelItem(String DB_PATH) {
		this(Neo4jSingleton.getInstance().getDatabase(DB_PATH));
	}
	
	public ModelItem(Graph graph) {
		this.graph = graph;
		this.children = new ArrayList<>();
	}
	
	public void update(Bounds b) {
		System.out.println(getClass().getCanonicalName() + ": " + b.contains(getBoundsInParent()));
		System.out.println(getBoundsInParent());
		
		if (b.contains(getBoundsInParent())) {
			for (ModelItem m : children) {
				m.update(b);
				getChildren().addAll(m.children);
			}
		} else {
			for (ModelItem m : children) {
				m.getChildren().clear();
			}
		}
	}
}
