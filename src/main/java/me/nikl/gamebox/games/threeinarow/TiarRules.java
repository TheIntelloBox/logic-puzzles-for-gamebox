package me.nikl.gamebox.games.threeinarow;

import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.games.GameRuleRewards;

/**
 * Created by nikl on 25.02.18.
 */
public class TiarRules extends GameRuleRewards {
    public TiarRules(String key, boolean saveStats, double moneyToPay, double reward, int token) {
        super(key, saveStats, SaveType.WINS, moneyToPay, reward, token);
    }
}
