package engine;

import processing.core.PShape;

public class Platform extends GameObj {
	private boolean movable;

	public Platform(PShape shape, boolean movable, float x, float y) {
		super(shape.getWidth(), shape.getHeight(), 0, x, y, shape, false, false);
		
		this.movable = movable;
	}

	@Override
	public String getType() {
		return "platform";
	}
	
	public boolean isMovable() {
		return this.movable;
	}

}
