package me.nikl.gamebox.games.sudoku;

import me.nikl.gamebox.games.GameLanguage;

import java.util.List;


public class SudokuLanguage extends GameLanguage{

	public List<String> RESTART_LORE;

	public String GAME_TITLE, GAME_TITLE_WON, GAME_PAYED, GAME_NOT_ENOUGH_MONEY, GAME_WON_MONEY, GAME_WON;
	public String RESTART_NAME;

	public SudokuLanguage(me.nikl.gamebox.games.Game game) {
		super(game);
	}


	@Override
	protected void loadMessages() {
		RESTART_NAME = getString("restartButton.displayName");
		RESTART_LORE = getStringList("restartButton.lore");
		this.GAME_TITLE = getString("game.inventoryTitles.gameTitle");
		this.GAME_TITLE_WON = getString("game.inventoryTitles.won");
		this.GAME_WON_MONEY = getString("game.econ.wonMoney");
		this.GAME_WON = getString("game.won");
		this.GAME_PAYED = getString("game.econ.payed");
		this.GAME_NOT_ENOUGH_MONEY = getString("game.econ.notEnoughMoney");
	}
}

