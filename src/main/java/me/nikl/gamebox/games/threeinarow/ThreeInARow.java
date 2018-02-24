package me.nikl.gamebox.games.threeinarow;

import me.nikl.gamebox.GameBox;
import me.nikl.gamebox.games.Game;
import me.nikl.gamebox.games.GameSettings;
import me.nikl.gamebox.games.LogicPuzzles;

/**
 * Created by nikl on 25.02.18.
 */
public class ThreeInARow extends Game {
    protected ThreeInARow(GameBox gameBox) {
        super(gameBox, LogicPuzzles.THREE_IN_A_ROW);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void init() {

    }

    @Override
    public void loadSettings() {
        gameSettings.setGameType(GameSettings.GameType.SINGLE_PLAYER);
        gameSettings.setHandleClicksOnHotbar(false);
        gameSettings.setGameGuiSize(54);
    }

    @Override
    public void loadLanguage() {
        gameLang = new TiarLanguage(this);
    }

    @Override
    public void loadGameManager() {
        gameManager = new TiarManager(this);
    }
}
