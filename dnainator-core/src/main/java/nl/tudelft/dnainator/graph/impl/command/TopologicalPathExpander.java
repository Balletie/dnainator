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
		System.out.println(from.getProperty(SequenceProperties.ID.name()));
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
		boolean newSource = n.getDegree(RelTypes.NEXT, Direction.OUTGOING) >= 2;
		for (Relationship out : n.getRelationships(RelTypes.NEXT, Direction.OUTGOING)) {
			propagateSourceIDs(ins, n.getDegree(RelTypes.NEXT, Direction.INCOMING),
					out, newSource, n.getId());
		}
		n.addLabel(NodeLabels.BUBBLE_SOURCE);
	}

	private void propagateSourceIDs(Iterable<Relationship> ins, int inDegree, Relationship rel,
			boolean newSource, long newSourceID) {
		List<Long> propagatedSources = new ArrayList<>();
		for (Relationship in : ins) {
			long[] oldSources = (long[]) in.getProperty(BUBBLE_SOURCE_IDS);
			IntStream.range(0, oldSources.length)
					.mapToObj(i -> oldSources[i])
					.forEach(id -> {
						propagatedSources.add(id);
					});
		}
		Set<Long> propagatedUnique = new HashSet<Long>(propagatedSources);
		propagatedUnique.forEach(id ->
			bubbleSourceIDtoNumEdges.put(id, Collections.frequency(propagatedSources, id)));
		if (newSource) {
			propagatedUnique.add(newSourceID);
			bubbleSourceIDtoNumEdges.put(newSourceID,
					bubbleSourceIDtoNumEdges.getOrDefault(newSourceID, 0) + 1);
		}

		GraphDatabaseService s = rel.getEndNode().getGraphDatabase();
		System.out.println("set "
				+ propagatedUnique.stream().map(l -> s.getNodeById(l).getProperty("ID"))
					.collect(Collectors.toList())
				+ ": " + rel.getStartNode().getProperty(SequenceProperties.ID.name())
				+ " -> " + rel.getEndNode().getProperty(SequenceProperties.ID.name()));

		rel.setProperty(BUBBLE_SOURCE_IDS, propagatedUnique
				.stream().mapToLong(l -> l).toArray());
	}

	private void createBubbleSink(Node n) {
		if (n.getDegree(RelTypes.NEXT, Direction.INCOMING) >= 2) {
			Set<Long> bubbleSourceID = new HashSet<>();
			n.addLabel(NodeLabels.BUBBLE_SINK);
			for (Relationship in : n.getRelationships(RelTypes.NEXT, Direction.INCOMING)) {

				System.out.println("get " + in.getStartNode().getProperty(SequenceProperties.ID.name())
						+ " -> " + in.getEndNode().getProperty(SequenceProperties.ID.name()));

				for (long sourceID : (long[]) in.getProperty(BUBBLE_SOURCE_IDS)) {
					bubbleSourceID.add(sourceID);
				}
			}
			bubbleSourceID.forEach(id -> {
				Node bubbleSource = n.getGraphDatabase().getNodeById(id);
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