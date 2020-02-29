package com.trashman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapController extends TiledMap implements InputProcessor {
    private final int xSize;
    private final int ySize;
    private final int xGrid;
    private final int yGrid;

    private TiledMapTileLayer baseLayer;
    private TiledMapTileLayer objectLayer;
    private AssetManager manager = Assets.manager;

    private TiledMapTileLayer.Cell grass = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bush = new TiledMapTileLayer.Cell();

    public MapController(int xGrid, int yGrid) {
        super();
        this.xGrid = xGrid;
        this.yGrid = yGrid;
        this.xSize = 32*xGrid;
        this.ySize = 32*yGrid;

        grass.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/grass.png", Texture.class))));
        bush.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bush.png", Texture.class))));

        createMap();
    }
    private void createMap() {
        MapLayers layers = getLayers();

        baseLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);
        objectLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);

        Set<Position> entrances = new HashSet<>();
        entrances.add(new Position(0, 2));
        entrances.add(new Position(17, 0));
        entrances.add(new Position(xGrid - 1, 11));
        entrances.add(new Position(5, yGrid - 1));

        Map<Position, Boolean> walls = MapGenerator.generate(xGrid, yGrid, entrances);

        for (int row = 0; row < xGrid; row++) {
            for (int col = 0; col < yGrid; col++) {
                baseLayer.setCell(col, row, grass);
                if (walls.getOrDefault(new Position(col, row), false)) {
                    objectLayer.setCell(col, row, bush);
                }
            }
        }

        layers.add(baseLayer);
        layers.add(objectLayer);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
