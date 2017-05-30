package MODQlearning;

import pers.FileIO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Peter on 7. 1. 2017.
 */
public class QLearning {

    private final double alpha = 0.4;  // learning rate  0 - no learning
    private final double gamma = 0.9; // discount factor (importance of future rewards) 0 - only-short sighted
    private double random = 0;

    private static int countRandomChooses = 0;
    private static int countAllChooses = 0;

    private State states[];
    private Action actions[];

    private double[][] qMatrix;
    private double[][] sMatrix;

    private HashMap<State, Integer> stateIndices = new HashMap<>();
    private HashMap<Action, Integer> actionIndices = new HashMap<>();

    private FileIO qMatrixFile;
    private FileIO sMatrixFile;

    private final Random probabilityRandom;
    private final Random actionIndexRandom;
    private final Random sameActionValueRandom;


    public QLearning() {
        initializeStates();
        initializeActions();
        loadMatrixIO();

        if (qMatrixFile.loadFromFile() != null) {
            if (qMatrixFile.loadFromFile().length == states.length && qMatrixFile.loadFromFile()[0].length == actions.length) {
                this.qMatrix = qMatrixFile.loadFromFile();
                this.sMatrix = sMatrixFile.loadFromFile();
            }
        } else {
            buildMatrix();
        }

        buildIndices();

        probabilityRandom = new Random();
        actionIndexRandom = new Random();
        sameActionValueRandom = new Random();
    }

    public void initializeStates() {
        System.out.println(":: Initializing states ::");
        states = MatrixBuilder.build();
    }

    public void initializeActions() {
        System.out.println(":: Initializing actions ::");
        actions = new Action[]
                {
                        new SafeAction(),
                        new NormalAction(),
                        new RiskAction()
                };
    }

    public void loadMatrixIO() {
        //  System.out.println(":: Loading matrix ::");
        qMatrixFile = new FileIO("qMatrix.txt");
        sMatrixFile = new FileIO("sMatrix.txt");

    }

    public void saveMatrixIO() {
        //  System.out.println(":: Saving matrix ::");
        qMatrixFile.saveToFile(getQMatrix());
        sMatrixFile.saveToFile(getSMatrix());
    }


    public void saveLearningForChart(LinkedList<Double> paLearning) {
        qMatrixFile.saveLearningChart(paLearning);
    }


    public void buildIndices() {

        System.out.println(":: Building indices ::");

        for (int i = 0; i < states.length; i++) {
            stateIndices.put(states[i], i);
        }

        for (int i = 0; i < actions.length; i++) {
            actionIndices.put(actions[i], i);
        }

        System.out.println(":: State indices size = " + stateIndices.size() + " ::");
        System.out.println(":: Action indices size = " + actionIndices.size() + " ::");

    }

