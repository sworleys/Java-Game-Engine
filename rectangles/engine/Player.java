package engine;


import java.util.HashMap;
import java.util.UUID;

import engine.events.Event;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

public class Player extends GameObj {
	private float dim;
	
	public Player(PApplet inst, float dim, float x, float y) {
		super(dim, dim, 0, x, y, false, true);
		this.dim = dim;
		int[] color = {(int) random(255), (int) random(255), (int) random(255)};
		this.rend = new Renderable(inst, color, PShape.RECT, dim, dim);
	}

	@Override
	public String getType() {
		return "player";
	}
	
	public float getDim() {
		return this.dim;
	}

	@Override
	public String toSerial() {
		String serial = "{"
				+ "dim:" + this.dim + ","
				+ "x:" + this.getPy().getLocation().x + ","
				+ "y:" + this.getPy().getLocation().y + ","
				+ "color:" + this.rend.getColorToString()
				+ "}";
		return serial;
	}
	
	/*
	 * Helper to get spawn for script
	 */
	public Spawn getRandomSpawn() {
		return Rectangles.spawnPoints[Rectangles.generator.nextInt(2)];
	}
	
	public static Player deSerial(PApplet inst, String serial) {
		float dim = 0;
		float x = 0;
		float y = 0;
		int[] color = new int[3];

		serial = serial.replace("{", "").replace("}", "");
		String[] data = serial.split(",");

		for (String d : data) {
			String[] subData = d.split(":");
			String key = subData[0];
			String value = subData[1];

			switch(key) {
			case("dim"):
				dim = Float.parseFloat(value);
				break;
			case("x"):
				x = Float.parseFloat(value);
				break;
			case("y"):
				y = Float.parseFloat(value);
				break;
			case("color"):
				value = value.replace("[", "").replace("]", "");
				int i = 0;
				for(String c : value.split(" ")) {
					color[i] = Integer.parseInt(c);
					i++;
				}
				break;
			default:
				break;
			}
		}
		Player res = new Player(inst, dim, x, y);
		res.rend.setColor(color);
		return res;
	}

}
