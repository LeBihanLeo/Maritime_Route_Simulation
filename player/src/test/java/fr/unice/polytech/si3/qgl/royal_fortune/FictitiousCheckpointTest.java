package fr.unice.polytech.si3.qgl.royal_fortune;

import fr.unice.polytech.si3.qgl.royal_fortune.environment.FictitiousCheckpoint;
import fr.unice.polytech.si3.qgl.royal_fortune.ship.Position;
import fr.unice.polytech.si3.qgl.royal_fortune.environment.shape.Circle;
import fr.unice.polytech.si3.qgl.royal_fortune.environment.Checkpoint;
import fr.unice.polytech.si3.qgl.royal_fortune.target.Beacon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FictitiousCheckpointTest {
    FictitiousCheckpoint emptyFictitiousCheckpoints;

    @BeforeEach
    void init(){
        emptyFictitiousCheckpoints = new FictitiousCheckpoint(null);
    }

    @Test
    void createFictitiousCheckpointTestVertically(){
        // CurrentCheckpoint is located in (0,0) and has a radius of 100
        Position currentCheckpointPosition = new Position(0, 0, 0);
        Circle currentCheckpointShape= new Circle(1000);
        Checkpoint currentCheckpoint = new Checkpoint(currentCheckpointPosition, currentCheckpointShape);

        // NextCheckpoint is located in (0,1000) and has a radius of 100
        Position nextCheckpointPosition = new Position(0, 1000, 0);
        Circle nextCheckpointShape = new Circle(100);
        Checkpoint nextCheckpoint = new Checkpoint(nextCheckpointPosition, nextCheckpointShape);

        Checkpoint fictitiousCheckpoint = emptyFictitiousCheckpoints.createFictitiousCheckpoint(currentCheckpoint, nextCheckpoint);
        assertEquals(0, fictitiousCheckpoint.getPosition().getX());
        assertEquals(500, fictitiousCheckpoint.getPosition().getY());
        assertEquals(500, ((Circle) fictitiousCheckpoint.getShape()).getRadius());
    }

    @Test
    void createFictitiousCheckpointTestHorizontally(){
        // CurrentCheckpoint is located in (0,0) and has a radius of 100
        Position currentCheckpointPosition = new Position(0, 0, 0);
        Circle currentCheckpointShape= new Circle(1000);
        Checkpoint currentCheckpoint = new Checkpoint(currentCheckpointPosition, currentCheckpointShape);

        // NextCheckpoint is located in (1000,0) and has a radius of 100
        Position nextCheckpointPosition = new Position(1000, 0, 0);
        Circle nextCheckpointShape = new Circle(100);
        Checkpoint nextCheckpoint = new Checkpoint(nextCheckpointPosition, nextCheckpointShape);

        Checkpoint fictitiousCheckpoint = emptyFictitiousCheckpoints.createFictitiousCheckpoint(currentCheckpoint, nextCheckpoint);
        assertEquals(500, fictitiousCheckpoint.getPosition().getX());
        assertEquals(0, fictitiousCheckpoint.getPosition().getY());
        assertEquals(500, ((Circle) fictitiousCheckpoint.getShape()).getRadius());
    }

    @Test
    void createFictitiousCheckpointTestDiagonally(){
        // CurrentCheckpoint is located in (0,0) and has a radius of 100
        Position currentCheckpointPosition = new Position(0, 0, 0);
        Circle currentCheckpointShape= new Circle(1000);
        Checkpoint currentCheckpoint = new Checkpoint(currentCheckpointPosition, currentCheckpointShape);

        // NextCheckpoint is located in (1000,1000) and has a radius of 100
        Position nextCheckpointPosition = new Position(1000, 1000, 0);
        Circle nextCheckpointShape = new Circle(100);
        Checkpoint nextCheckpoint = new Checkpoint(nextCheckpointPosition, nextCheckpointShape);

        Checkpoint fictitiousCheckpoint = emptyFictitiousCheckpoints.createFictitiousCheckpoint(currentCheckpoint, nextCheckpoint);
        assertEquals(354, Math.round(fictitiousCheckpoint.getPosition().getX()));
        assertEquals(354, Math.round(fictitiousCheckpoint.getPosition().getY()));
        assertEquals(500, ((Circle) fictitiousCheckpoint.getShape()).getRadius());
    }

    @Test
    void createFictitiousCheckpointTest(){
        // CurrentCheckpoint is located in (0,0) and has a radius of 100
        Position currentCheckpointPosition = new Position(0, 0, 0);
        Circle currentCheckpointShape= new Circle(1000);
        Checkpoint currentCheckpoint = new Checkpoint(currentCheckpointPosition, currentCheckpointShape);

        // NextCheckpoint is located in (120,-90) and has a radius of 20
        Position nextCheckpointPosition = new Position(120, -90, 0);
        Circle nextCheckpointShape = new Circle(20);
        Checkpoint nextCheckpoint = new Checkpoint(nextCheckpointPosition, nextCheckpointShape);

        Checkpoint fictitiousCheckpoint = emptyFictitiousCheckpoints.createFictitiousCheckpoint(currentCheckpoint, nextCheckpoint);
        assertEquals(400, fictitiousCheckpoint.getPosition().getX());
        assertEquals(-300, fictitiousCheckpoint.getPosition().getY());
        assertEquals(500, ((Circle) fictitiousCheckpoint.getShape()).getRadius());
    }

    @Test
    void createFictitiousCheckpointsTest(){
        List<Checkpoint> originalCheckpoints = new ArrayList<>();

        // CurrentCheckpoint is located in (0,0) and has a radius of 100
        Position currentCheckpointPosition = new Position(0, 0, 0);
        Circle currentCheckpointShape= new Circle(1000);
        Checkpoint currentCheckpoint = new Checkpoint(currentCheckpointPosition, currentCheckpointShape);
        originalCheckpoints.add(currentCheckpoint);

        // NextCheckpoint is located in (120,-90) and has a radius of 20
        Position secondCheckpointPosition = new Position(120, -90, 0);
        Circle secondCheckpointShape = new Circle(200);
        Checkpoint secondCheckpoint = new Checkpoint(secondCheckpointPosition, secondCheckpointShape);
        originalCheckpoints.add(secondCheckpoint);

        // NextCheckpoint is located in (200,-90) and has a radius of 30
        Position thirdCheckpointPosition = new Position(200, -90, 0);
        Circle thirdCheckpointShape = new Circle(300);
        Checkpoint thirdCheckpoint = new Checkpoint(thirdCheckpointPosition, thirdCheckpointShape);
        originalCheckpoints.add(thirdCheckpoint);

        FictitiousCheckpoint fictitiousCheckpoints = new FictitiousCheckpoint(originalCheckpoints);

        // First Checkpoint
        assertEquals(400, fictitiousCheckpoints.getCurrentCheckPoint().getPosition().getX());
        assertEquals(-300, fictitiousCheckpoints.getCurrentCheckPoint().getPosition().getY());
        assertEquals(500, ((Circle) fictitiousCheckpoints.getCurrentCheckPoint().getShape()).getRadius());

        // Second Checkpoint
        fictitiousCheckpoints.nextCheckPoint();
        assertEquals(220, fictitiousCheckpoints.getCurrentCheckPoint().getPosition().getX());
        assertEquals(-90, fictitiousCheckpoints.getCurrentCheckPoint().getPosition().getY());
        assertEquals(100, ((Circle) fictitiousCheckpoints.getCurrentCheckPoint().getShape()).getRadius());

        // Third Checkpoint (unchanged)
        fictitiousCheckpoints.nextCheckPoint();
        assertEquals(200, fictitiousCheckpoints.getCurrentCheckPoint().getPosition().getX());
        assertEquals(-90, fictitiousCheckpoints.getCurrentCheckPoint().getPosition().getY());
        assertEquals(300, ((Circle) fictitiousCheckpoints.getCurrentCheckPoint().getShape()).getRadius());

        fictitiousCheckpoints.nextCheckPoint();
        assertNull(fictitiousCheckpoints.getCurrentCheckPoint());
    }

    @Test
    void addBeaconsTest(){
        Stack<Beacon> beaconStack = new Stack<>();
        beaconStack.add(new Beacon(new Position(0, 0, 0)));
        beaconStack.add(new Beacon(new Position(1, 0, 0)));
        beaconStack.add(new Beacon(new Position(2, 0, 0)));
        List<Checkpoint> initialCheckpoints = new ArrayList<>();
        initialCheckpoints.add(new Checkpoint(new Position(0, 0), new Circle(0)));


        FictitiousCheckpoint fictitiousCheckpoints = new FictitiousCheckpoint(initialCheckpoints);

        fictitiousCheckpoints.addBeacons(beaconStack);
        List<Checkpoint> checkpoints = fictitiousCheckpoints.getFictitiousCheckpoints();
        assertEquals(0, checkpoints.get(0).getPosition().getX());
        assertEquals(1, checkpoints.get(1).getPosition().getX());
        assertEquals(2, checkpoints.get(2).getPosition().getX());

        beaconStack = new Stack<>();
        beaconStack.add(new Beacon(new Position(4, 0, 0)));
        fictitiousCheckpoints.addBeacons(beaconStack);
        assertEquals(2, fictitiousCheckpoints.getFictitiousCheckpoints().size());
        assertEquals(4, checkpoints.get(0).getPosition().getX());
    }



}
