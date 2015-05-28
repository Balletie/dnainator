package nl.tudelft.dnainator.ui.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.dnainator.core.impl.Cluster;
import nl.tudelft.dnainator.graph.Graph;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;

/**
 * The {@link GraphItem} class represents the top level object in the viewable model.
 * It is a {@link CompositeItem}, that can hold both content and children.
 */
public class GraphItem extends ModelItem {
	private static final int FOUR = 4;
	private static final int CLUSTER_DIVIDER = 100;

	private Graph graph;
	private List<RankItem> children;
	private Map<String, ClusterItem> clusters;
	private Group childContent;

	/**
	 * Construct a new top level {@link GraphItem} using the default graph.
	 */
	public GraphItem() {
		this(Neo4jSingleton.getInstance().getDatabase());
	}

	/**
	 * Construct a new top level {@link GraphItem} using the specified graph.
	 * @param graph	the specified graph
	 */
	public GraphItem(Graph graph) {
		super(null, 0);
		this.graph = graph;
		this.children = new ArrayList<>();
		this.childContent = new Group();
		getChildren().add(childContent);
		this.clusters = new HashMap<>();

		for (int i = 0; i < FOUR; i++) {
			int width = NO_CLUSTERS * RANK_WIDTH / FOUR;
			Rectangle r = new Rectangle(width, RANK_WIDTH, Color.BLACK);
			r.setTranslateX(i * width);
			getContent().getChildren().add(r);
		}
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	@Override
	public Map<String, ClusterItem> getClusters() {
		return clusters;
	}

	@Override
	public ModelItem getRoot() {
		return this;
	}

	public void loadRanks(Bounds b) {
		System.out.println("load iteration");
		children.clear();
		childContent.getChildren().clear();

		int minrank = (int) (Math.max(b.getMinX() / RANK_WIDTH, 0));
		int maxrank = (int) (b.getMaxX() / RANK_WIDTH);
		List<String> roots = getGraph().getRank(minrank).stream()
						.map(e -> e.getId()).collect(Collectors.toList());
		System.out.println(b.getWidth() / CLUSTER_DIVIDER);
		Map<Integer, List<Cluster>> clusters = getGraph().getAllClusters(roots, maxrank,
										(int) (b.getWidth() / CLUSTER_DIVIDER));

		clusters.forEach((k, v) -> children.add(new RankItem(this, k, v)));
	};

	/**
	 * Toggle between displaying own content or children.
	 * @param b		the bounds of the viewport
	 * @param visible	true for visible
	 */
	public void toggle(Bounds b, boolean visible) {
		if (visible && !getContent().isVisible()) {
			childContent.getChildren().clear();
			getContent().setVisible(true);
		}
		if (!visible) {
			loadRanks(b);
			getContent().setVisible(false);
			childContent.getChildren().addAll(children);
		}
	}

	/**
	 * Update visibility for this node and children.
	 * @param b	the bounds of the viewport
	 * @param t	the threshold for viewing
	 */
	public void update(Bounds b, Thresholds t) {
		if (b.getWidth() > t.get()) {
			toggle(b, true);
		} else {
			toggle(b, false);

			for (RankItem r : children) {
				r.update(b);
			}
		}
	}

	@Override
	public void update(Bounds b) {
		update(b, Thresholds.GRAPH);
	}
}
