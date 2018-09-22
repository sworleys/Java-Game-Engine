import processing.core.PShape;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class GameObj extends PApplet {
	private Physics py;
	private PShape shape;
	private float objWidth;
	private float objHeight;
	private boolean isFloor;

	public GameObj(float objWidth, float objHeight, float mass, float x, float y, PShape shape, 
			boolean isFloor) {
		this.isFloor = isFloor;
		this.py = new Physics(x, y, objWidth, objHeight, mass, 20);
		try {
			this.shape = shape;
		} catch (NullPointerException e) {
			// Do nothing, its probably screen limits
		}
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
