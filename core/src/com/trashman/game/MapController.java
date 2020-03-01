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

import java.util.*;
import java.util.stream.Collectors;

import static com.trashman.game.GameObject.*;

public class MapController extends TiledMap implements InputProcessor {
    private final int xSize;
    private final int ySize;
    private final int xGrid;
    private final int yGrid;
    // add the player
    private Player player;
    private Evil evil;
    HashMap<Trash, TiledMapTileLayer.Cell> trash_map = new HashMap<>();

    private Random random = new Random();


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
    private final List<GameObject> trashList = List.of(BANANA, BOTTLE, PAPER, CHEMICAL_WASTE);

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
        cells.put(PAPER, papers);
        cells.put(BOTTLE, bottles);
        cells.put(CHEMICAL_WASTE, chemicals);
        cells.put(BANANA, bananas);
        cells.put(EVIL_ROBOT, evils);
        cells.put(BIN_RED, bins);

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

        evil = section.getRobot();

        //initialize player
        player = new Player();
        section.placeObject(player);

        //initialize trash
//        trash_map.put(BANANA,bananas);
//        trash_map.put(PAPER,papers);
//        trash_map.put(BOTTLE,bottles);
//        trash_map.put(CHEMICAL_WASTE,chemicals);

        updateTiles();
    }

    private void playerMoved() {
        Position pos = player.getPosition();
        section.moveObject(player, pos);

        //Change the section
        if (pos.getX() == -1 || pos.getX() == xGrid || pos.getY() == -1 || pos.getY() == yGrid) {
            section.removeAt(pos);
            objectLayer.setCell(pos.getX(), pos.getY(), null);

            //Adjust the player position and section position
            if (pos.getX() == -1) {
                player.setX(xGrid - 1);
                sectionPos = new Position(sectionPos.getX() - 1, sectionPos.getY());
            } else if (pos.getX() == xGrid) {
                player.setX(0);
                sectionPos = new Position(sectionPos.getX() + 1, sectionPos.getY());
            } else if (pos.getY() == -1) {
                player.setY(yGrid - 1);
                sectionPos = new Position(sectionPos.getX(), sectionPos.getY() - 1);
            } else if (pos.getY() == yGrid) {
                player.setY(0);
                sectionPos = new Position(sectionPos.getX(), sectionPos.getY() + 1);
            }

            //reload section or create new section
            if (sections.containsKey(sectionPos)) {
                section = sections.get(sectionPos);
            } else {
                Set<Position> newEntrances = new HashSet<>();

                //ensure that entrances all align
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
            section.placeObject(player, player.getPosition());
            evil = section.getRobot();
        }
    }

    private void updateTiles() {
        for (int row = 0; row < xGrid; row++) {
            for (int col = 0; col < yGrid; col++) {
                baseLayer.setCell(col, row, grass);
                Position pos = new Position(col, row);
                if (section.walls.getOrDefault(pos, false)) {
                    objectLayer.setCell(col, row, bush);
                } else if (section.getObject(pos) != null) {
                    GameObject type = section.getObject(pos).getType();
                    objectLayer.setCell(col, row, cells.get(section.getObject(pos).getType()));
                } else {
                    objectLayer.setCell(col, row, null);
                }
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.DPAD_RIGHT ||
            keycode == Input.Keys.DPAD_LEFT ||
            keycode == Input.Keys.DPAD_UP ||
            keycode == Input.Keys.DPAD_DOWN) {

            Position newPos;
            if (keycode == Input.Keys.DPAD_RIGHT) {
                newPos = player.getPosition().add(1, 0);
            } else if (keycode == Input.Keys.DPAD_LEFT) {
                newPos = player.getPosition().add(-1, 0);
            } else if (keycode == Input.Keys.DPAD_UP) {
                newPos = player.getPosition().add(0, 1);
            } else {
                newPos = player.getPosition().add(0, -1);
            }

            if (section.isEmpty(newPos)) {
                section.moveObject(player, newPos);
                playerMoved();
                moveEvil();
            }
        }

        if (keycode == Input.Keys.SPACE) {

            Set<Position> neighbours = Set.of(
                    player.getPosition().add(1, 0),
                    player.getPosition().add(-1, 0),
                    player.getPosition().add(0, 1),
                    player.getPosition().add(0, -1)
            );

            for (Position pos : neighbours) {
                Item item = section.getObject(pos);
                if (item instanceof Trash) {
                    player.pickup((Trash) item);
                    section.removeObject(item);
                }
            }
        }

        if (keycode == Input.Keys.ENTER) {
            if ((objectLayer.getCell(player.getPosition().getX(), player.getPosition().getY() - 1) == bins ||
                    objectLayer.getCell(player.getPosition().getX(), player.getPosition().getY() + 1) == bins ||
                    objectLayer.getCell(player.getPosition().getX() + 1, player.getPosition().getY()) == bins ||
                    objectLayer.getCell(player.getPosition().getX() - 1, player.getPosition().getY()) == bins) &&
                    !player.checkbag()) {
                //objectLayer.setCell(bin.getPosition().getX(), bin.getPosition().getY(), null);
                player.putdown();
            }
        }

        updateTiles();

        return false;
    }

    //moving loop to move the evil figure
    public void moveEvil(){
        //setting moving values to 0
        int dx = random.nextInt(3) - 1;
        int dy = random.nextInt(3) - 1;

        Position newPos = new Position(dx, dy).add(evil.getPosition());

        //test if future evil position is free
        if (section.isEmpty(newPos)) {
            //decide that after every 3rd movement the robot leaves a banana
            Position oldPos = new Position(evil.getPosition().getX(), evil.getPosition().getY());
            section.moveObject(evil, newPos);
            dropTrash(oldPos);
        }
    }

    public void dropTrash(Position pos){
        counter++;
        if (counter%3 == 0){
            section.placeObject(new Trash(Trash.trashSet.get(random.nextInt(Trash.trashSet.size()))), pos);
        }

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