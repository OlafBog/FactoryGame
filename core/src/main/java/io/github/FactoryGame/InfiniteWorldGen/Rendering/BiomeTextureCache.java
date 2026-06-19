package io.github.FactoryGame.InfiniteWorldGen.Rendering;

import com.badlogic.gdx.graphics.Texture;
import com.mazatech.gdx.SVGAssetsGDX;
import io.github.FactoryGame.InfiniteWorldGen.BiomeType;

import java.util.HashMap;
import java.util.Map;

public class BiomeTextureCache {
    private Map<String, Texture> textureCache = new HashMap<>();
    private SVGAssetsGDX svg;
    private int tileSize;

    public BiomeTextureCache(SVGAssetsGDX svg, int tileSize) {
        this.svg = svg;
        this.tileSize = tileSize;
    }

    // Gets: "lushJungle/base/2" returns lushJungle base2.svg
    public Texture getTexture(BiomeType biome, String layer, int variation) {
        String key = biome.name() + "/" + layer + "/" + variation;

        Texture tex = textureCache.get(key);
        if (tex != null) return tex;

        // Map biome to folder name
        String biomePath = biomeToFolderPath(biome);
        
        // Handle different layer types (base/decor vs objects)
        String folderName = layer;
        String filePrefix = layer;
        
        // Map object types: "boulder" → "objectBoulder" (folder), "boulder" (file prefix)
        if (isObjectType(layer)) {
            folderName = "object" + capitalizeFirst(layer);  // "boulder" → "objectBoulder"
            filePrefix = layer;  // "boulder" stays "boulder" for filename
        }
        
        String assetPath = String.format("terrain/%s/%s/%s%d.svg",
            biomePath, folderName, filePrefix, variation);

        tex = svg.createTexture(assetPath, tileSize, tileSize);
        // Use nearest-neighbor filtering for pixel-perfect rendering
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        textureCache.put(key, tex);
        return tex;
    }

    private boolean isObjectType(String layer) {
        return layer.equals("boulder") || layer.equals("bush") || layer.equals("tree");
    }

    private String capitalizeFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private String biomeToFolderPath(BiomeType biome) {
        // Map all 9 biomes to terrain folders
        switch(biome) {
            // Cold (T0) biomes
            case H0T0: return "crackedPermafrost";      // Cold & Dry
            case H1T0: return "sporeForest";    // Cold & Medium
            case H2T0: return "frozenGrassland";           // Cold & Wet
            
            // Temperate (T1) biomes
            case H0T1: return "dryPlains";            // Temperate & Dry
            case H1T1: return "mossHills";            // Temperate & Medium
            case H2T1: return "mushroomGrove";          // Temperate & Wet
            
            // Hot (T2) biomes
            case H0T2: return "basaltDesert";         // Hot & Dry
            case H1T2: return "bushySteps";        // Hot & Medium
            case H2T2: return "lushJungle";           // Hot & Wet
            
            default: return "dryPlains";
        }
    }

    public void dispose() {
        for (Texture tex : textureCache.values()) {
            tex.dispose();
        }
        textureCache.clear();
    }
}
