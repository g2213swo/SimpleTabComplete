package me.g2213swo.simpletabcomplete.hooks;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.entity.Player;

public class SuperVanishHook implements Hooks{
    @Override
    public boolean isVanished(Player player) {
        return VanishAPI.isInvisible(player);
    }
}
