package nl.tudelft.dnainator.ui.models;

import java.util.List;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import nl.tudelft.dnainator.core.SequenceNode;
import nl.tudelft.dnainator.ui.drawables.DrawableEdge;

/**
 * The {@link ClusterItem} class represents the mid level object in the viewable model.
 * Just like the {@link GraphItem} class, it's a {@link CompositeItem},
 * that can hold both content and children.
 */
public class ClusterItem extends Group {
	private static final int CLUSTER_SIZE = 3;
	private List<SequenceNode> clustered;
	private Group edges;
	private ModelItem parent;

	/**
	 * Construct a new mid level {@link ClusterItem} using the default graph.
	 * Every {@link ClusterItem} needs a reference to its parent.
	 * @param parent	the parent of this {@link ClusterItem}
	 * @param clustered	the clustered {@link SequenceNode}s in this cluster.
	 */
	public ClusterItem(ModelItem parent, List<SequenceNode> clustered) {
		this.parent = parent;
		this.clustered = clustered;
		this.edges = new Group();

		Circle c = new Circle(CLUSTER_SIZE, Color.BLUE);
		c.setOnMouseClicked(e -> System.out.println(clustered));

		getChildren().add(edges);
		getChildren().add(c);

		// Point for each node the cluster it is in.
		clustered.forEach(node -> parent.getClusters().put(node.getId(), this));
	}

	public void load() {
		edges.getChildren().clear();
		for (SequenceNode sn : clustered) {
			for (String out : sn.getOutgoing()) {
				ClusterItem cluster = parent.getClusters().get(out);
				if (cluster == this) {
					continue;
				}
				if (cluster != null) {
					edges.getChildren().add(new DrawableEdge(this, cluster));
				}
			}
		}
	}
}
