package me.nikl.sudoku;

import me.nikl.gamebox.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.BitSet;
import java.util.Map;

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

    private int[] gridNumbers = new int[81];

    private ItemStack[][] numberTiles = new ItemStack[9][9];

    private BitSet tips = new BitSet(81);


    public Game(GameRules rule, Main plugin, Player player, boolean playSounds, String puzzle, Map<Integer, ItemStack> cover, Map<Integer, ItemStack> tip, Map<Integer, ItemStack> number){
        this.plugin = plugin;
        this.lang = plugin.lang;
        this.rule = rule;
        this.player = player;

        this.cover = cover;
        this.tip = tip;

        loadNumberTiles(number);

        // only play sounds if the game setting allows to
        this.playSounds = plugin.getPlaySounds() && playSounds;

        // create inventory
        this.inventory = Bukkit.createInventory(null, 81, lang.GAME_TITLE.replace("%score%", String.valueOf(score)));

        buildStartingGrid(puzzle);

        player.openInventory(inventory);
    }

    private void loadNumberTiles(Map<Integer, ItemStack> number) {
        ItemMeta meta;
        ItemStack numberTile;
        for(int smallGrid = 0; smallGrid < 9; smallGrid ++){
            for(int value = 1; value < 10; value++){

                numberTile = number.get(smallGrid + 1).clone();
                meta = numberTile.getItemMeta();
                meta.setDisplayName(meta.getDisplayName().replace("%value%", String.valueOf(value)));
                numberTile.setItemMeta(meta);
                numberTile.setAmount(value);

                this.numberTiles[smallGrid][value - 1] = numberTile.clone();
            }
        }
    }

    private void buildStartingGrid(String puzzle) {
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

            tips.set(slot);
            gridNumbers[slot] = value;

            tip = this.tip.get(y*3 + x).clone();
            meta = tip.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replace("%value%", String.valueOf(value)));
            tip.setItemMeta(meta);
            tip.setAmount(valueInt);
            inventory.setItem(slot, tip);
        }
    }

    private boolean isWon() {
        //Check rows and columns
        for (int i = 0; i < 9; i++) {
            BitSet bsRow = new BitSet(9);
            BitSet bsColumn = new BitSet(9);
            for (int j = 0; j < 9; j++) {
                if (gridNumbers[i*9 + j] == 0 || gridNumbers[j*9 + i] == 0) return false;
                if (bsRow.get(gridNumbers[i*9 + j] - 1) || bsColumn.get(gridNumbers[j*9 + i] - 1))
                    return false;
                else {
                    bsRow.set(gridNumbers[i*9 + j] - 1);
                    bsColumn.set(gridNumbers[j*9 + i] - 1);
                }
            }
        }
        //Check within 3 x 3 grid
        for (int rowOffset = 0; rowOffset < 9; rowOffset += 3) {
            for (int columnOffset = 0; columnOffset < 9; columnOffset += 3) {
                BitSet threeByThree = new BitSet(9);
                for (int i = rowOffset; i < rowOffset + 3; i++) {
                    for (int j = columnOffset; j < columnOffset + 3; j++) {
                        // cant happen since all where checked in the loop before
                        if (gridNumbers[i*9 + j] == 0) return false;
                        if (threeByThree.get(gridNumbers[i*9 + j] - 1))
                            return false;
                        else
                            threeByThree.set(gridNumbers[i*9 + j] - 1);
                    }
                }
            }
        }
        return true;
    }

    public void onClick(InventoryClickEvent inventoryClickEvent) {
        if(inventoryClickEvent.getCurrentItem() == null) return;

        int slot = inventoryClickEvent.getSlot();

        // if the bit set is set for the slot it is a tip
        if(tips.get(slot)) return;

        int x = (slot%9)/3;
        int y = (slot/9)/3;

        if(inventoryClickEvent.getAction() == InventoryAction.PICKUP_ALL || inventoryClickEvent.getAction() == InventoryAction.PLACE_ALL) {
            if (gridNumbers[slot] != 9) {
                inventory.setItem(slot, numberTiles[y * 3 + x][gridNumbers[slot]]);
                gridNumbers[slot]++;
            } else {
                gridNumbers[slot] = 0;
                inventory.setItem(slot, cover.get(y * 3 + x + 1));
            }
        } else if (inventoryClickEvent.getAction() == InventoryAction.PICKUP_HALF){
            if (gridNumbers[slot] == 0) {
                inventory.setItem(slot, numberTiles[y * 3 + x][8]);
                gridNumbers[slot] = 9;
            } else if(gridNumbers[slot] == 1) {
                gridNumbers[slot] --;
                inventory.setItem(slot, cover.get(y * 3 + x + 1));
            } else {
                gridNumbers[slot] --;
                inventory.setItem(slot, numberTiles[y * 3 + x][gridNumbers[slot] - 1]);
            }
        } else {
            return;
        }

        if (isWon()){
            Bukkit.getConsoleSender().sendMessage("Game Won!");
        }
    }
}
