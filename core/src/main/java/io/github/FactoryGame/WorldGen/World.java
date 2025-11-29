package io.github.FactoryGame.WorldGen;


import com.badlogic.gdx.utils.Array;

public class World {
    public final int width;
    public final int height;

    public Tile[][] tiles;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
    }
}
