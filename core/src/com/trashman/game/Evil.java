package com.trashman.game;

public class Evil {
    private Position position;


    public Evil(Position position) {
        this.position = position;
    }

    public void setPosition(int xval, int yval){
        position.setX(xval);
        position.setY(yval);
    }

    public Position getPosition() {
        return position;
    }

    public void move(){

    }

    public void throwTrash(){

    }
}
