
import processing.core.PApplet;
import processing.core.PShape;

public class Rectangles extends PApplet {

	private ShapeObj square;
	private ShapeObj rectangle;

	public void settings() {
		size(640, 360);
	}

	public void setup() {
		background(0);
		frameRate(60);
		// Place square and rectangle in bottom corners of screen
		this.square = new ShapeObj(50, 50, 0, height - 50);
		this.rectangle = new ShapeObj(100, 50, this.width - 100, this.height - 50);

	}

	public void draw() {
		background(0);
		this.square.update(1/60);
		System.out.println("(" + this.square.py.getxPos() + ", " +
				this.square.py.getyPos() + ")");
	}

	public void keyPressed() {
		if (key == CODED) {
			if (keyCode == LEFT) {
				System.out.println("LEFT pressed");
				this.square.py.setxA(-1);
			}
			if (keyCode == RIGHT) {
				System.out.println("RIGHT pressed");
				this.square.py.setxA(1);
			}
		}
		if (key == ' ') {
			System.out.println("SPACE pressed");
			this.square.py.setyA(20);
		}
	}

	// API stuff from https://happycoding.io/tutorials/java/processing-in-java

	public static void main(String[] args) {
		String[] processingArgs = {"Rectangles"};
		Rectangles sketch = new Rectangles();
		PApplet.runSketch(processingArgs, sketch);
	}

	private class ShapeObj {
		public Physics py;
		public PShape shape;

		public ShapeObj(float width, float height, float x, float y) {
			this.py = new Physics(x, y, width, height);
			this.shape = createShape(RECT, 0, 0, width, height);
			this.shape.setFill(color(random(255), random(255), random(255)));
			this.shape.setStroke(false);
			shape(this.shape, x, y);
		}

		public void update(float t) {
			this.py.updateX(t);
			this.py.updateY(t);
			shape(this.shape, this.py.getxPos(), this.py.getyPos());
		}

		public void setPos(float x, float y) {
			this.py.setxPos(x);
			this.py.setyPos(y);
			shape(this.shape, x, y);
		}
		
	}
}
