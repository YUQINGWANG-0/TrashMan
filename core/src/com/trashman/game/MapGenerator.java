package com.trashman.game;

import java.util.*;

import static java.lang.StrictMath.sqrt;

public class MapGenerator {

    static Map<Position, Boolean> generate(int xGrid, int yGrid, Set<Position> entrances) {
        for(Position pos : entrances) {
            assert (pos.getX() == 0 || pos.getX() == xGrid - 1);
            assert (pos.getY() == 0 || pos.getY() == yGrid - 1);
        }

        Map<Position, Boolean> walls = new HashMap<>();
        //boolean[][] walls = new boolean[yGrid][xGrid];

        //fill with grass
        for (int row = 0; row < yGrid; row++) {
            for (int col = 0; col < xGrid; col++) {
                walls.put(new Position(col, row), false);
            }
        }

        //fill top/bottom walls
        for (int x = 0; x < xGrid; x++) {
            if (!entrances.contains(new Position(x, 0))) {
                walls.put(new Position(x, 0), true);
            }
            if (!entrances.contains(new Position(x, yGrid - 1))) {
                walls.put(new Position(x, yGrid - 1), true);
            }
        }

        //fill left/right walls
        for (int y = 1; y < yGrid - 1; y++) {
            if (!entrances.contains(new Position(0, y))) {
                walls.put(new Position(0, y), true);
            }
            if (!entrances.contains(new Position(xGrid - 1, y))) {
                walls.put(new Position(xGrid - 1, y), true);
            }
        }

        //randomly place walls
        for (int row = 1; row < yGrid - 1; row++) {
            for (int col = 1; col < xGrid - 1; col++) {
                if (new Random().nextInt(100) < 60) {
                    walls.put(new Position(col, row), true);
                }
            }
        }

        //run the cell automaton
        for (int i = 0; i < 50; i++) {
            for (int row = 1; row < yGrid - 1; row++) {
                for (int col = 1; col < xGrid - 1; col++) {
                    Position pos = new Position(col, row);
                    int neighbours = countNeighbours(walls, pos, xGrid, yGrid);
                    int freeSpaces = 8 - neighbours;

                    if (freeSpaces < 3) {
                        walls.put(pos, true);
                    }
                    if (freeSpaces > 5) {
                        walls.put(pos, false);
                    }
                }
            }
        }

        //ensure entrances are connected
        List<Position> entranceList = new ArrayList<>(entrances);
        for (int i = 0; i < entranceList.size() - 1; i++) {
            for (int j = i + 1; j < entranceList.size(); j++) {
                Position start = entranceList.get(i);
                Position end = entranceList.get(j);
                start = new Position(
                        start.getX() == 0 ? 1 : start.getX() == xGrid - 1 ? xGrid - 2 : start.getX(),
                        start.getY() == 0 ? 1 : start.getY() == yGrid - 1 ? yGrid - 2 : start.getY()
                );
                end = new Position(
                        end.getX() == 0 ? 1 : end.getX() == xGrid - 1 ? xGrid - 2 : end.getX(),
                        end.getY() == 0 ? 1 : end.getY() == yGrid - 1 ? yGrid - 2 : end.getY()
                );
                Set<Position> path = createWalk(start, end);
                for (Position pos : path) {
                    walls.put(pos, false);
                }
            }
        }


        //walls.put(new Position(2, 3), true);
        return walls;
    }

    public static boolean isOnBoard(Position pos, int xGrid, int yGrid){
        return ((0 < pos.getX() && pos.getX() < xGrid - 1) && (0 < pos.getY() && pos.getY() < yGrid - 1));
    }

    public static List<Position> getEntrances(Map<Position, Boolean> walls, int xGrid, int yGrid){
        List<Position> entrances = new ArrayList<>();
        for (int xi = 0; xi <xGrid; xi++){
            if (!walls.getOrDefault(new Position(xi, 0), false)){
                entrances.add(new Position(xi, 0));
            }
            if (!walls.getOrDefault(new Position(xi, yGrid -1), false)){
                entrances.add(new Position(xi, yGrid -1));
            }
        }
        for (int yi = 0; yi <xGrid; yi++){
            if (!walls.getOrDefault(new Position(0, yi), false)){
                entrances.add(new Position(0, yi));
            }
            if (!walls.getOrDefault(new Position(xGrid -1, yi), false)){
                entrances.add(new Position(xGrid - 1, yi));
            }
        }
        return entrances;
    }

    public static Set<Position> connected(Map<Position, Boolean> walls, int xGrid, int yGrid){
        List<Position> entrances = getEntrances(walls, xGrid, yGrid);

        Position pos = entrances.get(0);
        Stack<Position> toExplore = new Stack<>();
        Set<Position> seen = new HashSet<>();
        toExplore.add(pos);
        while (!toExplore.empty()){
            Position node = toExplore.pop();
            seen.add(node);
            for (int i = -1; i < 2; i++){
                if (i == 0) continue;
                Position toAddX = new Position( node.getX() + i , node.getY() );
                if (isOnBoard( toAddX ,xGrid,yGrid) && !walls.getOrDefault(toAddX ,false) && !seen.contains(toAddX)){
                    toExplore.add(toAddX);
                }
                Position toAddY = new Position( node.getX(), node.getY() + i );
                if (isOnBoard( toAddY ,xGrid,yGrid) && !walls.getOrDefault(toAddY ,false) && !seen.contains(toAddY)){
                    toExplore.add(toAddY);
                }
            }
        }

        return seen;
    }

    public static boolean isConnected(Map<Position, Boolean> walls, Set<Position> connected, Position pos, int xGrid, int yGrid) {
        if (walls.getOrDefault(pos,false)) return false;
        if (!(0 < pos.getX() && pos.getX() < xGrid - 1) || !(0 < pos.getY() && pos.getY() < yGrid - 1)) return false;

        return connected.contains(pos);
    }

    private static int countNeighbours(Map<Position, Boolean> walls, Position pos, int xGrid, int yGrid) {
        assert (0 < pos.getX() && pos.getX() < xGrid - 1);
        assert (0 < pos.getY() && pos.getY() < yGrid - 1);

        int count = walls.get(pos) ? -1 : 0;
        for (int y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
            for (int x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
                if (walls.getOrDefault(new Position(x, y), false)) {
                    count++;
                }
            }
        }

        return count;
    }

    public static Set<Position> createWalk(Position start, Position end) {
        Set<Position> toReturn = new HashSet<>();
        toReturn.add(start);

        int dx = Math.abs(end.getX() - start.getX());
        int dy = Math.abs(end.getY() - start.getY());

        int currentX = start.getX();
        int currentY = start.getY();

        while (dx + dy > 0){
            Random rand = new Random();
            int nextInt = rand.nextInt(dx + dy);
            if (nextInt >= dx) { // it is dy that we decrement
                currentY += end.getY() > start.getY() ? 1 : -1;
                dy--;
            } else {
                currentX += end.getX() > start.getX() ? 1 : -1;
                dx--;
            }
            toReturn.add(new Position(currentX, currentY));
        }
        return toReturn;
    }
}
