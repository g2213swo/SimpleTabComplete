package me.g2213swo.simpletabcomplete;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.g2213swo.simpletabcomplete.hooks.Hooks;
import me.g2213swo.simpletabcomplete.hooks.HooksId;
import me.g2213swo.simpletabcomplete.hooks.SuperVanishHook;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleTabComplete extends JavaPlugin {

    public ProtocolManager manager;

    private static SimpleTabComplete instance;

    public static SimpleTabComplete getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        manager = ProtocolLibrary.getProtocolManager();
        getServer().getPluginManager().registerEvents(new ChatCompletionListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        HandlerList.unregisterAll(this);
    }

    public Hooks getHooks(String name) {
        HooksId hooksId = HooksId.valueOf(name);
        if (hooksId == HooksId.SuperVanish) {
            return new SuperVanishHook();
        }
        return null;
    }
}
