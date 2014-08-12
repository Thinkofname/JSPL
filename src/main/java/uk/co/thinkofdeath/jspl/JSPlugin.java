package uk.co.thinkofdeath.jspl;

import com.avaje.ebean.EbeanServer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginLogger;

import javax.script.ScriptContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JSPlugin implements Plugin, Listener {

    PluginDescriptionFile description;
    JavascriptPluginLoader loader;
    ScriptContext context;
    boolean enabled = false;
    Server server;
    PluginLogger logger;
    File dataFolder;
    File configFile;
    private FileConfiguration config;

    public JSPlugin on(String event, Consumer<Event> callable) {
        return on(event, "normal", callable);
    }

    public JSPlugin on(String event, String priority, Consumer<Event> callable) {
        return on(event, priority, true, callable);
    }

    public JSPlugin on(String event, String priority, boolean ignoreCanceled, Consumer<Event> callable) {
        Class<? extends Event> eClass;
        try {
            eClass = Class.forName("org.bukkit.event." + event.replace("/", ".")).asSubclass(Event.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unknown event: " + event);
        }
        EventPriority p = EventPriority.valueOf(priority.toUpperCase());
        if (p == null) {
            throw new RuntimeException("Unknown priority level: " + priority);
        }
        getServer().getPluginManager()
                .registerEvent(eClass, this, p, (l, e) -> callable.accept(e), this, ignoreCanceled);
        return this;
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return description;
    }

    @Override
    public PluginLoader getPluginLoader() {
        return loader;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public ScriptContext getContext() {
        return context;
    }

    public JavascriptPluginLoader getLoader() {
        return loader;
    }

    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    @Override
    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    @Override
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public boolean isNaggable() {
        return false;
    }

    @Override
    public void setNaggable(boolean canNag) {

    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return null;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String getName() {
        return description.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    // Unsupported

    @Override
    public EbeanServer getDatabase() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getResource(String filename) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveDefaultConfig() {
        throw new UnsupportedOperationException();
    }
}
