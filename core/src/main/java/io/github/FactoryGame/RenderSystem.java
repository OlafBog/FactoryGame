package io.github.FactoryGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.FactoryGame.InfiniteWorldGen.*;
import io.github.FactoryGame.InfiniteWorldGen.Object;
import io.github.FactoryGame.InfiniteWorldGen.Rendering.BiomeTextureCache;
import com.badlogic.gdx.graphics.Texture;

import java.util.EnumMap;

public class RenderSystem {
    private SpriteBatch batch;
    private BiomeTextureCache textureCache;
    private TextureManager textureManager;
    private EnumMap<BiomeType, BiomePalette> biomeColors;
    private BitmapFont font;
    private int tileSize;

    public RenderSystem(SpriteBatch batch, BiomeTextureCache textureCache, TextureManager textureManager,
                        EnumMap<BiomeType, BiomePalette> biomeColors, BitmapFont font, int tileSize) {
        this.batch = batch;
        this.textureCache = textureCache;
        this.textureManager = textureManager;
        this.biomeColors = biomeColors;
        this.font = font;
        this.tileSize = tileSize;
    }

    public void renderTile(int x, int y, LayerType layer, BiomeType biome, World world,
                          boolean drawNumbers, Texture whiteTexture, HeatmapColorizer heatmapColorizer,
                          boolean debugTemperature, boolean debugHumidity) {
        float drawX = x * tileSize;
        float drawY = y * tileSize;

        // Debug heatmap rendering
        if (debugTemperature || debugHumidity) {
            float noiseValue = debugTemperature
                ? world.getTemperatureAt(x, y)
                : world.getHumidityAt(x, y);

            Color debugColor = heatmapColorizer.getColor(noiseValue);
            batch.setColor(debugColor);
            batch.draw(whiteTexture, drawX, drawY, tileSize, tileSize);
            batch.setColor(Color.WHITE);
            return;
        }

        // Normal game rendering
        if (layer == LayerType.LAYER1) {
            renderSurfaceLayer(x, y, biome, world);
        } else {
            renderUndergroundLayer(x, y, layer, biome);
        }

        // Render objects on LAYER1
        if (layer == LayerType.LAYER1) {
            renderObjects(x, y, biome, world);
        }

        // Render resources
        ResourceType res = world.getResourceAt(x, y);
        if (res == ResourceType.GOLD && layer == LayerType.LAYER1) {
            batch.setColor(Color.WHITE);
            batch.draw(textureManager.texGold, x * tileSize + 8, y * tileSize + 8);
        }

        // Debug layer depth numbers
        if (layer != LayerType.BEDROCK && layer != LayerType.AIR && drawNumbers) {
            int layersLeft = world.getLayersLeftAt(x, y);
            if (layersLeft <= 3) font.setColor(Color.RED);
            else font.setColor(Color.WHITE);
            font.draw(batch, String.valueOf(layersLeft), x * tileSize + 10, y * tileSize + 22);
        }

        batch.setColor(Color.WHITE);
    }

    private void renderSurfaceLayer(int x, int y, BiomeType biome, World world) {
        int baseVar = world.getBaseVariation(x, y);
        if (baseVar <= 0) baseVar = 1;

        // Draw base tile
        Texture baseTex = textureCache.getTexture(biome, "base", baseVar);
        if (baseTex != null) {
            batch.setColor(Color.WHITE);
            batch.draw(baseTex, x * tileSize, (y + 1) * tileSize, tileSize, -tileSize);
        }

        // Only draw decor if no object is present on this tile
        Object obj = world.getObjectAt(x, y);
        if (obj == null || obj == Object.NONE) {
            int decorVar = world.getDecorVariation(x, y);
            if (decorVar > 0) {
                Texture decorTex = textureCache.getTexture(biome, "decor", decorVar);
                if (decorTex != null) {
                    batch.draw(decorTex, x * tileSize, (y + 1) * tileSize, tileSize, -tileSize);
                }
            }
        }
    }

    private void renderUndergroundLayer(int x, int y, LayerType layer, BiomeType biome) {
        Texture tex = textureManager.getTextureForLayerAndBiome(layer, biome);
        if (tex != null) {
            BiomePalette palette = biomeColors.getOrDefault(biome, biomeColors.get(BiomeType.H1T1));
            batch.setColor(layer == LayerType.LAYER2 ? palette.subsoil : Color.WHITE);
            batch.draw(tex, x * tileSize, y * tileSize);
        }
    }

    private void renderObjects(int x, int y, BiomeType biome, World world) {
        Object obj = world.getObjectAt(x, y);
        if (obj == null || obj == Object.NONE) return;

        int objectVar = world.getObjectVariation(x, y);
        if (objectVar <= 0) objectVar = 1;

        String objectType = obj.toString().toLowerCase();
        Texture objTex = textureCache.getTexture(biome, objectType, objectVar);

        if (objTex != null) {
            batch.setColor(Color.WHITE);
            batch.draw(objTex, x * tileSize, (y + 1) * tileSize, tileSize, -tileSize);
        }
    }
}
