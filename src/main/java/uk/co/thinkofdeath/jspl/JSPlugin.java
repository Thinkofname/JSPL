package uk.co.thinkofdeath.jspl;

import com.avaje.ebean.EbeanServer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginLogger;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

public abstract class JSPlugin implements Plugin {

    PluginDescriptionFile description;
    JavascriptPluginLoader loader;
    boolean enabled = false;
    Server server;
    PluginLogger logger;

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return description;
    }

    @Override
    public FileConfiguration getConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getResource(String filename) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveDefaultConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reloadConfig() {
        throw new UnsupportedOperationException();
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
    public EbeanServer getDatabase() {
        throw new UnsupportedOperationException();
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
}
