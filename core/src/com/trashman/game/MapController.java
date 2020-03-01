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
//import jdk.internal.util.xml.impl.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MapController extends TiledMap implements InputProcessor {
    private final int xSize;
    private final int ySize;
    private final int xGrid;
    private final int yGrid;
    // add the player
    private Player player;
    private Trash banana;
    private Bin bin;
    private Evil evil;

    private TiledMapTileLayer baseLayer;
    private TiledMapTileLayer objectLayer;
    private AssetManager manager = Assets.manager;

    //adding all tilemap layer fields
    private TiledMapTileLayer.Cell grass = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bush = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell players = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bananas = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bins = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell evils = new TiledMapTileLayer.Cell();
    //private TiledMapTileLayer.Cell red_bins = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell yellow_bins = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell blue_bins = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell green_bins = new TiledMapTileLayer.Cell();

    //random trash dropping counter
    private int counter;

    public MapController(int xGrid, int yGrid) {
        super();
        this.xGrid = xGrid;
        this.yGrid = yGrid;
        this.xSize = 32*xGrid;
        this.ySize = 32*yGrid;

        //initialize the counter
        counter = 0;

        //initialize player
        this.player = new Player(new Position(0,4));
        //initialize trash
        this.banana = new Trash(new Position(8,6));
        //initialize bin
        this.bin = new Bin(new Position(10,11));
        //initializing the evil
        this.evil = new Evil(new Position(3,3));

        //initialize the object sprites
        grass.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/grass.png", Texture.class))));
        bush.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bush.png", Texture.class))));
        players.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/goodman_L.png", Texture.class))));
        bananas.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/banana.png", Texture.class))));
        bins.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bin_red.png", Texture.class))));
        evils.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/evil_robot.png", Texture.class))));
        //green_bins.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bin_green.png", Texture.class))));
        //yellow_bins.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bin_yellow.png", Texture.class))));
        //blue_bins.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bin_blue.png", Texture.class))));
        createMap();
    }
    private void createMap() {
        MapLayers layers = getLayers();

        //create background
        baseLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);
        //create grid to place objects on
        objectLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);

        //create and connect all entrances
        Set<Position> entrances = new HashSet<>();
        entrances.add(new Position(0, 2));
        entrances.add(new Position(17, 0));
        entrances.add(new Position(xGrid - 1, 11));
        entrances.add(new Position(5, yGrid - 1));

        //generate the walls
        Map<Position, Boolean> walls = MapGenerator.generate(xGrid, yGrid, entrances);

        //drawing the wallbushes onto the map
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

        // add the trash
        objectLayer.setCell(8, 6, bananas);

        // add the bin
        objectLayer.setCell(10,11,bins);

        //add evil
        objectLayer.setCell(3,3, evils);


    }



    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.DPAD_RIGHT) {
            if (objectLayer.getCell(player.getposition().getX() + 1, player.getposition().getY()) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                player.moveright();
                moveEvil();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_LEFT) {
            if (objectLayer.getCell(player.getposition().getX() - 1, player.getposition().getY()) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                player.moveleft();
                moveEvil();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_UP) {
            if (objectLayer.getCell(player.getposition().getX(), player.getposition().getY() + 1) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                player.moveup();
                moveEvil();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_DOWN) {
            if (objectLayer.getCell(player.getposition().getX(), player.getposition().getY() - 1) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                player.movedown();
                moveEvil();
            } else {
                return false;
            }
        }
        objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), players);

        if (keycode == Input.Keys.SPACE) {
            if ((objectLayer.getCell(player.getposition().getX(), player.getposition().getY() - 1) == bananas ||
                    objectLayer.getCell(player.getposition().getX(), player.getposition().getY() + 1) == bananas ||
                    objectLayer.getCell(player.getposition().getX() + 1, player.getposition().getY()) == bananas ||
                    objectLayer.getCell(player.getposition().getX() - 1, player.getposition().getY()) == bananas) &&
                    player.checkbag()) {
                objectLayer.setCell(banana.getPosition().getX(), banana.getPosition().getY(), null);
                player.pickup(banana);

            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.ENTER) {
            if ((objectLayer.getCell(player.getposition().getX(), player.getposition().getY() - 1) == bins ||
                    objectLayer.getCell(player.getposition().getX(), player.getposition().getY() + 1) == bins ||
                    objectLayer.getCell(player.getposition().getX() + 1, player.getposition().getY()) == bins ||
                    objectLayer.getCell(player.getposition().getX() - 1, player.getposition().getY()) == bins) &&
                    !player.checkbag()) {
                objectLayer.setCell(bin.getPosition().getX(), bin.getPosition().getY(), null);
                player.putdown();
            } else {
                return false;
            }
        }
            return false;
        }
    //moving loop to move the evil figure
    public void moveEvil(){
        //setting moving values to 0
        int movexint = 0;
        int moveyint = 0;
        //array declaring possible moves
        int[] positionarray = {1,-1, 0};
        //generate random movement position
        movexint = getRandom(positionarray);
        moveyint = getRandom(positionarray);
        //test if future evil position is free
        if (objectLayer.getCell(evil.getPosition().getX()+movexint, evil.getPosition().getY()+moveyint) == null) {
            //clear current evil position
            objectLayer.setCell(evil.getPosition().getX(), evil.getPosition().getY(), null);
            //decide that after every 3rd movement the robot leaves a banana
            droptrash();
            //set view layer evil position to new position
            objectLayer.setCell(evil.getPosition().getX()+movexint, evil.getPosition().getY()+moveyint, evils);
            //set object position to new position
            evil.setPosition(evil.getPosition().getX()+movexint, evil.getPosition().getY()+moveyint);
        }
    }

    public void droptrash(){
        counter++;
        if (counter%3 == 0){
            banana = new Trash(new Position(evil.getPosition().getX(),evil.getPosition().getY()));
            objectLayer.setCell(evil.getPosition().getX(), evil.getPosition().getY(), bananas);
        }
    }

    //random number position in array generator
    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
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
