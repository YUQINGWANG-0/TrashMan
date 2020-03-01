package com.trashman.game;

public class Bin {
    private Position position;

    public Bin(Position position){
        this.position = position;
    }

    public Position getPosition(){
        return position;
    }

    public void setPosition(int x, int y) {
        position.setX(x);
        position.setY(y);
    }
}

