package net.clownercraft.randomtpCC;

import org.bukkit.plugin.java.JavaPlugin;

public class RandomTPCC extends JavaPlugin {

    private static RandomTPCC instance;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        Config.init();
        this.getCommand("randomtp").setExecutor(new RTPCommandExecutor());
    }

    public static RandomTPCC getInstance() {
        return instance;
    }

}
