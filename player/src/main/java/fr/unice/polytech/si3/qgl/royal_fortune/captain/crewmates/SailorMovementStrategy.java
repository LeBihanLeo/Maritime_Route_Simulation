package fr.unice.polytech.si3.qgl.royal_fortune.captain.crewmates;

import fr.unice.polytech.si3.qgl.royal_fortune.calculus.PreCalculator;
import fr.unice.polytech.si3.qgl.royal_fortune.captain.Associations;
import fr.unice.polytech.si3.qgl.royal_fortune.captain.DirectionsManager;
import fr.unice.polytech.si3.qgl.royal_fortune.ship.Ship;
import fr.unice.polytech.si3.qgl.royal_fortune.ship.entities.*;

import java.util.*;

/**
 * @author Bonnet Kilian Imami Ayoub Karrakchou Mourad Le Bihan Leo
 *
 */
public class SailorMovementStrategy {
    private final List<Sailor> sailors;
    private final Ship ship;
    private final Associations associations;
    private final PreCalculator preCalculator;

    private SailorPlacement currentSailorPlacement;

    public static final int MAX_MOVING_RANGE = 5;

    public SailorMovementStrategy(List<Sailor> sailors, Ship ship, Associations associations,PreCalculator preCalculator){
        this.sailors = sailors;
        this.associations = associations;
        this.ship = ship;
        this.preCalculator = preCalculator;
        this.currentSailorPlacement = new SailorPlacement();
    }

    /**
     * Will try to associate as best the requested parameters
     * Priority order : Rudder, Sail, Oar.
     *
     * @return The SailorPlacement actually made by the SailorPlacementStrategy.
     */
    public SailorPlacement askPlacement(SailorPlacement requestedSailorPlacement) {
        currentSailorPlacement = new SailorPlacement();

        // We are associating every Sailor to a starving entity.
        continueAssociatingStarvingEntities(requestedSailorPlacement);

        // We are associating (if possible) the nearest sailor to the Watch. And if an association has been made.
        associateNearestSailorWatch(requestedSailorPlacement);

        // We are associating (if possible) the nearest sailor to the Rudder. And if an association has been made.
        associateNearestSailorRudder(requestedSailorPlacement);

        // We are associating (if possible) the nearest sailor to the Sail.
        associateNearestSailorSail(requestedSailorPlacement);

        // We are associating (if possible) the left or right oar to the nearest sailor according to the oarWeight.
        associateNearestSailorToOars(requestedSailorPlacement);

        continueAssociatingSailorsToOarEvenly();

        return currentSailorPlacement;
    }

    /**
     * Associates the nearest sailor to the watch
     * @param requestedSailorPlacement requests
     */
    private void associateNearestSailorWatch(SailorPlacement requestedSailorPlacement){
        if(requestedSailorPlacement.hasWatch() && associateNearestSailor(ship.getWatch())){
            requestedSailorPlacement.setWatch(false);
            currentSailorPlacement.setWatch(true);
        }
    }

    /**
     * Associates the nearest sailor to the rudder
     * @param requestedSailorPlacement requests
     */
    private void associateNearestSailorRudder(SailorPlacement requestedSailorPlacement){
        if(requestedSailorPlacement.hasRudder() && associateNearestSailor(ship.getRudder())){
            requestedSailorPlacement.setRudder(false);
            currentSailorPlacement.setRudder(true);
        }
    }

    /**
     * Associates the nearest sailor to the sails
     * @param requestedSailorPlacement requests
     */
    private void associateNearestSailorSail(SailorPlacement requestedSailorPlacement){
        if(requestedSailorPlacement.hasSail()){
            int nbAssociations = 0;
            List<Sail> sails = ship.getSail();

            for(Sail sail : sails){
                if(associations.isFree(sail)){
                    if(associateNearestSailor(sail)) nbAssociations++;
                    currentSailorPlacement.setSail(true);
                }
                else nbAssociations++;
            }

            if(nbAssociations == sails.size())
                requestedSailorPlacement.setSail(false);
        }
    }

    /**
     * RECURSIVE
     * Will do all the different "AssociateSailorsToOarEvenly" cycle until no newer association are made.
     */
    public void continueAssociatingSailorsToOarEvenly(){
        boolean newAssociationMade = false;
        if (canContinueToOarEvenly()){
            newAssociationMade = associateSpecialistSailorToOarEvenly();
        }

        if (canContinueToOarEvenly()){
            newAssociationMade = associateSpecialistSailorAndSailorToOarEvenly();
        }

        if (canContinueToOarEvenly()){
            newAssociationMade = associateSailorsToOarEvenly();
        }

        if(newAssociationMade)
            continueAssociatingSailorsToOarEvenly();
    }

