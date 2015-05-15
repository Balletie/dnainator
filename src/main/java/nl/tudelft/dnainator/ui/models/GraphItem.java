package nl.tudelft.dnainator.ui.models;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;

public class GraphItem extends CompositeItem {
	public GraphItem() {
		super(Neo4jSingleton.getInstance().getDatabase());

		localToRootProperty().set(new Translate());

		setContent(new Rectangle(1000, 20, Color.BLACK));
	}

	@Override
	public void update(Bounds b) {
		if (b.getWidth() > 1000) {
			getContent().setVisible(true);
			getChildRoot().getChildren().clear();
		} else {
			getContent().setVisible(false);

			for (int i = 0; i < 10; i++) {
				ClusterItem ci = new ClusterItem(localToRootProperty());
				ci.setTranslateX(i * 100);
				getChildItems().add(ci);
				getChildRoot().getChildren().add(ci);
			}

			for (ModelItem m : getChildItems()) {
				m.update(b);
			}
		}
	}
}
