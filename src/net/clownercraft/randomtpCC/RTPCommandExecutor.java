package net.clownercraft.randomtpCC;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPCommandExecutor implements CommandExecutor {

    public RTPCommandExecutor() {}

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            if (label.equalsIgnoreCase("rtp")) {
                Player sender = (Player) commandSender;
                if (args.length < 1) {
                    String world = sender.getWorld().getName();
                    if (Config.worldAliases.containsValue(world)) {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "[RTP] "+ ChatColor.DARK_AQUA + "Finding a safe spot in " + world + " to teleport you to...");
                        Bukkit.getScheduler().runTaskAsynchronously(RandomTPCC.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                teleport(sender, world);
                            }
                        });
                    } else {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "[RTP] "+ ChatColor.DARK_AQUA + "Finding a safe spot in " + Config.defaultWorld + " to teleport you to...");
                        Bukkit.getScheduler().runTaskAsynchronously(RandomTPCC.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                teleport(sender, Config.defaultWorld);
                            }
                        });
                    }
                    return true;
                } else {
                    if (Config.worldAliases.containsKey(args[0])) {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "[RTP] "+ ChatColor.DARK_AQUA + "Finding a safe spot in " + args[0] + " to teleport you to...");
                        Bukkit.getScheduler().runTaskAsynchronously(RandomTPCC.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                teleport(sender, Config.worldAliases.get(args[0]));
                            }
                        });
                    } else {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "[RTP] "+ ChatColor.DARK_AQUA + "Finding a safe spot in " + Config.defaultWorld + " to teleport you to...");

                        Bukkit.getScheduler().runTaskAsynchronously(RandomTPCC.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                teleport(sender, Config.defaultWorld);                            }
                        });

                    }
                    return true;
                }
            } else return false;
        } else return false;
    }

    private void teleport(Player player, String world) {
        Location loc = randLocation(world);
        if (loc == null) {
            player.sendMessage(ChatColor.DARK_PURPLE + "[RTP] "+ ChatColor.DARK_AQUA + "Couldn't find a safe place! D:");

            return;
        }
        World w = Bukkit.getWorld(world);
        Chunk chunk = w.getChunkAt(loc);
        Bukkit.getScheduler().runTask(RandomTPCC.getInstance(), () -> {
            w.loadChunk(chunk);
            player.teleport(loc.add(0.0, 2.0, 0.0));
        });
    }

    private boolean isSafe(Location loc) {
        Block block = loc.getBlock();
        if (loc.getY() < -63) return false;
        if (block.getType().equals(Material.LAVA)) return false;
        if (block.getType().equals(Material.WATER)) return false;
        if (block.getType().equals(Material.FIRE)) return false;
        if (block.getType().equals(Material.AIR)) return false;
        if (block.getType().equals(Material.CACTUS)) return false;
        if (block.getType().equals(Material.MAGMA_BLOCK)) return false;

        loc.add(0,1,0);
        block = loc.getBlock();
        if (!(block.getType().equals(Material.AIR) || block.getType().equals(Material.CAVE_AIR) || block.getType().equals(Material.VOID_AIR))) return false;


        loc.add(0,1,0);
        block = loc.getBlock();
        return block.getType().equals(Material.AIR) || block.getType().equals(Material.CAVE_AIR) || block.getType().equals(Material.VOID_AIR);
    }

    private Location randLocation(String w) {
        int centerX = 0,centerZ = 0,radius = 0;
        try {
            centerX = Config.getWorldX(w);
            centerZ = Config.getWorldZ(w);
            radius = Math.floorDiv(Config.getWorldRadius(w), 32);
        } catch (NullPointerException e) {
            e.printStackTrace();
            RandomTPCC.getInstance().getLogger().info("World Name: " + w);
        }

        boolean safe = false;
        int count = 0;

        Location outloc = null;

        World world = Bukkit.getWorld(w);
        if (world==null) return null;

        while (!safe && count < 20) {
            double rand1 = (Math.random() * radius * 2) - radius;
            double rand2 = (Math.random() * radius * 2) - radius;

            double resultX = centerX + 32 * rand1;
            double resultZ = centerZ + 32 * rand2;

            double resultY;
            try{
                resultY = world.getHighestBlockYAt((int) Math.round(resultX),(int) Math.round(resultZ));
            } catch (NullPointerException e) {
                break;
            }
            Location loc = new Location(world, resultX, resultY, resultZ);
            safe = isSafe(loc.clone());
            if (safe) outloc = loc;
            count++;
        }

        return outloc;
    }
}
