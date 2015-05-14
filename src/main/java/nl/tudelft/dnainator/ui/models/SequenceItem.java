package nl.tudelft.dnainator.ui.models;

import java.util.List;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import nl.tudelft.dnainator.core.SequenceNode;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;


public class SequenceItem extends ModelItem {
	public SequenceItem(ObjectProperty<Transform> parent) {
		super(Neo4jSingleton.getInstance().getDatabase());

		ObjectBinding<Transform> transform = new ObjectBinding<Transform>() {
			{
				super.bind(parent);
				super.bind(localToParentTransformProperty());
			}
			@Override
			protected Transform computeValue() {
				return parent.get().createConcatenation(getLocalToParentTransform());
			}
		};
		rootToItemProperty().bind(transform);
	}

	@Override
	public void update(Bounds b) {
		int rank = (int) localToRoot(new Rectangle().getBoundsInLocal()).getMinX() / 25;

		Group g = new Group();
		List<SequenceNode> nodes = getGraph().getRank(rank);
		for (int i = 0; i < nodes.size(); i++) {
			Circle c = new Circle(10, Color.ORANGE);
			c.setTranslateY(i * 25);
			g.getChildren().add(c);
		}

		setContent(g);
		// System.out.println(localToRoot(new Rectangle().getBoundsInLocal()));
	}
}
