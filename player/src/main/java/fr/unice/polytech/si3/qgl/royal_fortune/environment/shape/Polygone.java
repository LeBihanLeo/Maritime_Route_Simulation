package fr.unice.polytech.si3.qgl.royal_fortune.environment.shape;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.royal_fortune.calculus.GeometryPolygone;
import fr.unice.polytech.si3.qgl.royal_fortune.calculus.Mathematician;
import fr.unice.polytech.si3.qgl.royal_fortune.calculus.Vector;
import fr.unice.polytech.si3.qgl.royal_fortune.ship.Position;
import fr.unice.polytech.si3.qgl.royal_fortune.target.Beacon;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static fr.unice.polytech.si3.qgl.royal_fortune.Cockpit.SECURITY_UPSCALE;

/**
 * @author Bonnet Kilian Imami Ayoub Karrakchou Mourad Le Bihan Leo
 *
 */

@JsonIgnoreProperties(value = {
        "type",
        "segmentList"
        })
public class Polygone extends Shape{
    private static final Logger Log = Logger.getLogger(Polygone.class.getName());
    private double orientation;
    private Point[] vertices;
    private List<Segment> segmentList = new ArrayList<>();

    public Polygone() {}

    public Polygone(Point[] vertices, double orientation) {
        super("polygon");
        this.vertices = vertices;
        this.orientation = orientation;
    }

    public double getOrientation() {
        return orientation;
    }

    public Point[] getVertices() {
        return vertices;
    }

    public List<Segment> getSegmentList() {
        return segmentList;
    }

    /**
     * For a given polygon will shift all local coordinates to their global coordinates and upscale their size
     * to prevent from oaring to close from a rift.
     * @param center The center of the shape.
     */
    public void updatePolygone(Position center){
        if (!super.updated){
            for(int i = 0; i < vertices.length; i++){
                Point currentPont = vertices[i];
                Vector centerPointUnitVector = new Vector(new Point(0,0), currentPont).unitVector();
                vertices[i] = new Point(
                        (int) Math.ceil(currentPont.getX() + centerPointUnitVector.x * SECURITY_UPSCALE),
                        (int) Math.ceil(currentPont.getY() + centerPointUnitVector.y * SECURITY_UPSCALE)
                );
            }
            Mathematician.changeBasePointList(vertices, center);
        }

        super.updated=true;

    }

    @Override
    public List<Position> computeIntersectionWith(Segment segment, Position seaEntitiesPos) {
        return GeometryPolygone.computeIntersectionWith(segment, seaEntitiesPos, this.getSegmentList());
    }

    @Override
    public List<Beacon> generateBeacon(Position aPosition, boolean isAReef) {
        return GeometryPolygone.generateBeacon(aPosition, this);
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            Log.info("Json Exception");
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Polygone polygone = (Polygone) o;

        if (Double.compare(polygone.orientation, orientation) != 0)
            return false;

        return (Arrays.equals(vertices, polygone.vertices));
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(orientation, segmentList);
        result = 31 * result + Arrays.hashCode(vertices);
        return result;
    }
}
