package uk.co.thinkofdeath.jspl;

import jdk.internal.dynalink.beans.StaticClass;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.*;

import javax.script.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class JavascriptPluginLoader implements PluginLoader {

    private static final Pattern[] javascriptMatcher = {Pattern.compile("\\.js$")};
    private final Server server;
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private final Bindings global = engine.getBindings(ScriptContext.GLOBAL_SCOPE);

    private static JavascriptPluginLoader instance;

    public JavascriptPluginLoader(Server server) {
        this.server = server;
        instance = this;
        global.put("Plugin", StaticClass.forClass(JSPlugin.class));
    }

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
        ScriptContext context = new SimpleScriptContext();
        context.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        context.setBindings(global, ScriptContext.GLOBAL_SCOPE);

        try (Reader reader = new FileReader(file)) {
            engine.eval(reader, context);

            Object oDesc = context.getAttribute("pluginDescription");
            if (!(oDesc instanceof ScriptObjectMirror)) {
                throw new InvalidPluginException("Missing/Incorrect plugin description");
            }
            ScriptObjectMirror desc = (ScriptObjectMirror) oDesc;
            YamlConfiguration config = new YamlConfiguration();
            config.set("main", "<script>");
            desc.keySet().forEach(k -> config.set(k, desc.get(k)));
            PluginDescriptionFile descriptionFile = new PluginDescriptionFile(
                    new StringReader(config.saveToString())
            );

            Object plugin = context.getAttribute("plugin");
            if (!(plugin instanceof JSPlugin)) {
                System.out.println(plugin.getClass().getName());
                throw new InvalidPluginException("Missing var - plugin, plugin must be a JSPlugin");
            }

            JSPlugin jsPlugin = (JSPlugin) plugin;
            jsPlugin.context = context;
            jsPlugin.description = descriptionFile;
            jsPlugin.loader = this;
            jsPlugin.server = server;
            jsPlugin.logger = new PluginLogger(jsPlugin);
            jsPlugin.dataFolder = new File(file.getParentFile(), descriptionFile.getName());
            jsPlugin.configFile = new File(jsPlugin.dataFolder, "config.yml");
            jsPlugin.getLogger().info("Loading " + jsPlugin.getDescription().getFullName());
            context.setAttribute("$", jsPlugin, ScriptContext.ENGINE_SCOPE);

            // Override print
            engine.eval("print = function(msg) { $.logger.info(msg) };", context);

            jsPlugin.onLoad();
            return jsPlugin;
        } catch (IOException
                | ScriptException
                | InvalidDescriptionException e) {
            throw new InvalidPluginException(e);
        }
    }

    @Override
    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        ScriptContext context = new SimpleScriptContext();
        context.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        context.setBindings(global, ScriptContext.GLOBAL_SCOPE);

        try (Reader reader = new FileReader(file)) {
            engine.eval(reader, context);
            Object oDesc = context.getAttribute("pluginDescription");
            if (!(oDesc instanceof ScriptObjectMirror)) {
                throw new InvalidDescriptionException("Missing/Incorrect plugin description");
            }
            ScriptObjectMirror desc = (ScriptObjectMirror) oDesc;
            YamlConfiguration config = new YamlConfiguration();
            config.set("main", "<script>");
            desc.keySet().forEach(k -> config.set(k, desc.get(k)));
            return new PluginDescriptionFile(
                    new StringReader(config.saveToString())
            );
        } catch (IOException
                | ScriptException e) {
            throw new InvalidDescriptionException(e);
        }
    }

    @Override
    public Pattern[] getPluginFileFilters() {
        return javascriptMatcher.clone();
    }

    @Override
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin plugin) {
        throw new UnsupportedOperationException("Listeners");
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        if (!plugin.isEnabled() && plugin instanceof JSPlugin) {
            plugin.getLogger().info("Enabling " + plugin.getDescription().getFullName());
            JSPlugin jsPlugin = ((JSPlugin) plugin);
            jsPlugin.enabled = true;
            jsPlugin.onEnable();
            server.getPluginManager().callEvent(new PluginEnableEvent(jsPlugin));
        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        if (plugin.isEnabled() && plugin instanceof JSPlugin) {
            plugin.getLogger().info("Disabling " + plugin.getDescription().getFullName());
            JSPlugin jsPlugin = ((JSPlugin) plugin);
            jsPlugin.enabled = false;
            jsPlugin.onDisable();
            server.getPluginManager().callEvent(new PluginDisableEvent(plugin));

        }
    }

    public static Pattern[] getJavascriptMatcher() {
        return javascriptMatcher;
    }

    /**
     * Used because the bukkit api provides no way to get a loader
     * without a plugin loaded by it
     *
     * @return The plugin loader
     */
    public static JavascriptPluginLoader getInstance() {
        return instance;
    }

    public Bindings getGlobal() {
        return global;
    }

    public ScriptEngine getEngine() {
        return engine;
    }
}
