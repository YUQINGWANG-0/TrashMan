package com.trashman.game;

public class Evil extends Item {

    private boolean dead = false;

    public Evil() {
        super(GameObject.EVIL_ROBOT);
    }

    public void kill() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }
}
