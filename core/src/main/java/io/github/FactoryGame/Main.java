package io.github.FactoryGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import io.github.FactoryGame.InfiniteWorldGen.*;


public class Main extends ApplicationAdapter implements InputProcessor {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private World gameWorld;
    private BitmapFont font;

    private Texture texGrass;
    private Texture texDirt;
    private Texture texDarkSoil;
    private Texture texStone;
    private Texture texDeepStone;
    private Texture texHardStone;
    private Texture texBedrock;
    private Texture texGold;

    private final int TILE_SIZE = 32;
    private final float CAMERA_SPEED = 600.0f;
    private java.util.Map<BiomeType, BiomePalette> biomeColors;

    private boolean drawNumbers = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        initBiomeColors();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);

        gameWorld = new World(1234567890);
        generateDebugTextures();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        handleInput(Gdx.graphics.getDeltaTime());
        camera.update();

        // Tło
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Obliczanie widocznego obszaru
        int startX = (int) Math.floor((camera.position.x - camera.viewportWidth / 2 * camera.zoom) / TILE_SIZE) - 1;
        int endX   = (int) Math.floor((camera.position.x + camera.viewportWidth / 2 * camera.zoom) / TILE_SIZE) + 1;
        int startY = (int) Math.floor((camera.position.y - camera.viewportHeight / 2 * camera.zoom) / TILE_SIZE) - 1;
        int endY   = (int) Math.floor((camera.position.y + camera.viewportHeight / 2 * camera.zoom) / TILE_SIZE) + 1;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {

                LayerType layer = gameWorld.getLayerAt(x, y);
                BiomeType biome = gameWorld.getBiomeAt(x, y);
                Texture tex = getTextureForLayer(layer);

                // Wewnątrz pętli for (x, y)...
                if (tex != null) {
                    // 1. Pobierz paletę dla aktualnego biomu
                    BiomePalette palette = biomeColors.get(biome);

                    // Zabezpieczenie na wypadek błędu (gdyby mapa nie miała biomu)
                    if (palette == null) palette = biomeColors.get(BiomeType.H1T1);

                    // 2. Ustaw kolor w zależności od warstwy
                    // (Zakładam, że LAYER1 to Trawa/Powierzchnia, a LAYER2 to Ziemia)
                    if (layer == LayerType.LAYER1) {
                        batch.setColor(palette.surface);
                    }
                    else if (layer == LayerType.LAYER2) {
                        batch.setColor(palette.subsoil);
                    }
                    else {
                        // Dla kamienia, bedrocka itp. resetujemy na biały
                        batch.setColor(Color.WHITE);
                    }

                    // 3. Rysuj
                    batch.draw(tex, x * TILE_SIZE, y * TILE_SIZE);

                    // 4. Reset koloru dla reszty elementów
                    batch.setColor(Color.WHITE);
                }

                // Surowce
                ResourceType res = gameWorld.getResourceAt(x, y);
                if (res == ResourceType.GOLD) {
                    batch.draw(texGold, x * TILE_SIZE + 8, y * TILE_SIZE + 8);
                }

                // Napisy (głębokość)
                if (layer != LayerType.BEDROCK && layer != LayerType.AIR) {
                    int layersLeft = gameWorld.getLayersLeftAt(x, y);
                    if (layersLeft <= 3) font.setColor(Color.RED);
                    else font.setColor(Color.WHITE);
                    // Odkomentuj, jeśli chcesz widzieć numerki
                    if (drawNumbers) font.draw(batch, String.valueOf(layersLeft), x * TILE_SIZE + 10, y * TILE_SIZE + 22);
                }
            }
        }
        batch.end();
    }

    private Texture getTextureForLayer(LayerType layer) {
        if (layer == null) return null;
        switch (layer) {
            case LAYER1: return texGrass;
            case LAYER2: return texDirt;
            case LAYER3: return texDarkSoil;
            case LAYER4: return texStone;
            case LAYER5: return texDeepStone;
            case LAYER6: return texHardStone;
            case BEDROCK: return texBedrock;
            default: return null;
        }
    }

    private void initBiomeColors() {
        biomeColors = new java.util.EnumMap<>(BiomeType.class);

        // --- ZIMNE (T0) ---
        // H0T0: Zimno i Sucho (Lodowa pustynia/Tundra) -> Biały / Szary
        biomeColors.put(BiomeType.H0T0, new BiomePalette(
            new Color(0.9f, 0.9f, 1.0f, 1f), // Lekko niebieskawy śnieg
            new Color(0.7f, 0.7f, 0.75f, 1f) // Zmarznięta ziemia
        ));
        // H1T0: Zimno i Średnio (Śnieżna tajga) -> Biały / Brązowy
        biomeColors.put(BiomeType.H1T0, new BiomePalette(
            new Color(1.0f, 1.0f, 1.0f, 1f), // Czysty śnieg
            new Color(0.5f, 0.4f, 0.35f, 1f) // Ziemia
        ));
        // H2T0: Zimno i Mokro (Zmarznięte bagno) -> Szaro-niebieski
        biomeColors.put(BiomeType.H2T0, new BiomePalette(
            new Color(0.7f, 0.8f, 0.9f, 1f), // Lodowaty błękit
            new Color(0.4f, 0.4f, 0.5f, 1f)  // Ciemny kamień/muł
        ));

        // --- UMIARKOWANE (T1) ---
        // H0T1: Umiarkowanie i Sucho (Step/Wypalona trawa) -> Żółto-zielony
        biomeColors.put(BiomeType.H0T1, new BiomePalette(
            new Color(0.7f, 0.8f, 0.4f, 1f), // Sucha trawa
            new Color(0.6f, 0.5f, 0.3f, 1f)  // Sucha ziemia
        ));
        // H1T1: Umiarkowanie (Las/Równiny - STANDARD) -> Zielony
        biomeColors.put(BiomeType.H1T1, new BiomePalette(
            new Color(0.4f, 0.8f, 0.4f, 1f), // Ładna zieleń (DOMYŚLNY)
            new Color(0.5f, 0.3f, 0.1f, 1f)  // Zwykła brązowa ziemia
        ));
        // H2T1: Umiarkowanie i Mokro (Las deszczowy/Bagno) -> Ciemna zieleń
        biomeColors.put(BiomeType.H2T1, new BiomePalette(
            new Color(0.2f, 0.5f, 0.2f, 1f), // Ciemna, soczysta zieleń
            new Color(0.3f, 0.25f, 0.2f, 1f) // Błotnista ziemia
        ));

        // --- GORĄCE (T2) ---
        // H0T2: Gorąco i Sucho (Pustynia) -> Piaskowy
        biomeColors.put(BiomeType.H0T2, new BiomePalette(
            new Color(1.0f, 0.9f, 0.5f, 1f), // Piasek
            new Color(0.9f, 0.7f, 0.4f, 1f)  // Piaskowiec
        ));
        // H1T2: Gorąco i Średnio (Sawanna) -> Wypłowiała zieleń/Oliwkowy
        biomeColors.put(BiomeType.H1T2, new BiomePalette(
            new Color(0.6f, 0.7f, 0.2f, 1f), // Oliwkowa trawa
            new Color(0.7f, 0.5f, 0.3f, 1f)  // Czerwonawa ziemia
        ));
        // H2T2: Gorąco i Mokro (Dżungla tropikalna) -> Jaskrawa/Neonowa zieleń
        biomeColors.put(BiomeType.H2T2, new BiomePalette(
            new Color(0.1f, 0.8f, 0.3f, 1f), // Jaskrawa dżungla
            new Color(0.4f, 0.2f, 0.1f, 1f)  // Wilgotna gleba
        ));
    }

    private void generateDebugTextures() {
        // Generujemy proste tekstury (kwadraty)
        texGrass = createSolidTexture(new Color(0.8f, 0.8f, 0.8f, 1)); // Bazowo szary/biały, żeby dobrze przyjmował kolory
        texDirt = createSolidTexture(new Color(0.5f, 0.3f, 0.1f, 1));  // Brąz
        texDarkSoil = createSolidTexture(new Color(0.3f, 0.2f, 0.05f, 1));
        texStone = createSolidTexture(new Color(0.6f, 0.6f, 0.6f, 1)); // Szary
        texDeepStone = createSolidTexture(new Color(0.4f, 0.4f, 0.4f, 1));
        texHardStone = createSolidTexture(new Color(0.25f, 0.25f, 0.3f, 1));
        texBedrock = createSolidTexture(new Color(0.1f, 0.1f, 0.1f, 1));
        texGold = createSolidTexture(new Color(1f, 0.9f, 0.1f, 1), 16, 16);
    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) camera.translate(0, CAMERA_SPEED * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) camera.translate(0, -CAMERA_SPEED * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) camera.translate(-CAMERA_SPEED * dt, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) camera.translate(CAMERA_SPEED * dt, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) camera.zoom += 1.0f * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom -= 1.0f * dt;
            if (camera.zoom < 0.1f) camera.zoom = 0.1f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) drawNumbers = !drawNumbers;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
            int gridX = (int) Math.floor(worldPos.x / TILE_SIZE);
            int gridY = (int) Math.floor(worldPos.y / TILE_SIZE);
            gameWorld.digAt(gridX, gridY);
            return true;
        }
        return false;
    }

    private Texture createSolidTexture(Color color, int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        // Dodajemy ramkę dla lepszej widoczności kratek
        pixmap.setColor(color.cpy().mul(0.8f));
        pixmap.drawRectangle(0, 0, w, h);
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    private Texture createSolidTexture(Color color) { return createSolidTexture(color, TILE_SIZE, TILE_SIZE); }

    @Override
    public void dispose() {
        batch.dispose();
        if (font != null) font.dispose();
        texGrass.dispose(); texDirt.dispose(); texDarkSoil.dispose();
        texStone.dispose(); texDeepStone.dispose(); texHardStone.dispose();
        texBedrock.dispose(); texGold.dispose();
    }

    // Puste metody interfejsu InputProcessor
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
