package me.nikl.sudoku;

import me.nikl.gamebox.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Niklas on 14.04.2017.
 *
 * Game
 */
public class Game {

    private Main plugin;

    private GameRules rule;
    private boolean playSounds;
    private Language lang;

    private Inventory inventory;

    private Player player;

    private Sounds gameOver = Sounds.ANVIL_LAND;

    private float volume = 0.5f, pitch= 1f;

    private int score = 0;

    private Map<Integer, ItemStack> cover;
    private Map<Integer, ItemStack> tip;
    private Map<Integer, ItemStack> number;


    public Game(GameRules rule, Main plugin, Player player, boolean playSounds, String puzzle, Map<Integer, ItemStack> cover, Map<Integer, ItemStack> tip, Map<Integer, ItemStack> number){
        this.plugin = plugin;
        this.lang = plugin.lang;
        this.rule = rule;
        this.player = player;

        this.cover = cover;
        this.tip = tip;
        this.number = number;

        // only play sounds if the game setting allows to
        this.playSounds = plugin.getPlaySounds() && playSounds;

        // create inventory
        this.inventory = Bukkit.createInventory(null, 81, lang.GAME_TITLE.replace("%score%", String.valueOf(score)));

        buildGrid(puzzle);

        player.openInventory(inventory);
    }

    private void buildGrid(String puzzle) {
        int x,y, valueInt;
        char value;
        ItemStack tip;
        ItemMeta meta;
        for(int slot = 0; slot < inventory.getSize(); slot++){
            x = (slot%9)/3 + 1;
            y = (slot/9)/3;
            value = puzzle.charAt(slot);
            try {
                valueInt = Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException exception){
                inventory.setItem(slot, cover.get(y*3 + x));
                continue;
            }

            if(valueInt == 0){
                inventory.setItem(slot, cover.get(y*3 + x));
                continue;
            }

            tip = this.tip.get(y*3 + x).clone();
            meta = tip.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replace("%value%", String.valueOf(value)));
            tip.setItemMeta(meta);
            tip.setAmount(valueInt);
            inventory.setItem(slot, tip);
        }
    }


}
