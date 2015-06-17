package nl.tudelft.dnainator.graph.impl.command;

import nl.tudelft.dnainator.graph.impl.NodeLabels;
import nl.tudelft.dnainator.graph.impl.RelTypes;
import nl.tudelft.dnainator.graph.impl.properties.SequenceProperties;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.BranchState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * PathExpander for determining the topological ordering.
 */
public class TopologicalPathExpander implements PathExpander<Object> {
	private static final String PROCESSED = "PROCESSED";
	private static final String BUBBLE_SOURCE_IDS = "BUBBLE_SOURCE_IDS";
	private HashMap<Long, Integer> bubbleSourceIDtoNumEdges;

	/**
	 * Constructs a new {@link TopologicalPathExpander}.
	 */
	public TopologicalPathExpander() {
		this.bubbleSourceIDtoNumEdges = new HashMap<>();
	}

	private boolean hasUnprocessedIncoming(Node n) {
		Iterable<Relationship> in = n.getRelationships(RelTypes.NEXT, Direction.INCOMING);
		for (Relationship r : in) {
			if (!r.hasProperty(PROCESSED)) {
				return true;
			}
		}
		// Clean up after ourselves.
		in.forEach(rel -> rel.removeProperty(PROCESSED));
		// All incoming edges have been processed.
		return false;
	}

	@Override
	public Iterable<Relationship> expand(Path path,
			BranchState<Object> noState) {
		Node from = path.endNode();
		createBubbleSource(from);
		List<Relationship> expand = new LinkedList<>();
		for (Relationship r : from.getRelationships(RelTypes.NEXT, Direction.OUTGOING)) {
			setNumStrainsThrough(r);
			r.setProperty(PROCESSED, true);
			if (!hasUnprocessedIncoming(r.getEndNode())) {
				createBubbleSink(r.getEndNode());
				// All of the dependencies of this node have been added to the result.
				expand.add(r);
			}
		}
		return expand;
	}

	private void createBubbleSource(Node n) {
		Iterable<Relationship> ins = n.getRelationships(RelTypes.NEXT, Direction.INCOMING);
		int outDegree = n.getDegree(RelTypes.NEXT, Direction.OUTGOING);
		int inDegree = n.getDegree(RelTypes.NEXT, Direction.INCOMING);
		int degreeDiff = outDegree - inDegree;
		boolean newSource = outDegree >= 2;

		List<Long> propagatedSources = new ArrayList<>();
		for (Relationship in : ins) {
			long[] oldSources = (long[]) in.getProperty(BUBBLE_SOURCE_IDS);
			IntStream.range(0, oldSources.length)
					.mapToObj(i -> oldSources[i])
					.forEach(id -> {
						if (bubbleSourceIDtoNumEdges.get(id) != null) {
							propagatedSources.add(id);
						}
					});
		}
		Set<Long> propagatedUnique = new HashSet<Long>(propagatedSources);

		// begin testprint
		System.out.print("at " + n.getProperty(SequenceProperties.ID.name()) + ": ");
		for (Entry<Long, Integer> e : bubbleSourceIDtoNumEdges.entrySet()) {
			Node source = n.getGraphDatabase().getNodeById(e.getKey());
			System.out.print(source.getProperty("ID") + " = " + e.getValue() + " paths, ");
		}
		System.out.println();
		// end testprint

		propagatedUnique.forEach(id -> {
			if (bubbleSourceIDtoNumEdges.get(id) != null) {
				bubbleSourceIDtoNumEdges.put(id, bubbleSourceIDtoNumEdges.get(id) + degreeDiff);
			}
		});

		for (Relationship out : n.getRelationships(RelTypes.NEXT, Direction.OUTGOING)) {
			propagateSourceIDs(propagatedUnique, out, newSource, n.getId());
		}
		n.addLabel(NodeLabels.BUBBLE_SOURCE);
	}

	private void propagateSourceIDs(Set<Long> propagatedUnique, Relationship out,
			boolean newSource, long newSourceID) {
		if (newSource) {
			propagatedUnique.add(newSourceID);
			bubbleSourceIDtoNumEdges.put(newSourceID,
					bubbleSourceIDtoNumEdges.getOrDefault(newSourceID, 0) + 1);
		}
		GraphDatabaseService s = out.getEndNode().getGraphDatabase();
		System.out.println("set "
				+ propagatedUnique.stream().map(l -> s.getNodeById(l).getProperty("ID"))
					.collect(Collectors.toList())
				+ ": " + out.getStartNode().getProperty(SequenceProperties.ID.name())
				+ " -> " + out.getEndNode().getProperty(SequenceProperties.ID.name()));

		out.setProperty(BUBBLE_SOURCE_IDS, propagatedUnique
				.stream().mapToLong(l -> l).toArray());
	}

	private void createBubbleSink(Node n) {
		int degree = n.getDegree(RelTypes.NEXT, Direction.INCOMING);
		if (degree >= 2) {
			Set<Long> bubbleSourceID = new HashSet<>();
			n.addLabel(NodeLabels.BUBBLE_SINK);
			for (Relationship in : n.getRelationships(RelTypes.NEXT, Direction.INCOMING)) {

				System.out.println("get " + in.getStartNode().getProperty(SequenceProperties.ID.name())
						+ " -> " + in.getEndNode().getProperty(SequenceProperties.ID.name()));

				for (long sourceID : (long[]) in.getProperty(BUBBLE_SOURCE_IDS)) {
					if (bubbleSourceIDtoNumEdges.get(sourceID) <= degree) {
						bubbleSourceID.add(sourceID);
					}
				}
			}
			bubbleSourceID.forEach(id -> {
				if (bubbleSourceIDtoNumEdges.get(id) == degree) {
					bubbleSourceIDtoNumEdges.remove(id);
				}
				Node bubbleSource = n.getGraphDatabase().getNodeById(id);
				System.out.println("bubble: " + bubbleSource.getProperty("ID")
						+ " " + n.getProperty("ID"));
				n.createRelationshipTo(bubbleSource, RelTypes.BUBBLE_SINK_OF);
				bubbleSource.createRelationshipTo(n, RelTypes.BUBBLE_SOURCE_OF);
			});
		}
	}

	private void setNumStrainsThrough(Relationship r) {
		r.setProperty(SequenceProperties.EDGE_NUM_STRAINS.name(), Math.abs(
				r.getStartNode().getDegree(RelTypes.SOURCE)
				- r.getEndNode().getDegree(RelTypes.SOURCE)));
	}

	@Override
	public PathExpander<Object> reverse() {
		throw new UnsupportedOperationException();
	}

}