    /**
     * Will associate (if possible) the nearest sailors from the entity to it.
     * @param entity The entity to be associated.
     * @return True/False - The association has been proceeded.
     */
    public boolean associateNearestSailor(Entities entity){
        if(!associations.isFree(entity))
            return false;

        Optional<Sailor> nearestSailor = entity.getNearestSailor(sailors, MAX_MOVING_RANGE, associations);

        if (nearestSailor.isEmpty())
            return false;

        associations.addAssociation(nearestSailor.get(), entity);
        return true;
    }

    /**
     * Will associate (if possible) a Sailor to a starving entity.
     * A starving entity is an entity having only one sailor nearby.
     * If the entity isn't starving no association will be proceeded.
     *
     * @param entity The entity to associate.
     * @return Boolean, the association can be made.
     */
    public boolean associateStarvingEntity(Entities entity){
        List<Sailor> possibleSailors = entity.getSailorsInRange(sailors, MAX_MOVING_RANGE, associations);

        if (possibleSailors.size() != 1)
            return false;

        associations.addAssociation(possibleSailors.get(0), entity);
        return true;
    }

    /**
     * Will associate a sailor to an oar only if the oar is starving.
     *
     * @param direction LEFT - RIGHT, the direction to place the sailors to.
     * @return If a sailor has been associated.
     */
    public boolean associateStarvingOar(int direction){
        List<Oar> oarList = sortEntitiesByDistanceToNearestSailor(ship.getOarList(direction, associations));
        int oarIndex = 0;

        while(oarIndex < oarList.size()){
            if (associateStarvingEntity(oarList.get(oarIndex)))
                return true;
            oarIndex++;
        }
        return false;
    }

    /**
     * RECURSIVE
     * Associating (if possible) the nearest sailor from the oars until the number of requested sailor is reached or
     * until the list of oar is reached.
     *
     * @param requestedSailorPlacement the requested sailor placement.
     */
    public void associateNearestSailorToOars(SailorPlacement requestedSailorPlacement){
        int direction = requestedSailorPlacement.getOarWeight() > 0 ? DirectionsManager.RIGHT : DirectionsManager.LEFT;
        List<Oar> oarList = sortEntitiesByDistanceToNearestSailor(ship.getOarList(direction, associations));

        if (!oarList.isEmpty() && requestedSailorPlacement.getOarWeight() != 0) {
            Optional<Sailor> possibleSailor = oarList.get(0).getNearestSailor(sailors, MAX_MOVING_RANGE, associations);
            if (possibleSailor.isPresent()) {
                associations.addAssociation(possibleSailor.get(), oarList.get(0));
                if (direction == DirectionsManager.LEFT)
                    currentSailorPlacement.incrementNbLeftSailor(1);
                else
                    currentSailorPlacement.incrementNbRightSailor(1);
                requestedSailorPlacement.incrementOarWeight(-direction);
                associateNearestSailorToOars(requestedSailorPlacement);
            }
        }
    }

    /**
     * Will associate (if possible) a Sailor to a starving entity.
     * A starving entity is an entity having only one sailor nearby.
     */
    public boolean associateStarvingEntities(SailorPlacement requestedSailorPlacement){
        // If we need a watch.
        if(checkForWatch(requestedSailorPlacement))
            return true;

        // If we need a rudder.
        if(checkForRudder(requestedSailorPlacement))
            return true;

        // If we need a sail.
        if(checkForSail(requestedSailorPlacement))
            return true;

        // If we need at least on right oar. And if an association has been made.
        if(requestedSailorPlacement.getOarWeight() > 0 && associateStarvingOar(DirectionsManager.RIGHT)) {
            currentSailorPlacement.incrementNbRightSailor(1);
            requestedSailorPlacement.incrementOarWeight(-1);
            return true; // We are returning to be sure to keep the association priority.
        }

        // If we need at least on left oar. And If an association has been made.
        if(requestedSailorPlacement.getOarWeight() < 0 && associateStarvingOar(DirectionsManager.LEFT)) {
            currentSailorPlacement.incrementNbLeftSailor(1);
            requestedSailorPlacement.incrementOarWeight(1);
            return true; // We are returning to be sure to keep the association priority.
        }

        return false;
    }

    /**
     * Checks if a sailor can be associated to sails
     * @param requestedSailorPlacement requests
     * @return if the association succeed
     */
    private boolean checkForSail(SailorPlacement requestedSailorPlacement){
        if (!requestedSailorPlacement.hasSail())
            return false;

        List<Sail> sails = ship.getSail();
        int nbAssociations = 0;

        for(Sail sail : sails){
            // If an association has been made.
            if(associations.isFree(sail)){
                if(associateStarvingEntity(sail)) {
                    currentSailorPlacement.setSail(true);
                    if(++nbAssociations == sails.size())
                        requestedSailorPlacement.setSail(false);
                    return true; // We are returning to be sure to keep the association priority.
                }
            }
            else nbAssociations++;
        }
        return false;
    }

