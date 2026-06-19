package io.github.FactoryGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.FactoryGame.InfiniteWorldGen.ResourceType;

import java.util.HashMap;
import java.util.Map;

public class Player {
    public float x, y;
    private Texture bodyTexture;
    private int tileSize;
    private Map<ResourceType, Integer> inventory;
    private String lastCollectMessage = "";
    private float collectMessageTimer = 0;

    // Eye tracking
    private float eyeOffsetX = 0;
    private float eyeOffsetY = 0;
    private float lastMoveX = 0;
    private float lastMoveY = 0;

    public Player(int tileSize) {
        this.tileSize = tileSize;
        this.x = 0;
        this.y = 0;
        this.inventory = new HashMap<>();

        // Create body texture (blue character with lighter face, NO eyes)
        Pixmap pixmap = new Pixmap(tileSize - 4, tileSize - 4, Pixmap.Format.RGBA8888);
        // Body
        pixmap.setColor(new Color(0.2f, 0.5f, 1.0f, 1f));
        pixmap.fill();
        // Face
        pixmap.setColor(new Color(0.6f, 0.8f, 1.0f, 1f));
        pixmap.fillCircle((tileSize - 4) / 2, (tileSize - 4) / 2, (tileSize - 4) / 4);

        bodyTexture = new Texture(pixmap);
        bodyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
    }

    public void update(float delta) {
        // Smoothly move eyes toward last movement direction
        float targetX = lastMoveX * 2f;
        float targetY = lastMoveY * 2f;
        eyeOffsetX += (targetX - eyeOffsetX) * 8f * delta;
        eyeOffsetY += (targetY - eyeOffsetY) * 8f * delta;

        // Decay movement direction when not moving
        lastMoveX *= 0.9f;
        lastMoveY *= 0.9f;
    }

    public void setMovement(float dx, float dy) {
        if (dx != 0 || dy != 0) {
            lastMoveX = dx;
            lastMoveY = dy;
        }
    }

    public void render(SpriteBatch batch) {
        batch.setColor(Color.WHITE);
        float drawX = x - (tileSize - 4) / 2f;
        float drawY = y - (tileSize - 4) / 2f;
        batch.draw(bodyTexture, drawX, drawY);

        // Draw eyes that follow movement direction
        int cx = (tileSize - 4) / 2;
        int cy = (tileSize - 4) / 2;
        int ex = Math.round(eyeOffsetX);
        int ey = Math.round(eyeOffsetY);

        // Left eye (white + pupil)
        batch.setColor(Color.WHITE);
        batch.draw(bodyTexture, drawX + cx - 3 + ex - 1, drawY + cy + 1 + ey - 1, 2, 2);
        batch.setColor(Color.BLACK);
        batch.draw(bodyTexture, drawX + cx - 3 + ex, drawY + cy + 1 + ey, 1, 1);

        // Right eye (white + pupil)
        batch.setColor(Color.WHITE);
        batch.draw(bodyTexture, drawX + cx + 1 + ex - 1, drawY + cy + 1 + ey - 1, 2, 2);
        batch.setColor(Color.BLACK);
        batch.draw(bodyTexture, drawX + cx + 1 + ex, drawY + cy + 1 + ey, 1, 1);

        batch.setColor(Color.WHITE);
    }

    public void collectResource(ResourceType resource) {
        if (resource == null) return;
        inventory.put(resource, inventory.getOrDefault(resource, 0) + 1);
        lastCollectMessage = "+1 " + resource.name();
        collectMessageTimer = 2.0f;
    }

    public void updateCollectMessage(float delta) {
        if (collectMessageTimer > 0) {
            collectMessageTimer -= delta;
        }
    }

    public String getCollectMessage() {
        return collectMessageTimer > 0 ? lastCollectMessage : "";
    }

    public float getCollectMessageAlpha() {
        if (collectMessageTimer <= 0) return 0;
        if (collectMessageTimer < 0.5f) return collectMessageTimer / 0.5f;
        return 1.0f;
    }

    public Map<ResourceType, Integer> getInventory() {
        return inventory;
    }

    public int getTotalItems() {
        return inventory.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void dispose() {
        if (bodyTexture != null) bodyTexture.dispose();
    }
}