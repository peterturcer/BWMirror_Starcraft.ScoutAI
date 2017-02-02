package MODQlearning;

import UnitManagement.ScoutingUnit;
import bwapi.Game;


public class ScoutController {

	private ScoutingUnit scoutingUnit;
	private Game game;

	private QLearning qlearning;

	private State lastState = null;
	private Action executingAction = null;


	private int reward = 1000;
	private int rewardDiscount = 0;

	public ScoutController(QLearning pQLearning, Game pGame) {
		qlearning = pQLearning;
		game = pGame;
	}

	public void initializeQLearning() {
		qlearning.initializeStates();
		qlearning.initializeActions();
	}

	public void onStart() {
		qlearning.onStart();
	}

	public void onEnd() {
		qlearning.onEnd();
	}


	public void decrementRewardPerFrame()
	{
		rewardDiscount += 1;
	}

	public void update()
	{
		State currentState = detectState(getScoutingUnit());

		if (lastState != null) {

			if (scoutingUnit.)
			{
				reward += 100;
				reward -= rewardDiscount;

			} else if (scoutingUnit.)
			{
				reward -= 200;
				reward -= rewardDiscount;
			}

			qlearning.experience(lastState, executingAction, currentState, reward);
		}

		executingAction = qlearning.estimateBestActionIn(currentState);
		lastState = currentState;
	}

	/*public void updateOnEnd(ScoutingUnit pScoutingUnit) {

		State currentState = detectState(pScoutingUnit);

		double currentStateValue = currentState.getValue(game, unit);
		
		if (game.enemy().getUnits().isEmpty()) {
			currentStateValue = 0;
			for (Unit myUnit : game.self().getUnits()) {
				currentStateValue += myUnit.getType().maxHitPoints() + myUnit.getHitPoints() + myUnit.getShields();
			}
		}

		if (lastState != null) {
			double reward = (currentStateValue - lastStateValue) * 1000;
			qlearning.experience(lastState, executingAction, currentState, reward);
		}
	}*/


	private State detectState(ScoutingUnit pScoutingUnit) {

		double HP_bound1=0.4;
		double HP_bound2=0.7;

		double RATIO_bound1=0.4;
		double RATIO_bound2=0.7;

		double DANGER_bound1=0.4;
		double DANGER_bound2=0.7;



		int HP;

		int SAFEPATH; //Pomer najbezpecnejsej cesty k najdlhsej
		int NORMALPATH; //Pomer normalnej cesty k najdlhsej
		int RISKPATH; //Pomer najriskantnejsej cesty k najdlhsej

		int SAFEDANGER;
		int NORMALDANGER;
		int RISKDANGER;

		String code="";

		if(pScoutingUnit.getUnit().getHitPoints()<pScoutingUnit.getUnit().getHitPoints()*HP_bound1) {
			HP=1; //Malo HP
		} else if(pScoutingUnit.getUnit().getHitPoints()<pScoutingUnit.getUnit().getHitPoints()*HP_bound2) {
			HP=2; //Stredne vela HP
		} else {
			HP=3; //Skoro full HP
		}



		if(pScoutingUnit.getSafePathDistanceRatio()<RATIO_bound1) {
			SAFEPATH=1; //O vela kratsia cesta
		} else if(pScoutingUnit.getSafePathDistanceRatio()<RATIO_bound2) {
			SAFEPATH=2; //Trochu kratsia cesta
		} else {
			SAFEPATH=3; //Skoro rovnaka cesta
		}

		if(pScoutingUnit.getNormalPathDistanceRatio()<RATIO_bound1) {
			NORMALPATH=1; //O vela kratsia cesta
		} else if(pScoutingUnit.getNormalPathDistanceRatio()<RATIO_bound2) {
			NORMALPATH=2; //Trochu kratsia cesta
		} else {
			NORMALPATH=3; //Skoro rovnaka cesta
		}

		if(pScoutingUnit.getRiskPathDistanceRatio()<RATIO_bound1) {
			RISKPATH=1; //O vela kratsia cesta
		} else if(pScoutingUnit.getRiskPathDistanceRatio()<RATIO_bound2) {
			RISKPATH=2; //Trochu kratsia cesta
		} else {
			RISKPATH=3; //Skoro rovnaka cesta
		}




		if(pScoutingUnit.getSafePathDangerRatio()<DANGER_bound1) {
			SAFEDANGER=1;
		} else if(pScoutingUnit.getSafePathDangerRatio()<DANGER_bound2) {
			SAFEDANGER=2;
		} else {
			SAFEDANGER=3;
		}

		if(pScoutingUnit.getNormalPathDangerRatio()<DANGER_bound1) {
			NORMALDANGER=1;
		} else if(pScoutingUnit.getNormalPathDangerRatio()<DANGER_bound2) {
			NORMALDANGER=2;
		} else {
			NORMALDANGER=3;
		}

		if(pScoutingUnit.getRiskPathDangerRatio()<DANGER_bound1) {
			RISKDANGER=1;
		} else if(pScoutingUnit.getRiskPathDangerRatio()<DANGER_bound2) {
			RISKDANGER=2;
		} else {
			RISKDANGER=3;
		}

		code=""+HP+SAFEPATH+NORMALPATH+RISKPATH+SAFEDANGER+NORMALDANGER+RISKDANGER;

		State state=new State(code,HP,SAFEPATH,NORMALPATH,RISKPATH,SAFEDANGER,NORMALDANGER,RISKDANGER);

		return state;
	}

	public QLearning getQlearning() {
		return qlearning;
	}

	public void setQlearning(QLearning qlearning) {
		this.qlearning = qlearning;
	}

	public State getLastState() {
		return lastState;
	}

	public void setLastState(State lastState) {
		this.lastState = lastState;
	}

	public Action getExecutingAction() {
		return executingAction;
	}

	public void setExecutingAction(Action executingAction) {
		this.executingAction = executingAction;
	}

	public int getStartState() {
		return startState;
	}

	public void setStartState(int startState) {
		this.startState = startState;
	}

	public ScoutingUnit getScoutingUnit() {
		return scoutingUnit;
	}

	public void setScoutingUnit(ScoutingUnit scoutingUnit) {
		this.scoutingUnit = scoutingUnit;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
}
