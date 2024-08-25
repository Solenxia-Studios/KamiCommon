package com.kamikazejam.kamicommon.nms.wrappers.packet.impl;

import com.kamikazejam.kamicommon.nms.reflection.FieldHandle;
import com.kamikazejam.kamicommon.nms.reflection.FieldHandles;
import net.minecraft.server.v1_8_R2.PacketPlayOutEntityDestroy;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public class OutEntityDestroy_1_8_R2 implements NMSOutEntityDestroy {
    private static final FieldHandle handle = FieldHandles.getHandle("a", PacketPlayOutEntityDestroy.class);

    private final @NotNull PacketPlayOutEntityDestroy packet;

    public OutEntityDestroy_1_8_R2(@NotNull PacketPlayOutEntityDestroy packet) {
        this.packet = packet;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.packet;
    }

    @Override
    public int[] getToDestroy() {
        return (int[]) OutEntityDestroy_1_8_R2.handle.get(this.packet);
    }
}