package com.kamikazejam.kamicommon.modules.integration;

import com.kamikazejam.kamicommon.KamiPlugin;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.event.CitizensReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class CitizensIntegration extends ModuleIntegration {
    public CitizensIntegration(KamiPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onCitizensEnable(CitizensEnableEvent event) {
        getPlugin().getLogger().info("[CitizensEnableEvent] Calling Modules in 1 second ...");
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                getPlugin().getModuleManager().onCitizensLoaded(), 20L);
    }
    @EventHandler
    public void onCitizensLoaded(CitizensReloadEvent event) {
        getPlugin().getLogger().info("[CitizensReloadEvent] Calling Modules in 1 second ...");
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                getPlugin().getModuleManager().onCitizensLoaded(), 20L);
    }
}