    public void buildMatrix() {

        qMatrix = new double[states.length][actions.length];
        sMatrix = new double[states.length][actions.length];

        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < actions.length; j++) {
                qMatrix[i][j] = 0.0;
            }
        }

        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < actions.length; j++) {
                sMatrix[i][j] = 0.0;
            }
        }

        qMatrixFile.createMatrixFile();
        sMatrixFile.createStateMatrixFile();
        System.out.println(":: Matrix = [" + states.length + "]x[" + actions.length + "]  ::");
    }


    public double maxQ(int stateIndex) {
        double maxValue = Double.MIN_VALUE;

        for (int actionIndex = 0; actionIndex < actions.length; actionIndex++) {

            double value = qMatrix[stateIndex][actionIndex];
            if (value > maxValue) {
                maxValue = value;
            }

        }
        return maxValue;
    }

    public Action estimateBestActionIn(State state) {

        int stateIndex = stateIndices.get(state);
        int bestActionIndex = -1;
        double maxValue = Double.MIN_VALUE;

        double resault_action0 = 0.0;
        double resault_action1 = 0.0;
        double resault_action2 = 0.0;

        boolean firstMatch = false;
        boolean secondMatch = false;


        for (int actionIndex = 0; actionIndex < actions.length; actionIndex++) {
            double value = qMatrix[stateIndex][actionIndex];

            switch (actionIndex) {
                case 0:
                    resault_action0 = value;
                    break;
                case 1:
                    resault_action1 = value;
                    break;
                case 2:
                    resault_action2 = value;
                    break;
            }

            if (bestActionIndex == -1 || value > maxValue) {
                maxValue = value;
                bestActionIndex = actionIndex;
            }
        }

        switch (bestActionIndex) {
            case 0:

                if ((new Double(resault_action0).compareTo(new Double(resault_action1))) == 0) {
                    bestActionIndex = sameActionValueRandom.nextInt(2);
                    firstMatch = true;
                }

                if ((new Double(resault_action0).compareTo(new Double(resault_action2))) == 0) {
                    bestActionIndex = sameActionValueRandom.nextInt(2);
                    secondMatch = true;
                    if (bestActionIndex == 1) {
                        bestActionIndex = 2;
                    }
                }

                if (firstMatch == true && secondMatch == true) {
                    bestActionIndex = sameActionValueRandom.nextInt(3);
                }
                break;

            case 1:

                if ((new Double(resault_action1).compareTo(new Double(resault_action0))) == 0) {
                    bestActionIndex = sameActionValueRandom.nextInt(2);
                    firstMatch = true;
                }

                if ((new Double(resault_action1).compareTo(new Double(resault_action2))) == 0) {
                    bestActionIndex = sameActionValueRandom.nextInt(2) + 1;
                    secondMatch = true;
                }

                if (firstMatch == true && secondMatch == true) {
                    bestActionIndex = sameActionValueRandom.nextInt(3);
                }
                break;

            case 2:

                if ((new Double(resault_action2).compareTo(new Double(resault_action0))) == 0) {
                    bestActionIndex = sameActionValueRandom.nextInt(2);
                    firstMatch = true;
                    if (bestActionIndex == 1) {
                        bestActionIndex = 2;
                    }
                }

                if ((new Double(resault_action2).compareTo(new Double(resault_action1))) == 0) {
                    bestActionIndex = sameActionValueRandom.nextInt(2) + 1;
                    secondMatch = true;
                }

                if (firstMatch == true && secondMatch == true) {
                    bestActionIndex = sameActionValueRandom.nextInt(3);
                }
        }

        countAllChooses++;

        if (probabilityRandom.nextDouble() < random) {
            bestActionIndex = actionIndexRandom.nextInt(actions.length);
            countRandomChooses++;
        }

        return actions[bestActionIndex];

         // return actions[0];  //safe
         // return actions[1];  //normal
         // return actions[2];  //risk
    }


    public void experience(State currentState, Action action, State nextState, double paReward) {

        // System.out.println(":: Calculating experience ::");

        int currentStateIndex = stateIndices.get(currentState);
        int nextStateIndex = stateIndices.get(nextState);
        int actionIndex = actionIndices.get(action);

        double q = qMatrix[currentStateIndex][actionIndex];
        double r = paReward;

        double maxQ = maxQ(nextStateIndex);

        double value = q + alpha * (r + gamma * maxQ - q);

        qMatrix[currentStateIndex][actionIndex] = value;
        sMatrix[currentStateIndex][actionIndex] += 1;

      /*System.out.println("Input reward: " + r);
        System.out.println("Learned Q value: " + value);
        System.out.println("");*/
    }

    public void setRandom(double random) {
        this.random = random;
    }

    public double getRandom() {
        return random;
    }

    public double[][] getSMatrix() {
        return sMatrix;
    }

    public static int getCountRandomChooses() {
        return countRandomChooses;
    }

    public static int getCountAllChooses() {
        return countAllChooses;
    }

    public double[][] getQMatrix() {
        return qMatrix;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getGamma() {
        return gamma;
    }

    public State[] getStates() {
        return states;
    }

    public void setStates(State[] states) {
        this.states = states;
    }

    public Action[] getActions() {
        return actions;
    }

    public void setActions(Action[] actions) {
        this.actions = actions;
    }

    public double[][] getqMatrix() {
        return qMatrix;
    }

    public void setqMatrix(double[][] qMatrix) {
        this.qMatrix = qMatrix;
    }

    public HashMap<State, Integer> getStateIndices() {
        return stateIndices;
    }

    public void setStateIndices(HashMap<State, Integer> stateIndices) {
        this.stateIndices = stateIndices;
    }

    public HashMap<Action, Integer> getActionIndices() {
        return actionIndices;
    }

    public void setActionIndices(HashMap<Action, Integer> actionIndices) {
        this.actionIndices = actionIndices;
    }

    public FileIO getqMatrixFile() {
        return qMatrixFile;
    }

    public void setqMatrixFile(FileIO qMatrixFile) {
        this.qMatrixFile = qMatrixFile;
    }
}
