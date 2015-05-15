package nl.tudelft.dnainator.ui.models;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import nl.tudelft.dnainator.graph.Graph;

/**
 * The abstract {@link ModelItem} represents a single object in the viewable model.
 * This class follows the composite design pattern,
 * together with the {@link CompositeItem} and the leaf class, {@link SequenceItem}.
 *
 * Every {@link ModelItem} can hold JavaFX content that can be drawn to the screen.
 * What content is drawn will change dynamically based on the scale of the viewport.
 *
 * In order to determine the positioning of the JavaFX content,
 * every modelitem holds a rootToItem transform property, which should be bound to
 * the concatenation of all parent rootToItem properties when instantiating a
 * concrete subclass.
 */
public abstract class ModelItem extends Pane {
	private Graph graph;
	private Group content;
	private ObjectProperty<Transform> localToRoot;

	/**
	 * Base constructor for a {@link ModelItem}.
	 * Every {@link ModelItem} needs a reference to it's graph. 
	 * @param graph	a {@link Graph}
	 */
	public ModelItem(Graph graph) {
		this.graph = graph;
		this.content = new Group();
		this.localToRoot = new SimpleObjectProperty<>();

		getChildren().add(content);
	}

	/**
	 * This method binds localToRoot to the concatenated transforms of the parent and child.
	 * Every subclass, except the root, should bind its parent for correct positioning.
	 * @param parent	the parent transform
	 */
	public void bindLocalToRoot(ObjectProperty<Transform> parent) {
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
		localToRootProperty().bind(transform);
	}

	/**
	 * Return the content of this {@link ModelItem}.
	 * @return	the content
	 */
	public Group getContent() {
		return content;
	}

	/**
	 * Set the content of this {@link ModelItem}.
	 * @param node	the new content
	 */
	public void setContent(Node node) {
		content.getChildren().clear();
		content.getChildren().add(node);
	}

	/**
	 * Return the underlying graph of this {@link ModelItem}.
	 * @return	the underlying graph
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Return the concatenation of transforms from the root to this item.
	 * @return	a concatenation of transforms
	 */
	public Transform getLocalToRoot() {
		return localToRoot.get();
	}

	/**
	 * Set the concatenation of transforms from the root to this item.
	 * @param t	a concatenation of transforms
	 */
	public void setLocalToRoot(Transform t) {
		localToRoot.set(t);
	}

	/**
	 * Return the property containing the concatenation of transforms from the root to this item.
	 * @return	a concatenation of transforms
	 */
	public ObjectProperty<Transform> localToRootProperty() {
		return localToRoot;
	}

	/**
	 * Transform a given bounding box b from local coordinates to root coordinates.
	 * @param b	the bounds to transform
	 * @return	the transformed bounds
	 */
	public Bounds localToRoot(Bounds b) {
		return getLocalToRoot().transform(b);
	}

	public boolean isInViewport(Bounds b) {
		return b.intersects(localToRoot(getContent().getBoundsInLocal()));
	}

	/**
	 * Update method that should be called after scaling.
	 * This method checks how zoomed in we are by transforming bounds to root coordinates,
	 * and then dynamically adds and deletes items in the JavaFX scene graph.
	 *
	 * TODO: check whether something is visible in the viewport!
	 * @param b	the bounds of the viewport to update
	 */
	public abstract void update(Bounds b);
}
