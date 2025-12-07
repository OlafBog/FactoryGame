package io.github.FactoryGame.InfiniteWorldGen;

import com.badlogic.gdx.graphics.Color;

public class BiomePalette {
    public Color surface; // Kolor powierzchni (np. Trawa/Piasek/Śnieg)
    public Color subsoil; // Kolor pod spodem (np. Ziemia/Piaskowiec)

    public BiomePalette(Color surface, Color subsoil) {
        this.surface = surface;
        this.subsoil = subsoil;
    }
}
