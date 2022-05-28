package fr.unice.polytech.si3.qgl.royal_fortune.calculus.dijkstra;

import fr.unice.polytech.si3.qgl.royal_fortune.calculus.Cartologue;
import fr.unice.polytech.si3.qgl.royal_fortune.environment.shape.Segment;
import fr.unice.polytech.si3.qgl.royal_fortune.ship.Position;
import fr.unice.polytech.si3.qgl.royal_fortune.target.Beacon;
import fr.unice.polytech.si3.qgl.royal_fortune.target.Route;

import java.util.*;

/**
 * @author Bonnet Kilian Imami Ayoub Karrakchou Mourad Le Bihan Leo
 *
 */
public class Dijkstra {
    private static Map<Integer, Double> routeMap = new HashMap<>();

    /**
     * For a given list of Beacons, will proceed the Dijkstra algorithm to find the shortest path possible.
     * @param departure - The position of the departure point.
     * @param arrival - The position of the arrival point.
     * @param cartologue - The class providing the list of reef.
     * @param beacons - The list of all the beacons on the map.
     * @return The shortest path as a stack of Beacons (departure & arrival positions are excluded).
     */
    public static List<Beacon> proceedDijkstra(Position departure, Position arrival, Cartologue cartologue, List <Beacon> beacons){
        List<DijkstraNode> availableNodes = generateDijkstraNodes(beacons);
        DijkstraNode arrivalNode = new DijkstraNode(arrival);
        availableNodes.add(arrivalNode);

        DijkstraNode departureNode = DijkstraNode.generateStartingNode(departure);

        Set<DijkstraNode> updatedNodes = updateNodes(departureNode, availableNodes, cartologue);

        DijkstraNode minNode;
        while((minNode = Collections.min(updatedNodes)) != arrivalNode){
            updatedNodes.addAll(updateNodes(minNode, availableNodes, cartologue));
            updatedNodes.remove(minNode);
        }

        return minNode.getPath();
    }

    /**
     * For a given node, will check every available neighbor nodes and update their node value if the new value
     * is lower than the previous one.
     *
     * @param currentNode - The node to update all the available neighbor nodes.
     * @param availableNodes - The list of all available nodes (nodes we don't already go through).
     * @param cartologue - The tool used to calculate the new node value.
     * @return The set of all updated nodes.
     */
    private static Set<DijkstraNode> updateNodes(DijkstraNode currentNode, List<DijkstraNode> availableNodes, Cartologue cartologue){
        // Creating a set of all updated nodes.
        availableNodes.remove(currentNode);
        Set<DijkstraNode> updatedNodes = new HashSet<>();

        for (DijkstraNode node : availableNodes){
            // Creating a route between the currentNode and one of the possible node.
            double newNodeValue = getRouteValue(currentNode.getNode().getPosition(), node.getNode().getPosition(), cartologue) + currentNode.getNodeValue();
            if(newNodeValue < node.getNodeValue()){
                node.setNodeValue(newNodeValue);
                node.setPreviousNode(currentNode);
                updatedNodes.add(node);
            }
        }
        return updatedNodes;
    }

    /**
     * For each beacon on the beacon list, we will generate a list of node containing the beacon.
     * @param beacons - The provided list of beacons.
     * @return The generated list of nodes.
     */
    private static List<DijkstraNode> generateDijkstraNodes(List<Beacon> beacons){
        List<DijkstraNode> dijkstraNodes = new ArrayList<>();
        beacons.forEach(beacon -> dijkstraNodes.add(new DijkstraNode(beacon)));
        return dijkstraNodes;
    }

    /**
     * Give a value to a segment
     * @param a position
     * @param b position
     * @param cartologue cartologue
     * @return value of a Route
     */
    private static double getRouteValue(Position a, Position b, Cartologue cartologue){
        Segment s = new Segment(a, b);
        int hash = s.hashCode();
        if(!routeMap.containsKey(hash))
             routeMap.put(hash, new Route(s, cartologue).getValue());
        return routeMap.get(hash);
    }

    public static void clearMap(){
        routeMap = new HashMap<>();
    }
}
