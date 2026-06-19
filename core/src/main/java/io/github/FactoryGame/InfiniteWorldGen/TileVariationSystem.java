package io.github.FactoryGame.InfiniteWorldGen;

public class TileVariationSystem {
    private FastNoiseLite baseVariationNoise;
    private FastNoiseLite decorVariationNoise;
    private FastNoiseLite objectNoise;
    private FastNoiseLite patternNoise;

    public TileVariationSystem(long seed) {
        // Each noise generator for different aspects
        baseVariationNoise = new FastNoiseLite((int)(seed + 1001));
        baseVariationNoise.SetFrequency(0.2f); // Spread out variations

        decorVariationNoise = new FastNoiseLite((int)(seed + 1002));
        decorVariationNoise.SetFrequency(0.3f); // Decor changes more often

        objectNoise = new FastNoiseLite((int)(seed + 1003));
        objectNoise.SetFrequency(0.15f);

        patternNoise = new FastNoiseLite((int)(seed + 1004));
        patternNoise.SetFrequency(0.1f);
    }

    // Returns which base tile to use (1-4)
    public int getBaseVariation(int worldX, int worldY) {
        float noise = baseVariationNoise.GetNoise(worldX, worldY);
        return Math.floorMod((int)(noise * 100), 4) + 1; // Returns 1-4
    }

    // Returns which decor overlay to use (1-8, or 0 for none)
    public int getDecorVariation(int worldX, int worldY) {
        float noise = decorVariationNoise.GetNoise(worldX, worldY);
        if (noise < -0.5f) return 0; // 30% chance of no decor
        return Math.floorMod((int)(noise * 100), 8) + 1; // Returns 1-8
    }

    // Returns which object type (1-4, or 0 for none)
    public int getObjectVariation(int worldX, int worldY) {
        float noise = objectNoise.GetNoise(worldX, worldY);
        if (noise < -0.6f) return 0; // Object placement probability
        return Math.floorMod((int)(noise * 100), 4) + 1;
    }

    // Returns terrain pattern type (smooth/rough/sharp/medium)
    public TerrainPatternType getTerrainPattern(int worldX, int worldY) {
        float noise = patternNoise.GetNoise(worldX, worldY);
        if (noise < -0.5f) return TerrainPatternType.SMOOTH;
        if (noise < 0f) return TerrainPatternType.MEDIUM;
        if (noise < 0.5f) return TerrainPatternType.ROUGH;
        return TerrainPatternType.SHARP;
    }
}
