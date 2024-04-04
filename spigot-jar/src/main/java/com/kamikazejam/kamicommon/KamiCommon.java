package com.kamikazejam.kamicommon;

import com.kamikazejam.kamicommon.command.KamiCommonCommandRegistration;
import com.kamikazejam.kamicommon.command.type.RegistryType;
import com.kamikazejam.kamicommon.gui.MenuManager;
import com.kamikazejam.kamicommon.gui.MenuTask;
import com.kamikazejam.kamicommon.integrations.PremiumVanishIntegration;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.util.engine.EngineScheduledTeleport;
import com.kamikazejam.kamicommon.util.engine.EngineTeleportMixinCause;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import com.kamikazejam.kamicommon.util.mixin.*;
import com.kamikazejam.kamicommon.yaml.standalone.YamlUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings("unused")
public class KamiCommon extends KamiPlugin implements Listener {
    private static KamiCommon plugin;
    private @Nullable PremiumVanishIntegration vanishIntegration = null;

    @Override
    public void onEnableInner(){
        long start = System.currentTimeMillis();
        getLogger().info("KamiCommon enabling...");

        plugin = this;
        plugin.getServer().getPluginManager().registerEvents(new MenuManager(), plugin);
        getServer().getPluginManager().registerEvents(this, this);

        // Register Integrations
        if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
            vanishIntegration = new PremiumVanishIntegration(this);
        }

        // Activate Actives
        EngineScheduledTeleport.get().setActive(this);
        EngineTeleportMixinCause.get().setActive(this);
        MixinPlayed.get().setActive(this);
        MixinDisplayName.get().setActive(this);
        MixinTeleport.get().setActive(this);
        MixinSenderPs.get().setActive(this);
        MixinWorld.get().setActive(this);

        // Schedule menu task to run every 1 second
        Bukkit.getScheduler().runTaskTimer(this, new MenuTask(), 0L, 20L);

        if (isWineSpigot()) {
            getLogger().info("WineSpigot (1.8.8) detected!");
        }

        // Create Yaml Loader
        getLogger().info("Creating Yaml Loader");
        YamlUtil.getYaml();

        // Setup IdUtil
        IdUtilLocal.setup(this);

        // Setup RegistryType (Types for Commands)
        RegistryType.registerAll();

        // Setup Commands
        new KamiCommonCommandRegistration(this);

        getLogger().info("KamiCommon enabled in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisableInner() {
        // Deactivate Actives
        EngineScheduledTeleport.get().setActive(null);
        EngineTeleportMixinCause.get().setActive(null);
        MixinPlayed.get().setActive(null);
        MixinDisplayName.get().setActive(null);
        MixinTeleport.get().setActive(null);
        MixinSenderPs.get().setActive(null);
        MixinWorld.get().setActive(null);

        // Unregister all listeners
        HandlerList.unregisterAll((Plugin) plugin);

        // Save IdUtil
        IdUtilLocal.saveCachefileDatas();

        Bukkit.getLogger().info("KamiCommon disabled");
    }

    public static JavaPlugin get() {
        return plugin;
    }


    public static boolean isWineSpigot() {
        return NmsVersion.isWineSpigot();
    }




}
