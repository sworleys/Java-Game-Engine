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
				+ "rotation:" + this.getRotation() + ","
				+ "color:" + this.rend.getColorToString()
				+ "}";
		return serial;
	}
	
	/*
	 * Helper to get spawn for script
	 */
	public Spawn getRandomSpawn() {
		return Rectangles.spawnPoints[Rectangles.generator.nextInt(Rectangles.spawnPoints.length)];
	}
	
	public void draw() {
		//if (obj.getType() == "player") {
		this.getRend().getInst().pushMatrix();
		//this.translate(obj.getPy().getLocation().x,
			//obj.getPy().getLocation().y);
	//	if (obj.getRotation() != 0) {
		this.getRend().getInst().translate(this.getRend().getInst().width/2, this.getRend().getInst().height);
		this.getRend().getInst().rotate(PApplet.radians(getRotation()));
		//System.out.println(Math.cos(PApplet.radians(getRotation())) + ":" + Math.sin(PApplet.radians(getRotation())));
		//}
	//}
		this.getRend().getInst().shape(getRend().getShape(), 100, 0);
	//if (obj.getType() == "player") {
		this.getRend().getInst().popMatrix();
	//}
	}

	
	/**
	 * Aim vector based on the scope
	 * @return aim vector
	 */
	public float[] getAim() {
		float[] aim = {(float) Math.cos(PApplet.radians(getRotation())), (float) Math.sin(PApplet.radians(getRotation()))};
		return aim;
	}
	
	public static Player deSerial(PApplet inst, String serial) {
		float dim = 0;
		float x = 0;
		float y = 0;
		int rotation = 0;
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
			case("rotation"):
				rotation = Integer.parseInt(value);
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
		res.setRotation(rotation);
		res.rend.setColor(color);
		return res;
	}

}
