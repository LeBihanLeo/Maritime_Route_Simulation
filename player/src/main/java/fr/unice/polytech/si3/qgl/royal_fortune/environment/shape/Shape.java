package fr.unice.polytech.si3.qgl.royal_fortune.environment.shape;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.unice.polytech.si3.qgl.royal_fortune.calculus.GeometryPolygone;
import fr.unice.polytech.si3.qgl.royal_fortune.calculus.GeometryRectangle;
import fr.unice.polytech.si3.qgl.royal_fortune.ship.Position;
import fr.unice.polytech.si3.qgl.royal_fortune.target.Beacon;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bonnet Killian Imami Ayoub Karrakchou Mourad Le Bihan Leo
 *
 */
@JsonTypeInfo(use = Id.NAME, property = "type", include = As.EXTERNAL_PROPERTY)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = Circle.class, name = "circle"),
        @JsonSubTypes.Type(value = Rectangle.class, name = "rectangle"),
		@JsonSubTypes.Type(value = Polygone.class, name = "polygon")
})

public class Shape {
	private String type;
	final Logger logger = Logger.getLogger(Shape.class.getName());
	boolean updated = false;

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public Shape() {
	}

	public Shape(String type) {
		this.type = type;
	}


	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode oarActionJSON = mapper.createObjectNode();
		oarActionJSON.put("life", type);

		try {
			return mapper.writeValueAsString(oarActionJSON);
		} catch (JsonProcessingException e) {
			logger.log(Level.INFO, "Exception");
		}
		return "";
	}

	public Optional<Circle> isCircle() {
		if (this instanceof Circle current) {
			return Optional.of(current);
		}
		return Optional.empty();
	}

	public Optional<Rectangle> isRectangle() {
		if (this instanceof Rectangle current) {
			return Optional.of(current);
		}
		return Optional.empty();
	}

	/**
	 * Computes a shape into segments
	 * @param position position
	 */
	public void computeSegmentsIfPossible(Position position) {
		if (this instanceof Rectangle currentRectangle) {
			List<Segment> listSeg = GeometryRectangle.computeSegments(position, currentRectangle);
			currentRectangle.getSegmentList().clear();
			currentRectangle.getSegmentList().addAll(listSeg);
		}
		else if (this instanceof Polygone currentPoly) {
			List<Segment> listSeg = GeometryPolygone.computeSegments(currentPoly.getVertices());
			currentPoly.getSegmentList().clear();
			currentPoly.getSegmentList().addAll(listSeg);
		}
	}

	public void updateForReef() {
		//Make reefs bigger for safety
	}

	public Boolean positionIsInTheShape(Position pointA, Position seaEntitiesPos) {
		return false;
	}

	public List<Position> computeIntersectionWith(Segment segment, Position seaEntitiesPos) {
		return Collections.emptyList();
	}

	public List<Beacon> generateBeacon(Position aPosition, boolean isAReef) {
		return Collections.emptyList();
	}


	public Optional<Polygone> isPolygone() {
		if (this instanceof Polygone current) {
			return Optional.of(current);
		}
		return Optional.empty();
	}

}
