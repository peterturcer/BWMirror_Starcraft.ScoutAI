import UnitManagement.ActionManager;

/**
 * Created by Silent1 on 09.01.2017.
 */
public class ConsoleHandler {

    private ActionManager actionManager;

    public ConsoleHandler(ActionManager pActionManager) {
        actionManager=pActionManager;
    }

    /**
     * In-game commands used for test and demonstration
     *
     * @param pMessage
     */
    public void messageHandler(String pMessage) {
        switch (pMessage) {
            case "scoutPos":
                break;
            case "returnHome":
                break;
        }
    }

}
