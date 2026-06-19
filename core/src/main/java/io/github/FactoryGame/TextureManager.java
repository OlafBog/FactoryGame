package io.github.FactoryGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.mazatech.gdx.SVGAssetsGDX;
import io.github.FactoryGame.InfiniteWorldGen.BiomeType;
import io.github.FactoryGame.InfiniteWorldGen.LayerType;

import io.github.FactoryGame.InfiniteWorldGen.ResourceType;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class TextureManager {
    private SVGAssetsGDX svg;
    private int tileSize;

    // Layer 1 textures
    public EnumMap<BiomeType, Texture> layer1Textures;

    // Underground layer textures
    public EnumMap<LayerType, Texture> lowerLayersTextures;

    // Resource icons
    public Map<ResourceType, Texture> resourceIcons;
    // Tool icons
    public Map<ToolType, Texture> toolIcons;

    // Individual textures
    public Texture texDirt;
    public Texture texDarkSoil;
    public Texture texStone;
    public Texture texDeepStone;
    public Texture texHardStone;
    public Texture texBedrock;
    public Texture texGold;

    public TextureManager(SVGAssetsGDX svg, int tileSize) {
        this.svg = svg;
        this.tileSize = tileSize;
        this.layer1Textures = new EnumMap<>(BiomeType.class);
        this.lowerLayersTextures = new EnumMap<>(LayerType.class);
        this.resourceIcons = new HashMap<>();
        this.toolIcons = new HashMap<>();
    }

    public void loadAll() {
        loadTextures();
        generateDebugTextures();
        generateToolIcons();
    }

    private void loadTextures() {
        Texture tex;

        tex = svg.createTexture("bases/h0/h0t0.svg", tileSize, tileSize);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        layer1Textures.put(BiomeType.H0T0, tex);

        tex = svg.createTexture("bases/h0/h0t1.svg", tileSize, tileSize);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        layer1Textures.put(BiomeType.H0T1, tex);

        tex = svg.createTexture("bases/h0/h0t2.svg", tileSize, tileSize);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        layer1Textures.put(BiomeType.H0T2, tex);

        tex = svg.createTexture("bases/h1/h1t0.svg", tileSize, tileSize);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        layer1Textures.put(BiomeType.H1T0, tex);

        tex = svg.createTexture("bases/h1/h1t1.svg", tileSize, tileSize);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        layer1Textures.put(BiomeType.H1T1, tex);

        tex = svg.createTexture("bases/h1/h1t2.svg", tileSize, tileSize);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        layer1Textures.put(BiomeType.H1T2, tex);

        tex = svg.createTexture("bases/h2/h2t0.svg", tileSize, tileSize);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        layer1Textures.put(BiomeType.H2T0, tex);

        tex = svg.createTexture("bases/h2/h2t1.svg", tileSize, tileSize);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        layer1Textures.put(BiomeType.H2T1, tex);

        tex = svg.createTexture("bases/h2/h2t2.svg", tileSize, tileSize);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        layer1Textures.put(BiomeType.H2T2, tex);

        lowerLayersTextures.put(LayerType.LAYER1, layer1Textures.get(BiomeType.H1T1));
    }

    private void generateDebugTextures() {
        texDirt = createSolidTexture(new Color(0.5f, 0.3f, 0.1f, 1));
        texDarkSoil = createSolidTexture(new Color(0.3f, 0.2f, 0.05f, 1));
        texStone = createSolidTexture(new Color(0.6f, 0.6f, 0.6f, 1));
        texDeepStone = createSolidTexture(new Color(0.4f, 0.4f, 0.4f, 1));
        texHardStone = createSolidTexture(new Color(0.25f, 0.25f, 0.3f, 1));
        texBedrock = createSolidTexture(new Color(0.1f, 0.1f, 0.1f, 1));
        texGold = createSolidTexture(new Color(1f, 0.9f, 0.1f, 1), 16, 16);

        // Resource icons (8x8 colored squares)
        resourceIcons.put(ResourceType.COAL, createSolidTexture(new Color(0.2f, 0.2f, 0.2f, 1), 8, 8));
        resourceIcons.put(ResourceType.IRON, createSolidTexture(new Color(0.8f, 0.6f, 0.4f, 1), 8, 8));
        resourceIcons.put(ResourceType.COPPER, createSolidTexture(new Color(0.9f, 0.5f, 0.2f, 1), 8, 8));
        resourceIcons.put(ResourceType.GOLD, createSolidTexture(new Color(1f, 0.9f, 0.1f, 1), 8, 8));
        resourceIcons.put(ResourceType.QUARTZ, createSolidTexture(new Color(0.9f, 0.9f, 1f, 1), 8, 8));
        resourceIcons.put(ResourceType.LIMESTONE, createSolidTexture(new Color(0.8f, 0.8f, 0.6f, 1), 8, 8));
        resourceIcons.put(ResourceType.LITHIUM, createSolidTexture(new Color(0.6f, 0.8f, 0.6f, 1), 8, 8));
        resourceIcons.put(ResourceType.BAUXITE, createSolidTexture(new Color(0.7f, 0.3f, 0.3f, 1), 8, 8));
        resourceIcons.put(ResourceType.SULFUR, createSolidTexture(new Color(0.9f, 0.9f, 0.2f, 1), 8, 8));
        resourceIcons.put(ResourceType.DIAMOND, createSolidTexture(new Color(0.3f, 0.8f, 1f, 1), 8, 8));
        resourceIcons.put(ResourceType.RUBY, createSolidTexture(new Color(1f, 0.2f, 0.2f, 1), 8, 8));
        resourceIcons.put(ResourceType.URANIUM, createSolidTexture(new Color(0.2f, 1f, 0.2f, 1), 8, 8));
        resourceIcons.put(ResourceType.TITANIUM, createSolidTexture(new Color(0.6f, 0.6f, 0.8f, 1), 8, 8));

        lowerLayersTextures.put(LayerType.LAYER2, texDirt);
        lowerLayersTextures.put(LayerType.LAYER3, texDarkSoil);
        lowerLayersTextures.put(LayerType.LAYER4, texStone);
        lowerLayersTextures.put(LayerType.LAYER5, texDeepStone);
        lowerLayersTextures.put(LayerType.LAYER6, texHardStone);
        lowerLayersTextures.put(LayerType.BEDROCK, texBedrock);
    }

    private void generateToolIcons() {
        final int S = 12;
        Pixmap pixmap;

        // HAND: small circle (fist)
        pixmap = new Pixmap(S, S, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1f, 0.8f, 0.6f, 1f)); // skin color
        pixmap.fillCircle(S / 2, S / 2, S / 2 - 1);
        toolIcons.put(ToolType.HAND, new Texture(pixmap));
        pixmap.dispose();

        // SHOVEL: triangle (blade shape)
        pixmap = new Pixmap(S, S, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.6f, 0.5f, 0.3f, 1f)); // brown
        pixmap.fillTriangle(1, S-1, S/2, 2, S-1, S-1);
        pixmap.fillRectangle(S/2 - 1, 0, 2, 4); // handle
        toolIcons.put(ToolType.SHOVEL, new Texture(pixmap));
        pixmap.dispose();

        // PICKAXE: arrow shape
        pixmap = new Pixmap(S, S, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.5f, 0.5f, 0.5f, 1f)); // gray
        pixmap.fillTriangle(2, S/2, S-1, S/4, S-1, 3*S/4); // pick head
        pixmap.fillRectangle(S/2 - 2, S/2 - 1, 6, 2); // handle
        toolIcons.put(ToolType.PICKAXE, new Texture(pixmap));
        pixmap.dispose();

        // DRILL: rectangle with lines
        pixmap = new Pixmap(S, S, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.7f, 0.7f, 0.9f, 1f)); // silver
        pixmap.fillRectangle(2, 1, S-4, S-2);
        pixmap.setColor(new Color(0.4f, 0.4f, 0.6f, 1f)); // darker
        pixmap.fillRectangle(2, 2, S-4, 2);
        pixmap.fillRectangle(2, 6, S-4, 2);
        pixmap.fillRectangle(2, S-4, S-4, 2);
        toolIcons.put(ToolType.DRILL, new Texture(pixmap));
        pixmap.dispose();

        // Set nearest-neighbor filtering on all tool icons
        for (Texture tex : toolIcons.values()) {
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    private Texture createSolidTexture(Color color, int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        return tex;
    }

    private Texture createSolidTexture(Color color) {
        return createSolidTexture(color, tileSize, tileSize);
    }

    public Texture getTextureForLayerAndBiome(LayerType layer, BiomeType biomeType) {
        if (layer == null) return null;

        if (layer == LayerType.LAYER1) {
            Texture t = layer1Textures.get(biomeType);
            return (t != null) ? t : lowerLayersTextures.get(LayerType.LAYER1);
        }
        return lowerLayersTextures.get(layer);
    }

    public void dispose() {
        Set<Texture> textures = Collections.newSetFromMap(new IdentityHashMap<>());

        if (lowerLayersTextures != null) textures.addAll(lowerLayersTextures.values());
        if (layer1Textures != null) textures.addAll(layer1Textures.values());

        textures.add(texGold);
        textures.add(texDirt);
        textures.add(texDarkSoil);
        textures.add(texStone);
        textures.add(texDeepStone);
        textures.add(texHardStone);
        textures.add(texBedrock);

        if (resourceIcons != null) textures.addAll(resourceIcons.values());
        if (toolIcons != null) textures.addAll(toolIcons.values());

        for (Texture t : textures) {
            if (t != null) t.dispose();
        }
    }
}
