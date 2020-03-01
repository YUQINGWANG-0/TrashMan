package com.trashman.game;

import java.util.*;
import java.util.stream.Collectors;

public class Section {
    private final int xGrid;
    private final int yGrid;

    private Evil robot;
    private Bin bin;

    private Random random = new Random();

    private Set<Position> entrances;
    private Set<Position> connected;
    private Set<Position> trees = new HashSet<>();
    private Map<Position, Boolean> walls;
    private Map<Position, Item> objects = new HashMap<>();

    public Section(int xGrid, int yGrid, Set<Position> entrances) {
        this.xGrid = xGrid;
        this.yGrid = yGrid;
        this.entrances = entrances;

        if (getLeftEntrances().size() == 0) {
            entrances.add(new Position(0, random.nextInt(yGrid - 2) + 1));
        }
        if (getRightEntrances().size() == 0) {
            entrances.add(new Position(xGrid - 1, random.nextInt(yGrid - 2) + 1));
        }
        if (getTopEntrances().size() == 0) {
            entrances.add(new Position(random.nextInt(xGrid - 2) + 1, yGrid - 1));
        }
        if (getBottomEntrances().size() == 0) {
            entrances.add(new Position(random.nextInt(xGrid - 2) + 1, 0));
        }

        walls = MapGenerator.generate(xGrid, yGrid, entrances);
        connected = MapGenerator.connected(walls, xGrid, yGrid);

        for (Position wall : walls.keySet()) {
            if (random.nextInt(100) < 5) {
                trees.add(wall);
            }
        }

        robot = new Evil();
        placeObject(robot);

        bin = new Bin();
        placeObject(bin);
    }


    public boolean isEmpty(Position pos) {
        return !(walls.getOrDefault(pos, false) || objects.getOrDefault(pos, null) != null);
    }

    public void placeObject(Item item) {
        Position pos;
        while (true) {
            pos = new Position(random.nextInt(xGrid - 2) + 1, random.nextInt(yGrid - 2) + 1);
            if (!walls.getOrDefault(pos, false) && objects.get(pos) == null && MapGenerator.isConnected(walls, connected, pos, xGrid, yGrid)) {
                objects.put(pos, item);
                break;
            }
        }
        item.setPosition(pos);
    }

    public void placeObject(Item item, Position pos) {
        item.setPosition(pos);
        objects.put(item.getPosition(), item);
    }

    public void moveObject(Item item, Position pos) {
        assert objects.get(pos) == item;

        objects.remove(item.getPosition());
        item.setPosition(pos);
        objects.put(item.getPosition(), item);
    }

    public void removeObject(Item item) {
        assert objects.get(item.getPosition()) == item;

        objects.remove(item.getPosition());
    }

    public void removeAt(Position pos) {
        objects.remove(pos);
    }

    public Item getObject(Position pos) {
        return objects.get(pos);
    }

    public Set<Position> getLeftEntrances() {
        return entrances.stream().filter(pos -> pos.getX() == 0).collect(Collectors.toSet());
    }

    public Set<Position> getRightEntrances() {
        return entrances.stream().filter(pos -> pos.getX() == xGrid - 1).collect(Collectors.toSet());
    }

    public Set<Position> getTopEntrances() {
        return entrances.stream().filter(pos -> pos.getY() == yGrid - 1).collect(Collectors.toSet());
    }

    public Set<Position> getBottomEntrances() {
        return entrances.stream().filter(pos -> pos.getY() == 0).collect(Collectors.toSet());
    }

    public Evil getRobot() {
        return robot;
    }

    public Bin getBin() {
        return bin;
    }

    public boolean isTree(Position pos) {
        return trees.contains(pos);
    }

    public boolean isWall(Position pos) {
        return walls.getOrDefault(pos, false);
    }
}
