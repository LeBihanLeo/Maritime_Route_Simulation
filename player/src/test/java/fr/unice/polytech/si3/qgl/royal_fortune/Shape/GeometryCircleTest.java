package fr.unice.polytech.si3.qgl.royal_fortune.Shape;

import fr.unice.polytech.si3.qgl.royal_fortune.calculus.GeometryCircle;
import fr.unice.polytech.si3.qgl.royal_fortune.environment.shape.Circle;
import fr.unice.polytech.si3.qgl.royal_fortune.environment.shape.Rectangle;
import fr.unice.polytech.si3.qgl.royal_fortune.environment.shape.Segment;
import fr.unice.polytech.si3.qgl.royal_fortune.ship.Position;
import fr.unice.polytech.si3.qgl.royal_fortune.target.Beacon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeometryCircleTest {
    GeometryCircle geometryCircle;

    @BeforeEach
    void init() {
        geometryCircle = new GeometryCircle();
    }

    @Test
    void rectangleIsInCircle(){
        double radius = 500.;
        double h = 100;
        double w = 100;
        double o = 0;
        Position seaEntitiePos = new Position(0,0,0);

        Rectangle r = new Rectangle(w, h , o);

        assertTrue(GeometryCircle.rectangleIsInCircle(r, seaEntitiePos, radius));
    }

    @Test
    void rectangleIsInCircleLimitTest(){
        double radius = Math.sqrt(Math.pow(50, 2) + Math.pow(50, 2));
        double h = 100;
        double w = 100;
        double o = 0;
        Position seaEntitiePos = new Position(0,0,0);

        Rectangle r = new Rectangle(w, h , o);

        assertTrue(GeometryCircle.rectangleIsInCircle(r, seaEntitiePos, radius));
    }

    @Test
    void rectangleIsNotInCircle(){
        double radius = 50.;
        double h = 100;
        double w = 100;
        double o = 0;
        Position circlePos = new Position(1000,0,0);

        Rectangle r = new Rectangle(w, h , o);

        assertFalse(GeometryCircle.rectangleIsInCircle(r, circlePos, radius));
    }

    @Test
    void toStringTest(){
        double radius = 50.;
        Circle c = new Circle(radius);

        assertNotEquals("", c.toString());
    }

    @Test
    void generateBeaconTest() {
        Position reefPosition = new Position(100, -10);
        Circle reefShape = new Circle(1000);

        List<Beacon> beaconList = GeometryCircle.generateBeacon(reefPosition, reefShape);

        assertEquals(50, beaconList.size());

        assertEquals(1200, beaconList.get(0).getPosition().getX());
        assertEquals(-10, beaconList.get(0).getPosition().getY());

        assertEquals(100 + Math.cos((2 * Math.PI) / 50) * 1100, beaconList.get(1).getPosition().getX());
        assertEquals(-10 + Math.sin((2 * Math.PI) / 50) * 1100, beaconList.get(1).getPosition().getY());

        assertEquals(100 + Math.cos(37*(2 * Math.PI) / 50) * 1100, beaconList.get(37).getPosition().getX());
        assertEquals(-10 + Math.sin(37*(2 * Math.PI) / 50) * 1100, beaconList.get(37).getPosition().getY());
    }

    @Test
    void discriminantValuePositiveTest() {
        Segment segmentToWorkOn = new Segment(new Position(0,0), new Position(10, 0));
        Position pointASave = new Position(1, 2);
        Position pointBSave = new Position(2, 3);
        double discriminant = 0.000001;
        double circlePositionX = 4;
        double circlePositionY = 5;
        List<Position> intersectionList =  new ArrayList<>();
        Segment segment=new Segment(pointASave,pointBSave);
        Position circlePosition=new Position(circlePositionX,circlePositionY);


        GeometryCircle.discriminantValue(segmentToWorkOn, segment, discriminant,circlePosition, intersectionList);

        intersectionList.add(0, new Position(0,0)); //triche
        assertEquals(1, intersectionList.size());
    }

    @Test
    void discriminantValuePositiveButIntersectionsNotOnSegmentTest() {
        Segment segmentToWorkOn = new Segment(new Position(0,0), new Position(10, 0));
        Position pointASave = new Position(1, 2);
        Position pointBSave = new Position(2, 3);
        double discriminant = 0.000001;
        double circlePositionX = 4;
        double circlePositionY = 5;
        List<Position> intersectionList =  new ArrayList<>();
        Segment segment=new Segment(pointASave,pointBSave);
        Position circlePosition=new Position(circlePositionX,circlePositionY);


        GeometryCircle.discriminantValue(segmentToWorkOn, segment, discriminant,circlePosition, intersectionList);
        assertEquals(0, intersectionList.size());
    }

    @Test
    void discriminantValueZeroTest() {
        Segment segmentToWorkOn = new Segment(new Position(0,0), new Position(17, 0));
        Position pointASave = new Position(0, 0);
        Position pointBSave = new Position(17, 0);
        double discriminant = 0;
        double circlePositionX = 8;
        double circlePositionY = 3;
        List<Position> intersectionList =  new ArrayList<>();
        Segment segment=new Segment(pointASave,pointBSave);
        Position circlePosition=new Position(circlePositionX,circlePositionY);


        GeometryCircle.discriminantValue(segmentToWorkOn, segment, discriminant,circlePosition, intersectionList);
        assertEquals(1, intersectionList.size());
    }

    @Test
    void discriminantValueZero2Test() {
        Segment segmentToWorkOn = new Segment(new Position(-4,-8), new Position(0, 4));
        Position pointASave = new Position(-4, -8);
        Position pointBSave = new Position(0, 4);
        double discriminant = 0;
        double circlePositionX = 3;
        double circlePositionY = -3;
        List<Position> intersectionList =  new ArrayList<>();
        Segment segment=new Segment(pointASave,pointBSave);
        Position circlePosition=new Position(circlePositionX,circlePositionY);


        GeometryCircle.discriminantValue(segmentToWorkOn, segment, discriminant,circlePosition, intersectionList);
        assertEquals(1, intersectionList.size());
    }

    @Test
    void discriminantValueZeroButIntersectionNotOnSegmentTest() {
        Segment segmentToWorkOn = new Segment(new Position(0,0), new Position(10, 0));
        Position pointASave = new Position(1, 2);
        Position pointBSave = new Position(2, 3);
        double discriminant = 0;
        double circlePositionX = 4;
        double circlePositionY = 5;
        List<Position> intersectionList =  new ArrayList<>();
        Segment segment=new Segment(pointASave,pointBSave);
        Position circlePosition=new Position(circlePositionX,circlePositionY);

        GeometryCircle.discriminantValue(segmentToWorkOn, segment, discriminant,circlePosition, intersectionList);
        assertEquals(1, intersectionList.size());
    }

    @Test
    void segmentToWorkOnTest() {
        Segment segment = new Segment(new Position(-2, 3), new Position(4, 7));
        Position circlePosition = new Position(2, 1);

        Segment newSegment = GeometryCircle.segmentToWorkOn(segment, circlePosition);

        Segment expectedSegment = new Segment(new Position(-4, 2), new Position(2, 6));

        assertEquals(expectedSegment.getPointA().getX(), newSegment.getPointA().getX());
        assertEquals(expectedSegment.getPointA().getY(), newSegment.getPointA().getY());

        assertEquals(expectedSegment.getPointB().getX(), newSegment.getPointB().getX());
        assertEquals(expectedSegment.getPointB().getY(), newSegment.getPointB().getY());
    }

    @Test
    void discriminantTest() {
        double discriminant = GeometryCircle.discriminant(0, 6, 6);
        assertEquals(0, discriminant);

        discriminant = GeometryCircle.discriminant(-4.2, 6.7, 5);
        assertEquals(1684.4399999999998, discriminant);
    }

    @Test
    void zeroDiscriminantTest() {
        Segment segmentToWorkOn = new Segment(new Position(1958.6115846399198, -555.325402717619), new Position(1958.6115846399216, 567.540386430971));
        Position pointASave = new Position(6106.437671596443, 4721.547561451762);
        Position pointBSave = new Position(6106.4376715964445, 5844.413350600352);
        double circlePositionX = 4147.826086956523;
        double circlePositionY = 5276.872964169381;
        List<Position> intersectionList = new ArrayList<>();
        Segment segment=new Segment(pointASave,pointBSave);
        Position circlePosition=new Position(circlePositionX,circlePositionY);

        GeometryCircle.zeroDiscriminant(segmentToWorkOn, segment,circlePosition, intersectionList);

        assertEquals(1, intersectionList.size());
        assertEquals(6106.437671596444, intersectionList.get(0).getX());
        assertEquals(5276.872964169381, intersectionList.get(0).getY());
        assertEquals(0, intersectionList.get(0).getOrientation());
    }

    @Test
    void realPositionTest() {
        Position realPosition = GeometryCircle.realPosition(3.4, 5.2, 8.7, 14.5, 6.5);

        assertEquals(17.9, realPosition.getX());
        assertEquals(32.879999999999995, realPosition.getY());
    }

    @Test
    void positiveDiscriminantTest() {
        Segment segmentToWorkOn = new Segment(new Position(-546.1814567862507, -721.6029491783202), new Position(-721.6029491783202, 546.1814567862502));
        Position pointASave = new Position(2427.731586692009, 2185.5631746001827);
        Position pointBSave = new Position(2252.3100942999395, 3453.347580564753);
        Segment segment=new Segment(pointASave,pointBSave);
        double discriminant = 1.8633379671894073E7;
        double circlePositionX = 2973.9130434782596;
        double circlePositionY = 2907.166123778503;
        Position circlePosition=new Position(circlePositionX,circlePositionY);
        List<Position> intersectionList = new ArrayList<>();

        GeometryCircle.positiveDiscriminant(segmentToWorkOn, segment, discriminant,circlePosition, intersectionList);

        assertEquals(2, intersectionList.size());

        assertEquals(2380.567424348, intersectionList.get(0).getX());
        assertEquals(2526.422146467071, intersectionList.get(0).getY());
        assertEquals(0, intersectionList.get(0).getOrientation());

        assertEquals(2299.474256643948, intersectionList.get(1).getX());
        assertEquals(3112.488608697866, intersectionList.get(1).getY());
        assertEquals(0, intersectionList.get(1).getOrientation());
    }

    @Test
    void positiveDiscriminant2Test() {
        Segment segmentToWorkOn = new Segment(new Position(-1108.6956521739094, -1758.9576547231277), new Position(-471.48492234688365, -524.1440336391834));
        Position pointASave = new Position(1865.2173913043503, 1148.2084690553752);
        Position pointBSave = new Position(2502.428121131376, 2383.0220901393195);
        double discriminant = 8846974.069128951;
        double circlePositionX = 2973.9130434782596;
        double circlePositionY = 2907.166123778503;
        Segment segment=new Segment(pointASave,pointBSave);
        Position circlePosition=new Position(circlePositionX,circlePositionY);
        List<Position> intersectionList = new ArrayList<>();

        GeometryCircle.positiveDiscriminant(segmentToWorkOn, segment, discriminant,circlePosition, intersectionList);

        assertEquals(0, intersectionList.size());
    }

    @Test
    void positiveDiscriminant3Test() {
        Segment segmentToWorkOn = new Segment(new Position(-1108.6956521739094, -1758.9576547231277), new Position(2517.725529782771, 5268.473086254841));
        Position pointASave = new Position(1865.2173913043503, 1148.2084690553752);
        Position pointBSave = new Position(5491.638573261031, 8175.639210033344);
        double discriminant = 8846974.069128953;
        double circlePositionX = 2973.9130434782596;
        double circlePositionY = 2907.166123778503;
        Segment segment=new Segment(pointASave,pointBSave);
        Position circlePosition=new Position(circlePositionX,circlePositionY);
        List<Position> intersectionList = new ArrayList<>();

        GeometryCircle.positiveDiscriminant(segmentToWorkOn, segment, discriminant,circlePosition, intersectionList);

        assertEquals(2, intersectionList.size());

        assertEquals(2502.428121131376, intersectionList.get(0).getX());
        assertEquals(2383.0220901393195, intersectionList.get(0).getY());
        assertEquals(0, intersectionList.get(0).getOrientation());

        assertEquals(3127.92587438173, intersectionList.get(1).getX());
        assertEquals(3595.1378149963166, intersectionList.get(1).getY());
        assertEquals(0, intersectionList.get(1).getOrientation());
    }

    @Test
    void computeIntersectionWithTest() {
        Segment segment = new Segment(new Position(0,0), new Position(100, 0));
        Position circlePosition = new Position(50, 0);
        Circle circle = new Circle(20);

        List<Position> intersectionList = GeometryCircle.computeIntersectionWith(segment, circlePosition, circle);

        assertEquals(4, intersectionList.size());

        assertEquals(0, intersectionList.get(0).getX());
        assertEquals(0, intersectionList.get(0).getY());

        assertEquals(30, intersectionList.get(1).getX());
        assertEquals(0, intersectionList.get(1).getY());

        assertEquals(70, intersectionList.get(2).getX());
        assertEquals(0, intersectionList.get(2).getY());

        assertEquals(100, intersectionList.get(3).getX());
        assertEquals(0, intersectionList.get(3).getY());

    }
}
