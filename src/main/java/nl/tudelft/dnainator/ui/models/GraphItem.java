package nl.tudelft.dnainator.ui.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;

public class GraphItem extends CompositeItem {
	private ObjectProperty<Transform> rootToItem;


	public GraphItem() {
		super(Neo4jSingleton.getInstance().getDatabase());

		rootToItem = new SimpleObjectProperty<>();
		rootToItem.bind(localToParentTransformProperty());

		setContent(new Rectangle(1000, 20, Color.BLACK));

		ModelItem mi;
		mi = new ClusterItem(rootToItem);
		mi.setTranslateX(0);
		getChildItems().add(mi);
		mi = new ClusterItem(rootToItem);
		mi.setTranslateX(100);
		getChildItems().add(mi);
		mi = new ClusterItem(rootToItem);
		mi.setTranslateX(200);
		getChildItems().add(mi);
		mi = new ClusterItem(rootToItem);
		mi.setTranslateX(300);
		getChildItems().add(mi);
	}

	@Override
	public Transform getRootToItem() {
		return rootToItem.get();
	}

	@Override
	public void update() {
		update(300);
	}
}
