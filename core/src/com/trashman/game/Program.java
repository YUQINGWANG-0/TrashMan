package com.trashman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;

import java.io.BufferedReader;
import java.util.Scanner;

public class Program extends ApplicationAdapter {
	private OrthographicCamera camera;
	private TiledMapRenderer tiledMapRenderer;
	private int frameCount = 0;
	private MapController mapController;
	private long startTime;
	public static final long limit = 6*1000;
	private int prevSecs = -1;

	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false,w,h);
		camera.update();

		mapController = new MapController(20,20);
		tiledMapRenderer = new OrthogonalTiledMapRenderer(mapController);

		startTime = System.currentTimeMillis();
	}

	@Override
	public void render () {
		frameCount++;
		Gdx.gl.glClearColor(0.2f, 0.8f, 0.3f, 1.0f);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

		long elapsed = System.currentTimeMillis() - startTime;
		long remaining = limit - elapsed;
		int mins = (int) remaining/60000;
		int secs = (int) ((remaining - mins*60000)/1000);

		if (secs != prevSecs) {
			System.out.println("Time remaining: \t" + mins + ":" + (secs < 10 ? "0" : "") + secs + " \t" + mapController.getScoreToPrint());
		}
		prevSecs = secs;

		if (remaining <= 0) {
			Gdx.app.exit();
			System.out.println();
			System.out.println("#----------------------------------------------#");
			System.out.println("#*******************GAME OVER******************#");
			System.out.println("#----------------------------------------------#");
			System.out.println();
			System.out.println(mapController.getScoreToPrint());
		}
	}

	
	@Override
	public void dispose () {
	}
}
