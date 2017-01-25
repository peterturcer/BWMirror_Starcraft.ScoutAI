package MODQlearning;

/**
 * Created by Peter on 17. 1. 2017.
 */
public class State {


    private String hashCode;
    private int life;
    private int distance;
    private int danger;


    public State(String paHashCode, int paLife, int paDistance, int paDanger)
    {
        this.hashCode = paHashCode;
        this.life = paLife;
        this.distance = paDistance;
        this.danger = paDanger;
    }

    public int getHashCode() {
        return hashCode.hashCode();
    }

}
