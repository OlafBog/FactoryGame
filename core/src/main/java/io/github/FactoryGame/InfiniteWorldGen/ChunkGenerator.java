package io.github.FactoryGame.InfiniteWorldGen;

public class ChunkGenerator {
    private FastNoiseLite temperatureNoise;
    private FastNoiseLite humidityNoise;
    private FastNoiseLite heightNoise;
    private FastNoiseLite resourceNoise;
    private FastNoiseLite edgeNoiseBig;
    private FastNoiseLite edgeNoiseSmall;
    private FastNoiseLite decorationsRare;
    private FastNoiseLite decorationsMedium;
    private FastNoiseLite decorationsDense;

    private static float biomeSizeMod = 0.005f; //rozmiary biomowo
    private static float edgeNoiseStrength = 0.05f; //rodzielenie granic
    private static float biomeDivider = 0.12f; //roznorodnosc biomow

    public ChunkGenerator(long seed) {
        // 1. TEMPERATURA
        temperatureNoise = new FastNoiseLite((int)seed);
        temperatureNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        temperatureNoise.SetFrequency(biomeSizeMod);

        // 2. WILGOTNOŚĆ
        humidityNoise = new FastNoiseLite((int)seed + 1000);
        humidityNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        humidityNoise.SetFrequency(biomeSizeMod);

        // 3. WYSOKOŚĆ
        heightNoise = new FastNoiseLite((int)seed + 2000);
        heightNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        heightNoise.SetFrequency(0.1f);
        heightNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        heightNoise.SetFractalOctaves(3);

        // 4. SUROWCE
        resourceNoise = new FastNoiseLite((int)seed + 3000);
        resourceNoise.SetFrequency(0.1f);

        // 5. SZUM KRAWĘDZI (Jitter)
        edgeNoiseBig = new FastNoiseLite((int)seed + 4000);
        edgeNoiseBig.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        edgeNoiseBig.SetFrequency(0.05f);

        edgeNoiseSmall = new FastNoiseLite((int)seed + 4100);
        edgeNoiseSmall.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        // Częstotliwość szumu krawędzi - więcej - bardziej poszarpane
        edgeNoiseSmall.SetFrequency(0.3f);

        // 6. OZDOBY
        decorationsRare = new FastNoiseLite((int)seed + 5000);
        decorationsRare.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        decorationsRare.SetFrequency(0.02f);

        decorationsMedium = new FastNoiseLite((int)seed + 5100);
        decorationsMedium.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        decorationsMedium.SetFrequency(0.1f);

        decorationsDense = new FastNoiseLite((int)seed + 5200);
        decorationsDense.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        decorationsDense.SetFrequency(0.4f);
    }

    public void fillChunk(Chunk chunk, int chunkX, int chunkY) {
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                int globalX = chunkX * Chunk.SIZE + x;
                int globalY = chunkY * Chunk.SIZE + y;

                // --- 1. OBLICZANIE BIOMÓW ---

                // Wartości biomów
                float baseTemp = temperatureNoise.GetNoise(globalX, globalY);
                float baseHum = humidityNoise.GetNoise(globalX, globalY);

                // Pobieramy wartość zakłócenia (-1 do 1)
                float edgeVal = edgeNoiseBig.GetNoise(globalX, globalY)*5f/6f + edgeNoiseSmall.GetNoise(globalX, globalY)/6f;

                // Poszarpanie krawędzi - więcej - bardziej
                float distortion = edgeVal * edgeNoiseStrength;

                // Zakłucenie krawędzi
                float finalTemp = baseTemp + distortion;
                float finalHum = baseHum + distortion;

                BiomeType biome;

                // Decyzja T-temperatura H-wilgotność 2-wysoka 1-średnia 0-niska
                if (finalTemp > biomeDivider) {
                    if (finalHum > biomeDivider) biome = BiomeType.H2T2;
                    else if (finalHum > -biomeDivider) biome = BiomeType.H1T2;
                    else biome = BiomeType.H0T2;
                } else if (finalTemp > -biomeDivider){
                    if (finalHum > biomeDivider) biome = BiomeType.H2T1;
                    else if (finalHum > -biomeDivider) biome = BiomeType.H1T1;
                    else biome = BiomeType.H0T1;
                } else {
                    if (finalHum > biomeDivider) biome = BiomeType.H2T0;
                    else if (finalHum > -biomeDivider) biome = BiomeType.H1T0;
                    else biome = BiomeType.H0T0;
                }

                chunk.setBiome(x, y, biome);

                // --- 2. GŁĘBOKOŚĆ ---
                float heightVal = heightNoise.GetNoise(globalX, globalY);

                int minMapDepth = 15;
                int maxMapDepth = 30;
/*
                switch (biome) {
                    case H2T2:
                        minMapDepth = 20;
                        maxMapDepth = 50;
                        break;
                    case H2T1:
                        minMapDepth = 12;
                        maxMapDepth = 25;
                        break;
                    case H2T0:
                        minMapDepth = 10;
                        maxMapDepth = 20;
                        break;
                    case H1T2:
                        minMapDepth = 10;
                        maxMapDepth = 20;
                        break;
                    case H1T1:
                        minMapDepth = 10;
                        maxMapDepth = 20;
                        break;
                    case H1T0:
                        minMapDepth = 10;
                        maxMapDepth = 20;
                        break;
                    case H0T2:
                        minMapDepth = 10;
                        maxMapDepth = 20;
                        break;
                    case H0T1:
                        minMapDepth = 10;
                        maxMapDepth = 20;
                        break;
                    case H0T0:
                        minMapDepth = 10;
                        maxMapDepth = 20;
                        break;
                    default:
                        break;
                }
                */

                int depth = minMapDepth + Math.round(((heightVal + 1) / 2f) * (maxMapDepth - minMapDepth));
                chunk.setMaxDepth(x, y, depth);

                // --- 3. OZDOBY ---

                Object temp = Object.NONE;
                if(decorationsDense.GetNoise(globalX,globalY) < -0.99f) temp = Object.ROCK;
                if(decorationsMedium.GetNoise(globalX,globalY)+edgeNoiseSmall.GetNoise(globalX,globalY)/50 < -0.99f) temp = Object.BUSH;
                if(decorationsRare.GetNoise(globalX,globalY)+edgeNoiseSmall.GetNoise(globalX,globalY)/100 < -0.99f) temp = Object.TREE;

                chunk.setObject(x, y, temp);

                // --- 4. SUROWCE ---
                /*
                float resVal = resourceNoise.GetNoise(globalX, globalY);
                if (resVal > 0.8f && depth > 5) {
                    int resDepth = MathUtils.random(3, depth - 2);
                    chunk.setResource(x, y, ResourceType.GOLD, resDepth);
                }
                 */
            }
        }
    }

    public float getTemperature(int x, int y) {
        return temperatureNoise.GetNoise(x, y) + (edgeNoiseBig.GetNoise(x, y)*5/6 + edgeNoiseSmall.GetNoise(x, y)/6) * edgeNoiseStrength;
    }

    public float getHumidity(int x, int y) {
        return humidityNoise.GetNoise(x, y) + (edgeNoiseBig.GetNoise(x, y)*5/6 + edgeNoiseSmall.GetNoise(x, y)/6) * edgeNoiseStrength;
    }

    public static float getBiomeDivider() {
        return biomeDivider;
    }

}
