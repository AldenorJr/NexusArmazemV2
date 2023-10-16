package br.com.nexus.armazem.main;

import br.com.nexus.armazem.main.cache.BonusCache;
import br.com.nexus.armazem.main.command.CommandArmazem;
import br.com.nexus.armazem.main.inventory.ArmazemInventory;
import br.com.nexus.armazem.main.listener.InventoryArmazemListener;
import br.com.nexus.armazem.main.listener.PlayerChatEvent;
import br.com.nexus.armazem.main.listener.PlotListener;
import br.com.nexus.armazem.main.listener.SpawnItemListener;
import br.com.nexus.armazem.main.storage.HikariConnect;
import br.com.nexus.armazem.main.storage.database.DatabaseMethod;
import lombok.SneakyThrows;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    public String prefix = "§5[NexusArmazem] ";
    private HikariConnect hikariConnect = new HikariConnect();
    private DatabaseMethod databaseMethod = new DatabaseMethod(hikariConnect, this);
    private ArmazemInventory armazemInventory = new ArmazemInventory(this);
    private BonusCache bonusCache = new BonusCache(this);
    private PlayerChatEvent playerChatEvent = new PlayerChatEvent(databaseMethod);

    public static Economy economy = null;

    @Override @SneakyThrows
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getConsoleSender().sendMessage(prefix+ "§aConfiguração iniciado.");
        onEnableBanco();
        Bukkit.getConsoleSender().sendMessage(prefix+ "§aBanco de dados iniciado.");
        databaseMethod.createTable();
        Bukkit.getConsoleSender().sendMessage(prefix+ "§aTabela do banco de dados carregado.");
        Bukkit.getConsoleSender().sendMessage(prefix+ "§aRegistrando os comandos e eventos.");
        bonusCache.loadCacheBonus();
        registerListener();
        registerCommand();
        setupEconomy();
        Bukkit.getConsoleSender().sendMessage(prefix+ "§aPlugin iniciado com sucesso.");
    }

    public void onEnableBanco() {
        switch (getConfig().getString("Banco")) {
            case "SQLite":
                hikariConnect.SQLConnectLoad(new File(getDataFolder(), "database").toString());
                break;
            case "MySQL":
                hikariConnect.MySQLConnectLoad(getConfig().getString("MySQL.host"), getConfig().getString("MySQL.database"),
                        getConfig().getString("MySQL.user"), getConfig().getString("MySQL.password"));
                break;
        }
    }

    public void registerListener() {
        getServer().getPluginManager().registerEvents(new PlotListener(databaseMethod, this), this);
        getServer().getPluginManager().registerEvents(new SpawnItemListener(databaseMethod), this);
        getServer().getPluginManager().registerEvents(new InventoryArmazemListener(databaseMethod, this), this);
        getServer().getPluginManager().registerEvents(playerChatEvent, this);
    }

    public void registerCommand() {
        getCommand("armazem").setExecutor(new CommandArmazem(armazemInventory, databaseMethod));
    }

    @Override
    public void onDisable() {
        hikariConnect.closeHikariDataSource();
        Bukkit.getConsoleSender().sendMessage(prefix+ "§aConexão com o banco de dados encerrada.");
        Bukkit.getConsoleSender().sendMessage(prefix+ "§aPlugin desligando.");
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
    }

}
