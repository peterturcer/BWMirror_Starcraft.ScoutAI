package ScoutModule;

import UnitManagement.*;
import bwapi.*;
import MapManager.*;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by Silent1 on 25.10.2016.
 */
public class Scout_module {

    public static boolean DEBUG=true;

    public static final int SAFETY_LEVEL=1;

    public static final int MAP_REFRESH_FRAME_COUNT=30;

    public static final int UNIT_DANGERCHECK_FRAME_COUNT=35;

    private Game game;
    private UnitManager unitManager;
    private MapManager mapManager;
    private ActionManager actionManager;

    public Scout_module(Game pGame) {
        game=pGame;
        unitManager=new UnitManager(game);
        mapManager=new MapManager(game);
        actionManager=new ActionManager(mapManager,game);
    }

    public void TEST_initializeAll() {
        mapManager.initializeAll();
        unitManager.TEST_initScoutingUnits();
    }

    public void TEST_scoutBase() {
        actionManager.scoutBase(unitManager.getGroundScoutingUnits().get(0),SAFETY_LEVEL);
    }

    public void onFrame() {

        if(game.getFrameCount()==70) {
            System.out.println("Starting initializations...");
            TEST_initializeAll();
            System.out.println("Initializations done !");
        }
        if(game.getFrameCount()==90) {
            System.out.println("Scouting base...");
            TEST_scoutBase();
        }
        manageAll();
        drawAll();
    }

    public void drawAll() {
        mapManager.drawAll();
        unitManager.drawAll();
    }

    public void manageAll() {
        mapManager.manageAll(game);
        unitManager.manageAll(mapManager);
    }
}
