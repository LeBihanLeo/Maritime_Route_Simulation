package fr.unice.polytech.si3.qgl.royal_fortune;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.unice.polytech.si3.qgl.regatta.cockpit.ICockpit;
import fr.unice.polytech.si3.qgl.royal_fortune.captain.crewmates.Sailor;
import fr.unice.polytech.si3.qgl.royal_fortune.environment.FictitiousCheckpoint;
import fr.unice.polytech.si3.qgl.royal_fortune.dao.InitGameDAO;
import fr.unice.polytech.si3.qgl.royal_fortune.dao.NextRoundDAO;
import fr.unice.polytech.si3.qgl.royal_fortune.captain.Captain;
import fr.unice.polytech.si3.qgl.royal_fortune.exception.EmptyDaoException;
import fr.unice.polytech.si3.qgl.royal_fortune.json_management.JsonManager;
import fr.unice.polytech.si3.qgl.royal_fortune.ship.Ship;
import fr.unice.polytech.si3.qgl.royal_fortune.target.Goal;

/**
 * @author Bonnet Kilian Imami Ayoub Karrakchou Mourad Le Bihan Leo
 *
 */
public class Cockpit implements ICockpit {
	private Ship ship;
	private List<Sailor> sailors;
	private Goal goal;
	private Captain captain;
	public static final int SECURITY_UPSCALE = 30;
	private static final Logger LOGGER = Logger.getLogger(Cockpit.class.getName());

	public Cockpit(){}
	public Cockpit(Ship ship, List<Sailor> sailors, Goal goal, Captain captain) {
		this.ship = ship;
		this.sailors = sailors;
		this.goal = goal;
		this.captain = captain;
	}

	/**
	 * Initialize the run
	 * @param game game data
	 */
	public void initGame(String game)  {
		String out = "Init game input: " + game;
		LOGGER.info(out);
		InitGameDAO initGameDAO = null;
		try {
			initGameDAO = createInitGameDAO(game);
		} catch (EmptyDaoException e) {
			LOGGER.info("Empty Dao");
		}
		assert initGameDAO != null;
		ship = initGameDAO.getShip();
		sailors = initGameDAO.getSailors();
		goal = initGameDAO.getGoal();
		captain = new Captain(ship, sailors, goal, new FictitiousCheckpoint(goal.getCheckPoints()), initGameDAO.getWind());
	}

	/**
	 * Build the next round
	 * @param round data of the round
	 * @return actions for the next round
	 */
	public String nextRound(String round){
		NextRoundDAO nextRoundDAO = null;
		try {
			nextRoundDAO = createNextRoundDAO(round);
		} catch (EmptyDaoException e) {
			LOGGER.info("Empty Dao");
		}
		LOGGER.log(Level.INFO, () -> "Next round input: " + round);
		assert nextRoundDAO != null;
		updateWithNextRound(nextRoundDAO);
		captain.setSeaEntities(nextRoundDAO.getVisibleEntities());
		String actions = captain.roundDecisions();
		LOGGER.log(Level.INFO, () -> "Actions = "+actions);

		return actions;
	}

	/**
	 * Update data for the next round
	 * @param nextRoundDAO dao to get the data
	 */
	public void updateWithNextRound(NextRoundDAO nextRoundDAO){
		Ship newShip = nextRoundDAO.getShip();
		ship.updatePos(newShip.getPosition());
		ship.setEntities(newShip.getEntities());
		captain.updateSeaEntities(nextRoundDAO.getVisibleEntities());
		captain.setWind(nextRoundDAO.getWind());
		captain.getPreCalculator().setWind(captain.getWind());
	}

	/**
	 * Get data from dao
	 * @param json json containing the data of the run
	 * @return data
	 * @throws EmptyDaoException if InitGameDAO is null
	 */
	public InitGameDAO createInitGameDAO(String json)throws EmptyDaoException{
		InitGameDAO initGameDAO = JsonManager.readInitGameDAOJson(json);
		if(initGameDAO == null) {
			throw new EmptyDaoException("InitGameDAO is null check the InitGame JSON");
		}
		return initGameDAO;
	}

	/**
	 * Create next round from dao data
	 * @param json json containing the data of the run
	 * @return data
	 * @throws EmptyDaoException if NextRoundDAO is null
	 */
	public NextRoundDAO createNextRoundDAO(String json)throws EmptyDaoException{
		NextRoundDAO nextRoundDAO = JsonManager.readNextRoundDAOJson(json);
		if(nextRoundDAO == null) {
			throw new EmptyDaoException("NextRoundDAO is null check the NextRound JSON");
		}
		nextRoundDAO.removeShipFromSeaEntities();
		return nextRoundDAO;
	}


	public Ship getShip() {
		return ship;
	}

	public List<Sailor> getSailors() {
		return sailors;
	}

	public Goal getGoal() {
		return goal;
	}

	public Captain getCaptain() {
		return captain;
	}

	public void setShip(Ship ship) {
		this.ship = ship;
	}


	@Override
	public ArrayList<String> getLogs() {
		return new ArrayList<>();
	}

}
