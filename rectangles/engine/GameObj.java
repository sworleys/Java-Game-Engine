package engine;


import java.io.Serializable;
import java.util.UUID;

import processing.core.PApplet;

public abstract class GameObj extends PApplet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Physics py;
	protected Renderable rend;
	private float objWidth;
	private float objHeight;
	private boolean isFloor;

	private UUID uuid = UUID.randomUUID();

	public GameObj(float objWidth, float objHeight, float mass, float x, float y, 
			boolean isFloor, boolean isGrav) {
		this.isFloor = isFloor;
		this.objHeight = objHeight;
		this.objWidth = objWidth;
		this.py = new Physics(x, y, objWidth, objHeight, mass, 20, isGrav);
		
	}
	
	public abstract String getType();
	
	public abstract String toSerial();
	
	public Renderable getRend() {
		return this.rend;
	}

	public UUID getUUID() {
		return this.uuid;
	}
	
	public void setUUID(UUID id) {
		this.uuid = id;
	}
	
	public Physics getPy() {
		return py;
	}

	public void setPy(Physics py) {
		this.py = py;
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
