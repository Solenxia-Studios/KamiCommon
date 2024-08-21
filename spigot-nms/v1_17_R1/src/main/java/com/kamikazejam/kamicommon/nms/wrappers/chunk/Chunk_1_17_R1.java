package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_17_R1 implements NMSChunk {
    private final @NotNull LevelChunk chunk;
    public Chunk_1_17_R1(@NotNull LevelChunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public @NotNull NMSChunkSection getSection(final int y) {
        return new ChunkSection_1_17_R1(this.chunk.getSections()[y]);
    }

    @Override
    public @NotNull NMSChunkSection getOrCreateSection(final int y) {
        if (this.chunk.getSections()[y] == null) {
            LevelChunkSection chunkSection = new LevelChunkSection(y << 4, this.chunk, this.chunk.level, true);
            this.chunk.getSections()[y] = chunkSection;
        }
        return new ChunkSection_1_17_R1(this.chunk.getSections()[y]);
    }

    @Override
    public void clearTileEntities() {
        this.chunk.blockEntities.clear();
    }
}