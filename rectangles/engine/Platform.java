package engine;

import java.util.HashMap;
import java.util.UUID;

import engine.events.Event;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Platform extends GameObj {
	private boolean movable;
	private PApplet inst;
	private boolean queued = false;

	public Platform(PApplet inst, float width, float height, float x, float y, boolean movable) {
		super(width, height, 0, x, y, false, false);
		this.movable = movable;

		int[] color = {(int) random(255), (int) random(255), (int) random(255)};
		this.rend = new Renderable(inst, color, PShape.RECT, width, height);
	}

	@Override
	public String getType() {
		return "platform";
	}
	
	public boolean isQueued() {
		return this.queued;
	}
	
	public void setQueued(boolean val) {
		this.queued = val;
	}
	
	public void register() {
		Rectangles.eventManager.registerHandler(this, Event.EVENT_COLLISION);
		Rectangles.eventManager.registerHandler(this, Event.EVENT_COLLISION);
		Rectangles.eventManager.registerHandler(this, Event.EVENT_PHYSICS);
		Rectangles.eventManager.registerHandler(this, Event.EVENT_INPUT);
		Rectangles.eventManager.registerHandler(this, Event.EVENT_DEATH);
		Rectangles.eventManager.registerHandler(this, Event.EVENT_SPAWN);
	}
	
	public void registerPhysics() {
		Rectangles.eventManager.registerHandler(this, Event.EVENT_PHYSICS);
	}

	
	public float[] getAim() {
		return Rectangles.player.getAim();
	}
	
	public boolean isMovable() {
		return this.movable;
	}
	
	@Override
	public String toSerial() {
		String serial = "{"
				+ "width:" + this.getObjWidth() + ","
				+ "height:" + this.getObjHeight() + ","
				+ "x:" + this.getPy().getLocation().x + ","
				+ "y:" + this.getPy().getLocation().y + ","
				+ "movable:" + this.movable + ","
				+ "color:" + this.rend.getColorToString()
				+ "}";

		return serial;
	}

	public static Platform deSerial(PApplet inst, String serial) {
		float width = 0;
		float height = 0;
		float x = 0;
		float y = 0;
		boolean movable = false;
		int[] color = new int[3];

		serial = serial.replace("{", "").replace("}", "");
		String[] data = serial.split(",");
		
		for (String d : data) {
			String[] subData = d.split(":");
			String key = subData[0];
			String value = subData[1];
			
			switch(key) {
			case("width"):
				width = Float.parseFloat(value);
				break;
			case("height"):
				height = Float.parseFloat(value);
				break;
			case("x"):
				x = Float.parseFloat(value);
				break;
			case("y"):
				y = Float.parseFloat(value);
				break;
			case("movable"):
				movable = Boolean.parseBoolean(value);
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
		Platform res = new Platform(inst, width, height, x, y, movable);
		res.rend.setColor(color);
		return res;
	}
}
