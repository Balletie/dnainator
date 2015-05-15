package nl.tudelft.dnainator.graph.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;

import nl.tudelft.dnainator.core.SequenceNode;
import nl.tudelft.dnainator.core.impl.Neo4jSequenceNodeFactory;
import nl.tudelft.dnainator.graph.GraphQueryDescription;

/**
 * A useful class for creating and executing a query on a Neo4j
 * database using a {@link GraphQueryDescription}.
 */
public class Neo4jQuery {
	private String cypherQuery;
	private Map<String, Object> parameters;
	private GraphQueryDescription description;

	/**
	 * Create a new query suitable for Neo4j from the given description.
	 * @param qd the query description to use for constructing the query.
	 */
	public Neo4jQuery(GraphQueryDescription qd) {
		this.description = qd;
		buildQuery();
	}

	private void addCondition(boolean conditionBefore, StringBuilder sb, String c) {
		if (conditionBefore) {
			sb.append("AND ");
		}
		sb.append(c);
	}

	private void buildQuery() {
		parameters = new HashMap<>();
		StringBuilder query = new StringBuilder("MATCH n\nWHERE ");
		boolean conditionBefore = false;
		if (description.shouldQueryIds()) {
			addCondition(conditionBefore, query, "n.id IN {ids}\n");
			parameters.put("ids", description.getIds());
			conditionBefore = true;
		}
		if (description.shouldQuerySources()) {
			addCondition(conditionBefore, query, "n.source IN {sources}\n");
			parameters.put("sources", description.getSources());
		}
		if (description.shouldQueryFrom()) {
			addCondition(conditionBefore, query, "dist > {from}");
			parameters.put("from", description.getFrom());
		}
		if (description.shouldQueryTo()) {
			addCondition(conditionBefore, query, "dist < {to}");
			parameters.put("to", description.getTo());
		}
		cypherQuery = query.toString();
	}

	/**
	 * Execute the query on the given database.
	 * @param db the database to execute the query on.
	 * @return the query result.
	 */
	public List<SequenceNode> execute(GraphDatabaseService db) {
		List<SequenceNode> result = new ArrayList<>();
		Predicate<SequenceNode> p;
		if (description.shouldFilter()) {
			p = description.getFilter();
		} else {
			p = (sn) -> true;
		}
		Neo4jSequenceNodeFactory nf = new Neo4jSequenceNodeFactory();
		try (Transaction tx = db.beginTx()) {
			Result r = db.execute(cypherQuery, parameters);
			ResourceIterator<Node> it = r.columnAs("n");
			for (Node n : IteratorUtil.loop(it)) {
				SequenceNode sn = nf.build(n);
				if (p.test(sn)) {
					result.add(sn);
				}
			}
		}
		return result;
	}
}
