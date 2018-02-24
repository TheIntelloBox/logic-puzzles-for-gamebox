package me.nikl.gamebox.games.threeinarow;

import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.games.GameManager;
import me.nikl.gamebox.games.GameRule;
import me.nikl.gamebox.games.exceptions.GameStartException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by nikl on 25.02.18.
 */
public class TiarManager implements GameManager {
    private ThreeInARow game;
    private Map<UUID, TiarGame> games = new HashMap<>();
    private TiarLanguage lang;
    private Map<String, TiarRules> gameTypes = new HashMap<>();
    private RandomAccessFile raf;
    private Random random;
    private boolean problemWhileLoading = false;

    TiarManager (ThreeInARow game) {
        this.game = game;
        loadPuzzles();
    }

    private void loadPuzzles() {
        File puzzle = new File(game.getDataFolder().toString() + File.separatorChar + "puzzles.yml");
        if(!puzzle.exists()){
            game.warn(" Puzzles file is missing!");
            problemWhileLoading = true;
            return;
        }
        try {
            this.raf  = new RandomAccessFile(puzzle, "r");
        } catch (FileNotFoundException e) {
            game.warn(" Puzzles file is missing!");
            problemWhileLoading = true;
            return;
        }
    }

    @Override
    public boolean onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        TiarGame game = games.get(inventoryClickEvent.getWhoClicked().getUniqueId());
        if(game == null) return false;
        return game.onClick(inventoryClickEvent);
    }

    @Override
    public boolean onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        removeFromGame(inventoryCloseEvent.getPlayer().getUniqueId());
        return true;
    }

    @Override
    public boolean isInGame(UUID uuid) {
        return false;
    }

    @Override
    public void startGame(Player[] players, boolean b, String... strings) throws GameStartException {

    }

    @Override
    public void removeFromGame(UUID uuid) {
        TiarGame game = games.get(uuid);
        if (game == null) return;
        game.onClose();
    }

    @Override
    public void loadGameRules(ConfigurationSection buttonSec, String buttonID) {
        double cost = buttonSec.getDouble("cost", 0.);
        boolean saveStats = buttonSec.getBoolean("saveStats", false);
        int token = buttonSec.getInt("token", 0);
        int money = buttonSec.getInt("money", 0);
        TiarRules rule = new TiarRules(buttonID, saveStats, SaveType.WINS, cost);
        rule.setMoneyToWin(money);
        rule.setTokenToWin(token);
        gameTypes.put(buttonID, rule);
    }

    @Override
    public Map<String, ? extends GameRule> getGameRules() {
        return null;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
