package MODQlearning;

/**
 * Created by Peter on 17. 1. 2017.
 */
public class State {


    private String hashCode;

    private int hp;

    private int safePath_distance_ratio;
    private int normalPath_distance_ratio;
    private int riskPath_distance_ratio;

    private int safePath_danger_ratio;
    private int normalPath_danger_ratio;
    private int riskPath_danger_ratio;


    public State(String paHashCode, int pHP, int pSAFEPATH_DISTANCE_RATIO, int pNORMALPATH_DISTANCE_RATIO, int pRISKPATH_DISTANCE_RATIO, int pSAFEPATH_DANGER_RATIO, int pNORMALPATH_DANGER_RATIO, int pRISKPATH_DANGER_RATIO)
    {
        this.hashCode = paHashCode;
        this.hp = pHP;
        this.safePath_distance_ratio = pSAFEPATH_DISTANCE_RATIO;
        this.normalPath_distance_ratio = pNORMALPATH_DISTANCE_RATIO;
        this.riskPath_distance_ratio = pRISKPATH_DISTANCE_RATIO;
        this.safePath_danger_ratio = pSAFEPATH_DANGER_RATIO;
        this.normalPath_danger_ratio = pNORMALPATH_DANGER_RATIO;
        this.riskPath_danger_ratio = pRISKPATH_DANGER_RATIO;
    }

    public int getHashCode() {
        return hashCode.hashCode();
    }

}
