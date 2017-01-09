import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;

import java.util.Random;

/**
 * Created by Silent1 on 25.10.2016.
 */
public class Scout_module {

    public static final int SAFETY_LEVEL=2;

    //Test public
    public void helloWorld(Game pGame) {
        Random rnd=new Random();
        int x;
        int y;
        for(Unit u:pGame.getAllUnits()) {
            if(u.canMove()&&!u.isMoving()) {
                x=rnd.nextInt(5000);
                y=rnd.nextInt(5000);
                u.move(new Position(x,y),false);
            }
        }
    }
}
