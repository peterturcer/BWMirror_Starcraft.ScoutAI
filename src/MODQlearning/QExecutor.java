package MODQlearning;

import ScoutModule.Scout_module;
import UnitManagement.ScoutingUnit;
import bwapi.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Silent1 on 17.01.2017.
 */
public class QExecutor {

    public static boolean DEBUG = false;
    public static boolean EXECUTE = false;
    public static boolean TEST = true;

    /* Q-learning */
    private Scout_module scout_module;
    private Game game;
    private QLearning qlearning;
    private State lastState = null;
    private Action executingAction = null;
    private ScoutingUnit actualScoutingUnit;

    private boolean nextScenario = true;
    private boolean running = false;
    private boolean finished = false;

    //------------ STATES

    private int HP_state;
    private int distance_safe;
    private int distance_normal;
    private int distance_risky;
    private int danger_safe;
    private int danger_normal;
    private int danger_risky;

    //------------ STATES


    //------------ TESTING VALUES

    private static final double deathReward = 30;
    private static final double hpDiscount = 0.02;
    private static final int graphSampleValue = 50;

    //------------ TESTING VALUES

    private static int testCounter = 1;
    private static int numberOfTests = 10;
    private static int deathCounter = 0;
    private static int numberOfUpdates = 0;
    private static int actionChanged = 0;
    private static int zeroValueLearned = 0;
    private static double sumTime = 0;
    private static double sumHP = 0;
    private static double temporaryObjectiveFunctionSum = 0;

    private int nextUnit = 1;

    private long[] timeArray = new long[12];
    private double[] hpArray = new double[12];
    private static final LinkedList<Double> objectiveFunctionList = new LinkedList<>();

    private double reward;
    private static double experimentResault = 0;

    private static int safePathCount;
    private static int normalPathCount;
    private static int riskPathCount;

    /* Q-learning */

    private Position safePosition_1;
    private Position safePosition_2;
    private Position safePosition_3;
    private Position safePosition_4;
    private Position safePosition_5;
    private Position safePosition_6;
    private Position safePosition_7;
    private Position safePosition_8;
    private Position safePosition_9;
    private Position safePosition_10;
    private Position safePosition_11;
    private Position safePosition_12;

    private Position endPosition;

    private ScoutingUnit scUnit_1;
    private ScoutingUnit scUnit_2;
    private ScoutingUnit scUnit_3;
    private ScoutingUnit scUnit_4;
    private ScoutingUnit scUnit_5;
    private ScoutingUnit scUnit_6;
    private ScoutingUnit scUnit_7;
    private ScoutingUnit scUnit_8;
    private ScoutingUnit scUnit_9;
    private ScoutingUnit scUnit_10;
    private ScoutingUnit scUnit_11;
    private ScoutingUnit scUnit_12;

    private ScoutingUnit endUnit;

    private long startTimeCase1;
    private long startTimeCase2;
    private long startTimeCase3;
    private long startTimeCase4;
    private long startTimeCase5;
    private long startTimeCase6;
    private long startTimeCase7;
    private long startTimeCase8;
    private long startTimeCase9;
    private long startTimeCase10;
    private long startTimeCase11;
    private long startTimeCase12;

    private List<Position> safePositions;
    private List<ScoutingUnit> scoutingUnits;

    public QExecutor(Scout_module pScout_module) {

        reward = 0;

        scout_module = pScout_module;
        game = pScout_module.getGame();
        safePositions = new LinkedList<>();
        scoutingUnits = new LinkedList<>();
        qlearning = new QLearning();
        qlearning.loadMatrixIO();                                             // New initialization of QMatrix from file

        if (!TEST) {
            if (testCounter > (numberOfTests * 0.3)) {
                qlearning.setRandom(0.1);
            } else if (testCounter > (numberOfTests * 0.2)) {
                qlearning.setRandom(0.2);
            } else if (testCounter > (numberOfTests * 0.1)) {
                qlearning.setRandom(0.3);
            }
        }
    }

    public void resetQLearning(ScoutingUnit pScoutingUnit) {
        reward = 0;                                                           // Reset reward
        actualScoutingUnit = pScoutingUnit;
        running = false;
    }

    public void onFrame() {
        //cameraLockOnActualUnit();

        /* type `qrun` into chat */
        if (QExecutor.EXECUTE) {


            if (actualScoutingUnit != null) {
                if (isSuccesfull()) {

                    if (!actualScoutingUnit.equals(endUnit)) {
                        hpArray[nextUnit - 1] = actualScoutingUnit.getUnit().getHitPoints();
                    }

                    update();

                } else if (isDead()) {
                    hpArray[nextUnit - 1] = 0;
                    deathCounter++;
                    update();
                }
            }

            if (!finished) {
                executeAll();
            }

           /* if (running) {
                decrementReward();
            }*/

        /* update every XX frames, when scoutingUnit is ready */
            if (actualScoutingUnit.isReadyForQLearning()) {
                update();
            }
        }
    }

