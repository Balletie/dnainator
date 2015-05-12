package nl.tudelft.dnainator.ui.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.dnainator.graph.impl.Neo4jGraph;

public class GraphItem extends ModelItem {
	public GraphItem() {
		super((Neo4jGraph) null);
		
		ModelItem mi;
		mi = new ClusterItem();
		mi.setTranslateX(200);
		children.add(mi);
		mi = new ClusterItem();
		mi.setTranslateX(400);
		children.add(mi);
		mi = new ClusterItem();
		mi.setTranslateX(600);
		children.add(mi);
		mi = new ClusterItem();
		mi.setTranslateX(800);
		children.add(mi);
		
		getChildren().add(new Rectangle(1000, 20, Color.BLACK));
		getChildren().addAll(children);
	}
}
