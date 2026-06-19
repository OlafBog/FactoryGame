package io.github.FactoryGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.FactoryGame.InfiniteWorldGen.BiomeType;
import io.github.FactoryGame.InfiniteWorldGen.ResourceType;

import java.util.Map;

public class HUD {
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private boolean showControls = false;
    private float controlsTimer = 5.0f; // auto-hide after 5 seconds
    private Map<ResourceType, Texture> resourceIcons;
    private Map<ToolType, Texture> toolIcons;

    public HUD() {
        this(null, null);
    }

    public HUD(Map<ResourceType, Texture> resourceIcons, Map<ToolType, Texture> toolIcons) {
        this.resourceIcons = resourceIcons;
        this.toolIcons = toolIcons;
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        shapeRenderer = new ShapeRenderer();
    }

    public void toggleControls() {
        showControls = !showControls;
        if (showControls) controlsTimer = 5.0f;
    }

    public void render(SpriteBatch batch, BiomeType biome, int playerTileX, int playerTileY,
                       int remainingLayers, ToolType currentTool, Player player) {
        // First pass: shapes (behind text)
        batch.end();

        // Draw semi-transparent background boxes
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Top-left info box
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.rect(5, Gdx.graphics.getHeight() - 80, 280, 75);

        // Bottom inventory bar
        shapeRenderer.rect(5, 5, Math.min(600, Gdx.graphics.getWidth() - 10), 50);

        if (showControls) {
            // Controls overlay
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2f - 180, Gdx.graphics.getHeight() / 2f - 120, 360, 240);
        }

        shapeRenderer.end();

        batch.begin();

        // Draw text info
        // Top-left: biome, coords, layer info
        String biomeName = biome != null ? biome.name() : "UNKNOWN";
        String coords = "X: " + playerTileX + "  Y: " + playerTileY;
        String layers = "Depth: " + remainingLayers + " layers remaining";
        String fps = "FPS: " + Gdx.graphics.getFramesPerSecond();

        font.setColor(Color.WHITE);
        font.draw(batch, "Biome: " + biomeName, 15, Gdx.graphics.getHeight() - 25);
        font.draw(batch, coords, 15, Gdx.graphics.getHeight() - 45);
        font.draw(batch, layers, 15, Gdx.graphics.getHeight() - 65);

        // Top-right: FPS
        font.draw(batch, fps, Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 15);

        // Bottom: tool bar + inventory
        drawToolBar(batch, currentTool);
        drawInventory(batch, player.getInventory());

        // Collection notification (screen space, bottom-center)
        String collectMsg = player.getCollectMessage();
        if (!collectMsg.isEmpty()) {
            float msgX = Gdx.graphics.getWidth() / 2f - 40;
            float msgY = 70;
            font.setColor(1, 1, 0, player.getCollectMessageAlpha());
            font.draw(batch, collectMsg, msgX, msgY);
            font.setColor(Color.WHITE);
        }

        // Controls hint
        if (showControls) {
            drawControls(batch);
        } else {
            font.setColor(new Color(1, 1, 1, 0.4f));
            font.draw(batch, "Press H for controls", 15, Gdx.graphics.getHeight() - 95);
            font.setColor(Color.WHITE);
        }

        // Auto-hide controls
        if (showControls) {
            controlsTimer -= Gdx.graphics.getDeltaTime();
            if (controlsTimer <= 0) showControls = false;
        }
    }

    private void drawToolBar(SpriteBatch batch, ToolType currentTool) {
        ToolType[] tools = ToolType.values();
        for (int i = 0; i < tools.length; i++) {
            float x = 15 + i * 145;
            boolean active = tools[i] == currentTool;

            // Draw tool icon
            if (toolIcons != null) {
                Texture icon = toolIcons.get(tools[i]);
                if (icon != null) {
                    batch.setColor(Color.WHITE);
                    batch.draw(icon, x, 28, 12, 12);
                }
            }
            x += 16;

            if (active) {
                font.setColor(Color.YELLOW);
                font.draw(batch, "[" + (i + 1) + "] " + tools[i].label + " (" + tools[i].power + ")", x, 40);
            } else {
                font.setColor(new Color(0.7f, 0.7f, 0.7f, 1f));
                font.draw(batch, "[" + (i + 1) + "] " + tools[i].label, x, 40);
            }
        }
        font.setColor(Color.WHITE);
    }

    private void drawInventory(SpriteBatch batch, Map<ResourceType, Integer> inventory) {
        if (inventory.isEmpty()) {
            font.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
            font.draw(batch, "Inventory: empty - dig to collect resources", 15, 20);
            font.setColor(Color.WHITE);
            return;
        }

        float x = 15;
        font.setColor(Color.WHITE);
        font.draw(batch, "Items:", x, 20);
        x += 55;

        int count = 0;
        for (Map.Entry<ResourceType, Integer> entry : inventory.entrySet()) {
            // Draw colored icon square
            if (resourceIcons != null) {
                Texture icon = resourceIcons.get(entry.getKey());
                if (icon != null) {
                    batch.setColor(Color.WHITE);
                    batch.draw(icon, x, 12, 8, 8);
                }
            }
            x += 10;

            // Draw text
            String text = entry.getKey().name() + ": " + entry.getValue();
            font.draw(batch, text, x, 20);
            x += text.length() * 10 + 15;
            count++;

            // Show max 8 items per row
            if (count >= 8) break;
        }
    }

    private void drawControls(SpriteBatch batch) {
        float cx = Gdx.graphics.getWidth() / 2f;
        float cy = Gdx.graphics.getHeight() / 2f;

        font.setColor(Color.WHITE);
        font.draw(batch, "=== CONTROLS ===", cx - 80, cy + 90);
        font.draw(batch, "WASD          - Move player", cx - 160, cy + 60);
        font.draw(batch, "Left Click    - Dig", cx - 160, cy + 35);
        font.draw(batch, "1-4           - Switch tool", cx - 160, cy + 10);
        font.draw(batch, "Q/E           - Zoom", cx - 160, cy - 15);
        font.draw(batch, "Z             - Show depth", cx - 160, cy - 40);
        font.draw(batch, "X/C           - Heatmap debug", cx - 160, cy - 65);
        font.draw(batch, "H             - Toggle controls", cx - 160, cy - 90);
        font.setColor(new Color(1, 1, 1, 0.5f));
        font.draw(batch, "Press H again to close", cx - 70, cy - 115);
        font.setColor(Color.WHITE);
    }

    public void dispose() {
        if (font != null) font.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
