package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.server.level.ServerChunkCache;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChunkProvider_1_18_R2 implements NMSChunkProvider {
    private final @NotNull ServerChunkCache handle;
    public ChunkProvider_1_18_R2(@NotNull ServerChunkCache server) {
        this.handle = server;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.handle;
    }

    @Override
    public @NotNull NMSChunk getOrCreateChunk(int x, int z) {
        return new Chunk_1_18_R2(Objects.requireNonNull(this.handle.getChunkAtMainThread(x, z)));
    }

    @Override
    public void saveChunk(@NotNull NMSChunk chunk) {
    }

    @Override
    public @NotNull NMSChunk wrap(org.bukkit.@NotNull Chunk chunk) {
        return new Chunk_1_18_R2(((CraftChunk) chunk).getHandle());
    }
}
