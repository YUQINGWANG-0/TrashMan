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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.trashman.game.GameObject.*;

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
    private Trash paper;
    private Trash bottle;
    private Trash chemical;
    HashMap<Trash, TiledMapTileLayer.Cell> trash_map = new HashMap<>();


    private TiledMapTileLayer baseLayer;
    private TiledMapTileLayer objectLayer;
    private AssetManager manager = Assets.manager;

    //adding all tilemap layer fields
    private TiledMapTileLayer.Cell grass = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bush = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell players = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bananas = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell papers = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bottles = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell chemicals = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bins = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell evils = new TiledMapTileLayer.Cell();
    //private TiledMapTileLayer.Cell red_bins = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell yellow_bins = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell blue_bins = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell green_bins = new TiledMapTileLayer.Cell();

    private Map<GameObject, TiledMapTileLayer.Cell> cells = new HashMap<>();

    private Position sectionPos;
    private Section section;
    private Map<Position, Section> sections;

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

        //initialize the object sprites
        grass.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/grass.png", Texture.class))));
        bush.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bush.png", Texture.class))));
        players.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/goodman_L.png", Texture.class))));

        //initialize all the trashes
        papers.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/paper.png", Texture.class))));
        bananas.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/banana.png", Texture.class))));
        bottles.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bottle.png", Texture.class))));
        chemicals.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/chemical_waste.png", Texture.class))));
        bins.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bin_red.png", Texture.class))));
        evils.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/evil_robot.png", Texture.class))));
        //green_bins.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bin_green.png", Texture.class))));
        //yellow_bins.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bin_yellow.png", Texture.class))));
        //blue_bins.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bin_blue.png", Texture.class))));

        cells.put(HERO, players);
        cells.put(BANANA, bananas);

        sectionPos = new Position(0, 0);

        MapLayers layers = getLayers();

        baseLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);
        objectLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);

        layers.add(baseLayer);
        layers.add(objectLayer);
        Gdx.input.setInputProcessor(this);

        // add the trash
        for(Map.Entry<Trash, TiledMapTileLayer.Cell> entry : trash_map.entrySet()){
            objectLayer.setCell(entry.getKey().getPosition().getX(),entry.getKey().getPosition().getY(),entry.getValue());
        }

        //createMap();
        section = new Section(xGrid, yGrid, new HashSet<>());
        sections = new HashMap<>();
        sections.put(sectionPos, section);


        //initialize bin
        bin = new Bin();
        section.placeObject(bin);

        evil = section.getRobot();

        //initialize player
        player = new Player();
        section.placeObject(player);

        banana = new Trash();
        section.placeObject(banana);

        //initialize trash
