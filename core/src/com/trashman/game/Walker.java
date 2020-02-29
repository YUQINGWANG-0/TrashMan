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