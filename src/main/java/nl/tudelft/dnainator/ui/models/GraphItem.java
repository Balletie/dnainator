package nl.tudelft.dnainator.ui.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import nl.tudelft.dnainator.graph.impl.Neo4jGraph;

public class GraphItem extends ModelItem {
	private ObjectProperty<Transform> rootToItem;

	public GraphItem() {
		super((Neo4jGraph) null);

		rootToItem = new SimpleObjectProperty<>();
		rootToItem.bind(localToParentTransformProperty());

		setContent(new Rectangle(1000, 20, Color.BLACK));
		getChildren().add(getContent());

		ModelItem mi;
		mi = new ClusterItem(rootToItem);
		mi.setTranslateX(0);
		getChildItems().add(mi);
		mi = new ClusterItem(rootToItem);
		mi.setTranslateX(400);
		getChildItems().add(mi);
		mi = new ClusterItem(rootToItem);
		mi.setTranslateX(600);
		getChildItems().add(mi);
		mi = new ClusterItem(rootToItem);
		mi.setTranslateX(800);
		getChildItems().add(mi);
	}

	@Override
	public Transform getRootToItem() {
		return rootToItem.get();
	}
}
