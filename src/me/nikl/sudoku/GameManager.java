package me.nikl.sudoku;

import me.nikl.gamebox.GameBox;
import me.nikl.gamebox.Permissions;
import me.nikl.gamebox.data.SaveType;
import me.nikl.gamebox.game.IGameManager;
import me.nikl.gamebox.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Niklas.
 *
 * 2048s GameManager
 */

public class GameManager implements IGameManager {
    private Main plugin;

    private Map<UUID, Game> games = new HashMap<>();
    private Language lang;

    private Map<String,GameRules> gameTypes;


    private RandomAccessFile raf;

    private Random random;

    private Map<Integer, ItemStack> cover = new HashMap<>();
    private Map<Integer, ItemStack> tip = new HashMap<>();
    private Map<Integer, ItemStack> number = new HashMap<>();

    private boolean problemWhileLoading = false;


    public GameManager(Main plugin){
        this.plugin = plugin;
        this.lang = plugin.lang;

        this.random = new Random(System.currentTimeMillis());


        File puzzle = new File(plugin.getDataFolder().toString() + File.separatorChar + "puzzles.yml");
        if(!puzzle.exists()){
            plugin.saveResource("puzzles.yml", false);
        }

        try {
            this.raf  = new RandomAccessFile(puzzle, "r");
        } catch (FileNotFoundException e) {
            problemWhileLoading = true;
            Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " Puzzles can't be found! No games possible");
            e.printStackTrace();
        }