    public double objectiveFunction(double paHP, double paElapsedTime, double paOptimalRoadTime, double paHPdiscount) {

        return Math.max(((4 * paOptimalRoadTime - paElapsedTime) - ((UnitType.Terran_SCV.maxHitPoints() - paHP) * paHPdiscount)), 0);

    }

    public void saveDataForGraph(int paNumberOfValues, double paValue) {
        temporaryObjectiveFunctionSum += paValue;

        if ((numberOfUpdates % paNumberOfValues) == 0) {
            objectiveFunctionList.add(temporaryObjectiveFunctionSum / paNumberOfValues);
            temporaryObjectiveFunctionSum = 0;
        }
    }

    public boolean isDead() {
        if (!actualScoutingUnit.getUnit().exists()) {
            return true;
        }
        return false;
    }

    public boolean isSuccesfull() {
        if (actualScoutingUnit.getFinalDestination() != null) {
            if (actualScoutingUnit.getUnit().getDistance(actualScoutingUnit.getFinalDestination()) < 200) {
                return true;
            }
        }
        return false;
    }

    public void decrementReward() {
        reward--;
    }

    public void update() {

        if (!actualScoutingUnit.equals(endUnit)) {

            State currentState = detectState(actualScoutingUnit);

            if (lastState != null) {

                if (isSuccesfull()) {

                    if (nextUnit > 1) {
                        double elapsedTime = (double) timeArray[nextUnit - 2] / 1000000000.0;
                        sumTime += elapsedTime;
                        double unitHP = hpArray[nextUnit - 2];

                        if (TEST) {
                            sumHP += unitHP;
                        }

                        reward += objectiveFunction(unitHP, elapsedTime, 1.8, hpDiscount);
                        numberOfUpdates++;

                        if (!TEST) {
                            qlearning.experience(lastState, executingAction, currentState, reward);
                            saveDataForGraph(graphSampleValue, reward);
                        }

                        experimentResault += reward;
                    }

                    running = false;
                    nextUnit++;
                    nextScenario = true;


                } else if (isDead()) {

                    if (nextUnit > 1) {

                        reward -= deathReward;
                        numberOfUpdates++;

                        if (!TEST) {
                            qlearning.experience(lastState, executingAction, currentState, reward);
                            saveDataForGraph(graphSampleValue, reward);
                        }

                        experimentResault += reward;

                    }

                    running = false;
                    nextUnit++;
                    nextScenario = true;

                } else {
                    if (nextUnit > 1) {

                        if (!TEST) {
                            qlearning.experience(lastState, executingAction, currentState, 0);
                        }
                        zeroValueLearned++;
                    }
                }
            }

            executingAction = qlearning.estimateBestActionIn(currentState);

            if (nextUnit > 1) {

                actionChanged++;

                if (executingAction instanceof SafeAction) {
                    safePathCount++;
                }
                if (executingAction instanceof NormalAction) {
                    normalPathCount++;
                }
                if (executingAction instanceof RiskAction) {
                    riskPathCount++;
                }
            }

            lastState = currentState;
        }

        executingAction.executeAction(actualScoutingUnit);
    }