    /**
     * Checks if a sailor can be associated to the rudder
     * @param requestedSailorPlacement requests
     * @return if the association succeed
     */
    private boolean checkForRudder(SailorPlacement requestedSailorPlacement){
        if (requestedSailorPlacement.hasRudder()){
            Rudder rudder = ship.getRudder();
            // If an association has been made
            if(associateStarvingEntity(rudder)){
                requestedSailorPlacement.setRudder(false);
                currentSailorPlacement.setRudder(true);
                return true; // We are returning to be sure to keep the association priority.
            }
        }
        return false;
    }

    /**
     * Checks if a sailor can be associated to the watch
     * @param requestedSailorPlacement requests
     * @return if the association succeed
     */
    private boolean checkForWatch(SailorPlacement requestedSailorPlacement){
        if (requestedSailorPlacement.hasWatch()){
            Watch watch = ship.getWatch();
            // If an association has been made
            if(associateStarvingEntity(watch)){
                requestedSailorPlacement.setWatch(false);
                currentSailorPlacement.setWatch(true);
                return true; // We are returning to be sure to keep the association priority.
            }
        }
        return false;
    }

    /**
     * RECURSIVE
     * If a sailor is specialist, (meaning they can only go to left or right oars) associate
     * specific oar to this sailor and put on the other side sailor who can go both.
     *
     * If there is no two more specialist or normal sailor, the recursive call stops.
     */
    public boolean associateSpecialistSailorAndSailorToOarEvenly(){
        Set<Sailor> sailorsCanGoLeft = getSailorNearToOar(DirectionsManager.LEFT);
        Set<Sailor> sailorsCanGoRight = getSailorNearToOar(DirectionsManager.RIGHT);

        List<Sailor> sailorsCanGoBoth = sailorsCanGoLeft.stream()
                .filter(sailorsCanGoRight::contains)
                .toList();

        if (sailorsCanGoBoth.isEmpty())
            return false;

        List<Sailor> sailorsCanOnlyGoLeft = new ArrayList<>(sailorsCanGoLeft);
        sailorsCanOnlyGoLeft.removeAll(sailorsCanGoRight);

        List<Sailor> sailorsCanOnlyGoRight = new ArrayList<>(sailorsCanGoRight);
        sailorsCanOnlyGoRight.removeAll(sailorsCanGoLeft);


        if (!sailorsCanOnlyGoLeft.isEmpty()){
            Sailor specialistSailor = sailorsCanOnlyGoLeft.get(0);
            Oar nearestLeftOar = specialistSailor.getNearestOar(ship.getOarList(DirectionsManager.LEFT, associations), associations);
            Sailor normalSailor = sailorsCanGoBoth.get(0);
            Oar nearestRightOar = normalSailor.getNearestOar(ship.getOarList(DirectionsManager.RIGHT, associations), associations);

            associations.addAssociation(normalSailor, nearestRightOar);
            associations.addAssociation(specialistSailor, nearestLeftOar);
            currentSailorPlacement.incrementNbRightSailor(1);
            currentSailorPlacement.incrementNbLeftSailor(1);

            if (canContinueToOarEvenly()){
                associateSpecialistSailorAndSailorToOarEvenly();
            }
            return true;
        }

        if (!sailorsCanOnlyGoRight.isEmpty()){
            Sailor specialistSailor = sailorsCanOnlyGoRight.get(0);
            Oar nearestRightOar = specialistSailor.getNearestOar(ship.getOarList(DirectionsManager.RIGHT, associations), associations);
            Sailor normalSailor = sailorsCanGoBoth.get(0);
            Oar nearestLeftOar = normalSailor.getNearestOar(ship.getOarList(DirectionsManager.LEFT, associations), associations);

            associations.addAssociation(specialistSailor, nearestRightOar);
            associations.addAssociation(normalSailor, nearestLeftOar);
            currentSailorPlacement.incrementNbRightSailor(1);
            currentSailorPlacement.incrementNbLeftSailor(1);

            if (canContinueToOarEvenly()){
                associateSpecialistSailorAndSailorToOarEvenly();
            }
            return true;
        }
        return false;
    }

