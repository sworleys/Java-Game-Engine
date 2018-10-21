package engine;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;

public class Renderable {
	private PShape shape;
	private int[] color;
	private int type;
	private PApplet inst;

	public Renderable(PApplet inst, int[] color, int type, float width, float height) {
		this.inst = inst;
		this.color = color;
		this.type = type;
		this.shape = inst.createShape(type, 0, 0, width, height);
		this.shape.setFill(inst.color(color[0], color[1], color[2]));
		this.shape.setStroke(false);
	}

	public PShape getShape() {
		return shape;
	}

	public void setShape(PShape shape) {
		this.shape = shape;
	}

	public int[] getColor() {
		return color;
	}

	public void setColor(int[] color) {
		this.color = color;
		this.shape.setFill(this.inst.color(color[0], color[1], color[2]));
	}
	
	public String getColorToString() {
		String res = "["
				+ Integer.toString(this.color[0]) + " "
				+ Integer.toString(this.color[1]) + " "
				+ Integer.toString(this.color[2]) + "]";
		return res;
	}
}
