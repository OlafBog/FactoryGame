package io.github.FactoryGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DigEffect {
    private static class Effect {
        float worldX, worldY;
        float timer;
        float maxTimer;
        Color color;
        boolean isSparkle;
        boolean isToolSwing;
        Texture toolTexture;
        float startX, startY;

        Effect(float worldX, float worldY, float duration, Color color, boolean isSparkle) {
            this(worldX, worldY, duration, color, isSparkle, null, 0, 0);
        }

        Effect(float worldX, float worldY, float duration, Color color, boolean isSparkle,
               Texture toolTexture, float startX, float startY) {
            this.worldX = worldX;
            this.worldY = worldY;
            this.timer = duration;
            this.maxTimer = duration;
            this.color = color;
            this.isSparkle = isSparkle;
            this.isToolSwing = toolTexture != null;
            this.toolTexture = toolTexture;
            this.startX = startX;
            this.startY = startY;
        }
    }

    private List<Effect> effects = new ArrayList<>();
    private Texture whitePixel;

    public DigEffect() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
    }

    public void addDigEffect(int tileX, int tileY, int tileSize) {
        effects.add(new Effect(tileX * tileSize, tileY * tileSize, 0.2f, Color.WHITE, false));
    }

    public void addSparkleEffect(int tileX, int tileY, int tileSize) {
        effects.add(new Effect(tileX * tileSize + tileSize / 2f, tileY * tileSize + tileSize / 2f, 0.5f, Color.YELLOW, true));
    }

    public void addToolSwingEffect(int tileX, int tileY, int tileSize, Texture toolTexture) {
        float centerX = tileX * tileSize + tileSize / 2f;
        float centerY = tileY * tileSize + tileSize / 2f;
        effects.add(new Effect(centerX, centerY, 0.15f, Color.WHITE, false, toolTexture, centerX, centerY));
    }

    public void update(float delta) {
        Iterator<Effect> it = effects.iterator();
        while (it.hasNext()) {
            Effect e = it.next();
            e.timer -= delta;
            if (e.timer <= 0) it.remove();
        }
    }

    public void render(SpriteBatch batch, int tileSize) {
        for (Effect e : effects) {
            float alpha = Math.min(1f, e.timer / e.maxTimer);

            if (e.isToolSwing && e.toolTexture != null) {
                // Tool swing: moves from start position toward center, fades out
                float progress = 1f - alpha; // 0 to 1
                float offsetX = (e.startX - e.worldX) * (1f - progress) * 0.5f;
                float offsetY = (e.startY - e.worldY) * (1f - progress) * 0.5f;
                float drawX = e.worldX + offsetX - tileSize / 3f;
                float drawY = e.worldY + offsetY - tileSize / 3f;
                batch.setColor(1, 1, 1, alpha);
                batch.draw(e.toolTexture, drawX, drawY, tileSize * 0.66f, tileSize * 0.66f);
            } else if (e.isSparkle) {
                // Sparkle: small circle that expands and fades
                float size = 4 + (1f - alpha) * 8;
                batch.setColor(e.color.r, e.color.g, e.color.b, alpha);
                batch.draw(whitePixel, e.worldX - size / 2, e.worldY - size / 2, size, size);
            } else {
                // Dig flash: full tile flash that fades
                batch.setColor(e.color.r, e.color.g, e.color.b, alpha * 0.4f);
                batch.draw(whitePixel, e.worldX, e.worldY, tileSize, tileSize);
            }
        }
        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        if (whitePixel != null) whitePixel.dispose();
    }
}