package me.nikl.gamebox.games;

import me.nikl.gamebox.GameBox;
import me.nikl.gamebox.Module;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Niklas
 *
 * Main class of the GameBox game Sudoku
 */
public class LogicPuzzles extends JavaPlugin{
    public static final String SUDOKU = "sudoku";
    private GameBox gameBox;

    @Override
    public void onEnable(){
        Plugin plugin = Bukkit.getPluginManager().getPlugin("GameBox");
        if(plugin == null || !plugin.isEnabled()){
            getLogger().warning(" GameBox was not found! Disabling LogicPuzzles...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        gameBox = (GameBox) plugin;
        new Module(gameBox, SUDOKU
                , "me.nikl.gamebox.games.sudoku.Sudoku"
                , this, SUDOKU, "su");
    }
}
