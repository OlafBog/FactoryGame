package io.github.FactoryGame.WorldGen;

public class Tile {
    //private Building building;
    //private TileType[] layers;
    //private ResourceNode resourceNode;

    private TileType tileType;

    public Tile(TileType tileType) {
        this.tileType = tileType;
    }

    public TileType getTileType(){
        return this.tileType;
    }
}
