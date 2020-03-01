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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    private TiledMapTileLayer baseLayer;
    private TiledMapTileLayer objectLayer;
    private AssetManager manager = Assets.manager;

//    private Set<Position> entrances;
//    private Map<Position, Boolean> walls;
//    private Map<Position, Set<Position>> sectionEntrances = new HashMap<>();
//    private Map<Position, Map<Position, Boolean>> sectionWalls = new HashMap<>();
    private Position sectionPos;
    private Section section;
    private Map<Position, Section> sections;

    private TiledMapTileLayer.Cell grass = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bush = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell players = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bananas = new TiledMapTileLayer.Cell();
    private TiledMapTileLayer.Cell bins = new TiledMapTileLayer.Cell();

    private Map<GameObject, TiledMapTileLayer.Cell> cells = new HashMap<>();

    public MapController(int xGrid, int yGrid) {
        super();
        this.xGrid = xGrid;
        this.yGrid = yGrid;
        this.xSize = 32*xGrid;
        this.ySize = 32*yGrid;

        //initialize player
        this.player = new Player(new Position(0, 0));
        //initialize trash
        this.banana = new Trash(new Position(8,6));
        //initialize bin
        this.bin = new Bin(new Position(10,11));

        grass.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/grass.png", Texture.class))));
        bush.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bush.png", Texture.class))));
        players.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/goodman_L.png", Texture.class))));
        bananas.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/banana.png", Texture.class))));
        bins.setTile(new StaticTiledMapTile(new TextureRegion(manager.get("sprites/bin_red.png", Texture.class))));

        cells.put(HERO, players);
        cells.put(BANANA, bananas);
        cells.put(BIN_RED, bins);

        sectionPos = new Position(0, 0);

        MapLayers layers = getLayers();

        baseLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);
        objectLayer = new TiledMapTileLayer(xSize, ySize, 32, 32);

        layers.add(baseLayer);
        layers.add(objectLayer);
        Gdx.input.setInputProcessor(this);

        //createMap();
        section = new Section(xGrid, yGrid, new HashSet<>());
        sections = new HashMap<>();
        sections.put(sectionPos, section);

        Position playerPos = section.placeObject(HERO);
        player.setX(playerPos.getX());
        player.setY(playerPos.getY());

        section.placeObject(BANANA);

        updateTiles();
    }

    private void playerMoved() {
        Position pos = player.getposition();
        section.objects.put(pos, HERO);
        if (pos.getX() == -1 || pos.getX() == xGrid || pos.getY() == -1 || pos.getY() == yGrid) {
            section.objects.remove(pos);
            objectLayer.setCell(pos.getX(), pos.getY(), null);

            if (pos.getX() == -1) {
                objectLayer.setCell(xGrid - 1, pos.getY(), players);
                player.getposition().setX(xGrid - 1);
                sectionPos = new Position(sectionPos.getX() - 1, sectionPos.getY());
            } else if (pos.getX() == xGrid) {
                objectLayer.setCell(0, pos.getY(), players);
                player.getposition().setX(0);
                sectionPos = new Position(sectionPos.getX() + 1, sectionPos.getY());
            } else if (pos.getY() == -1) {
                objectLayer.setCell(pos.getX(), yGrid - 1, players);
                player.getposition().setY(yGrid - 1);
                sectionPos = new Position(sectionPos.getX(), sectionPos.getY() - 1);
            } else if (pos.getY() == yGrid) {
                objectLayer.setCell(pos.getX(), 0, players);
                player.getposition().setY(0);
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
            section.objects.put(player.getposition(), HERO);
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
                    objectLayer.setCell(col, row, cells.get(section.objects.get(pos)));
                } else {
                    objectLayer.setCell(col, row, null);
                }
            }
        }
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.DPAD_RIGHT) {
            if (objectLayer.getCell(player.getposition().getX() + 1, player.getposition().getY()) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                section.objects.remove(player.getposition());
                player.moveright();
                playerMoved();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_LEFT) {
            if (objectLayer.getCell(player.getposition().getX() - 1, player.getposition().getY()) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                section.objects.remove(player.getposition());
                player.moveleft();
                playerMoved();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_UP) {
            if (objectLayer.getCell(player.getposition().getX(), player.getposition().getY() + 1) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                section.objects.remove(player.getposition());
                player.moveup();
                playerMoved();
            } else {
                return false;
            }
        }
        if (keycode == Input.Keys.DPAD_DOWN) {
            if (objectLayer.getCell(player.getposition().getX(), player.getposition().getY() - 1) == null) {
                objectLayer.setCell(player.getposition().getX(), player.getposition().getY(), null);
                section.objects.remove(player.getposition());
                player.movedown();
                playerMoved();
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