    private State detectState(ScoutingUnit pScoutingUnit) {

        if (DEBUG) {
            System.out.println(":: Detecting state ::");
        }

        double HP_bound1 = 0.4;
        double HP_bound2 = 0.7;

        double RATIO_bound1 = 0.4;
        double RATIO_bound2 = 0.7;

        double DANGER_bound1 = 0.4;
        double DANGER_bound2 = 0.7;


        int HP;

        int SAFEPATH; //Pomer najbezpecnejsej cesty k najdlhsej
        int NORMALPATH; //Pomer normalnej cesty k najdlhsej
        int RISKPATH; //Pomer najriskantnejsej cesty k najdlhsej

        double DANGER_SAFE;
        double DANGER_NORMAL;
        double DANGER_RISKY;

        int SAFEDANGER;
        int NORMALDANGER;
        int RISKDANGER;

        double MOST_DANGEROUS_PATH_VALUE;

        String code = "";


        int pom = pScoutingUnit.getUnit().getHitPoints();

        if (pScoutingUnit.getUnit().getHitPoints() < UnitType.Terran_SCV.maxHitPoints() * HP_bound1) {
            HP = 1; //Malo HP
        } else if (pScoutingUnit.getUnit().getHitPoints() < UnitType.Terran_SCV.maxHitPoints() * HP_bound2) {
            HP = 2; //Stredne vela HP
        } else {
            HP = 3; //Skoro full HP
        }


        if (pScoutingUnit.getSafePathDistanceRatio() < RATIO_bound1) {
            SAFEPATH = 1; //O vela kratsia cesta
        } else if (pScoutingUnit.getSafePathDistanceRatio() < RATIO_bound2) {
            SAFEPATH = 2; //Trochu kratsia cesta
        } else {
            SAFEPATH = 3; //Skoro rovnaka cesta
        }

        if (pScoutingUnit.getNormalPathDistanceRatio() < RATIO_bound1) {
            NORMALPATH = 1; //O vela kratsia cesta
        } else if (pScoutingUnit.getNormalPathDistanceRatio() < RATIO_bound2) {
            NORMALPATH = 2; //Trochu kratsia cesta
        } else {
            NORMALPATH = 3; //Skoro rovnaka cesta
        }

        if (pScoutingUnit.getRiskPathDistanceRatio() < RATIO_bound1) {
            RISKPATH = 1; //O vela kratsia cesta
        } else if (pScoutingUnit.getRiskPathDistanceRatio() < RATIO_bound2) {
            RISKPATH = 2; //Trochu kratsia cesta
        } else {
            RISKPATH = 3; //Skoro rovnaka cesta
        }


        MOST_DANGEROUS_PATH_VALUE = pScoutingUnit.getMostDangerousPath(scout_module.getMapManager());

        DANGER_SAFE = pScoutingUnit.getSafePathDangerRatio(scout_module.getMapManager());


        if (DANGER_SAFE < MOST_DANGEROUS_PATH_VALUE * DANGER_bound1) {
            SAFEDANGER = 1;
        } else if (DANGER_SAFE < MOST_DANGEROUS_PATH_VALUE * DANGER_bound2) {
            SAFEDANGER = 2;
        } else {
            SAFEDANGER = 3;
        }

        DANGER_NORMAL = pScoutingUnit.getNormalPathDangerRatio(scout_module.getMapManager());

        if (DANGER_NORMAL < MOST_DANGEROUS_PATH_VALUE * DANGER_bound1) {
            NORMALDANGER = 1;
        } else if (DANGER_NORMAL < MOST_DANGEROUS_PATH_VALUE * DANGER_bound2) {
            NORMALDANGER = 2;
        } else {
            NORMALDANGER = 3;
        }

        DANGER_RISKY = pScoutingUnit.getRiskPathDangerRatio(scout_module.getMapManager());

        if (DANGER_RISKY < MOST_DANGEROUS_PATH_VALUE * DANGER_bound1) {
            RISKDANGER = 1;
        } else if (DANGER_RISKY < MOST_DANGEROUS_PATH_VALUE * DANGER_bound2) {
            RISKDANGER = 2;
        } else {
            RISKDANGER = 3;
        }

        code = "" + HP + SAFEPATH + NORMALPATH + RISKPATH + SAFEDANGER + NORMALDANGER + RISKDANGER;

        State state = new State(code, HP, SAFEPATH, NORMALPATH, RISKPATH, SAFEDANGER, NORMALDANGER, RISKDANGER);


        if(TEST) {
            HP_state = HP;
            distance_safe = SAFEPATH;
            distance_normal = NORMALPATH;
            distance_risky = RISKPATH;
            danger_safe = SAFEDANGER;
            danger_normal = NORMALDANGER;
            danger_risky = RISKDANGER;
        }

        if (DEBUG) {
            System.out.println(":: Detected state = " + state + " ::");
        }

        return state;
    }

    public void executeAll() {
        if (!running) {
            if (nextScenario) {

                switch (nextUnit) {
                    case 1:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 1 ::");
                        }

                        startTimeCase1 = System.nanoTime();

                        resetQLearning(scUnit_1);
                        execute_1();
                        running = true;
                        nextScenario = false;

                        break;
                    case 2:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 2 ::");
                        }

                        timeArray[0] = System.nanoTime() - startTimeCase1;
                        startTimeCase2 = System.nanoTime();

                        resetQLearning(scUnit_2);
                        execute_2();
                        running = true;
                        nextScenario = false;

                        break;
                    case 3:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 3 ::");
                        }

                        timeArray[1] = System.nanoTime() - startTimeCase2;
                        startTimeCase3 = System.nanoTime();

                        resetQLearning(scUnit_3);
                        execute_3();
                        running = true;
                        nextScenario = false;

                        break;
                    case 4:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 4 ::");
                        }

                        timeArray[2] = System.nanoTime() - startTimeCase3;
                        startTimeCase4 = System.nanoTime();

                        resetQLearning(scUnit_4);
                        execute_4();
                        running = true;
                        nextScenario = false;

