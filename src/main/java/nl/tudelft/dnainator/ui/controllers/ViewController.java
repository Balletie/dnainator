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
	private Translate toCenter;
	private Translate translate;
	private Transform worldToCamera;

	private ModelItem mi;
	private Point2D dragstart;

	@FXML
	private void initialize() {
		toCenter = new Translate();
		view.widthProperty().addListener((o, v1, v2) -> toCenter.setX(v2.intValue() / 2));
		view.heightProperty().addListener((o, v1, v2) -> toCenter.setY(v2.intValue() / 2));

		translate = new Translate();
		translate.setOnTransformChanged(e -> worldToCamera = worldToLocal());

		scale = new Scale();
		scale.setOnTransformChanged(e -> worldToCamera = worldToLocal());

		mi = new GraphItem();
		mi.getTransforms().add(toCenter);
		mi.getTransforms().add(scale);
		mi.getTransforms().add(translate);
		view.getChildren().add(mi);
	}

	private Transform worldToLocal() {
		return toCenter.createConcatenation(scale).createConcatenation(translate);
	}

	@FXML
	private void onMouseDragged(MouseEvent e) {
		if (e.getButton() == MouseButton.PRIMARY) {
			Point2D end = new Point2D(e.getX(), e.getY());
			Point2D delta = end.subtract(dragstart);

			translate.setX(translate.getX() + delta.getX());
			translate.setY(translate.getY() + delta.getY());
			mi.update(cameraToWorld(view.getLayoutBounds()));

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
		System.out.println("view in world space: " + cameraToWorld(view.getLayoutBounds()));
		mi.update(cameraToWorld(view.getLayoutBounds()));
	}

	public Bounds cameraToWorld(Bounds b) {
		Bounds world = null;
		try {
			world = worldToCamera.inverseTransform(b);
		} catch (NonInvertibleTransformException e) {
			e.printStackTrace();
		}
		return world;
	}
}
