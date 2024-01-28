package com.egor4.minesweeper;

import com.egor4.minesweeper.commands.MinesweeperCommand;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Minesweeper implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("minesweeper");

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(MinesweeperCommand::register);
		LOGGER.info("Hello Fabric world!");
	}
}