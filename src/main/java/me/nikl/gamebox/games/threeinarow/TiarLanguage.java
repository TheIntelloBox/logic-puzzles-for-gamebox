package me.nikl.gamebox.games.threeinarow;

import me.nikl.gamebox.games.Game;
import me.nikl.gamebox.games.GameLanguage;

/**
 * Created by nikl on 25.02.18.
 */
public class TiarLanguage extends GameLanguage {
    public String GAME_TITLE;
    public TiarLanguage(Game game) {
        super(game);
    }

    @Override
    protected void loadMessages() {
        GAME_TITLE = getString("game.inventoryTitles.gameTitle");
    }
}
