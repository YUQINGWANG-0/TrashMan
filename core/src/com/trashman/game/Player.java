package com.trashman.game;

public class Player extends Item {
    private Trash trash;
    // private boolean bag;

    public Player(){
        super(GameObject.HERO);
        trash = null;
    }

    public void pickup (Trash newtrash){
        if (bagEmpty()) {
            trash = newtrash;
        }
    }

    public boolean bagEmpty(){
        return trash == null;
    }

    public void putdown(){
        trash = null;
    }

    public Trash getTrash() {
        return trash;
    }
}
