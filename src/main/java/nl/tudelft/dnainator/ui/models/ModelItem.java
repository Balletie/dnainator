package nl.tudelft.dnainator.ui.models;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import nl.tudelft.dnainator.graph.Graph;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;

public abstract class ModelItem extends Pane {
	private Graph graph;
	private Node content;
	private List<ModelItem> children;
	private Group childRoot;

	public ModelItem() {
		this(Neo4jSingleton.getInstance().getDatabase());
	}

	public ModelItem(String DB_PATH) {
		this(Neo4jSingleton.getInstance().getDatabase(DB_PATH));
	}

	public ModelItem(Graph graph) {
		this.graph = graph;
		this.children = new ArrayList<>();
		this.childRoot = new Group();

		getChildren().add(childRoot);
	}

	public Node getContent() {
		return content;
	}

	public void setContent(Node content) {
		this.content = content;
	}

	public Group getChildRoot() {
		return childRoot;
	}

	public void setChildRoot(Group childroot) {
		this.childRoot = childroot;
	}

	public List<ModelItem> getChildItems() {
		return children;
	}

	public abstract Transform getRootToItem();

	public void setChildItems(List<ModelItem> childItems) {
		this.children = childItems;
	}

	public void update(Bounds b) {
		Bounds boundsInViewport = getRootToItem().transform(getContent().getBoundsInParent());

		System.out.println(getClass().getSimpleName() + ": " + b.contains(boundsInViewport));
		System.out.println("viewport:  " + boundsInViewport);
		System.out.println("parent:    " + localToParent(getContent().getBoundsInParent()));
		System.out.println("local:     " + getContent().getBoundsInParent() + "\n");

		if (b.contains(boundsInViewport)) {
			getContent().setVisible(true);
			getChildRoot().getChildren().clear();
		} else {
			getContent().setVisible(false);
			getChildRoot().getChildren().clear(); // FIXME
			getChildRoot().getChildren().addAll(getChildItems());
			for (ModelItem m : children) {
				m.update(b);
			}
		}
	}
}
