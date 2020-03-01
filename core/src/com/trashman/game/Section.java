package com.trashman.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Section {
    private final int xGrid;
    private final int yGrid;

    private Evil robot;

    private Random random = new Random();

    Set<Position> entrances;
    Map<Position, Boolean> walls;
    Map<Position, Item> objects = new HashMap<>();

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

        this.walls = MapGenerator.generate(xGrid, yGrid, entrances);

        robot = new Evil();
        placeObject(robot);
    }

    public void placeObject(Item item) {
        Position pos;
        while (true) {
            pos = new Position(random.nextInt(xGrid - 2) + 1, random.nextInt(yGrid - 2) + 1);
            if (!walls.getOrDefault(pos, false) && MapGenerator.isConnected(walls, pos)) {
                objects.put(pos, item);
                break;
            }
        }
        item.setPosition(pos);
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
}
