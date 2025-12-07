package io.github.FactoryGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.FactoryGame.WorldGen.Tile;
import io.github.FactoryGame.WorldGen.World;
import io.github.FactoryGame.WorldGen.WorldGenerator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainOLD extends ApplicationAdapter {
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 800;
    private static final float TILE_SIZE = 32f;

    private SpriteBatch batch;

    private FitViewport viewport;
    private OrthographicCamera camera;

    private Texture grassTex;
    private Texture stoneTex;
    private Texture dirtTex;

    private World world;

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();

        //viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        grassTex = new Texture("grass.png");
        stoneTex = new Texture("stone.png");
        dirtTex  = new Texture("dirt.png");

        // Create and generate world
        world = new World(10, 10);
        WorldGenerator.generate(world);

        viewport = new FitViewport(world.width, world.height, camera);
    }

    @Override
    public void render() {
//        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
//        batch.begin();
//        batch.end();
        this.generateWorld(world);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        grassTex.dispose();
        stoneTex.dispose();
        dirtTex.dispose();
    }

    public void draw(){
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        batch.end();
    }

    public void generateWorld(World world){
        //czyszczenie ekranu
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //
        batch.begin();

        for (int x = 0; x < world.width; x++) {
            for (int y = 0; y < world.height; y++) {
                Tile tile = world.tiles[x][y];
                Texture tex = null;

                switch (tile.getTileType()) {
                    case GRASS: tex = grassTex; break;
                    case STONE: tex = stoneTex; break;
                    case DIRT:  tex = dirtTex;  break;
                }

                // x,y are tile coordinates; multiply by tile size to get world coords
                batch.draw(tex, x, y, 1, 1);
            }
        }

        batch.end();

    }
}
