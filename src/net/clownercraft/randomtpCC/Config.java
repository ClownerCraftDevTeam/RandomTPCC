package net.clownercraft.randomtpCC;

import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

class Config {
    static String defaultWorld;
    static HashMap<String, String> worldAliases = new HashMap<>(); //Alias, worldname
    private static HashMap<String, Integer> worldCenterX = new HashMap<>(); //World Name, X
    private static HashMap<String, Integer> worldCenterZ = new HashMap<>(); //World Name, Z
    private static HashMap<String, Integer> worldRadius = new HashMap<>(); //World Name, Radius


    static void init() {
        RandomTPCC plugin = RandomTPCC.getInstance();
        Configuration conf = plugin.getConfig();

        try {
            defaultWorld = conf.getString("defaultWorld");

            ConfigurationSection worldsConf = conf.getConfigurationSection("Worlds");
            Set<String> worldsKeys = worldsConf.getKeys(false);
            Iterator keyIterator = worldsKeys.iterator();

            while (keyIterator.hasNext()) {
                String nextW = (String) keyIterator.next();
                worldAliases.put(worldsConf.getString(nextW + ".alias"), nextW);
                worldCenterX.put(nextW, worldsConf.getInt(nextW + ".center.x"));
                worldCenterZ.put(nextW, worldsConf.getInt(nextW + ".center.z"));
                worldRadius.put(nextW, worldsConf.getInt(nextW + ".radius"));
                RandomTPCC.getInstance().getLogger().info("Enabled for world: " + nextW);
                RandomTPCC.getInstance().getLogger().info("Center: " + worldCenterX.get(nextW) + ", " + worldCenterZ.get(nextW));
                RandomTPCC.getInstance().getLogger().info("Radius: " + worldRadius.get(nextW));
                RandomTPCC.getInstance().getLogger().info("-----");


            }
        } catch (Exception e) {
            plugin.getLogger().info("RandomTPCC failed to load config!");
            e.printStackTrace();
        }
    }

    static int getWorldX(String w) {
        return worldCenterX.get(w);
    }
    static int getWorldZ(String w) {
        return worldCenterZ.get(w);
    }
    static int getWorldRadius(String w) {
        return worldRadius.get(w);
    }
}
