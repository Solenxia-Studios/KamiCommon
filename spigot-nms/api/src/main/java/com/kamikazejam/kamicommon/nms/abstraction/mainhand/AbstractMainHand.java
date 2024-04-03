package com.kamikazejam.kamicommon.nms.abstraction.mainhand;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public abstract class AbstractMainHand {
    public final @Nullable ItemStack getItemInMainHand(@NotNull Player player) {
        return this.getItemInMainHand(player.getInventory());
    }
    public abstract @Nullable ItemStack getItemInMainHand(@NotNull PlayerInventory inventory);
}
