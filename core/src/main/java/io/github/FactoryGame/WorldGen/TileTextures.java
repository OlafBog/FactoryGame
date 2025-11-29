package io.github.FactoryGame.WorldGen;

import com.badlogic.gdx.graphics.Texture;

public class TileTextures {
    public static Texture GRASS;
    public static Texture DIRT;
    public static Texture STONE;

    public static void load() {
        GRASS = new Texture("grass.png");
        DIRT = new Texture("dirt.png");
        STONE = new Texture("stone.png");
    }
    public static void dispose() {
        GRASS.dispose();
        DIRT.dispose();
        STONE.dispose();
    }
}
