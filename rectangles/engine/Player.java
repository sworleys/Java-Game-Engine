package engine;

import processing.core.PShape;

public class Player extends GameObj {
	public Player(PShape shape, float x, float y) {
		super(shape.getWidth(), shape.getHeight(), 0, x, y, shape, false, false);
	}

	@Override
	public String getType() {
		return "player";
	}
}
