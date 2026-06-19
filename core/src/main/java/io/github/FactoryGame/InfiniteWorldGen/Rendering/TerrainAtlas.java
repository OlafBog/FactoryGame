package io.github.FactoryGame.InfiniteWorldGen.Rendering;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.FactoryGame.InfiniteWorldGen.BiomeType;

import java.util.HashMap;
import java.util.Map;

public class TerrainAtlas {
    private Texture atlasTexture;
    private Map<String, TextureRegion> regions = new HashMap<>();

    // Pre-load: terrain/lushJungle/base/base1.svg → atlas slot (0, 0)
    // Pre-load: terrain/lushJungle/base/base2.svg → atlas slot (32, 0)
    // etc.

    public TextureRegion getRegion(BiomeType biome, String layer, int variation) {
        String key = biome + "/" + layer + "/" + variation;
        return regions.get(key);
    }
}
