package com.trashman.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class TrashManAssetManager extends AssetManager {
    public TrashManAssetManager() {
        super();
        load("sprites/banana.png", Texture.class);
        load("sprites/bin-green.png", Texture.class);
        finishLoading();
    }
}
