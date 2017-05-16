package me.nikl.sudoku;

/**
 * Created by Niklas
 *
 * Game rules container for Whac a mole
 */
public class GameRules {

    private double cost;
    private boolean saveStats;
    private String key;
    private int reward, tokens;

    public GameRules(String key, double cost, int reward, int tokens, boolean saveStats){
        this.cost = cost;
        this.saveStats = saveStats;
        this.key = key;
        this.reward = reward;
        this.tokens = tokens;
    }

    public double getCost() {
        return cost;
    }

    public boolean isSaveStats() {
        return saveStats;
    }

    public String getKey() {
        return key;
    }

    public int getReward() {
        return reward;
    }

    public int getTokens() {
        return tokens;
    }
}
