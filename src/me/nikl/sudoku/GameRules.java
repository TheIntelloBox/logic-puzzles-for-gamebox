package me.nikl.sudoku;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niklas
 *
 * Game rules container for Whac a mole
 */
public class GameRules {

    private double cost;
    private boolean saveStats;
    private String key;
    private int moneyReward, tokenReward;

    public GameRules(String key, double cost, int moneyReward, int tokenReward, boolean saveStats){
        this.cost = cost;
        this.saveStats = saveStats;
        this.key = key;
        this.moneyReward = moneyReward;
        this.tokenReward = tokenReward;
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

    public int getMoneyReward() {
        return moneyReward;
    }

    public int getTokenReward() {
        return tokenReward;
    }
}
