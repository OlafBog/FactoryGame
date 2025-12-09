package io.github.FactoryGame.InfiniteWorldGen;

public class Chunk {
    public static final int SIZE = 32;
    private static final int AREA = SIZE * SIZE;
    public final int chunkX, chunkY;

    private byte[] currentDigDepth;
    private byte[] maxDepth;
    private byte[] biomeId;
    private byte[] hiddenResource;
    private byte[] resourceLayerTarget;

    public Chunk(int x, int y) {
        this.chunkX = x;
        this.chunkY = y;
        this.currentDigDepth = new byte[AREA];
        this.maxDepth = new byte[AREA];
        this.biomeId = new byte[AREA];
        this.hiddenResource = new byte[AREA];
        this.resourceLayerTarget = new byte[AREA];
    }

    private int getIndex(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) return -1;
        return y * SIZE + x;
    }

    public void setMaxDepth(int x, int y, int depth) {
        int idx = getIndex(x, y);
        if (idx != -1) maxDepth[idx] = (byte) depth;
    }

    public void setBiome(int x, int y, BiomeType biome) {
        int idx = getIndex(x, y);
        if (idx != -1) biomeId[idx] = (byte) biome.ordinal();
    }

    /*
    public void setResource(int x, int y, ResourceType type, int depthIdx) {
        int idx = getIndex(x, y);
        if (idx != -1) {
            hiddenResource[idx] = (byte) type.ordinal();
            resourceLayerTarget[idx] = (byte) depthIdx;
        }
    }
     */

    public BiomeType getBiome(int x, int y) {
        int idx = getIndex(x, y);
        if (idx == -1) return BiomeType.H1T1;
        return BiomeType.values()[biomeId[idx]];
    }

    public LayerType getVisibleLayer(int x, int y) {
        int idx = getIndex(x, y);
        if (idx == -1) return LayerType.AIR;

        int current = currentDigDepth[idx];
        int max = maxDepth[idx];

        // 1. Dno
        if (current >= max) return LayerType.BEDROCK;

        // 2. Powierzchnia
        if (current == 0) return LayerType.LAYER1;

        // 3. WARSTWY POŚREDNIE
        // current to nasza pozycja (np. 5). max to dno (np. 20).
        float diggingProgress = (float) (current - 4) / (max-1);

        if (current <= 2) return LayerType.LAYER2;
        if (current <= 4) return LayerType.LAYER3;
        if (diggingProgress <= 0.25f) return LayerType.LAYER4;
        if (diggingProgress <= 0.6f) return LayerType.LAYER5;
        return LayerType.LAYER6;
    }

    public int getLayersLeft(int x, int y) {
        int idx = getIndex(x, y);
        if (idx == -1) return 0;
        return maxDepth[idx] - currentDigDepth[idx];
    }

    public void dig(int x, int y) {
        int idx = getIndex(x, y);
        if (idx == -1) return;
        if (currentDigDepth[idx] < maxDepth[idx]) {
            currentDigDepth[idx]++;
        }
    }

    public ResourceType getVisibleResource(int x, int y) {
        int idx = getIndex(x, y);
        if (idx == -1) return null;
        if (hiddenResource[idx] == 0) return null;
        if (currentDigDepth[idx] >= resourceLayerTarget[idx]) {
            return ResourceType.values()[hiddenResource[idx]];
        }
        return null;
    }
}
