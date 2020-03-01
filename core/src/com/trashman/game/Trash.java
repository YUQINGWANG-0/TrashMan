package com.trashman.game;

public class Trash extends Item {

    public Trash(){
        super(GameObject.BANANA);
    }

    public Position getPosition(){
        return position;
    }
}
