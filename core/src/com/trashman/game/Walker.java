package com.trashman.game;

import com.badlogic.gdx.scenes.scene2d.ui.List;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Walker {
    public static Set<Position> createWalk(Position start, Position end) {
        Set<Position> toReturn = new HashSet<>();
        toReturn.add(start);

        Position newEnd = end;

        int dx = end.getX() - start.getX() + 1;
        int dy = end.getY() - start.getY() + 1;

        while (dx != 0 || dy != 0){
            Random rand = new Random();
            int nextInt = rand.nextInt(dx + dy);
            int xCurrentEnd = newEnd.getX();
            int yCurrentEnd = newEnd.getY();
            if (nextInt >= dx) { // it is dy that we decrement
                yCurrentEnd--;
                dy--;
            } else {
                xCurrentEnd--;
                dx--;
            }
            toReturn.add(end);
            newEnd = new Position(xCurrentEnd,yCurrentEnd);
        }
        return toReturn;
    }
}
