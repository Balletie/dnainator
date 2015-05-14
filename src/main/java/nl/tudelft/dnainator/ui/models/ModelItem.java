package nl.tudelft.dnainator.ui.models;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import nl.tudelft.dnainator.graph.Graph;

public abstract class ModelItem extends Pane {
	private Graph graph;
	private Group content;

	public ModelItem(Graph graph) {
		this.graph = graph;
		this.content = new Group();

		getChildren().add(content);
	}

	public Group getContent() {
		return content;
	}

	public void setContent(Node node) {
		content.getChildren().clear();
		content.getChildren().add(node);
	}

	public abstract Transform getRootToItem();
	
	public Bounds localToRoot(Bounds b) {
		return getRootToItem().transform(b);
	}

	public abstract void update();
}
