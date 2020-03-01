package com.trashman.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.trashman.game.Program;

import java.util.Scanner;

public class DesktopLauncher {
	public static void main (String[] arg) {
		System.out.println("#----------------------------------------------#");
		System.out.println("#*******************TrashMan*******************#");
		System.out.println("#----------------------------------------------#");
		System.out.println("Move: \t\tarrow keys");
		System.out.println("Pick up food: \tSPACE");
		System.out.println("Drop in bin: \tENTER");
		System.out.println("Kill robot: \tK");
		System.out.println();
		System.out.println("You have " + Program.limit/60000 + " minute(s).");
		System.out.println();
		System.out.println("Press enter to begin...");

		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		System.out.println();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "TrashMan";
		config.width = 640;
		config.height = 640;
		new LwjglApplication(new Program(), config);
	}
}
