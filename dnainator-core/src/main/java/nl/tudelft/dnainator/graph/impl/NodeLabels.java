package nl.tudelft.dnainator.graph.impl;

import org.neo4j.graphdb.Label;

/**
 * Static labels for Neo4j nodes.
 */
public enum NodeLabels implements Label {
	ANCESTOR,
	ANNOTATION,
	DRUGRESISTANCE,
	SOURCE,
	NODE,
	BUBBLE_SOURCE,
	BUBBLE_SINK
}
