package me.dsate1.betterwarps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.raftpowered.ChatColor;
import org.raftpowered.Command;
import org.raftpowered.player.Player;
import org.raftpowered.CommandSender;
import org.raftpowered.Location;
import org.raftpowered.Raft;
import org.raftpowered.config.Config;

public class WarpCommand {
	private static Scanner in; // Legacy conversions
	private static Config config;
	
	@Command(description = "Manage and use warps.", name = "warp", usage = "/warp help")
	public static void warp(CommandSender sender, String[] args) {
		if(config == null) config = new Config("plugins/EasyWarp/warps.txt");
		if(!sender.isPlayer()) {
			sender.sendMessage("Only players can use /warp!");
			return;
		}
		Player p = sender.asPlayer();
		
		if(args.length == 0 || (args.length > 0 && args[0].equalsIgnoreCase("help"))){
			p.sendMessage("EasyWarp by crushh87");
			p.sendMessage(ChatColor.GRAY + "/warp help" + ChatColor.RESET + " - See this page.");
			p.sendMessage(ChatColor.GRAY + "/warp list" + ChatColor.RESET + " - List all warps.");
			p.sendMessage(ChatColor.GRAY + "/warp set <name>" + ChatColor.RESET + " - Sets a warp at your current position.");
			p.sendMessage(ChatColor.GRAY + "/warp delete <name>" + ChatColor.RESET + " - Deletes a warp.");
			p.sendMessage(ChatColor.GRAY + "/warp <warp>" + ChatColor.RESET + " - Warps you to a warp point.");
		}
		
		else if(args.length >= 1){
			
			/* |  /warp set <name> |  */ 
			if(args[0].equalsIgnoreCase("set")){
				
				// Checks
				if(!sender.hasPermission("easywarp.edit", true)){
					p.sendMessage(ChatColor.RED + "I'm sorry, you don't have permission to do that!");
					return;
				}
				if(args.length != 2){
					p.sendMessage(ChatColor.RED + "Incorrect arguments! (/warp set <name>) ");
					return;
				}
				if(warpExists(args[1]) == true){
					p.sendMessage(ChatColor.RED + "That warp already exists!");
					return;
				}
				
				// Set Warp
				boolean wasCreated = createWarp(args[1], p.getLocation());
				if(wasCreated)
					p.sendMessage("Warp " + args[1] + " set!");
				else
					p.sendMessage(ChatColor.RED + "There was an error while creating that warp?!");

			}
			/* | /warp delete <name> |  */ 
			else if(args[0].equalsIgnoreCase("delete")){
				
				// Checks 
				if(!sender.hasPermission("easywarp.edit", true)){
					p.sendMessage(ChatColor.RED + "I'm sorry, you don't have permission to do that!");
					return;
				}
				if(args.length != 2){
					p.sendMessage(ChatColor.RED + "Incorrect arguments! (/warp delete <name>) ");
					return;
				}
				if(!warpExists(args[1])){
					p.sendMessage(ChatColor.RED + "That warp does not exist!");
					return;
				}
				
				// Try to delete Warp 
				boolean wasDeleted = deleteWarp(args[1]);
				if(wasDeleted)
					p.sendMessage("Warp deleted successfully!");
				else
					p.sendMessage(ChatColor.RED + "There was an error while deleting that warp?!");
			}
			
			else if(args[0].equalsIgnoreCase("list")){
				// Checks
				if(!sender.hasPermission("easywarp.use", false)){
					p.sendMessage(ChatColor.RED + "I'm sorry, you don't have permission to do that!");
					return;
				}
				if(args.length != 1){
					p.sendMessage(ChatColor.RED + "Incorrect arguments! (/warp list)");
					return;
				}
				String[] warps = getAllWarps();
				p.sendMessage("Warps (" + warps.length + "): " + StringUtils.join(warps, ", "));
			}
			
			/* | /warp <name> | */
			else if(args.length == 1) {
				
				// Checks
				if(!sender.hasPermission("easywarp.use", false)){
					p.sendMessage(ChatColor.RED + "I'm sorry, you don't have permission to do that!");
					return;
				}
				if(!warpExists(args[0])){
					p.sendMessage(ChatColor.RED + "That warp does not exist!");
					return;
				}
				
				// Warp
				Location w = getWarp(args[0]);
				if(w != null){
					p.setLocation(w);
				}else
					p.sendMessage(ChatColor.RED + "There was an error while warping?!");
				
			}else
				p.sendMessage(ChatColor.RED + "Unknown Command! (/warp help)");
		}
	}
	
	public static String[] getAllWarps() {
		List<String> warps = new ArrayList<String>();
		String[] list = config.getKeys();
		for(String warp : list) {
			if(!config.getString("warp").equalsIgnoreCase("null")) {
				warps.add(warp);
			}
		}
		return warps.toArray(new String[warps.size()]);
	}
	
	public static boolean deleteWarp(String warp) {
		config.setString(warp, null);
		return true; // keyExists() will not update this fast
	}
	
	public static boolean createWarp(String warp, Location loc) {
		config.setString(warp, loc.toString());
		return config.keyExists(warp);
	}
	
	public static boolean warpExists(String warp) {
		return config.keyExists(warp) && !config.getString(warp).equalsIgnoreCase("null");
	}
	
	public static Location getWarp(String warp) {
		String[] cords = config.getString(warp).split(",");
		return new Location(
				/* World */ Raft.getWorld(cords[5]),
				/* X */ Double.parseDouble(cords[0]), 
				/* Y */ Double.parseDouble(cords[1]),
				/* Z */ Double.parseDouble(cords[2]),
				/* Yaw */ Float.parseFloat(cords[3]),
				/* Pitch */ Float.parseFloat(cords[4]));
	}
	
	public static ArrayList<String> legacy_getAllWarps(){
		ArrayList<String> warps = new ArrayList<String>();
		String s = "";
		
		try{
			File inputFile = new File("warps.txt");
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			while( (s = reader.readLine()) != null){
				if(s.length() > 0)
					warps.add(s.substring(0, s.indexOf(":")).trim());
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return warps;
	}

	public static Location legacy_getWarp(String warp){
		File file = new File("warps.txt");
		Location loc = null;
		try {
			in = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while(in.hasNextLine()){
			String line = in.nextLine();
			if(line.startsWith(warp + ":")){
				String warpData = line;
				warpData = warpData.substring(warpData.indexOf(warp + ":") + (warp.length() + 1));
				String[] cords = warpData.split(",");
						
				return new Location(
						/* World */ Raft.getWorld(cords[5]),
						/* X */ Double.parseDouble(cords[0]), 
						/* Y */ Double.parseDouble(cords[1]),
						/* Z */ Double.parseDouble(cords[2]),
						/* Yaw */ Float.parseFloat(cords[3]),
						/* Pitch */ Float.parseFloat(cords[4]));
			}
		}
		
		return loc;
	}
}
