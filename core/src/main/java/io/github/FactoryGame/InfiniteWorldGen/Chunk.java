package io.github.FactoryGame.InfiniteWorldGen;

public class Chunk {
    public static final int SIZE = 32;
    private static final int AREA = SIZE * SIZE;
    public final int chunkX, chunkY;

    private byte[] currentDigDepth;
    private byte[] maxDepth;
    private byte[] biomeId;
    private byte[] object;
    private byte[] hiddenResource;
    private byte[] resourceLayerTarget;

    private byte[] baseVariation;    // Which base tile (1-4)
    private byte[] decorVariation;   // Which decor overlay (0-8)
    private byte[] objectVariation;  // Which object variant (0-4)
    private byte[] patternType;      // Terrain pattern (0=smooth, 1=medium, 2=rough, 3=sharp)

    public Chunk(int x, int y) {
        this.chunkX = x;
        this.chunkY = y;
        this.currentDigDepth = new byte[AREA];
        this.maxDepth = new byte[AREA];
        this.biomeId = new byte[AREA];
        this.object = new byte[AREA];
        this.hiddenResource = new byte[AREA];
        this.resourceLayerTarget = new byte[AREA];
        this.baseVariation = new byte[AREA];
        this.decorVariation = new byte[AREA];
        this.objectVariation = new byte[AREA];
        this.patternType = new byte[AREA];
    }

    private int getIndex(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) return -1;
        return y * SIZE + x;
    }

    public void setBaseVariation(int x, int y, int variation) {
        int idx = getIndex(x, y);
        if (idx != -1) baseVariation[idx] = (byte) variation;
    }

    public void setDecorVariation(int x, int y, int variation) {
        int idx = getIndex(x, y);
        if (idx != -1) decorVariation[idx] = (byte) variation;
    }

    public void setObjectVariation(int x, int y, int variation) {
        int idx = getIndex(x, y);
        if (idx != -1) objectVariation[idx] = (byte) variation;
    }

    public void setPatternType(int x, int y, TerrainPatternType pattern) {
        int idx = getIndex(x, y);
        if (idx != -1) patternType[idx] = (byte) pattern.ordinal();
    }

    public void setMaxDepth(int x, int y, int depth) {
        int idx = getIndex(x, y);
        if (idx != -1) maxDepth[idx] = (byte) depth;
    }

    public void setBiome(int x, int y, BiomeType biome) {
        int idx = getIndex(x, y);
        if (idx != -1) biomeId[idx] = (byte) biome.ordinal();
    }

    public void setObject(int x, int y, Object object) {
        int idx = getIndex(x, y);
        if (idx != -1) this.object[idx] = (byte) object.ordinal();
    }

    

    public void setResource(int x, int y, ResourceType type, int depthIdx) {
        int idx = getIndex(x, y);
        if (idx != -1) {
            hiddenResource[idx] = (byte) (type.ordinal() + 1); // +1 so 0 means "no resource"
            resourceLayerTarget[idx] = (byte) depthIdx;
        }
    }

    public int getBaseVariation(int x, int y) {
        int idx = getIndex(x, y);
        if (idx == -1) return 0;
        return baseVariation[idx];
    }

    public int getDecorVariation(int x, int y) {
        int idx = getIndex(x, y);
        if (idx == -1) return 0;
        return decorVariation[idx];
    }

    public int getObjectVariation(int x, int y) {
        int idx = getIndex(x, y);
        if (idx == -1) return 0;
        return objectVariation[idx];
    }

    public TerrainPatternType getPatternType(int x, int y) {
        int idx = getIndex(x, y);
        if (idx == -1) return TerrainPatternType.SMOOTH;
        return TerrainPatternType.values()[patternType[idx]];
    }

    public BiomeType getBiome(int x, int y) {
        int idx = getIndex(x, y);
        if (idx == -1) return BiomeType.H1T1;
        return BiomeType.values()[biomeId[idx]];
    }

    public Object getObject(int x, int y) {
        int idx = getIndex(x, y);
        if (idx == -1) return null;
        return Object.values()[this.object[idx]];
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
        byte res = hiddenResource[idx];
        byte target = resourceLayerTarget[idx];
        if (res == 0 || target == 0) return null;
        if (currentDigDepth[idx] >= target) {
            return ResourceType.values()[res - 1]; // -1 because we stored ordinal+1
        }
        return null;
    }
}
