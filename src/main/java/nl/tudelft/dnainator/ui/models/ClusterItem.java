package nl.tudelft.dnainator.ui.models;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Transform;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;

public class ClusterItem extends CompositeItem {
	public ClusterItem(ObjectProperty<Transform> parent) {
		super(Neo4jSingleton.getInstance().getDatabase());

		ObjectBinding<Transform> transform = new ObjectBinding<Transform>() {
			{
				super.bind(parent);
			}
			@Override
			protected Transform computeValue() {
				return parent.get().createConcatenation(getLocalToParentTransform());
			}
		};
		rootToItemProperty().bind(transform);

		setContent(new Circle(20, Color.BLUE));

		ModelItem mi;
		mi = new SequenceItem(rootToItemProperty());
		mi.setTranslateX(0);
		getChildItems().add(mi);
		mi = new SequenceItem(rootToItemProperty());
		mi.setTranslateX(25);
		getChildItems().add(mi);
		mi = new SequenceItem(rootToItemProperty());
		mi.setTranslateX(50);
		getChildItems().add(mi);
		mi = new SequenceItem(rootToItemProperty());
		mi.setTranslateX(75);
		getChildItems().add(mi);
	}

	@Override
	public void update() {
		update(1000);
	}
}
