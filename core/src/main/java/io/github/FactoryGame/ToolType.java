package io.github.FactoryGame;

public enum ToolType {
    HAND(1, "Hand"),
    SHOVEL(3, "Shovel"),
    PICKAXE(6, "Pickaxe"),
    DRILL(12, "Drill");

    public final int power; // layers dug per click
    public final String label;

    ToolType(int power, String label) {
        this.power = power;
        this.label = label;
    }
}