package com.trashman.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    public static AssetManager manager;
    static {
        manager = new AssetManager();
        manager.load("sprites/banana.png", Texture.class);
        manager.load("sprites/bin_green.png", Texture.class);
        manager.load("sprites/bin_blue.png", Texture.class);
        manager.load("sprites/bin_red.png", Texture.class);
        manager.load("sprites/bin_yellow.png", Texture.class);
        manager.load("sprites/bottle.png", Texture.class);
        manager.load("sprites/bush.png", Texture.class);
        manager.load("sprites/chemical_waste.png", Texture.class);
        manager.load("sprites/evil_robot.png", Texture.class);
        manager.load("sprites/goodman_L.png", Texture.class);
        manager.load("sprites/goodman_R.png", Texture.class);
        manager.load("sprites/grass.png", Texture.class);
        manager.load("sprites/paper.png", Texture.class);
        manager.load("sprites/tree.png", Texture.class);
        manager.finishLoading();
    }
}
