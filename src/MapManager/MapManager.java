package MapManager;

import MODaStar.AStarModule;
import MODaStar.AStarPathCalculator;
import bwapi.*;
import bwta.Chokepoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chudjak Kristián on 05.01.2017.
 */
public class MapManager {
    /**
     * Size of the edge of grid block
     */
    public static final int GRIDEDGESIZE=18; //18

    private AStarModule aStarModule;

    private HeatMap heatMap;

    /**
     * Postiton of my base
     */
    private Position myBasePosition;

    /**
     * List of enemy base positons
     */
    private ArrayList<PotentialField> enemyBasePositions;

    /**
     * List of expansion positons
     */
    private ArrayList<PotentialField> expansionPositions;

    /**
     * List of danger fields
     */
    private ArrayList<PotentialField> dangerFields;

    /**
     * List of retreat fields
     */
    private ArrayList<PotentialField> retreatFields;

    private List<Chokepoint> chokePoints;

    private ArrayList<ScoutingArea> scoutingAreas;

    private ArrayList<ScoutingArea> armyArea;

    private AStarPathCalculator staticPathCalculator;

    public void refreshMap(Game pGame) {
        Player player = pGame.getPlayer(0);


        for(Unit pUnit:pGame.enemy().getUnits()) {
            if (pUnit.getPlayer().isEnemy(player) && pUnit.getType().canAttack()&&!pUnit.getType().isWorker()) {
                PotentialField p = getDangerFieldByID(pUnit.getID());
                if (p != null) {
                    if(p.getX()!=pUnit.getPosition().getX()&&p.getY()!=pUnit.getPosition().getY()) {
                        aStarModule.getGridMap().refreshGridMap(p);
                        p.setX(pUnit.getPosition().getX());
                        p.setY(pUnit.getPosition().getY());
                        aStarModule.getGridMap().updateGridMap(p);
                    }
                } else {
                    PotentialField pf = new PotentialField(pGame, pUnit);
                    dangerFields.add(pf);
                    aStarModule.getGridMap().updateGridMap(pf);
                }
            }
        }




        for(PotentialField pf:dangerFields) {
            if(pf.isVisible(pGame.self().getUnits())) {
                if(pGame.enemy().getUnits().size()<1 || removeField(pGame,pf)) {
                    aStarModule.getGridMap().refreshGridMap(pf);
                    dangerFields.remove(pf);
                }
            }
        }
    }


    private boolean removeField(Game pGame, PotentialField pf){
        for (Unit unit :
                pGame.enemy().getUnits()) {
            if (unit.getID() == pf.getId()) {
                return false;
            }
        }
        return true;
    }


    public PotentialField getDangerFieldByID(int pID) {
        for(PotentialField pf:dangerFields) {
            if(pf.getId()==pID) {
                return pf;
            }
        }
        return null;
    }

    public int getExpanzionCount() {
        return expansionPositions.size();
    }

    public AStarModule getaStarModule() {
        return aStarModule;
    }


    public AStarPathCalculator buildPath(Unit pUnit, Position pDestination, int pLevelOfSafety, boolean pAirPath, Game game,Color color) {
        return aStarModule.buildPath(pUnit.getPosition(), pDestination, pUnit.getHitPoints(), pLevelOfSafety, pAirPath, game, color);
    }


    public AStarPathCalculator buildPath(Unit pUnit,Position pStart, Position pDestination, int pLevelOfSafety, boolean pAirPath, Game game, Color pColor) {
        return aStarModule.buildPath(pStart, pDestination, pUnit.getHitPoints(), pLevelOfSafety, pAirPath, game,pColor);
    }


    public ArrayList<PotentialField> getDangerFields() {
        return dangerFields;
    }

    public void setaStarModule(AStarModule aStarModule) {
        this.aStarModule = aStarModule;
    }

    public HeatMap getHeatMap() {
        return heatMap;
    }

    public void setHeatMap(HeatMap heatMap) {
        this.heatMap = heatMap;
    }

    public Position getMyBasePosition() {
        return myBasePosition;
    }

    public void setMyBasePosition(Position myBasePosition) {
        this.myBasePosition = myBasePosition;
    }

    public ArrayList<PotentialField> getEnemyBasePositions() {
        return enemyBasePositions;
    }

    public void setEnemyBasePositions(ArrayList<PotentialField> enemyBasePositions) {
        this.enemyBasePositions = enemyBasePositions;
    }

    public ArrayList<PotentialField> getExpansionPositions() {
        return expansionPositions;
    }

    public void setExpansionPositions(ArrayList<PotentialField> expansionPositions) {
        this.expansionPositions = expansionPositions;
    }

    public void setDangerFields(ArrayList<PotentialField> dangerFields) {
        this.dangerFields = dangerFields;
    }

    public ArrayList<PotentialField> getRetreatFields() {
        return retreatFields;
    }

    public void setRetreatFields(ArrayList<PotentialField> retreatFields) {
        this.retreatFields = retreatFields;
    }

    public List<Chokepoint> getChokePoints() {
        return chokePoints;
    }

    public void setChokePoints(List<Chokepoint> chokePoints) {
        this.chokePoints = chokePoints;
    }

    public ArrayList<ScoutingArea> getScoutingAreas() {
        return scoutingAreas;
    }

    public void setScoutingAreas(ArrayList<ScoutingArea> scoutingAreas) {
        this.scoutingAreas = scoutingAreas;
    }

    public ArrayList<ScoutingArea> getArmyArea() {
        return armyArea;
    }

    public void setArmyArea(ArrayList<ScoutingArea> armyArea) {
        this.armyArea = armyArea;
    }

    public AStarPathCalculator getStaticPathCalculator() {
        return staticPathCalculator;
    }

    public void setStaticPathCalculator(AStarPathCalculator staticPathCalculator) {
        this.staticPathCalculator = staticPathCalculator;
    }
}
