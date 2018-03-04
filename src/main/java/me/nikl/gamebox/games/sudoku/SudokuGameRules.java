package me.nikl.gamebox.games.sudoku;

import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.game.rules.GameRuleRewards;

/**
 * Created by Niklas
 *
 * Game rules container for Whac a mole
 */
public class SudokuGameRules extends GameRuleRewards {
    private boolean restartButton;

    public SudokuGameRules(String key, double cost, int reward, int tokens, boolean restartButton, boolean saveStats){
        super(key, saveStats, SaveType.WINS, cost, reward, tokens);
        this.restartButton = restartButton;
    }

    public boolean hasRestartButton() {
        return restartButton;
    }
}
