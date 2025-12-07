package io.github.FactoryGame.InfiniteWorldGen;

import java.util.HashMap;
import java.util.Map;

public class World {
    private final int CHUNK_SIZE = Chunk.SIZE;
    private Map<Long, Chunk> chunks = new HashMap<>();
    private ChunkGenerator chunkGenerator;

    public World(long seed) {
        this.chunkGenerator = new ChunkGenerator(seed);
    }

    public LayerType getLayerAt(int worldX, int worldY) {
        Chunk chunk = getOrGenerateChunk(worldX, worldY);
        int localX = Math.floorMod(worldX, CHUNK_SIZE);
        int localY = Math.floorMod(worldY, CHUNK_SIZE);
        return chunk.getVisibleLayer(localX, localY);
    }

    public BiomeType getBiomeAt(int worldX, int worldY) {
        Chunk chunk = getOrGenerateChunk(worldX, worldY);
        int localX = Math.floorMod(worldX, CHUNK_SIZE);
        int localY = Math.floorMod(worldY, CHUNK_SIZE);
        return chunk.getBiome(localX, localY);
    }

    private Chunk getOrGenerateChunk(int worldX, int worldY) {
        int chunkX = Math.floorDiv(worldX, CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldY, CHUNK_SIZE);
        long key = getChunkKey(chunkX, chunkY);

        Chunk chunk = chunks.get(key);
        if (chunk == null) {
            chunk = new Chunk(chunkX, chunkY);
            chunkGenerator.fillChunk(chunk, chunkX, chunkY);
            chunks.put(key, chunk);
        }
        return chunk;
    }

    public ResourceType getResourceAt(int worldX, int worldY) {
        Chunk chunk = getOrGenerateChunk(worldX, worldY);
        int localX = Math.floorMod(worldX, CHUNK_SIZE);
        int localY = Math.floorMod(worldY, CHUNK_SIZE);
        return chunk.getVisibleResource(localX, localY);
    }

    public void digAt(int worldX, int worldY) {
        Chunk chunk = getOrGenerateChunk(worldX, worldY);
        int localX = Math.floorMod(worldX, CHUNK_SIZE);
        int localY = Math.floorMod(worldY, CHUNK_SIZE);
        chunk.dig(localX, localY);
    }

    public int getLayersLeftAt(int worldX, int worldY) {
        int chunkX = Math.floorDiv(worldX, CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldY, CHUNK_SIZE);
        Chunk chunk = chunks.get(getChunkKey(chunkX, chunkY));
        if (chunk == null) return 0;

        int localX = Math.floorMod(worldX, CHUNK_SIZE);
        int localY = Math.floorMod(worldY, CHUNK_SIZE);
        return chunk.getLayersLeft(localX, localY);
    }

    private long getChunkKey(int x, int y) {
        return (long)x << 32 | (y & 0xFFFFFFFFL);
    }
}
