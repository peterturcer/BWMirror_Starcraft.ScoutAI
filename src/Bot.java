import bwapi.*;
import bwta.BWTA;

/**
 * Created by Silent1 on 25.10.2016.
 */
public class Bot extends DefaultBWListener {

    Mirror mirror;
    Player player;
    Game game;
    Scout_module scout;

    public static void main(String[] args) {
        new Bot().run();
    }

    public Bot() {
        super();
        mirror=new Mirror();
    }

    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    @Override
    public void onStart() {
        super.onStart();
        game = mirror.getGame();
        player = game.self();
        scout=new Scout_module();

        game.setLocalSpeed(15);
        game.enableFlag(1);

        System.out.println("ScoutAI bot working.");
    }

    @Override
    public void onEnd(boolean b) {
        super.onEnd(b);
    }

    @Override
    public void onFrame() {
        super.onFrame();
        scout.helloWorld(game);
    }

    @Override
    public void onSendText(String s) {
        super.onSendText(s);
    }

    @Override
    public void onReceiveText(Player player, String s) {
        super.onReceiveText(player, s);
    }

    @Override
    public void onPlayerLeft(Player player) {
        super.onPlayerLeft(player);
    }

    @Override
    public void onNukeDetect(Position position) {
        super.onNukeDetect(position);
    }

    @Override
    public void onUnitDiscover(Unit unit) {
        super.onUnitDiscover(unit);
    }

    @Override
    public void onUnitEvade(Unit unit) {
        super.onUnitEvade(unit);
    }

    @Override
    public void onUnitShow(Unit unit) {
        super.onUnitShow(unit);
    }

    @Override
    public void onUnitHide(Unit unit) {
        super.onUnitHide(unit);
    }

    @Override
    public void onUnitCreate(Unit unit) {
        super.onUnitCreate(unit);
    }

    @Override
    public void onUnitDestroy(Unit unit) {
        super.onUnitDestroy(unit);
    }

    @Override
    public void onUnitMorph(Unit unit) {
        super.onUnitMorph(unit);
    }

    @Override
    public void onUnitRenegade(Unit unit) {
        super.onUnitRenegade(unit);
    }

    @Override
    public void onSaveGame(String s) {
        super.onSaveGame(s);
    }

    @Override
    public void onUnitComplete(Unit unit) {
        super.onUnitComplete(unit);
    }

    @Override
    public void onPlayerDropped(Player player) {
        super.onPlayerDropped(player);
    }
}