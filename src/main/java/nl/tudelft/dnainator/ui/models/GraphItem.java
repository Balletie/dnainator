package nl.tudelft.dnainator.ui.models;

import nl.tudelft.dnainator.graph.impl.Neo4jGraph;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class GraphItem extends ModelItem {
	public GraphItem() {
		super((Neo4jGraph) null);
		
		Circle c; Rectangle r;
		r = new Rectangle(1000, 20, Color.BLACK);
		getChildren().add(r);
		
		c = new Circle(20, Color.BLUE);
		c.setTranslateX(0);
		c.setTranslateY(100);
		getChildren().add(c);
		
		c = new Circle(20, Color.BLUE);
		c.setTranslateX(350);
		c.setTranslateY(100);
		getChildren().add(c);
		
		c = new Circle(20, Color.BLUE);
		c.setTranslateX(650);
		c.setTranslateY(100);
		getChildren().add(c);
		
		c = new Circle(20, Color.BLUE);
		c.setTranslateX(1000);
		c.setTranslateY(100);
		getChildren().add(c);
	}
}
