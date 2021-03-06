package nl.tudelft.dnainator.javafx.drawables.strains;

import nl.tudelft.dnainator.core.EnrichedSequenceNode;
import nl.tudelft.dnainator.core.PropertyType;
import nl.tudelft.dnainator.core.impl.Cluster;
import nl.tudelft.dnainator.javafx.ColorMap;
import nl.tudelft.dnainator.javafx.drawables.phylogeny.AbstractNode;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test the drawable class that is used for clusters.
 * This class contains a certain amount of clustered sequencenodes,
 * and will dynamically change its size based on that amount. 
 */
@RunWith(MockitoJUnitRunner.class)
public class ClusterDrawableTest {
	@Mock private ColorMap colorserver;
	@Mock private EnrichedSequenceNode node;
	
	private ClusterDrawable cluster;
	
	/**
	 * Set up common mocks and variables.
	 */
	@Before
	public void setup() {
		Set<String> sources = new HashSet<>();
		sources.add("1");
		Mockito.when(node.getSources()).thenReturn(sources);
	}
	
	/**
	 * Test creation of a cluster with a single node.
	 */
	@Test
	public void testCreate() {
		Cluster c = new Cluster(0, Arrays.asList(node));
		cluster = new ClusterDrawable(colorserver, c);
		
		// CHECKSTYLE.OFF: MagicNumber
		Mockito.verify(colorserver, Mockito.times(1)).getColor("1");
		assertEquals(ClusterDrawable.SINGLE_RADIUS, cluster.getRadius(), .001);
		assertEquals(1, cluster.getCluster().getNodes().size());
		// CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Test creation of a cluster with a small cluster.
	 */
	@Test
	public void testCreateSmall() {
		Cluster c = new Cluster(0, Arrays.asList(node, node, node));
		cluster = new ClusterDrawable(colorserver, c);
		
		// CHECKSTYLE.OFF: MagicNumber
		Mockito.verify(colorserver, Mockito.times(1)).getColor("1");
		assertEquals(ClusterDrawable.SMALL_RADIUS, cluster.getRadius(), .001);
		assertEquals(3, cluster.getCluster().getNodes().size());
		// CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Test creation of a cluster with a medium cluster.
	 */
	@Test
	public void testCreateMedium() {
		Cluster c = new Cluster(0, Arrays.asList(node, node, node, node));
		cluster = new ClusterDrawable(colorserver, c);
		
		// CHECKSTYLE.OFF: MagicNumber
		Mockito.verify(colorserver, Mockito.times(1)).getColor("1");
		assertEquals(ClusterDrawable.MEDIUM_RADIUS, cluster.getRadius(), .001);
		assertEquals(4, cluster.getCluster().getNodes().size());
		// CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Test creation of a cluster with a large cluster.
	 */
	@Test
	public void testCreateLarge() {
		Cluster c = new Cluster(0, Arrays.asList(node, node, node, node, node,
							node, node, node, node, node, node));
		cluster = new ClusterDrawable(colorserver, c);
		
		// CHECKSTYLE.OFF: MagicNumber
		Mockito.verify(colorserver, Mockito.times(1)).getColor("1");
		assertEquals(ClusterDrawable.LARGE_RADIUS, cluster.getRadius(), .001);
		assertEquals(11, cluster.getCluster().getNodes().size());
		// CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Get the property map from a {@link ClusterDrawable}.
	 */
	@Test
	public void testGetPropertyMap() {
		Cluster c = new Cluster(0, Arrays.asList(node));
		cluster = new ClusterDrawable(colorserver, c);
		Map<PropertyType, String> properties = cluster.getPropertyMap();
		
		assertNotNull(properties);
		// CHECKSTYLE.OFF: MagicNumber
		assertEquals(9, properties.size());
		// CHECKSTYLE.ON: MagicNumber
		assertEquals(properties, cluster.getPropertyMap());
	}
	
	/**
	 * Test adding and removing a css style on the children of the {@link AbstractNode}.
	 */
	@Test
	public void testAddAndRemoveStyle() {
		Cluster c = new Cluster(0, Arrays.asList(node));
		cluster = new ClusterDrawable(colorserver, c);

		// Add style.
		cluster.addStyle("some style");
		Pie p = (Pie) cluster.getChildren().get(1);
		assertFalse(p.getStyles().isEmpty());
		
		// Remove the added style.
		cluster.removeStyle("some style");
		assertTrue(p.getStyles().isEmpty());
	}	
}
