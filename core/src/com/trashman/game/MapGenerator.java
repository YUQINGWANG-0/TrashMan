package com.trashman.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class MapGenerator extends TiledMap {
    private final int xSize = 1000;
    private final int ySize = 1000;
    private final int xGrid;
    private final int yGrid;

    private TiledMapTileLayer baseLayer;
    private AssetManager manager = new TrashManAssetManager();

    public MapGenerator(int xGrid, int yGrid) {
        super();
        this.xGrid = xGrid;
        this.yGrid = yGrid;

        MapLayers layers = getLayers();

        baseLayer = new TiledMapTileLayer(xSize, ySize, xSize/xGrid, ySize/yGrid);
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

        cell.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/banana.png", Texture.class))));
        baseLayer.setCell(0, 0, cell);
        baseLayer.setCell(1, 1, cell);

        layers.add(baseLayer);
    }
}