                        break;
                    case 5:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 5 ::");
                        }

                        timeArray[3] = System.nanoTime() - startTimeCase4;
                        startTimeCase5 = System.nanoTime();

                        resetQLearning(scUnit_5);
                        execute_5();
                        running = true;
                        nextScenario = false;

                        break;
                    case 6:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 6 ::");
                        }

                        timeArray[4] = System.nanoTime() - startTimeCase5;
                        startTimeCase6 = System.nanoTime();

                        resetQLearning(scUnit_6);
                        execute_6();
                        running = true;
                        nextScenario = false;

                        break;
                    case 7:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 7 ::");
                        }

                        timeArray[5] = System.nanoTime() - startTimeCase6;
                        startTimeCase7 = System.nanoTime();

                        resetQLearning(scUnit_7);
                        execute_7();
                        running = true;
                        nextScenario = false;

                        break;
                    case 8:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 8 ::");
                        }

                        timeArray[6] = System.nanoTime() - startTimeCase7;
                        startTimeCase8 = System.nanoTime();

                        resetQLearning(scUnit_8);
                        execute_8();
                        running = true;
                        nextScenario = false;

                        break;
                    case 9:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 9 ::");
                        }

                        timeArray[7] = System.nanoTime() - startTimeCase8;
                        startTimeCase9 = System.nanoTime();

                        resetQLearning(scUnit_9);
                        execute_9();
                        running = true;
                        nextScenario = false;

                        break;
                    case 10:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 10 ::");
                        }

                        timeArray[8] = System.nanoTime() - startTimeCase9;
                        startTimeCase10 = System.nanoTime();

                        resetQLearning(scUnit_10);
                        execute_10();
                        running = true;
                        nextScenario = false;

                        break;
                    case 11:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 11 ::");
                        }

                        timeArray[9] = System.nanoTime() - startTimeCase10;
                        startTimeCase11 = System.nanoTime();

                        resetQLearning(scUnit_11);
                        execute_11();
                        running = true;
                        nextScenario = false;

                        break;
                    case 12:
                        if (DEBUG) {
                            System.out.println(":: Executing scenario 12 ::");
                        }

                        timeArray[10] = System.nanoTime() - startTimeCase11;
                        startTimeCase12 = System.nanoTime();

                        resetQLearning(scUnit_12);
                        execute_12();
                        running = true;
                        nextScenario = false;

                        break;

                    case 13:
                        if (DEBUG) {
                            System.out.println(":: Executing ending scenario 13 ::");
                        }

                        if (!TEST) {
                            qlearning.saveMatrixIO();
                        }

                        timeArray[11] = System.nanoTime() - startTimeCase12;
                        resetQLearning(endUnit);
                        execute_END();

                        nextUnit = 1;

                        if (testCounter == numberOfTests) {

                            qlearning.saveLearningForChart(objectiveFunctionList);

                            int safePath = safePathCount;
                            int normalPath = normalPathCount;
                            int riskPath = riskPathCount;

                            int pathCount = safePath + normalPath + riskPath;

                            double safePathPercenatage = ((double) safePath / (double) pathCount) * 100;
                            double normalPathPercentage = ((double) normalPath / (double) pathCount) * 100;
                            double riskPathPercentage = ((double) riskPath / (double) pathCount) * 100;

                            double averagePathChanged = actionChanged / numberOfUpdates;
                            double averageZeroReward = zeroValueLearned / numberOfUpdates;

                            //  System.out.println("\n:: Q-LEARNING ::\n");
                            //  System.out.println("\n:: SAFE TEST ::\n");
                            //  System.out.println("\n:: NORMAL TEST ::\n");
                            //  System.out.println("\n:: RISKY TEST ::\n");
                            System.out.println("\n:: LEARNNG PROCESS ::\n");

                            System.out.println("");
                            System.out.format("Objective function value:  %.4f\n", (experimentResault / numberOfUpdates));
                            System.out.format("Elapsed time:  %.2f MINUTES\n", sumTime / 60);
                            System.out.format("\nSafe path choosed: %.2f percent\n" + "Normal path choosed: %.2f percent\nRisk path choosed: %.2f percent\n", safePathPercenatage, normalPathPercentage, riskPathPercentage);

                            if (TEST) {
                                System.out.format("\nAverage scenario time:  %.2f SECONDS", sumTime / numberOfUpdates);
                                System.out.format("\nAverage hitpoints:  %.2f HP", sumHP / numberOfUpdates);
                            }

                            System.out.println("\nScenarios finished: " + numberOfUpdates + "\nDeaths: " + deathCounter);
                            System.out.println("");

                            if (!TEST) {
                                System.out.println("Non-zero visited states: " + visitedStates(qlearning.getQMatrix()));
                                System.out.println("All visited states: " + visitedStates(qlearning.getSMatrix()));
                                System.out.format("\nAverage path changed per scenario: %.2f\n", averagePathChanged);
                                System.out.format("Average zero reward learned per scenario: %.2f\n", averageZeroReward);
                                System.out.println("All/Random action chooses:  " + qlearning.getCountAllChooses() + " / " + qlearning.getCountRandomChooses());
                                System.out.println(unzeroStateCombinations());
                            }

                            System.exit(0);
                        }

                        testCounter++;
                        finished = true;
                }
            }
        }
    }


    public int visitedStates(double[][] paMatrix) {

        int occupiedStates = 0;
        boolean pomOccupiedStates = false;

        for (int i = 0; i < paMatrix.length; i++) {
            for (int j = 0; j < paMatrix[i].length; j++) {

                if (paMatrix[i][j] != 0) {
                    pomOccupiedStates = true;
                }
            }

            if (pomOccupiedStates) {
                occupiedStates++;
            }

            pomOccupiedStates = false;
        }

        return occupiedStates;
    }

    public String unzeroStateCombinations() {

        int hp_interval1 = 0;

        int safePath_distance_ratio_interval1 = 0;
        int normalPath_distance_ratio_interval1 = 0;
        int riskPath_distance_ratio_interval1 = 0;

        int safePath_danger_ratio_interval1 = 0;
        int normalPath_danger_ratio_interval1 = 0;
        int riskPath_danger_ratio_interval1 = 0;

        int hp_interval2 = 0;

        int safePath_distance_ratio_interval2 = 0;
        int normalPath_distance_ratio_interval2 = 0;
        int riskPath_distance_ratio_interval2 = 0;

        int safePath_danger_ratio_interval2 = 0;
        int normalPath_danger_ratio_interval2 = 0;
        int riskPath_danger_ratio_interval2 = 0;

        int hp_interval3 = 0;

        int safePath_distance_ratio_interval3 = 0;
        int normalPath_distance_ratio_interval3 = 0;
        int riskPath_distance_ratio_interval3 = 0;

        int safePath_danger_ratio_interval3 = 0;
        int normalPath_danger_ratio_interval3 = 0;
        int riskPath_danger_ratio_interval3 = 0;


        for (int i = 0; i < qlearning.getSMatrix().length; i++) {
            for (int j = 0; j < qlearning.getSMatrix()[i].length; j++) {

                if (qlearning.getSMatrix()[i][j] != 0) {

                    if (qlearning.getStates()[i].getHp() == 1) {
                        hp_interval1++;
                    }
                    if (qlearning.getStates()[i].getHp() == 2) {
                        hp_interval2++;
                    }
                    if (qlearning.getStates()[i].getHp() == 3) {
                        hp_interval3++;
                    }

                    if (qlearning.getStates()[i].getSafePath_distance_ratio() == 1) {
                        safePath_distance_ratio_interval1++;
                    }
                    if (qlearning.getStates()[i].getSafePath_distance_ratio() == 2) {
                        safePath_distance_ratio_interval2++;
                    }
                    if (qlearning.getStates()[i].getSafePath_distance_ratio() == 3) {
                        safePath_distance_ratio_interval3++;
                    }

                    if (qlearning.getStates()[i].getNormalPath_distance_ratio() == 1) {
                        normalPath_distance_ratio_interval1++;
                    }
                    if (qlearning.getStates()[i].getNormalPath_distance_ratio() == 2) {
                        normalPath_distance_ratio_interval2++;
                    }
                    if (qlearning.getStates()[i].getNormalPath_distance_ratio() == 3) {
                        normalPath_distance_ratio_interval3++;
                    }

                    if (qlearning.getStates()[i].getRiskPath_distance_ratio() == 1) {
                        riskPath_distance_ratio_interval1++;
                    }
                    if (qlearning.getStates()[i].getRiskPath_distance_ratio() == 2) {
                        riskPath_distance_ratio_interval2++;
                    }
                    if (qlearning.getStates()[i].getRiskPath_distance_ratio() == 3) {
                        riskPath_distance_ratio_interval3++;
                    }


                    if (qlearning.getStates()[i].getSafePath_danger_ratio() == 1) {
                        safePath_danger_ratio_interval1++;
                    }
                    if (qlearning.getStates()[i].getSafePath_danger_ratio() == 2) {
                        safePath_danger_ratio_interval2++;
                    }
                    if (qlearning.getStates()[i].getSafePath_danger_ratio() == 3) {
                        safePath_danger_ratio_interval3++;
                    }

                    if (qlearning.getStates()[i].getNormalPath_danger_ratio() == 1) {
                        normalPath_danger_ratio_interval1++;
                    }
                    if (qlearning.getStates()[i].getNormalPath_danger_ratio() == 2) {
                        normalPath_danger_ratio_interval2++;
                    }
                    if (qlearning.getStates()[i].getNormalPath_danger_ratio() == 3) {
                        normalPath_danger_ratio_interval3++;
                    }

                    if (qlearning.getStates()[i].getRiskPath_danger_ratio() == 1) {
                        riskPath_danger_ratio_interval1++;
                    }
                    if (qlearning.getStates()[i].getRiskPath_danger_ratio() == 2) {
                        riskPath_danger_ratio_interval2++;
                    }
                    if (qlearning.getStates()[i].getRiskPath_danger_ratio() == 3) {
                        riskPath_danger_ratio_interval3++;
                    }
                }
            }
        }

        return  "\nhp_interval1:  " + hp_interval1 +
                "\nhp_interval2:  " + hp_interval2 +
                "\nhp_interval3:  " + hp_interval3 +
                "\n" +
                "\nsafePath_distance_ratio_interval1:  " + safePath_distance_ratio_interval1 +
                "\nnormalPath_distance_ratio_interval1:  " + normalPath_distance_ratio_interval1 +
                "\nriskPath_distance_ratio_interval1:  " + riskPath_distance_ratio_interval1 +
                "\n" +
                "\nsafePath_danger_ratio_interval1:  " + safePath_danger_ratio_interval1 +
                "\nnormalPath_danger_ratio_interval1:  " + normalPath_danger_ratio_interval1 +
                "\nriskPath_danger_ratio_interval1:  " + riskPath_danger_ratio_interval1 +
                "\n" +
                "\nsafePath_distance_ratio_interval2:  " + safePath_distance_ratio_interval2 +
                "\nnormalPath_distance_ratio_interval2:  " + normalPath_distance_ratio_interval2 +
                "\nriskPath_distance_ratio_interval2:  " + riskPath_distance_ratio_interval2 +
                "\n" +
                "\nsafePath_danger_ratio_interval2:  " + safePath_danger_ratio_interval2 +
                "\nnormalPath_danger_ratio_interval2:  " + normalPath_danger_ratio_interval2 +
                "\nriskPath_danger_ratio_interval2 :  " + riskPath_danger_ratio_interval2 +
                "\n" +
                "\nsafePath_distance_ratio_interval3:  " + safePath_distance_ratio_interval3 +
                "\nnormalPath_distance_ratio_interval3:  " + normalPath_distance_ratio_interval3 +
                "\nriskPath_distance_ratio_interval3:  " + riskPath_distance_ratio_interval3 +
                "\n" +
                "\nsafePath_danger_ratio_interval3:  " + safePath_danger_ratio_interval3 +
                "\nnormalPath_danger_ratio_interval3:  " + normalPath_danger_ratio_interval3 +
                "\nriskPath_danger_ratio_interval3:  " + riskPath_danger_ratio_interval3;
    }


    public void initializeAll() {
        initializeSafePositions();
        initializeScoutingUnits();
    }

    public void initializeScoutingUnits() {
        for (Unit u : game.getAllUnits()) {
            if (u.getPlayer() == game.self()) {
                if (QExecutor.DEBUG) {
                    System.out.println("ID = " + u.getID());
                }

                if (u.getPosition().getDistance(270, 258) < 200) {
                    if (u.canMove()) {
                        endUnit = new ScoutingUnit(u);
                        scoutingUnits.add(endUnit);
                        scout_module.getUnitManager().addScoutingUnit(endUnit);
                        if (QExecutor.DEBUG) {
                            System.out.println("EndUnit initialized.");
                        }
                    }
                } else if (u.getPosition().getDistance(224, 1056) < 100) {
                    scUnit_1 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_1);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_1);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 1 initialized.");
                    }
                } else if (u.getPosition().getDistance(3200, 1056) < 100) {
                    scUnit_2 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_2);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_2);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 2 initialized.");
                    }
                } else if (u.getPosition().getDistance(5952, 1056) < 100) {
                    scUnit_3 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_3);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_3);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 3 initialized.");
                    }
                } else if (u.getPosition().getDistance(224, 2272) < 100) {
                    scUnit_4 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_4);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_4);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 4 initialized.");
                    }
                } else if (u.getPosition().getDistance(3200, 2272) < 100) {
                    scUnit_5 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_5);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_5);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 5 initialized.");
                    }
                } else if (u.getPosition().getDistance(5952, 2272) < 100) {
                    scUnit_6 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_6);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_6);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 6 initialized.");
                    }
                } else if (u.getPosition().getDistance(224, 3584) < 100) {
                    scUnit_7 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_7);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_7);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 7 initialized.");
                    }
                } else if (u.getPosition().getDistance(3200, 3584) < 100) {
                    scUnit_8 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_8);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_8);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 8 initialized.");
                    }
                } else if (u.getPosition().getDistance(5952, 3584) < 100) {
                    scUnit_9 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_9);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_9);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 9 initialized.");
                    }
                } else if (u.getPosition().getDistance(224, 4768) < 100) {
                    scUnit_10 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_10);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_10);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 10 initialized.");
                    }
                } else if (u.getPosition().getDistance(3200, 4768) < 100) {
                    scUnit_11 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_11);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_11);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 11 initialized.");
                    }
                } else if (u.getPosition().getDistance(5952, 4768) < 100) {
                    scUnit_12 = new ScoutingUnit(u);
                    scoutingUnits.add(scUnit_12);
                    scout_module.getUnitManager().addScoutingUnit(scUnit_12);
                    if (QExecutor.DEBUG) {
                        System.out.println("ScoutingUnit 12 initialized.");
                    }
                }
            }
        }
        if (QExecutor.DEBUG) {
            System.out.println("Scouting units count = " + scoutingUnits.size());
        }
    }

    public void initializeSafePositions() {
        for (Unit u : game.getAllUnits()) {
            if (u.getPlayer() == game.self()) {
                if (u.getType() == UnitType.Terran_Bunker) {
                    if (u.getPosition().getDistance(1008, 258) < 200) {
                        endPosition = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("EndPosition initialized.");
                        }
                    } else if (u.getPosition().getDistance((1632), 1056) < 300) {
                        safePosition_1 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 1 initialized.");
                        }
                    } else if (u.getPosition().getDistance(4640, 1056) < 300) {
                        safePosition_2 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 2 initialized.");
                        }
                    } else if (u.getPosition().getDistance(7392, 1056) < 300) {
                        safePosition_3 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 3 initialized.");
                        }
                    } else if (u.getPosition().getDistance(1632, 2272) < 300) {
                        safePosition_4 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 4 initialized.");
                        }
                    } else if (u.getPosition().getDistance(4640, 2272) < 300) {
                        safePosition_5 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 5 initialized.");
                        }
                    } else if (u.getPosition().getDistance(7392, 2272) < 300) {
                        safePosition_6 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 6 initialized.");
                        }
                    } else if (u.getPosition().getDistance(1632, 3584) < 300) {
                        safePosition_7 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 7 initialized.");
                        }
                    } else if (u.getPosition().getDistance(4640, 3584) < 300) {
                        safePosition_8 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 8 initialized.");
                        }
                    } else if (u.getPosition().getDistance(7392, 3584) < 300) {
                        safePosition_9 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 9 initialized.");
                        }
                    } else if (u.getPosition().getDistance(1632, 4768) < 300) {
                        safePosition_10 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 10 initialized.");
                        }
                    } else if (u.getPosition().getDistance(4640, 4768) < 300) {
                        safePosition_11 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 11 initialized.");
                        }
                    } else if (u.getPosition().getDistance(7392, 4768) < 300) {
                        safePosition_12 = u.getPosition();
                        safePositions.add(u.getPosition());
                        if (QExecutor.DEBUG) {
                            System.out.println("SafePosition 12 initialized.");
                        }
                    }
                }
            }
        }
        if (QExecutor.DEBUG) {
            System.out.println("Safepositions = " + safePositions.size());
        }
    }

    public void execute_1() {
        scout_module.getActionManager().scoutPosition(safePosition_1, scUnit_1);
    }

    public void execute_2() {
        scout_module.getActionManager().scoutPosition(safePosition_2, scUnit_2);
    }

    public void execute_3() {
        scout_module.getActionManager().scoutPosition(safePosition_3, scUnit_3);
    }

    public void execute_4() {
        scout_module.getActionManager().scoutPosition(safePosition_4, scUnit_4);
    }

    public void execute_5() {
        scout_module.getActionManager().scoutPosition(safePosition_5, scUnit_5);
    }

    public void execute_6() {
        scout_module.getActionManager().scoutPosition(safePosition_6, scUnit_6);
    }

    public void execute_7() {
        scout_module.getActionManager().scoutPosition(safePosition_7, scUnit_7);
    }

    public void execute_8() {
        scout_module.getActionManager().scoutPosition(safePosition_8, scUnit_8);
    }

    public void execute_9() {
        scout_module.getActionManager().scoutPosition(safePosition_9, scUnit_9);
    }

    public void execute_10() {
        scout_module.getActionManager().scoutPosition(safePosition_10, scUnit_10);
    }

    public void execute_11() {
        scout_module.getActionManager().scoutPosition(safePosition_11, scUnit_11);
    }

    public void execute_12() {
        scout_module.getActionManager().scoutPosition(safePosition_12, scUnit_12);
    }

    public void execute_END() {
        scout_module.getActionManager().scoutPosition(endPosition, endUnit);
    }

    public void drawAll() {
//        drawSafePositions();
//        drawSelectedIDs();
//        drawActualScoutingUnit();
    }

    public void drawSelectedIDs() {
        for (Unit u : game.getAllUnits()) {
            if (u.isSelected()) {
                game.drawTextMap(u.getPosition(), "" + u.getID());
                game.drawTextMap(u.getX(), u.getY() + 50, "" + u.getPosition().toString());
                if (u.getID() == endUnit.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "endUnit");
                } else if (u.getID() == scUnit_1.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 1");
                } else if (u.getID() == scUnit_2.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 2");
                } else if (u.getID() == scUnit_3.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 3");
                } else if (u.getID() == scUnit_4.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 4");
                } else if (u.getID() == scUnit_5.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 5");
                } else if (u.getID() == scUnit_6.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 6");
                } else if (u.getID() == scUnit_7.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 7");
                } else if (u.getID() == scUnit_8.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 8");
                } else if (u.getID() == scUnit_9.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 9");
                } else if (u.getID() == scUnit_10.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 10");
                } else if (u.getID() == scUnit_11.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 11");
                } else if (u.getID() == scUnit_12.getUnit().getID()) {
                    game.drawTextMap(u.getX(), u.getY() + 70, "scUnit 12");
                }
            }
        }
    }

    public void drawSafePositions() {
        for (Position pos : safePositions) {
            game.drawCircleMap(pos, 130, Color.White);
            if (pos.getX() == safePosition_1.getX() && pos.getY() == safePosition_1.getY()) {
                game.drawTextMap(pos, "SafePosition 1");
            } else if (pos.getX() == safePosition_2.getX() && pos.getY() == safePosition_2.getY()) {
                game.drawTextMap(pos, "SafePosition 2");
            } else if (pos.getX() == safePosition_3.getX() && pos.getY() == safePosition_3.getY()) {
                game.drawTextMap(pos, "SafePosition 3");
            } else if (pos.getX() == safePosition_4.getX() && pos.getY() == safePosition_4.getY()) {
                game.drawTextMap(pos, "SafePosition 4");
            } else if (pos.getX() == safePosition_5.getX() && pos.getY() == safePosition_5.getY()) {
                game.drawTextMap(pos, "SafePosition 5");
            } else if (pos.getX() == safePosition_6.getX() && pos.getY() == safePosition_6.getY()) {
                game.drawTextMap(pos, "SafePosition 6");
            } else if (pos.getX() == safePosition_7.getX() && pos.getY() == safePosition_7.getY()) {
                game.drawTextMap(pos, "SafePosition 7");
            } else if (pos.getX() == safePosition_8.getX() && pos.getY() == safePosition_8.getY()) {
                game.drawTextMap(pos, "SafePosition 8");
            } else if (pos.getX() == safePosition_9.getX() && pos.getY() == safePosition_9.getY()) {
                game.drawTextMap(pos, "SafePosition 9");
            } else if (pos.getX() == safePosition_10.getX() && pos.getY() == safePosition_10.getY()) {
                game.drawTextMap(pos, "SafePosition 10");
            } else if (pos.getX() == safePosition_11.getX() && pos.getY() == safePosition_11.getY()) {
                game.drawTextMap(pos, "SafePosition 11");
            } else if (pos.getX() == safePosition_12.getX() && pos.getY() == safePosition_12.getY()) {
                game.drawTextMap(pos, "SafePosition 12");
            } else if (pos.getX() == endPosition.getX() && pos.getY() == endPosition.getY()) {
                game.drawTextMap(pos, "endPosition");
            }
        }
    }

    public void drawActualScoutingUnit() {
        game.drawCircleMap(actualScoutingUnit.getUnit().getPosition(), 80, Color.Orange);
    }

    public void drawScoutingUnits() {
        for (ScoutingUnit scu : scoutingUnits) {
            if (scu.getUnit().getID() == endUnit.getUnit().getID()) {
                System.out.println("true");
                game.drawTextMap(scu.getUnit().getPosition(), "EndUnit");
            }
        }
    }

    public void showAll() {
        showReward();
        showActualUnitStats();
    }

    public void showReward() {
        game.drawTextScreen(20, 20, "OFV  = " + String.format("%.3f", (experimentResault / numberOfUpdates)));
    }

    public void showActualUnitStats() {
        game.drawTextScreen(20, 40, "Random val   = " + qlearning.getRandom());
        game.drawTextScreen(20, 60, "Unit HP       = " + actualScoutingUnit.getUnit().getHitPoints());
        game.drawTextScreen(20, 80, "Unit path     = " + actualScoutingUnit.getMicroPathChooser());
        game.drawTextScreen(20, 100, "Tests        = " + testCounter + " / " + numberOfTests);
        game.drawTextScreen(20, 120, "Deaths       = " + deathCounter);

        if (TEST) {
            game.drawTextScreen(20, 160, "HP state   = " + HP_state);
            game.drawTextScreen(20, 180, "Distance SP    = " + distance_safe);
            game.drawTextScreen(20, 200, "Distance NP    = " + distance_normal);
            game.drawTextScreen(20, 220, "Distance RP    = " + distance_risky);
            game.drawTextScreen(20, 240, "Danger SP    = " + danger_safe);
            game.drawTextScreen(20, 260, "Danger NP    = " + danger_normal);
            game.drawTextScreen(20, 280, "Danger RP    = " + danger_risky);
        }
    }

    public void cameraLockOnActualUnit() {
        if (actualScoutingUnit != null) {
            game.setScreenPosition(actualScoutingUnit.getUnit().getX() - 200, actualScoutingUnit.getUnit().getY() - 200);
        }
    }
}