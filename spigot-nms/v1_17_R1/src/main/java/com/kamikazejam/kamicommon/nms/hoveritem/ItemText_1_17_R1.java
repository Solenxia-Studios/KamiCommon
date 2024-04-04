package com.kamikazejam.kamicommon.nms.hoveritem;

import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractItemText;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemText_1_17_R1 implements AbstractItemText {
    @Override
    public BaseComponent[] getComponents(ItemStack item) {
        net.minecraft.world.item.ItemStack v1_17_R1Stack = CraftItemStack.asNMSCopy(item);
        if (v1_17_R1Stack == null) { return TextComponent.fromLegacyText(""); }
        return new BaseComponent[]{ new TextComponent(v1_17_R1Stack.save(new CompoundTag()).toString()) };
    }

    @Override
    public String getNbtStringTooltip(ItemStack item) {
        net.minecraft.world.item.ItemStack v1_17_R1Stack = CraftItemStack.asNMSCopy(item);
        if (v1_17_R1Stack == null) { return ""; }
        return v1_17_R1Stack.save(new CompoundTag()).toString();
    }
}