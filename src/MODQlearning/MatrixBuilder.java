package MODQlearning;

import java.util.LinkedList;

/**
 * Created by Peter on 25. 1. 2017.
 */
public class MatrixBuilder {


    public static State[] build() {

        LinkedList<State> result = new LinkedList<>();
        String code = "";


        for (int HP = 0; HP < 3; HP++) {
            for (int safePathDistance = 0; safePathDistance < 3; safePathDistance++) {
                for (int normalPathDistance = 0; normalPathDistance < 3; normalPathDistance++){
                    for (int riskPathDistance = 0; riskPathDistance < 3; riskPathDistance++) {
                        for (int safePathDanger = 0; safePathDanger < 3; safePathDanger++) {
                            for (int normalPathDanger = 0; normalPathDanger < 3; normalPathDanger++) {
                                for (int riskPathDanger = 0; riskPathDanger < 3; riskPathDanger++) {
                                    code =""+ HP + safePathDistance + normalPathDistance + riskPathDistance + safePathDanger + normalPathDanger + riskPathDanger;
                                    result.add(new State(code,HP,safePathDistance,normalPathDistance,riskPathDistance,safePathDanger,normalPathDanger,riskPathDanger));
                                }
                            }
                        }
                    }
                }
            }
        }

        State[] ar = new State[result.size()];

        int i = 0;
        while(!result.isEmpty()) {
            ar[i++] = result.removeFirst();
        }

        return ar;

    }




}
