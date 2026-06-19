package io.github.FactoryGame;

import com.badlogic.gdx.graphics.Color;
import io.github.FactoryGame.InfiniteWorldGen.ChunkGenerator;

public class HeatmapColorizer {
    private static final Color PURE_BLUE = new Color(0f, 0f, 1f, 1f);
    private static final Color BLUE_STRONG_GREEN = new Color(0f, 0.8f, 0.9f, 1f);
    private static final Color GREEN_STRONG_BLUE = new Color(0f, 1f, 0.9f, 1f);
    private static final Color PURE_GREEN = new Color(0f, 1f, 0f, 1f);
    private static final Color GREEN_STRONG_RED = new Color(0.8f, 0.9f, 0f, 1f);
    private static final Color RED_STRONG_GREEN = new Color(0.9f, 0.8f, 0f, 1f);
    private static final Color PURE_RED = new Color(1f, 0f, 0f, 1f);

    private static final float THRESHOLD = ChunkGenerator.getBiomeDivider();
    private final Color tempColor = new Color();

    public Color getColor(float value) {
        float val = Math.max(-1f, Math.min(1f, value));

        Color startColor, endColor;
        float rangeMin, rangeMax;

        if (val < -THRESHOLD) {
            startColor = PURE_BLUE;
            endColor = BLUE_STRONG_GREEN;
            rangeMin = -1.0f;
            rangeMax = -THRESHOLD;
        } else if (val < 0) {
            startColor = GREEN_STRONG_BLUE;
            endColor = PURE_GREEN;
            rangeMin = -THRESHOLD;
            rangeMax = 0.0f;
        } else if (val < THRESHOLD) {
            startColor = PURE_GREEN;
            endColor = GREEN_STRONG_RED;
            rangeMin = 0.0f;
            rangeMax = THRESHOLD;
        } else {
            startColor = RED_STRONG_GREEN;
            endColor = PURE_RED;
            rangeMin = THRESHOLD;
            rangeMax = 1.0f;
        }

        float ratio = (val - rangeMin) / (rangeMax - rangeMin);
        return tempColor.set(startColor).lerp(endColor, ratio);
    }
}
