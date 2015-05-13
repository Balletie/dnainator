package nl.tudelft.dnainator.ui.views;

import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import nl.tudelft.dnainator.ui.models.GraphItem;
import nl.tudelft.dnainator.ui.models.ModelItem;

/**
 * This class is the View part of the MVC pattern.
 */
public class View extends Region {
	private Group group;
	private Scale scale;

	/**
	 * Creates a new view instance.
	 */
	public View() {
		this.group = new Group();
		this.scale = new Scale();

		getChildren().add(this.group);
		getStyleClass().add("view");

		ModelItem mi = new GraphItem();
		mi.setTranslateX(400);
		mi.setTranslateY(400);
		mi.getTransforms().add(scale);

		setOnScroll(ev -> {
			scale.setX(scale.getX() + (scale.getX() * ev.getDeltaY() / 1000));
			scale.setY(scale.getY() + (scale.getX() * ev.getDeltaY() / 1000));
			System.out.println("scale: " + scale);
			System.out.println("view:  " + getLayoutBounds());
			mi.update(getLayoutBounds());
		});

		getChildren().add(mi);
	}
}
