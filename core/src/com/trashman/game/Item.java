package com.trashman.game;

public abstract class Item {
    protected Position position;
    private final GameObject type;

    Item(GameObject type) {
        this.type = type;
    }

    public void setPosition(int x, int y) {
        if (position == null) {
            position = new Position(x, y);
        } else {
            position.setX(x);
            position.setY(y);
        }
    }

    public void setPosition(Position pos) {
        position = pos;
    }


    public void setX(int x) {
        position.setX(x);
    }

    public void setY(int y) {
        position.setY(y);
    }

    public Position getPosition() {
        return position;
    }

    public GameObject getType() {
        return type;
    }
}
