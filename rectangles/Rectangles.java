
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Rectangles extends PApplet {

	private GameObj square;
	private GameObj rectangle;
	
	private ArrayList<GameObj> objects = new ArrayList<GameObj>();

	public void settings() {
		size(640, 360);
	}

	public void setup() {
		background(0);
		frameRate(60);
		// Place square and rectangle in bottom corners of screen
		float sqrDim = 50;
		float rectWidth = 100;
		float rectHeight = 50;
		PShape sqr = createShape(RECT, 0, 0, sqrDim, sqrDim);
		sqr.setFill(color(random(255), random(255), random(255)));
		sqr.setStroke(false);
		
		PShape rect = createShape(RECT, 0, 0, rectWidth, rectHeight);
		rect.setFill(color(random(255), random(255), random(255)));
		rect.setStroke(false);
		
		this.square = new GameObj(sqrDim, sqrDim, 0, height - sqrDim, sqr);
		this.rectangle = new GameObj(100, 50, width - rectWidth, height - rectHeight, rect);

		// TODO: This will need to be reworked for server-client
		this.objects.add(this.rectangle);
		
	}

	public void draw() {
		background(0);
		// Update physics
		PVector newLoc = this.square.getPy().update(objects);
		// Render
		shape(this.square.getShape(), newLoc.x, newLoc.y);

	}

	public void keyPressed() {
		if (key == CODED) {
			if (keyCode == LEFT) {
				System.out.println("LEFT pressed");
				this.square.getPy().setAccelerationX(-1);
			}
			if (keyCode == RIGHT) {
				System.out.println("RIGHT pressed");
				this.square.getPy().setAccelerationX(1);
			}
		}
		if (key == ' ') {
			System.out.println("SPACE pressed");
			this.square.getPy().setAccelerationY(20);
		}
	}

	// API stuff from https://happycoding.io/tutorials/java/processing-in-java

	public static void main(String[] args) {
		String[] processingArgs = {"Rectangles"};
		Rectangles sketch = new Rectangles();
		PApplet.runSketch(processingArgs, sketch);
	}
}
