package engine;

import processing.core.PShape;

public class Platform extends GameObj {

	public Platform(PShape shape, boolean movable, float x, float y) {
		super(shape.getWidth(), shape.getHeight(), 0, x, y, shape, false, false);
	}

}
