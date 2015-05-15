package nl.tudelft.dnainator.ui.models;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Transform;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;

public class ClusterItem extends CompositeItem {
	public ClusterItem(ObjectProperty<Transform> parent) {
		super(Neo4jSingleton.getInstance().getDatabase());

		bindLocalToRoot(parent);

		setContent(new Circle(20, Color.BLUE));
	}

	@Override
	public void update(Bounds b) {
		if (!isInViewport(b)) return;

		if (b.getWidth() > 600) {
			getContent().setVisible(true);
			getChildRoot().getChildren().clear();
		} else {
			if (!getContent().isVisible()) return;

			getContent().setVisible(false);

			for (int i = 0; i < 10; i++) {
				SequenceItem si = new SequenceItem(localToRootProperty());
				si.setTranslateX(i * 10);
				getChildItems().add(si);
				getChildRoot().getChildren().add(si);
			}

			for (ModelItem m : getChildItems()) {
				m.update(b);
			}
		}
	}
}
