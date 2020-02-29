package com.trashman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    // add the player
    private Player player;
    private Position position;

    private TiledMapTileLayer baseLayer;
    private TiledMapTileLayer objectLayer;
    private AssetManager manager = Assets.manager;

    private TiledMapTileLayer.Cell grass = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bush = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell players = new TiledMapTileLayer.Cell();

    public MapController(int xGrid, int yGrid) {
        super();
        this.xGrid = xGrid;
        this.yGrid = yGrid;
        this.xSize = 32*xGrid;
        this.ySize = 32*yGrid;

        //initialize player
        this.position = new Position(0,4);
        this.player = new Player(position);


        grass.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/grass.png", Texture.class))));
        bush.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bush.png", Texture.class))));
        players.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/goodman_L.png", Texture.class))));

        createMap();
    }
    private void createMap() {
        MapLayers layers = getLayers();

        baseLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);
        objectLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);

        Set<Position> entrances = new HashSet<>();
        entrances.add(new Position(0, 4));

        Map<Position, Boolean> walls = MapGenerator.generate(xGrid, yGrid, entrances);

        for (int row = 0; row < xGrid; row++) {
            for (int col = 0; col < yGrid; col++) {
                baseLayer.setCell(col, row, grass);
                if (walls.getOrDefault(new Position(col, row), false)) {
                    objectLayer.setCell(col, row, bush);
                }
            }
        }

        // add the player
        objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), players);

        layers.add(baseLayer);
        layers.add(objectLayer);
        Gdx.input.setInputProcessor(this);

    }



    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.DPAD_RIGHT){
            if (objectLayer.getCell(player.getposition().getX()+1,player.getposition().getY()) == null){
                objectLayer.setCell(player.getposition().getX(),player.getposition().getY(),null);
                player.moveright();
            }
            else{
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_LEFT){
            if (objectLayer.getCell(player.getposition().getX()-1,player.getposition().getY()) == null){
                objectLayer.setCell(player.getposition().getX(),player.getposition().getY(),null);
                player.moveleft();
            }
            else{
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_UP){
            if (objectLayer.getCell(player.getposition().getX(),player.getposition().getY()+1) == null){
                objectLayer.setCell(player.getposition().getX(),player.getposition().getY(),null);
                player.moveup();
            }
            else{
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_DOWN){
            if (objectLayer.getCell(player.getposition().getX(),player.getposition().getY()-1) == null){
                objectLayer.setCell(player.getposition().getX(),player.getposition().getY(),null);
                player.movedown();
            }
            else{
                return false;
            }
        }
        objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), players);
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
