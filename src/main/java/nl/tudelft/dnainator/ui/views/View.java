package nl.tudelft.dnainator.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import nl.tudelft.dnainator.ui.models.GraphModel;

/**
 * This abstract class is the View part of the MVC pattern.
 * <p>
 * Each extending class defines its own view on the data.
 * </p>
 */
public abstract class View extends Pane {
	private static final int L_PADDING = 20;
	protected GraphModel model;
	protected Group group;

	/**
	 * Creates a new view instance.
	 * @param model The {@link GraphModel} whose data to display.
	 */
	public View(GraphModel model) {
		this.model = model;
		this.group = new Group();

		getChildren().add(this.group);
		setPadding(new Insets(0, 0, 0, L_PADDING));
		getStyleClass().add("view");
	}
}
