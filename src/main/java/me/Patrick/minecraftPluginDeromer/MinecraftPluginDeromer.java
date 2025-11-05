package me.Patrick.minecraftPluginDeromer;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MinecraftPluginDeromer extends JavaPlugin implements Listener, Audience {

    private final Map<UUID, Integer> messageCount = new HashMap<>();
    private final Map<UUID, Boolean> timerRunning = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        UUID uuid = player.getUniqueId();

        // Zähler hochzählen
        int count = messageCount.getOrDefault(uuid, 0) + 1;
        messageCount.put(uuid, count);

        // Check for message count
        switch(count){
            case 15:
                mute(name,5,player);
                break;
            case 30:
                mute(name,15, player);
                break;
            case 60:
                mute(name, 1440, player);
                break;
        }

        // Timer nur starten, wenn noch keiner läuft
        if (!timerRunning.getOrDefault(uuid, false)) {
            timerRunning.put(uuid, true);

            // Scheduler startet einen Task nach 1 Minute
            getServer().getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                        messageCount.remove(uuid);
                        timerRunning.remove(uuid);
                }
            }, 20L * 60); // 20L =1 sek * wie viel sekunden man möchte
        }
    }
    public void mute (String name , int duration, Player player){
        String command = "carbonchat:mute " + name + " --duration " + duration + "m";
        getServer().getScheduler().runTask(this, () -> getServer().dispatchCommand(getServer().getConsoleSender(), command));
    }
}
