package MODQlearning;

import pers.FileIO;

import java.util.HashMap;

/**
 * Created by Peter on 7. 1. 2017.
 */
public class QLearning {

    private final double alpha = 0.1; // learning rate  0 - no learning
    private final double gamma = 0.9; // discount factor (importance of future rewards) 0 - only-short sighted

    private State states[];
    private Action actions[];

    private double[][] qMatrix;

    private HashMap<State, Integer> stateIndices = new HashMap<>();
    private HashMap<Action, Integer> actionIndices = new HashMap<>();

    private FileIO qMatrixFile;

    public QLearning() {
        initializeStates();
        initializeActions();
        onStart();

        if (qMatrixFile.loadFromFile() != null && qMatrixFile.loadFromFile().length == states.length && qMatrixFile.loadFromFile()[0].length == actions.length) {
            this.qMatrix = qMatrixFile.loadFromFile();
        } else {
            buildMatrix();
        }

        buildIndices();
    }

    public QLearning(State[] states, Action[] actions, double[][] qMatrixFromFile)
    {
        this.states = states;
        this.actions = actions;

        if (qMatrixFromFile != null && qMatrixFromFile.length == states.length && qMatrixFromFile[0].length == actions.length) {
            this.qMatrix = qMatrixFromFile;
        } else {
            buildMatrix();
        }

        buildIndices();
    }

    public void initializeStates() {
        states = MatrixBuilder.build();
    }

    public void initializeActions() {
        actions = new Action[]
                {
                   /* new RunAction(),
                    new AttackNearestAction(),
                    new DoNothingAction(),
                    new FleeFromEnemyAction()*/
                };
    }

    public void onStart() {
        qMatrixFile = new FileIO("qMatrix.txt");
    }

    public void onEnd() {
        qMatrixFile.saveToFile(getQMatrix());
    }


    public void buildIndices() {

        for (int i = 0; i < states.length; i++) {
            stateIndices.put(states[i], i);
        }

        for (int i = 0; i < actions.length; i++) {
            actionIndices.put(actions[i], i);
        }
    }

    public void buildMatrix() {
        qMatrix = new double[states.length][actions.length];
    }


    public double maxQ(int stateIndex) {
        double maxValue = Double.MIN_VALUE;

        for (int actionIndex = 0; actionIndex < actions.length; actionIndex++) {
            maxValue = Math.max(qMatrix[stateIndex][actionIndex], maxValue);
        }
        return maxValue;
    }


    public void experience(State currentState, Action action, State nextState, double paReward) {

        int currentStateIndex = stateIndices.get(currentState);
        int nextStateIndex = stateIndices.get(nextState);
        int actionIndex = actionIndices.get(action);

        double q = qMatrix[currentStateIndex][actionIndex];
        double r = paReward;

        double maxQ = maxQ(nextStateIndex);

        double value = q + alpha * (r + gamma * maxQ - q);

        qMatrix[currentStateIndex][actionIndex] = value;
    }

       public double[][] getQMatrix() {
        return qMatrix;
    }
}
