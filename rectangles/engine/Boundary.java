package engine;

import processing.core.PShape;

public class Boundary extends GameObj{

	public Boundary(float objWidth, float objHeight, float mass, float x, float y, PShape shape, boolean isFloor,
			boolean isGrav) {
		super(objWidth, objHeight, mass, x, y, shape, isFloor, isGrav);
	}

	@Override
	public String getType() {
		return "boundary";
	}

}
