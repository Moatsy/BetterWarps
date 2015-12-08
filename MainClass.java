package me.dsate1.betterwarps;
import java.io.File;
import java.io.IOException;

import org.raftpowered.Raft;
import org.raftpowered.Plugin;

@Plugin(description = "Just a better warp plugin", name = "BetterWarps", version = "0.2")
public class MainClass {
	public MainClass(){
		Raft.logInfo("Better Warps - Enabled!");
		Raft.addCommand(WarpCommand.class);
		checkWarpsFile();
	}
	
	/**
	 * Checks for warps.txt, makes if missing.
	 * Moves warps from legacy (warps.txt) to (plugins/EasyWarps/warps.txt)
	 */
	private void checkWarpsFile() {
		File plugins = new File("plugins/EasyWarp");
		if(!plugins.exists() || !plugins.isDirectory()) {
			if(plugins.exists()) plugins.delete();
			plugins.mkdirs();
		}
		File warps = new File("plugins/EasyWarp/warps.txt");
		if(!warps.exists() || !warps.isFile()) {
			if(warps.exists()) warps.delete();
			try {
				warps.createNewFile();
			} catch (IOException e) {
				Raft.logError("Could not create: plugins/EasyWarp/warps.txt");
			}
		}
		File legacy = new File("warps.txt");
		if(legacy.exists()) {
			Raft.logInfo("[EasyWarp] Converting " + WarpCommand.legacy_getAllWarps().size() + " warps!");
			for(String warp : WarpCommand.legacy_getAllWarps()) {
				if(!WarpCommand.warpExists(warp)) {
					WarpCommand.createWarp(warp, WarpCommand.legacy_getWarp(warp));
				}
			}
			legacy.delete();
			Raft.logInfo("[EasyWarp] Done.");
		}
	}
}
