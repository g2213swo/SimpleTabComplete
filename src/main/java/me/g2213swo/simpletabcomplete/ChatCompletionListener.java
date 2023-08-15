package me.g2213swo.simpletabcomplete;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import me.g2213swo.simpletabcomplete.utils.ChatColorUtils;
import me.g2213swo.simpletabcomplete.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ChatCompletionListener implements Listener {

    private static Object[] nmsClientboundCustomChatCompletionsPacketActions;

    private static final ProtocolManager protocolManager;

    static {
        try {
            Class<?> nmsClientboundCustomChatCompletionsPacketActionClass = NMSUtils.getNMSClass(
                    "net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket$Action",
                    "net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket$a");
            nmsClientboundCustomChatCompletionsPacketActions = nmsClientboundCustomChatCompletionsPacketActionClass.getEnumConstants();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        protocolManager = SimpleTabComplete.getInstance().manager;
    }

    private final Map<Player, List<String>> registered;

    public ChatCompletionListener() {
        this.registered = new ConcurrentHashMap<>();
        run();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        registered.remove(event.getPlayer());
    }

    private void run() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(SimpleTabComplete.getInstance(), () -> {
            for (Player tabCompleter : Bukkit.getOnlinePlayers()) {
                List<String> tab = getStrings(tabCompleter);

                List<String> oldList = registered.computeIfAbsent(tabCompleter, k -> new ArrayList<>());
                List<String> add = tab.stream().filter(each -> !oldList.contains(each)).collect(Collectors.toList());
                List<String> remove = oldList.stream().filter(each -> !tab.contains(each)).collect(Collectors.toList());
                oldList.removeAll(remove);
                oldList.addAll(add);

                if (!add.isEmpty()) {
                    PacketContainer chatCompletionPacket1 = protocolManager.createPacket(PacketType.Play.Server.CUSTOM_CHAT_COMPLETIONS);
                    chatCompletionPacket1.getModifier().write(0, nmsClientboundCustomChatCompletionsPacketActions[0]);
                    chatCompletionPacket1.getModifier().write(1, add);
                    protocolManager.sendServerPacket(tabCompleter, chatCompletionPacket1);
                }

                if (!remove.isEmpty()) {
                    PacketContainer chatCompletionPacket2 = protocolManager.createPacket(PacketType.Play.Server.CUSTOM_CHAT_COMPLETIONS);
                    chatCompletionPacket2.getModifier().write(0, nmsClientboundCustomChatCompletionsPacketActions[1]);
                    chatCompletionPacket2.getModifier().write(1, remove);
                    protocolManager.sendServerPacket(tabCompleter, chatCompletionPacket2);
                }
            }
        }, 0, 10);
    }

    @NotNull
    private static List<String> getStrings(Player tabCompleter) {
        List<String> tab = new ArrayList<>();

        tab.add("<item>");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getUniqueId().equals(tabCompleter.getUniqueId())) {
                tab.add(ChatColorUtils.stripColor("@" + player.getName()));
//                        for (String nickname : XXX.getNicknames(player.getUniqueId())) {
//                            tab.add(ChatColorUtils.stripColor("@" + nickname));
//                        }
            }
        }
        return tab;
    }

}