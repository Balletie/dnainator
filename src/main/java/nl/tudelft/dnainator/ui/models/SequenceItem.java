package nl.tudelft.dnainator.ui.models;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Transform;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;


public class SequenceItem extends ModelItem {
	public SequenceItem(ObjectProperty<Transform> parent) {
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

		setContent(new Circle(5, Color.ORANGE));
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
}
