package com.trashman.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MapGenerator {

    static Map<Position, Boolean> generate(int xGrid, int yGrid, Set<Position> entrances) {
        for(Position pos : entrances) {
            assert (pos.getX() == 0 || pos.getX() == xGrid - 1);
            assert (pos.getY() == 0 || pos.getY() == yGrid - 1);
        }

        Map<Position, Boolean> walls = new HashMap<>();

        for (int row = 0; row < yGrid; row++) {
            for (int col = 0; col < xGrid; col++) {
                walls.put(new Position(col, row), false);
            }
        }

        for (int x = 0; x < xGrid; x++) {
            if (!entrances.contains(new Position(x, 0))) {
                walls.put(new Position(x, 0), true);
            }
            if (!entrances.contains(new Position(x, yGrid - 1))) {
                walls.put(new Position(x, yGrid - 1), true);
            }
        }

        for (int y = 1; y < yGrid - 1; y++) {
            if (!entrances.contains(new Position(0, y))) {
                walls.put(new Position(0, y), true);
            }
            if (!entrances.contains(new Position(xGrid - 1, y))) {
                walls.put(new Position(xGrid - 1, y), true);
            }
        }

        for (int row = 1; row < yGrid - 1; row++) {
            for (int col = 1; col < xGrid - 1; col++) {
                if (new Random().nextInt(100) < 40) {
                    walls.put(new Position(col, row), true);
                }
            }
        }

        for (int i = 0; i < 50; i++) {
            for (int row = 2; row < yGrid - 2; row++) {
                for (int col = 2; col < xGrid - 2; col++) {
                    Position pos = new Position(col, row);
                    int count = getNeighbours(walls, pos, xGrid, yGrid);
                    if (count < 3) {
                        walls.put(pos, false);
                    } else if (count > 5) {
                        walls.put(pos, true);
                    }
                }
            }
        }

        //walls.put(new Position(2, 3), true);
        return walls;
    }

    static int getNeighbours(Map<Position, Boolean> walls, Position pos, int xGrid, int yGrid) {
        assert (0 < pos.getX() && pos.getX() < xGrid - 1);
        assert (0 < pos.getY() && pos.getY() < yGrid - 1);

        int count = walls.get(pos) ? -1 : 0;
        for (int y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
            for (int x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
                if (walls.get(new Position(x, y))) {
                    count++;
                }
            }
        }

        return count;
    }
}
