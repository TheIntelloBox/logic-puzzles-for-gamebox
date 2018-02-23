package me.nikl.gamebox.games.sudoku;

import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.games.GameRule;

/**
 * Created by Niklas
 *
 * Game rules container for Whac a mole
 */
public class SudokuGameRules extends GameRule{

    private double cost;
    private boolean restartButton;
    private int reward, tokens;

    public SudokuGameRules(String key, double cost, int reward, int tokens, boolean restartButton, boolean saveStats){
        super(key, saveStats, SaveType.WINS);
        this.cost = cost;
        this.reward = reward;
        this.tokens = tokens;
        this.restartButton = restartButton;
    }

    public double getCost() {
        return cost;
    }

    public int getReward() {
        return reward;
    }

    public int getTokens() {
        return tokens;
    }

    public boolean hasRestartButton() {
        return restartButton;
    }
}
