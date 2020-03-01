package com.trashman.game;

public class Player extends Item {
    private Trash trash;
    // private boolean bag;

    public Player(){
        super(GameObject.HERO);
        trash = null;
    }

    public void pickup (Trash newtrash){
        trash = newtrash;

    }

    public boolean checkbag(){
        if (trash == null){
            return true;
        }
        else {
            return false;
        }
    }

    public void putdown(){
        trash = null;
    }

    public boolean emptybag(){
        if (trash == null){
            return false;
        }
        else {
            return true;
        }
    }
}
