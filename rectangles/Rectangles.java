
import processing.core.PApplet;
import processing.core.PShape;

public class Rectangles extends PApplet {
	
	private PShape square;

	public void settings() {
		System.out.println(Test.testString);
		size(500, 500);
		//this.square = createShape(RECT, 0, 0, 50, 50);
		//this.square.setFill(color(0, 0, 255));
		//this.square.setStroke(false);
	}

	public void draw() {
		rect(30, 20, 25, 25);
	}

	//API stuff from https://happycoding.io/tutorials/java/processing-in-java

	public static void main(String[] args) {
		String[] processingArgs = {"Rectangles"};
		Rectangles sketch = new Rectangles();
		PApplet.runSketch(processingArgs, sketch);
	}
}
