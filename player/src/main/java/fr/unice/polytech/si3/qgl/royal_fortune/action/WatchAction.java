package fr.unice.polytech.si3.qgl.royal_fortune.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bonnet Kilian Imami Ayoub Karrakchou Mourad Le Bihan Leo
 *
 */
public class WatchAction extends Action {
    final Logger logger = Logger.getLogger(WatchAction.class.getName());

    public WatchAction(int sailorId) {
        super(sailorId, "OAR");
        this.type="OAR";
    }

    public WatchAction(){}

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode watchActionJSON = mapper.createObjectNode();
        watchActionJSON.put("sailorId", sailorId);
        watchActionJSON.put("type", "USE_WATCH");

        try {
            return mapper.writeValueAsString(watchActionJSON);
        } catch (JsonProcessingException e) {
            logger.log(Level.INFO, "Exception");
        }
        return "";
    }

}
