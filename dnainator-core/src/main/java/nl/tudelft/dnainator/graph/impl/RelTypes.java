package nl.tudelft.dnainator.graph.impl;

import org.neo4j.graphdb.RelationshipType;

/**
 * Edge relationship types.
 */
public enum RelTypes implements RelationshipType {
	ANCESTOR_OF,
	ANNOTATED,
	NEXT,
	SOURCE,
	BUBBLE_SINK_OF,
	BUBBLE_SOURCE_OF
}
