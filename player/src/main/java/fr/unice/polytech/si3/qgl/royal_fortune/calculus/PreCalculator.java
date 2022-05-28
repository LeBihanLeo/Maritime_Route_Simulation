package fr.unice.polytech.si3.qgl.royal_fortune.calculus;

import fr.unice.polytech.si3.qgl.royal_fortune.captain.crewmates.Sailor;
import fr.unice.polytech.si3.qgl.royal_fortune.environment.SeaMap;
import fr.unice.polytech.si3.qgl.royal_fortune.environment.Wind;
import fr.unice.polytech.si3.qgl.royal_fortune.ship.Ship;
import fr.unice.polytech.si3.qgl.royal_fortune.ship.entities.Sail;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bonnet Kilian Imami Ayoub Karrakchou Mourad Le Bihan Leo
 *
 */
public class PreCalculator {
    private final Ship ship;
    private final List<Sailor> sailors;
    private final SeaMap seaMap;
    private Wind wind;

    public PreCalculator(Ship ship, List<Sailor> sailors, SeaMap seaMap, Wind wind) {
        this.ship = ship;
        this.sailors = sailors;
        this.seaMap = seaMap;
        this.wind=wind;
    }

    /**
     * give the number of sailor needed to go to the next checkpoint(with max at sailors.size())
     *
     * @return number of sailors needed
     */
    public int numberOfSailorToOarToCheckPoint() {
        int numberOfSailor = 2;
        while (needSailorToOarToCheckpoint(numberOfSailor) && numberOfSailor <= sailors.size()) {
            numberOfSailor += 2;
        }
        return numberOfSailor;
    }

    /**
     * Predicts if we are going to be in the fictitious checkpoint.
     *
     * @param numberOfSailors The number of sailor to oar.
     * @return if with this numberOfSailors we are in the checkpoint or not
     */
    public boolean needSailorToOarToCheckpoint(int numberOfSailors) {
        return numberOfSailors < howManySailorsNeeded();
    }
    public int howManySailorsNeeded(){
        int numberOfSailors=0;
        double norm;
        double newX = ship.getPosition().getX();
        double newY = ship.getPosition().getY();
        double angle = ship.getPosition().getOrientation();

        double numberOfOpenedSail = 0;
        List<Boolean> sailsOpenedList = new ArrayList<>();
        List<Sail> sailList = ship.getSail();
        for(Sail sail : sailList) {
            boolean isOpened = sail.isOpenned();
            sailsOpenedList.add(isOpened);
            if(isOpened) numberOfOpenedSail++;
        }

        if(ship.getSail() != null && sailsOpenedList.contains(true)) {
            double windNorm = ( numberOfOpenedSail/ ship.getSail().size()) * wind.getStrength() * Math.cos(wind.getOrientation() - angle);

            newX += windNorm * Math.cos(angle);
            newY += windNorm * Math.sin(angle);
        }

        while(!seaMap.isInCheckpointShipPos(seaMap.getCurrentFictitiousToSlowCheckPoint(), newX, newY) && (numberOfSailors+2<=sailors.size())){
            norm = 165 * 2 / (double) ship.getNbrOar();
            newX += norm * Math.cos(angle);
            newY += norm * Math.sin(angle);
            numberOfSailors+=2;
        }

        return numberOfSailors;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Wind getWind() {
        return wind;
    }
}
