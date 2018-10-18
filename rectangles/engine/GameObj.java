package engine;

import processing.core.PShape;

import java.util.UUID;

import processing.core.PApplet;

public abstract class GameObj extends PApplet {
	private Physics py;
	private PShape shape;
	private float objWidth;
	private float objHeight;
	private boolean isFloor;
	private UUID uuid;

	public GameObj(float objWidth, float objHeight, float mass, float x, float y, PShape shape, 
			boolean isFloor, boolean isGrav) {
		this.isFloor = isFloor;
		this.objHeight = height;
		this.objWidth = width;
		this.py = new Physics(x, y, objWidth, objHeight, mass, 20, isGrav);
		this.uuid = UUID.randomUUID();
		try {
			this.shape = shape;
		} catch (NullPointerException e) {
			// Do nothing, its probably screen limits
		}
		
	}
	
	public abstract String getType();

	public UUID getUUID() {
		return this.uuid;
	}
	
	public Physics getPy() {
		return py;
	}

	public void setPy(Physics py) {
		this.py = py;
	}

	public PShape getShape() {
		return shape;
	}

	public void setShape(PShape shape) {
		this.shape = shape;
	}

	public float getObjWidth() {
		return objWidth;
	}

	public void setObjWidth(float objWidth) {
		this.objWidth = objWidth;
	}

	public float getObjHeight() {
		return objHeight;
	}

	public void setObjHeight(float objHeight) {
		this.objHeight = objHeight;
	}

	public boolean isFloor() {
		return this.isFloor;
	}
}
