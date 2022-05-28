package fr.unice.polytech.si3.qgl.royal_fortune.environment.shape;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.unice.polytech.si3.qgl.royal_fortune.calculus.GeometryRectangle;
import fr.unice.polytech.si3.qgl.royal_fortune.ship.Position;
import fr.unice.polytech.si3.qgl.royal_fortune.target.Beacon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static fr.unice.polytech.si3.qgl.royal_fortune.Cockpit.SECURITY_UPSCALE;

/**
 * @author Bonnet Killian Imami Ayoub Karrakchou Mourad Le Bihan Leo
 *
 */

@JsonIgnoreProperties(value = {
		"type",
		"segmentList"
})
public class
Rectangle extends Shape{

	private double width;
	private double height;
	private double orientation;
	private List<Segment> segmentList = new ArrayList<>();


	public Rectangle() {}
	
	public Rectangle(double width, double height, double orientation) {
		super("rectangle");
		this.width = width;
		this.height = height;
		this.orientation = orientation;
	}

	public double getWidth() {
		return width;
	}
	public double getHeight() {
		return height;
	}
	public double getOrientation() {
		return orientation;
	}


	@Override
	public List<Beacon> generateBeacon(Position aPosition, boolean isAReef) {
		return GeometryRectangle.generateBeacon(aPosition,this,isAReef);
	}

	@Override
	public List<Position> computeIntersectionWith(Segment segment, Position seaEntitiesPos) {
		return GeometryRectangle.computeIntersectionWith(segment, seaEntitiesPos, this.getSegmentList());
	}

	@Override
	public Boolean positionIsInTheShape(Position pointA, Position seaEntitiesPos) {
		return(GeometryRectangle.positionIsInTheRectangle(pointA,seaEntitiesPos,this));
	}

	@Override
	public void updateForReef() {
		if (!super.updated) {
			height+=SECURITY_UPSCALE;
			width+=SECURITY_UPSCALE;
		}
		super.updated=true;
	}

	@Override
	public boolean equals(Object object){
		if (!(object instanceof Rectangle rectangle))
			return false;
		return this.width == rectangle.getWidth() &&
				this.height == rectangle.height &&
				this.orientation == rectangle.orientation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(width, height, orientation, segmentList);
	}

	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode oarActionJSON = mapper.createObjectNode();
		oarActionJSON.put("type", "rectangle");
		oarActionJSON.put("width", width);
		oarActionJSON.put("height", height);
		oarActionJSON.put("orientation", orientation);


		try {
			return mapper.writeValueAsString(oarActionJSON);
		} catch (JsonProcessingException e) {
			logger.log(Level.INFO, "Exception");
		}
		return "";
	}

	public List<Segment> getSegmentList() {
		return segmentList;
	}
}
