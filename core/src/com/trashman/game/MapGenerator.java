package com.trashman.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapGenerator {

    static Map<Position, Boolean> generate(int xGrid, int yGrid, Set<Position> entrances) {
        for(Position pos : entrances) {
            assert(pos.getX() == 0 || pos.getX() == xGrid - 1);
            assert(pos.getY() == 0 || pos.getY() == yGrid - 1);
        }

        Map<Position, Boolean> map = new HashMap<>();

        for (int x = 0; x < xGrid; x++) {
            if (!entrances.contains(new Position(x, 0))) {
                map.put(new Position(x, 0), true);
            }
            if (!entrances.contains(new Position(x, yGrid - 1))) {
                map.put(new Position(x, yGrid - 1), true);
            }
        }

        for (int y = 1; y < yGrid - 1; y++) {
            if (!entrances.contains(new Position(0, y))) {
                map.put(new Position(0, y), true);
            }
            if (!entrances.contains(new Position(xGrid - 1, y))) {
                map.put(new Position(xGrid - 1, y), true);
            }
        }

        map.put(new Position(2, 3), true);
        return map;
    }
}
