package nl.tudelft.dnainator.ui.models;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import nl.tudelft.dnainator.graph.Graph;

public abstract class CompositeItem extends ModelItem {
	private Group childRoot;
	private List<ModelItem> children;

	public CompositeItem(Graph graph) {
		super(graph);

		childRoot = new Group();
		children = new ArrayList<>();
		getChildren().add(childRoot);
	}

	public Group getChildRoot() {
		return childRoot;
	}

	public void setChildRoot(Group childroot) {
		this.childRoot = childroot;
	}

	public List<ModelItem> getChildItems() {
		return children;
	}

	public void setChildItems(List<ModelItem> childItems) {
		this.children = childItems;
	}

	protected void update(double threshold) {
		if (localToRoot(new Rectangle(100, 1).getBoundsInLocal()).getWidth() < threshold) {
			getContent().setVisible(true);
			getChildRoot().getChildren().clear();
		} else {
			getContent().setVisible(false);
			getChildRoot().getChildren().clear(); // FIXME
			getChildRoot().getChildren().addAll(getChildItems());
			for (ModelItem m : children) {
				m.update();
			}
		}
	}
}
