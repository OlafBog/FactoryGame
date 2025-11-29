package io.github.FactoryGame.WorldGen;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class WorldGenerator {

    private static final long SEED = MathUtils.random(100000);
    private static final NoiseGenerator noiseGen = new NoiseGenerator(SEED);

    public static void generate(World world) {

        float scale = .05f;      // "Zoom" mapy. Mniejsze = większe kontynenty.
        int octaves = 4;          // Ile warstw szczegółów (4 to dobry standard)
        float persistence = 0.5f; // Jak bardzo "wypływają" mniejsze szczegóły
        float lacunarity = 2.0f;  // Jak szybko zagęszczają się szczegóły

        for (int i = 0; i < world.width; i++){
            for (int j = 0; j < world.height; j++) {

                double height = noiseGen.getOctaveNoise(
                    i * scale,
                    j * scale,
                    octaves,
                    persistence,
                    lacunarity
                );
                if (height < 0.0) height = 0.0;
                if (height > 1.0) height = 1.0;

                if (height > .66f)
                    world.tiles[i][j] = new Tile(TileType.STONE);
                else if (height > .33f)
                    world.tiles[i][j] = new Tile(TileType.DIRT);
                else
                    world.tiles[i][j] = new Tile(TileType.GRASS);

            }
        }
    }
}
