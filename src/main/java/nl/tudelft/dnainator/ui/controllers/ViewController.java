package nl.tudelft.dnainator.ui.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import nl.tudelft.dnainator.ui.models.GraphItem;
import nl.tudelft.dnainator.ui.models.ModelItem;
import nl.tudelft.dnainator.ui.views.View;

/**
 * Controller class for all graph interaction.
 */
public class ViewController {
	@FXML private View view;
	@FXML private Group group;

	private Scale scale;
	private Translate translate;
	private Transform worldToLocal;

	private ModelItem mi;
	private Point2D dragstart;

	@FXML
	private void initialize() {
		translate = new Translate(400, 400);
		translate.setOnTransformChanged(e -> worldToLocal = translate.createConcatenation(scale));

		scale = new Scale();
		scale.setOnTransformChanged(e -> worldToLocal = translate.createConcatenation(scale));

		mi = new GraphItem();
		mi.getTransforms().add(translate);
		mi.getTransforms().add(scale);
		view.getChildren().add(mi);
	}

	@FXML
	private void onMouseDragged(MouseEvent e) {
		if (e.getButton() == MouseButton.PRIMARY) {
			Point2D end = new Point2D(e.getX(), e.getY());
			Point2D delta = end.subtract(dragstart);
			translate.setX(translate.getX() + delta.getX());
			translate.setY(translate.getY() + delta.getY());

			dragstart = end;
		}
	}

	@FXML
	private void onMousePressed(MouseEvent e) {
		if (e.getButton() == MouseButton.PRIMARY) {
			dragstart = new Point2D(e.getX(), e.getY());
		}
	}

	@FXML
	private void onScroll(ScrollEvent e) {
		scale.setX(scale.getX() + (scale.getX() * e.getDeltaY() / 1000));
		scale.setY(scale.getY() + (scale.getY() * e.getDeltaY() / 1000));
		System.out.println("view in world space: " + localToWorld(view.getLayoutBounds()));
		mi.update(localToWorld(view.getLayoutBounds()));
	}

	public Bounds localToWorld(Bounds b) {
		Bounds world = null;
		try {
			world = worldToLocal.inverseTransform(b);
		} catch (NonInvertibleTransformException e) {
			e.printStackTrace();
		}
		return world;
	}
}
