package uk.co.thinkofdeath.jspl;

import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.thinkofdeath.command.bukkit.BukkitCommandManager;

import java.io.File;
import java.util.regex.Pattern;

public class JSPLPlugin extends JavaPlugin {

    private final BukkitCommandManager commandManager = new BukkitCommandManager(this);

    @Override
    public void onLoad() {
        getServer().getPluginManager().registerInterface(JavascriptPluginLoader.class);
        Pattern pattern = JavascriptPluginLoader.getJavascriptMatcher()[0];
        for (File file : getFile().getParentFile().listFiles()) {
            if (pattern.matcher(file.getName()).find()) {
                try {
                    getServer().getPluginManager().loadPlugin(file);
                } catch (InvalidPluginException | InvalidDescriptionException e) {
                    getLogger().warning("Failed to load " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onEnable() {
        commandManager.register(new JSPLCommands(this));

        getCommand("jspl").setExecutor(commandManager);
        getCommand("jspl").setTabCompleter(commandManager);
    }
}
