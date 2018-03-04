package me.nikl.gamebox.games.threeinarow;

import me.nikl.gamebox.game.Game;
import me.nikl.gamebox.game.GameLanguage;

/**
 * Created by nikl on 25.02.18.
 */
public class TiarLanguage extends GameLanguage {
    public String GAME_TITLE, GAME_TITLE_WON;
    public TiarLanguage(Game game) {
        super(game);
    }

    @Override
    protected void loadMessages() {
        GAME_TITLE = getString("game.inventoryTitles.gameTitle");
        GAME_TITLE_WON = getString("game.inventoryTitles.won");
    }
}