        loadItems();
    }

    private void loadItems() {
        if(!plugin.getConfig().isConfigurationSection("items.grid")){
            Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " items.grid section missing in config");
            problemWhileLoading = true;
            return;
        }
        ConfigurationSection items = plugin.getConfig().getConfigurationSection("items.grid");
        ItemStack tipItem, coverItem, numberItem;
        for (int i = 1; i < 10; i++){
            if(!plugin.getConfig().isConfigurationSection("items.grid." + i)){
                Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " items.grid." + i + " section missing in config");
                cover.put(i, new ItemStack(Material.STAINED_GLASS_PANE, 1));
                tip.put(i, new ItemStack(Material.HARD_CLAY, i));
                number.put(i, new ItemStack(Material.WOOL, i));
                continue;
            }

            tipItem = ItemStackUtil.getItemStack(items.getString(i + "." + "tip" + ".materialData", "HARD_CLAY"));
            coverItem = ItemStackUtil.getItemStack(items.getString(i + "." + "cover" + ".materialData", "STAINED_GLASS_PANE"));
            numberItem = ItemStackUtil.getItemStack(items.getString(i + "." + "number" + ".materialData", "WOOL"));


            if(tipItem == null){
                tipItem = new ItemStack(Material.HARD_CLAY, 1);
                Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + i + "." + "tip" + ".materialData" + " not valid material");
            }


            if(coverItem == null){
                coverItem = new ItemStack(Material.STAINED_GLASS_PANE, 1);
                Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + i + "." + "cover" + ".materialData" + " not valid material");
            }

            if(numberItem == null){
                numberItem = new ItemStack(Material.WOOL, 1);
                Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + i + "." + "number" + ".materialData" + " not valid material");
            }

            ItemMeta meta = tipItem.getItemMeta();
            meta.setDisplayName(GameBox.chatColor(items.getString(i + "." + "tip" + ".displayName", "&l&n" + i)));
            tipItem.setItemMeta(meta);

            meta = coverItem.getItemMeta();
            meta.setDisplayName(GameBox.chatColor(items.getString(i + "." + "cover" + ".displayName", "%value%")));
            coverItem.setItemMeta(meta);

            meta = numberItem.getItemMeta();
            meta.setDisplayName(GameBox.chatColor(items.getString(i + "." + "number" + ".displayName", "%value%")));
            numberItem.setItemMeta(meta);

            tip.put(i, tipItem.clone());
            cover.put(i, coverItem.clone());
            number.put(i, numberItem.clone());
        }
    }


    @Override
    public boolean onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        Game game = games.get(inventoryClickEvent.getWhoClicked().getUniqueId());
        if(game == null) return false;

        game.onClick(inventoryClickEvent);

        return true;
    }


    @Override
    public boolean onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        // ToDo:
        removeFromGame(inventoryCloseEvent.getPlayer().getUniqueId());

        return true;
    }


    @Override
    public boolean isInGame(UUID uuid) {
        return games.containsKey(uuid);
    }


    @Override
    public int startGame(Player[] players, boolean playSounds, String... strings) {
        if(raf == null) return GameBox.GAME_NOT_STARTED_ERROR;

        if (strings.length != 1) {
            Bukkit.getLogger().log(Level.WARNING, " unknown number of arguments to start a game: " + Arrays.asList(strings));
            return GameBox.GAME_NOT_STARTED_ERROR;
        }

        GameRules rule = gameTypes.get(strings[0]);

        if (rule == null) {
            Bukkit.getLogger().log(Level.WARNING, " unknown gametype: " + Arrays.asList(strings));
            return GameBox.GAME_NOT_STARTED_ERROR;
        }

        String puzzle = null;

        // for time out reasons when someone dumb messed with the puzzles file
        int count = 0;

        try {
            while (puzzle == null || !(puzzle.toCharArray().length >= 81)) {
                if(count > 20){
                    Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " Didn't the config tell you to not mess with the number of rows in the puzzles file?");
                    Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " Delete the changed file and reload. At the moment sometimes playing is not possible. There are not enough games!");
                    return GameBox.GAME_NOT_STARTED_ERROR;
                }
                count ++;
                raf.seek(random.nextInt(2000) * 83);
                puzzle = raf.readLine();
            }

        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " Could not start a game");
            Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " Could not find the puzzle file");
            return GameBox.GAME_NOT_STARTED_ERROR;
        }

        if (problemWhileLoading || !pay(players, rule.getCost())) {
            return GameBox.GAME_NOT_ENOUGH_MONEY;
        }

        games.put(players[0].getUniqueId(), new Game(rule, plugin, players[0], playSounds, puzzle, cover, tip, number));
        return GameBox.GAME_STARTED;
    }


    @Override
    public void removeFromGame(UUID uuid) {
        // Todo: handle stop of running game
        games.remove(uuid);
    }


    public void setGameTypes(Map<String, GameRules> gameTypes) {
        this.gameTypes = gameTypes;
    }


    private boolean pay(Player[] player, double cost) {
        if (plugin.isEconEnabled() && !player[0].hasPermission(Permissions.BYPASS_ALL.getPermission()) && !player[0].hasPermission(Permissions.BYPASS_GAME.getPermission(Main.gameID)) && cost > 0.0) {
            if (Main.econ.getBalance(player[0]) >= cost) {
                Main.econ.withdrawPlayer(player[0], cost);
                player[0].sendMessage(GameBox.chatColor(lang.PREFIX + plugin.lang.GAME_PAYED.replaceAll("%cost%", String.valueOf(cost))));
                return true;
            } else {
                player[0].sendMessage(GameBox.chatColor(lang.PREFIX + plugin.lang.GAME_NOT_ENOUGH_MONEY));
                return false;
            }
        } else {
            return true;
        }
    }

    public void onGameEnd(Player winner, String key) {

        GameRules rule = gameTypes.get(key);

        if(plugin.isEconEnabled()){
            if(!winner.hasPermission(Permissions.BYPASS_ALL.getPermission()) && !winner.hasPermission(Permissions.BYPASS_GAME.getPermission(Main.gameID))){
                Main.econ.depositPlayer(winner, rule.getReward());
                winner.sendMessage((lang.PREFIX + lang.GAME_WON_MONEY.replaceAll("%reward%", rule.getReward()+"")));
            } else {
                winner.sendMessage((lang.PREFIX + lang.GAME_WON));
            }
        } else {
            winner.sendMessage((lang.PREFIX + lang.GAME_WON));
        }

        if(rule.isSaveStats()){
            plugin.gameBox.getStatistics().addStatistics(winner.getUniqueId(), Main.gameID, key, 1., SaveType.WINS);
        }
        if(rule.getTokens() > 0){
            plugin.gameBox.wonTokens(winner.getUniqueId(), rule.getTokens(), Main.gameID);
        }
    }
}
