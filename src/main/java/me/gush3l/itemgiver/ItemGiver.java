package me.gush3l.itemgiver;

import org.bukkit.plugin.java.JavaPlugin;

public final class ItemGiver extends JavaPlugin {

    private static ItemGiver instance;

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        Util.createItemsStorage();
        this.getCommand("itemgiver").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ItemGiver getInstance(){
        return instance;
    }

}
