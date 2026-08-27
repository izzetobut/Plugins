package com.izzet.gorevler;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class GorevlerPlugin extends JavaPlugin {

    private static GorevlerPlugin instance;
    private Economy economy;
    private GorevYoneticisi gorevYoneticisi;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy()) {
            getLogger().severe("Vault veya bir ekonomi plugini bulunamadi! GorevSistemi devre disi birakiliyor.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        gorevYoneticisi = new GorevYoneticisi(this);

        GorevGUI gui = new GorevGUI(this);
        getCommand("gorevler").setExecutor(new GorevlerKomutu(gui));

        getServer().getPluginManager().registerEvents(gui, this);
        getServer().getPluginManager().registerEvents(new GorevListener(this), this);

        getLogger().info("Gorev Sistemi aktif edildi!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public Economy getEconomy() {
        return economy;
    }

    public GorevYoneticisi getGorevYoneticisi() {
        return gorevYoneticisi;
    }

    public static GorevlerPlugin getInstance() {
        return instance;
    }
}
