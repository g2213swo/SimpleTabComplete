package me.g2213swo.simpletabcomplete;

import me.g2213swo.simpletabcomplete.hooks.Hooks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatCompletionListener implements Listener {

    public ChatCompletionListener() {
        run();
    }

    private void run() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(SimpleTabComplete.getInstance(), () -> {
            for (Player tabCompleter : Bukkit.getOnlinePlayers()) {
                List<String> tab = getStrings(tabCompleter);
                tabCompleter.setCustomChatCompletions(tab);
            }
        }, 0, 10);
    }

    @NotNull
    private static List<String> getStrings(Player tabCompleter) {
        List<String> tab = new ArrayList<>();
        tab.add("<item>");
        tab.add("<inv>");
        Hooks hooks = SimpleTabComplete.getInstance().getHooks("SuperVanish");

        if (hooks == null) {
            return tab;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hooks.isVanished(player) && !player.getUniqueId().equals(tabCompleter.getUniqueId())) {
                tab.add("@" + player.getName());
            }
        }

        return tab;
    }

}