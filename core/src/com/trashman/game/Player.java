package com.trashman.game;

public class Player {
    private Position position;
    private Trash trash;
    // private boolean bag;

    public Player(Position position){
        this.position = position;
        trash = null;
    }

    public void pickup (Trash newtrash){
        trash = newtrash;

    }

    public void putdown(){

    }

    public Position getposition(){
        return position;
    }

    public void moveleft(){
        // depend on keyboard input
        position.setX(position.getX()-1);
    }
    public void moveright(){
        // depend on keyboard input
        position.setX(position.getX()+1);
    }
    public void moveup(){
        // depend on keyboard input
        position.setY(position.getY()+1);
    }
    public void movedown(){
        // depend on keyboard input
        position.setY(position.getY()-1);
    }

    public boolean fullbag(){
        if (trash == null){
            return false;
        }
        else {
            return true;
        }
    }
}
