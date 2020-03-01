package com.trashman.game;

import java.util.List;

import static com.trashman.game.GameObject.*;

public class Trash extends Item {

    public static final List<GameObject> trashSet = List.of(
            BANANA,
            PAPER,
            BOTTLE,
            CHEMICAL_WASTE
    );

    public Trash(GameObject type){
        super(type);
    }

    public Position getPosition(){
        return position;
    }
}
