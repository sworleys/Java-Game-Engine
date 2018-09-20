PShape square;

import processing.core.PApplet;
import Test;

public class Rectangles extends PApplet {

	public void settings() {
		Test hello;
		System.out.println(hello.testString);
		size(100, 100);
		square = createShape(RECT, 0, 0, 50, 50);
		square.setFill(int(0, 0, 255));
		square.setStroke(false);
	}

	public void draw() {
		shape(square, 25, 25);
	}

	//API stuff from https://happycoding.io/tutorials/java/processing-in-java

	public static void main(String[] args) {
		String processingArgs = "Rectangles";
		Rectangles sketch = new Rectangles();
		PApplet.runSketch(processingArgs, sketch);
	}
}