    /**
     * RECURSIVE
     * Will associate two sailors until we run out of sailors.
     */
    public boolean associateSailorsToOarEvenly(){
        Set<Sailor> sailorsCanGoLeft = getSailorNearToOar(DirectionsManager.LEFT);
        Set<Sailor> sailorsCanGoRight = getSailorNearToOar(DirectionsManager.RIGHT);

        List<Sailor> sailorsCanGoBoth = sailorsCanGoLeft.stream()
                .filter(sailorsCanGoRight::contains)
                .toList();

        if (sailorsCanGoBoth.isEmpty())
            return false;

        if (sailorsCanGoBoth.size() >= 2){
            Sailor rightSailor = sailorsCanGoBoth.get(0);
            Oar bestRightOar = rightSailor.getNearestOar(ship.getOarList(DirectionsManager.RIGHT,associations), associations);
            associations.addAssociation(rightSailor, bestRightOar);
            currentSailorPlacement.incrementNbRightSailor(1);

            Sailor leftSailor = sailorsCanGoBoth.get(1);
            Oar bestLeftOar = leftSailor.getNearestOar(ship.getOarList(DirectionsManager.LEFT,associations), associations);
            associations.addAssociation(leftSailor, bestLeftOar);
            currentSailorPlacement.incrementNbLeftSailor(1);

            if (canContinueToOarEvenly()){
                associateSailorsToOarEvenly();
            }
            return true;
        }
        return false;
    }

    /**
     * RECURSIVE
     * If two opposite sailors are specialist (meaning they can only go to left or right oars) they will both join their nearest
     * specific oar and call the associateSpecialistSailorToOar again.
     * If there is no two opposite sailors are specialist the recursive method stops.
     */
    public boolean associateSpecialistSailorToOarEvenly(){
        Set<Sailor> sailorsCanGoLeft = getSailorNearToOar(DirectionsManager.LEFT);
        Set<Sailor> sailorsCanGoRight = getSailorNearToOar(DirectionsManager.RIGHT);

        List<Sailor> sailorsCanOnlyGoLeft = new ArrayList<>(sailorsCanGoLeft);
        sailorsCanOnlyGoLeft.removeAll(sailorsCanGoRight);

        List<Sailor> sailorsCanOnlyGoRight = new ArrayList<>(sailorsCanGoRight);
        sailorsCanOnlyGoRight.removeAll(sailorsCanGoLeft);

        // If we have at least one sailor who can only go left and one sailor who can only go right.
        if(Math.min(sailorsCanOnlyGoLeft.size(), sailorsCanOnlyGoRight.size()) > 0) {
            Sailor rightSailor = sailorsCanOnlyGoRight.get(0);
            Sailor leftSailor = sailorsCanOnlyGoLeft.get(0);
            Oar bestLeftOar = leftSailor.getNearestOar(ship.getAllOar(), associations);
            Oar bestRightOar = rightSailor.getNearestOar(ship.getAllOar(), associations);

            associations.addAssociation(rightSailor, bestRightOar);
            currentSailorPlacement.incrementNbRightSailor(1);

            associations.addAssociation(leftSailor, bestLeftOar);
            currentSailorPlacement.incrementNbLeftSailor(1);

            // If the pre-calculator thinks adding two more sailor is not worth.
            if (canContinueToOarEvenly()){
                associateSpecialistSailorToOarEvenly();
            }
            return true;
        }
        return false;
    }

    /**
     * Get sailors near to aars
     * @param direction direction
     * @return a set of sailors
     */
    public Set<Sailor> getSailorNearToOar(int direction){
        Set<Sailor> nearbySailors = new HashSet<>();

        for(Oar unassignedOar : ship.getOarList(direction, associations)){
            nearbySailors.addAll(unassignedOar.getSailorsInRange(sailors, MAX_MOVING_RANGE, associations));
        }
        return nearbySailors;
    }

    /**
     * We are associating every starving entities.
     * until every entity has more than 1 sailor nearby.
     */
    public void continueAssociatingStarvingEntities(SailorPlacement requestedSailorPlacement){
        while(associateStarvingEntities(requestedSailorPlacement));
    }

    /**
     * For a given list of Oar, will sort the entities of sailor by the distance from their nearest sailor.
     * @param oar -
     * @return The list of sorted oar.
     */
    private List<Oar> sortEntitiesByDistanceToNearestSailor(List<Oar> oar){
        return oar.stream()
                .filter(entity -> entity.getNearestSailor(sailors, MAX_MOVING_RANGE , associations).isPresent())
                .sorted(Comparator.comparingInt(entity ->
                        entity.getNearestSailor(sailors, MAX_MOVING_RANGE , associations).get().getDistanceToEntity(entity)))
                .toList();
    }

    /**
     * Checks if sailors can continue to oar evenly
     * @return true if so
     */
    public boolean canContinueToOarEvenly(){
        return preCalculator.needSailorToOarToCheckpoint(Math.min(currentSailorPlacement.getNbLeftSailors(),
                currentSailorPlacement.getNbRightSailors()) * 2);
    }
}