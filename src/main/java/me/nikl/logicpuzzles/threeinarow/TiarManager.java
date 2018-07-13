package me.nikl.logicpuzzles.threeinarow;

import me.nikl.gamebox.game.exceptions.GameStartException;
import me.nikl.gamebox.game.manager.GameManager;
import me.nikl.gamebox.game.rules.GameRule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by nikl on 25.02.18.
 */
public class TiarManager implements GameManager {
    private static final int NEW_LINE_CHAR_LENGTH = System.lineSeparator().getBytes().length;
    private static int LINE_LENGTH;
    private ThreeInARow game;
    private Map<UUID, TiarGame> games = new HashMap<>();
    private TiarLanguage lang;
    private Map<String, TiarRules> gameTypes = new HashMap<>();
    private RandomAccessFile raf;
    private Random random;
    private boolean problemWhileLoading = false;

    TiarManager (ThreeInARow game) {
        this.game = game;
        this.random = new Random(System.currentTimeMillis());
        this.lang = (TiarLanguage) game.getGameLang();
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
            LINE_LENGTH = raf.readLine().getBytes().length + NEW_LINE_CHAR_LENGTH;
        } catch (FileNotFoundException e) {
            game.warn(" Puzzles file is missing!");
            problemWhileLoading = true;
            return;
        } catch (IOException e) {
            e.printStackTrace();
            problemWhileLoading = true;
            return;
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        TiarGame game = games.get(inventoryClickEvent.getWhoClicked().getUniqueId());
        if(game == null) return;
        game.onClick(inventoryClickEvent);
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        removeFromGame(inventoryCloseEvent.getPlayer().getUniqueId());
    }

    @Override
    public boolean isInGame(UUID uuid) {
        return games.keySet().contains(uuid);
    }

    @Override
    public void startGame(Player[] players, boolean playSounds, String... args) throws GameStartException {
        if(raf == null || problemWhileLoading) throw new GameStartException(GameStartException.Reason.ERROR);
        if (args.length != 1) {
            Bukkit.getLogger().log(Level.WARNING, " unknown number of arguments to start a game: " + Arrays.asList(args));
            throw new GameStartException(GameStartException.Reason.ERROR);
        }
        TiarRules rule = gameTypes.get(args[0]);
        if (rule == null) {
            Bukkit.getLogger().log(Level.WARNING, " unknown game mode: " + Arrays.asList(args));
            throw new GameStartException(GameStartException.Reason.ERROR);
        }
        String puzzle = null;
        // for time out reasons when someone messed with the puzzles file...
        int count = 0;
        try {
            while (puzzle == null || !(puzzle.toCharArray().length >= 73)) {
                if(count > 20){
                    game.warn(ChatColor.RED + " Unable to find a puzzle - Something is wrong with the puzzles file!");
                    game.warn(ChatColor.RED + " Delete the puzzles file and reload the plugin.");
                    throw new GameStartException(GameStartException.Reason.ERROR);
                }
                count ++;
                raf.seek(random.nextInt(378) * LINE_LENGTH);
                puzzle = raf.readLine();
            }
        } catch (IOException e) {
            game.warn(ChatColor.RED + " I/O Exception while looking for a puzzle!");
            throw new GameStartException(GameStartException.Reason.ERROR);
        }
        if (!game.payIfNecessary(players[0], rule.getCost())) {
            throw new GameStartException(GameStartException.Reason.NOT_ENOUGH_MONEY);
        }
        games.put(players[0].getUniqueId(), new TiarGame(game, rule, players[0], puzzle));
    }

    @Override
    public void removeFromGame(UUID uuid) {
        TiarGame game = games.get(uuid);
        if (game == null) return;
        game.onClose();
        games.remove(uuid);
    }

    @Override
    public void loadGameRules(ConfigurationSection buttonSec, String buttonID) {
        double cost = buttonSec.getDouble("cost", 0.);
        boolean saveStats = buttonSec.getBoolean("saveStats", false);
        boolean helpItems = buttonSec.getBoolean("helpItems", true);
        int token = buttonSec.getInt("token", 0);
        int money = buttonSec.getInt("money", 0);
        TiarRules rule = new TiarRules(buttonID, saveStats, cost, money, token, helpItems);
        gameTypes.put(buttonID, rule);
    }

    @Override
    public Map<String, ? extends GameRule> getGameRules() {
        return gameTypes;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
