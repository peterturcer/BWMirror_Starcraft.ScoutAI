package MODQlearning;

import java.util.LinkedList;

/**
 * Created by Peter on 25. 1. 2017.
 */
public class MatrixBuilder {


    public static State[] build() {

        LinkedList<State> result = new LinkedList<>();
        String code = "";


        for (int life = 0; life < 3; life++) {
            for (int danger = 0; danger < 3; danger++) {
                for (int distance = 0; distance < 3; distance++){

                    code = life + danger + distance + "";
                    result.add(new State(code,life,danger,distance));
            }}}


        State[] ar = new State[result.size()];

        int i = 0;
        while(!result.isEmpty()) {
            ar[i++] = result.removeFirst();
        }

        return ar;

    }




}