//        this.paper = new Trash(new Position(8,7));
//        this.banana = new Trash(new Position(10,6));
//        this.bottle = new Trash(new Position(10,16));
//        this.chemical = new Trash(new Position(10,26));
//        trash_map.put(banana,bananas);
//        trash_map.put(paper,papers);
//        trash_map.put(bottle,bottles);
//        trash_map.put(chemical,chemicals);

        updateTiles();
    }
    private void createMap() {
        MapLayers layers = getLayers();

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
        objectLayer.setCell(player.getPosition().getX(), player.getPosition().getY(), players);

        layers.add(baseLayer);
        layers.add(objectLayer);
        Gdx.input.setInputProcessor(this);


    }

    private void playerMoved() {
        Position pos = player.getPosition();
        section.objects.put(pos, player);

        //Change the section
        if (pos.getX() == -1 || pos.getX() == xGrid || pos.getY() == -1 || pos.getY() == yGrid) {
            section.objects.remove(pos);
            objectLayer.setCell(pos.getX(), pos.getY(), null);

            if (pos.getX() == -1) {
                objectLayer.setCell(xGrid - 1, pos.getY(), players);
                player.getPosition().setX(xGrid - 1);
                sectionPos = new Position(sectionPos.getX() - 1, sectionPos.getY());
            } else if (pos.getX() == xGrid) {
                objectLayer.setCell(0, pos.getY(), players);
                player.getPosition().setX(0);
                sectionPos = new Position(sectionPos.getX() + 1, sectionPos.getY());
            } else if (pos.getY() == -1) {
                objectLayer.setCell(pos.getX(), yGrid - 1, players);
                player.getPosition().setY(yGrid - 1);
                sectionPos = new Position(sectionPos.getX(), sectionPos.getY() - 1);
            } else if (pos.getY() == yGrid) {
                objectLayer.setCell(pos.getX(), 0, players);
                player.getPosition().setY(0);
                sectionPos = new Position(sectionPos.getX(), sectionPos.getY() + 1);
            }

            if (sections.containsKey(sectionPos)) {
                section = sections.get(sectionPos);
            } else {
                Set<Position> newEntrances = new HashSet<>();
                Position left = new Position(sectionPos.getX() - 1, sectionPos.getY());
                Position right = new Position(sectionPos.getX() + 1, sectionPos.getY());
                Position top = new Position(sectionPos.getX(), sectionPos.getY() + 1);
                Position bottom = new Position(sectionPos.getX(), sectionPos.getY() - 1);
                if (sections.containsKey(left)) {
                    newEntrances.addAll(sections.get(left).getRightEntrances().stream().map(p -> new Position(0, p.getY())).collect(Collectors.toSet()));
                }
                if (sections.containsKey(right)) {
                    newEntrances.addAll(sections.get(right).getLeftEntrances().stream().map(p -> new Position(xGrid - 1, p.getY())).collect(Collectors.toSet()));
                }
                if (sections.containsKey(top)) {
                    newEntrances.addAll(sections.get(top).getBottomEntrances().stream().map(p -> new Position(p.getX(), yGrid - 1)).collect(Collectors.toSet()));
                }
                if (sections.containsKey(bottom)) {
                    newEntrances.addAll(sections.get(bottom).getTopEntrances().stream().map(p -> new Position(p.getX(), 0)).collect(Collectors.toSet()));
                }

                section = new Section(xGrid, yGrid, newEntrances);
                sections.put(sectionPos, section);
            }
            section.objects.put(player.getPosition(), player);
            evil = section.getRobot();
            updateTiles();
        }
    }

    private void updateTiles() {
        for (int row = 0; row < xGrid; row++) {
            for (int col = 0; col < yGrid; col++) {
                baseLayer.setCell(col, row, grass);
                Position pos = new Position(col, row);
                if (section.walls.getOrDefault(pos, false)) {
                    objectLayer.setCell(col, row, bush);
                } else if (section.objects.containsKey(pos)) {
                    objectLayer.setCell(col, row, cells.get(section.objects.get(pos).getType()));
                } else {
                    objectLayer.setCell(col, row, null);
                }
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.DPAD_RIGHT) {
            if (objectLayer.getCell(player.getPosition().getX() + 1, player.getPosition().getY()) == null) {
                objectLayer.setCell(player.getPosition().getX(), player.getPosition().getY(), null);
                section.objects.remove(player.getPosition());
                player.moveright();
                playerMoved();
                moveEvil();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_LEFT) {
            if (objectLayer.getCell(player.getPosition().getX() - 1, player.getPosition().getY()) == null) {
                objectLayer.setCell(player.getPosition().getX(), player.getPosition().getY(), null);
                section.objects.remove(player.getPosition());
                player.moveleft();
                playerMoved();
                moveEvil();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_UP) {
            if (objectLayer.getCell(player.getPosition().getX(), player.getPosition().getY() + 1) == null) {
                objectLayer.setCell(player.getPosition().getX(), player.getPosition().getY(), null);
                section.objects.remove(player.getPosition());
                player.moveup();
                playerMoved();
                moveEvil();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_DOWN) {
            if (objectLayer.getCell(player.getPosition().getX(), player.getPosition().getY() - 1) == null) {
                objectLayer.setCell(player.getPosition().getX(), player.getPosition().getY(), null);
                section.objects.remove(player.getPosition());
                player.movedown();
                playerMoved();
                moveEvil();
            } else {
                return false;
            }
        }
        objectLayer.setCell(player.getPosition().getX(), player.getPosition().getY(), players);

        if (keycode == Input.Keys.SPACE) {
            for(Map.Entry<Trash, TiledMapTileLayer.Cell> entry : trash_map.entrySet()){
                if ((objectLayer.getCell(player.getPosition().getX(), player.getPosition().getY() - 1) == entry.getValue() ||
                        objectLayer.getCell(player.getPosition().getX(), player.getPosition().getY() + 1) == entry.getValue() ||
                        objectLayer.getCell(player.getPosition().getX() + 1, player.getPosition().getY()) == entry.getValue() ||
                        objectLayer.getCell(player.getPosition().getX() - 1, player.getPosition().getY()) == entry.getValue()) &&
                        player.checkbag()) {
                    objectLayer.setCell(entry.getKey().getPosition().getX(), entry.getKey().getPosition().getY(), null);
                    player.pickup(entry.getKey());
                } else {
                    return false;
                }
            }
        }
        if (keycode == Input.Keys.ENTER) {
            if ((objectLayer.getCell(player.getPosition().getX(), player.getPosition().getY() - 1) == bins ||
                    objectLayer.getCell(player.getPosition().getX(), player.getPosition().getY() + 1) == bins ||
                    objectLayer.getCell(player.getPosition().getX() + 1, player.getPosition().getY()) == bins ||
                    objectLayer.getCell(player.getPosition().getX() - 1, player.getPosition().getY()) == bins) &&
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
            banana = new Trash();
            banana.setPosition(evil.getPosition().getX(),evil.getPosition().getY());
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