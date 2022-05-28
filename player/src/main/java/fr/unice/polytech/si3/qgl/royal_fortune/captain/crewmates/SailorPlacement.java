package fr.unice.polytech.si3.qgl.royal_fortune.captain.crewmates;

/**
 * @author Bonnet Kilian Imami Ayoub Karrakchou Mourad Le Bihan Leo
 *
 */
public class SailorPlacement {
    private int oarWeight;
    private boolean rudder;
    private boolean sail;
    private boolean watch;
    private int nbLeftSailors;
    private int nbRightSailors;

    public SailorPlacement(){
        this.oarWeight = 0;
        this.rudder = false;
        this.sail = false;
        this.watch = false;
        this.nbLeftSailors = 0;
        this.nbRightSailors = 0;
    }

    public SailorPlacement(int oarWeight, boolean rudder, boolean sail) {
        this.oarWeight = oarWeight;
        this.rudder = rudder;
        this.sail = sail;
        this.watch = false;
    }

    public SailorPlacement(int oarWeight, boolean rudder, boolean sail, boolean watch) {
        this.oarWeight = oarWeight;
        this.rudder = rudder;
        this.sail = sail;
        this.watch = watch;
    }

    public boolean hasWatch() {
        return watch;
    }

    public boolean hasRudder() {
        return rudder;
    }

    public void setSail(boolean sail) {
        this.sail = sail;
    }

    public boolean hasSail() {
        return sail;
    }

    public int getNbLeftSailors() {
        return nbLeftSailors;
    }

    public int getNbRightSailors() {
        return nbRightSailors;
    }

    public int getOarWeight() {
        return oarWeight;
    }

    public void setRudder(boolean rudder) {
        this.rudder = rudder;
    }

    public void setWatch(boolean watch) {
        this.watch = watch;
    }

    public void incrementNbLeftSailor(int incrementation){
        this.nbLeftSailors += incrementation;
    }

    public void incrementNbRightSailor(int incrementation){
        this.nbRightSailors += incrementation;
    }

    public void incrementOarWeight(int incrementation){
        this.oarWeight += incrementation;
    }

    @Override
    public String toString() {
        return "SailorPlacement{" +
                "rudder=" + rudder +
                ", sail=" + sail +
                ", nbLeftSailors=" + nbLeftSailors +
                ", nbRightSailors=" + nbRightSailors +
                '}';
    }
}
