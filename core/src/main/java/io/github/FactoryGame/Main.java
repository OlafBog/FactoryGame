package io.github.FactoryGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import io.github.FactoryGame.InfiniteWorldGen.*;
import io.github.FactoryGame.InfiniteWorldGen.Rendering.BiomeTextureCache;

import com.mazatech.gdx.SVGAssetsConfigGDX;
import com.mazatech.gdx.SVGAssetsGDX;


public class Main extends ApplicationAdapter implements InputProcessor {

    private SVGAssetsGDX svg;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private World gameWorld;
    private BitmapFont font;
    private Texture whiteTexture;

    private final int TILE_SIZE = 32;
    private final float CAMERA_SPEED = 600.0f;
    private boolean debugTemperature = false;
    private boolean debugHumidity = false;
    private boolean drawNumbers = false;

    // Managers
    private BiomeTextureCache textureCache;
    private TextureManager textureManager;
    private HeatmapColorizer heatmapColorizer;
    private RenderSystem renderSystem;
    private java.util.EnumMap<BiomeType, BiomePalette> biomeColors;

    // New game features
    private Player player;
    private HUD hud;
    private ToolType currentTool = ToolType.HAND;
    private boolean hudInitialized = false;
    private DigEffect digEffect;

    @Override
    public void create() {
        batch = new SpriteBatch();
        batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        // Initialize biome colors for underground layers
        biomeColors = new java.util.EnumMap<>(BiomeType.class);
        biomeColors.put(BiomeType.H0T0, new BiomePalette(new Color(0.9f, 0.9f, 1.0f, 1f), new Color(0.7f, 0.7f, 0.75f, 1f)));
        biomeColors.put(BiomeType.H1T0, new BiomePalette(new Color(1.0f, 1.0f, 1.0f, 1f), new Color(0.5f, 0.4f, 0.35f, 1f)));
        biomeColors.put(BiomeType.H2T0, new BiomePalette(new Color(0.7f, 0.8f, 0.9f, 1f), new Color(0.4f, 0.4f, 0.5f, 1f)));
        biomeColors.put(BiomeType.H0T1, new BiomePalette(new Color(0.7f, 0.8f, 0.4f, 1f), new Color(0.6f, 0.5f, 0.3f, 1f)));
        biomeColors.put(BiomeType.H1T1, new BiomePalette(new Color(0.4f, 0.8f, 0.4f, 1f), new Color(0.5f, 0.3f, 0.1f, 1f)));
        biomeColors.put(BiomeType.H2T1, new BiomePalette(new Color(0.2f, 0.5f, 0.2f, 1f), new Color(0.3f, 0.25f, 0.2f, 1f)));
        biomeColors.put(BiomeType.H0T2, new BiomePalette(new Color(1.0f, 0.9f, 0.5f, 1f), new Color(0.9f, 0.7f, 0.4f, 1f)));
        biomeColors.put(BiomeType.H1T2, new BiomePalette(new Color(0.6f, 0.7f, 0.2f, 1f), new Color(0.7f, 0.5f, 0.3f, 1f)));
        biomeColors.put(BiomeType.H2T2, new BiomePalette(new Color(0.1f, 0.8f, 0.3f, 1f), new Color(0.4f, 0.2f, 0.1f, 1f)));

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();

        SVGAssetsConfigGDX cfg = new SVGAssetsConfigGDX(
            Gdx.graphics.getBackBufferWidth() * 4,
            Gdx.graphics.getBackBufferHeight() * 4,
            Gdx.graphics.getPpiX()
        );
        svg = new SVGAssetsGDX(cfg);

        // Initialize managers
        textureManager = new TextureManager(svg, TILE_SIZE);
        textureManager.loadAll();

        textureCache = new BiomeTextureCache(svg, TILE_SIZE);
        heatmapColorizer = new HeatmapColorizer();
        renderSystem = new RenderSystem(batch, textureCache, textureManager, biomeColors, font, TILE_SIZE);

        gameWorld = new World(1002003004);

        // Create white texture for heatmap
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(TILE_SIZE, TILE_SIZE, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        whiteTexture.setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest, com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest);
        pixmap.dispose();

        // Initialize player, effects and HUD
        player = new Player(TILE_SIZE);
        player.x = camera.position.x;
        player.y = camera.position.y;

        digEffect = new DigEffect();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        handleInput(Gdx.graphics.getDeltaTime());

        // Camera follows player smoothly
        camera.position.x += (player.x - camera.position.x) * 0.08f;
        camera.position.y += (player.y - camera.position.y) * 0.08f;
        camera.update();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        int startX = (int) Math.floor((camera.position.x - camera.viewportWidth / 2 * camera.zoom) / TILE_SIZE) - 1;
        int endX   = (int) Math.floor((camera.position.x + camera.viewportWidth / 2 * camera.zoom) / TILE_SIZE) + 1;
        int startY = (int) Math.floor((camera.position.y - camera.viewportHeight / 2 * camera.zoom) / TILE_SIZE) - 1;
        int endY   = (int) Math.floor((camera.position.y + camera.viewportHeight / 2 * camera.zoom) / TILE_SIZE) + 1;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                LayerType layer = gameWorld.getLayerAt(x, y);
                BiomeType biome = gameWorld.getBiomeAt(x, y);

                renderSystem.renderTile(x, y, layer, biome, gameWorld, drawNumbers,
                                       whiteTexture, heatmapColorizer, debugTemperature, debugHumidity);
            }
        }

        // Render player character
        player.render(batch);

        // Render dig effects
        if (digEffect != null) digEffect.render(batch, TILE_SIZE);

        batch.end();

        // Render HUD overlay (uses its own projection matrix)
        if (!hudInitialized) {
            hud = new HUD(textureManager.resourceIcons, textureManager.toolIcons);
            hudInitialized = true;
        }

        // Get biome under player for HUD
        int playerTileX = Math.round(player.x / TILE_SIZE);
        int playerTileY = Math.round(player.y / TILE_SIZE);
        BiomeType playerBiome = gameWorld.getBiomeAt(playerTileX, playerTileY);
        int layersRemaining = gameWorld.getLayersLeftAt(playerTileX, playerTileY);

        // Set up screen-space projection for HUD
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();

        hud.render(batch, playerBiome, playerTileX, playerTileY, layersRemaining, currentTool, player);

        batch.end();

        // Update effects
        player.update(Gdx.graphics.getDeltaTime());
        player.updateCollectMessage(Gdx.graphics.getDeltaTime());
        if (digEffect != null) digEffect.update(Gdx.graphics.getDeltaTime());
    }

    private void handleInput(float dt) {
        float speed = 200.0f; // player movement speed
        float dx = 0, dy = 0;

        // Player movement with WASD
        if (Gdx.input.isKeyPressed(Input.Keys.W)) { player.y += speed * dt; dy = 1; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { player.y -= speed * dt; dy = -1; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { player.x -= speed * dt; dx = -1; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { player.x += speed * dt; dx = 1; }

        // Update player eye tracking
        player.setMovement(dx, dy);

        // Tool selection
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) currentTool = ToolType.HAND;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) currentTool = ToolType.SHOVEL;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) currentTool = ToolType.PICKAXE;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) currentTool = ToolType.DRILL;

        // Zoom
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom += 1.0f * dt;
            camera.zoom = Math.min(4.0f, camera.zoom);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom -= 1.0f * dt;
            camera.zoom = Math.max(0.25f, camera.zoom);
        }

        // Debug toggles
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) drawNumbers = !drawNumbers;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            if (hud != null) hud.toggleControls();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            debugTemperature = !debugTemperature;
            debugHumidity = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            debugHumidity = !debugHumidity;
            debugTemperature = false;
        }

        // Snap camera position to pixel-aligned coordinates to prevent blur
        camera.position.x = Math.round(camera.position.x);
        camera.position.y = Math.round(camera.position.y);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
            int gridX = (int) Math.floor(worldPos.x / TILE_SIZE);
            int gridY = (int) Math.floor(worldPos.y / TILE_SIZE);

            // Dig with current tool power
            ResourceType lastResource = null;
            for (int i = 0; i < currentTool.power; i++) {
                gameWorld.digAt(gridX, gridY);
                // Check for resource after each dig
                ResourceType res = gameWorld.getResourceAt(gridX, gridY);
                if (res != null) lastResource = res;
            }

            // Collect resources if found
            if (lastResource != null) {
                player.collectResource(lastResource);
            }

            // Visual dig effects
            if (digEffect != null) {
                digEffect.addDigEffect(gridX, gridY, TILE_SIZE);
                // Tool swing animation
                Texture toolTex = textureManager.toolIcons.get(currentTool);
                if (toolTex != null) {
                    digEffect.addToolSwingEffect(gridX, gridY, TILE_SIZE, toolTex);
                }
                if (lastResource != null) {
                    digEffect.addSparkleEffect(gridX, gridY, TILE_SIZE);
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (font != null) font.dispose();
        if (svg != null) svg.dispose();
        if (textureCache != null) textureCache.dispose();
        if (textureManager != null) textureManager.dispose();
        if (whiteTexture != null) whiteTexture.dispose();
        if (player != null) player.dispose();
        if (hud != null) hud.dispose();
        if (digEffect != null) digEffect.dispose();
    }

    // Unused InputProcessor methods
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}