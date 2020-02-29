package com.trashman.game;

import java.util.HashMap;
import java.util.Map;

public class MapGenerator {

    static Map<Position, Boolean> generate(int xGrid, int yGrid) {
        Map<Position, Boolean> map =  new HashMap<>();
        map.put(new Position(1,1), true);
        return map;
    }
}
