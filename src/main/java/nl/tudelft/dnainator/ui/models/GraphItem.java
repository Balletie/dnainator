package nl.tudelft.dnainator.ui.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;

public class GraphItem extends CompositeItem {
	public GraphItem() {
		super(Neo4jSingleton.getInstance().getDatabase());

		rootToItemProperty().bind(localToParentTransformProperty());

		setContent(new Rectangle(1000, 20, Color.BLACK));

		ModelItem mi;
		mi = new ClusterItem(rootToItemProperty());
		mi.setTranslateX(0);
		getChildItems().add(mi);
		mi = new ClusterItem(rootToItemProperty());
		mi.setTranslateX(100);
		getChildItems().add(mi);
		mi = new ClusterItem(rootToItemProperty());
		mi.setTranslateX(200);
		getChildItems().add(mi);
		mi = new ClusterItem(rootToItemProperty());
		mi.setTranslateX(300);
		getChildItems().add(mi);
	}

	@Override
	public void update() {
		update(300);
	}
}
