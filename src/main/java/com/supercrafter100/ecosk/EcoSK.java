package com.supercrafter100.ecosk;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.supercrafter100.ecosk.storage.SQLiteManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class EcoSK extends JavaPlugin {

    private static EcoSK instance;
    private static SQLiteManager db;
    private static SkriptAddon addon;

    @Override
    public void onEnable() {
        instance = this;

        // Database
        getDataFolder().mkdirs();
        db = new SQLiteManager(getDataFolder().getAbsolutePath() + File.separator + "database.db");

        // Skript
        addon = Skript.registerAddon(this);
        try {
            addon.loadClasses("com.supercrafter100.ecosk", "elements");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        db.destroy();
    }

    // TODO: Make user manager to handle user data, so no request has to be made everytime.

    public static EcoSK getInstance() { return instance; }
    public static SkriptAddon getAddonInstance() { return addon; }
    public static SQLiteManager getDatabaseManager() { return db; }
}
