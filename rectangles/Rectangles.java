
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Rectangles extends PApplet {

	private boolean isServer;
	private ThreadPoolServer server;
	private ThreadPoolClient client;
	private ExecutorService threadPool;
	private GameObj floor;
	private GameObj ceiling;
	private GameObj leftWall;
	private GameObj rightWall;
	private GameObj square;
	private GameObj rectangle;

	private ArrayList<GameObj> objects = new ArrayList<GameObj>();

	public Rectangles(boolean isServer) {
		this.isServer = isServer;
	}
	
	public void settings() {
		size(640, 360);
	}

	public void setup() {
		background(0);
		frameRate(60);

		this.floor = new GameObj(width, (float) 100, 0, 0, height, null, true);
		this.ceiling = new GameObj(width, (float) 100, 0, 0, -100, null, false);
		this.leftWall = new GameObj((float) 100, height, 0, -100, 0, null, false);
		this.rightWall = new GameObj((float) 100, height, width, width, 0, null, false);

		this.objects.add(this.floor);
		this.objects.add(this.ceiling);
		this.objects.add(this.leftWall);
		this.objects.add(this.rightWall);

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

		this.square = new GameObj(sqrDim, sqrDim, 0, height - sqrDim - 2, 1, sqr, false);
		this.rectangle = new GameObj(rectWidth, rectHeight, 2, width - rectWidth, height - rectHeight, rect, false);

		// TODO: This will need to be reworked for server-client
		this.objects.add(this.rectangle);
		
		// Setup Server
		this.server = new ThreadPoolServer(9000, threadPool);
		new Thread(this.server).start();

	}

	public void draw() {
		background(0);
		// Walls
		rect((float) this.floor.getPy().getBounds2D().getX(), (float) this.floor.getPy().getBounds2D().getY(),
				(float) this.floor.getPy().getBounds2D().getWidth(), (float) this.floor.getPy().getBounds2D().getHeight());
		rect((float) this.ceiling.getPy().getBounds2D().getX(), (float) this.ceiling.getPy().getBounds2D().getY(),
				(float) this.ceiling.getPy().getBounds2D().getWidth(), (float) this.ceiling.getPy().getBounds2D().getHeight());
		rect((float) this.leftWall.getPy().getBounds2D().getX(), (float) this.leftWall.getPy().getBounds2D().getY(),
				(float) this.leftWall.getPy().getBounds2D().getWidth(), (float) this.leftWall.getPy().getBounds2D().getHeight());
		rect((float) this.rightWall.getPy().getBounds2D().getX(), (float) this.rightWall.getPy().getBounds2D().getY(),
				(float) this.rightWall.getPy().getBounds2D().getWidth(), (float) this.rightWall.getPy().getBounds2D().getHeight());

		// Update physics
		this.square.getPy().update(objects);

		// Render
		shape(this.square.getShape(), this.square.getPy().getLocation().x,
				this.square.getPy().getLocation().y);
		shape(this.rectangle.getShape(), this.rectangle.getPy().getLocation().x,
				this.rectangle.getPy().getLocation().y);

	}

	public void dispose() {
		System.out.println("Stopping Server");
		this.server.stop();
	}
	
	public void keyPressed() {
		if (key == CODED) {
			if (keyCode == LEFT) {
				this.square.getPy().setAccelerationX(-5);
			}
			if (keyCode == RIGHT) {
				this.square.getPy().setAccelerationX(5);
			}
		}
		if (key == ' ') {
			this.square.getPy().setAccelerationY(-20);
		}
	}

	// API stuff from https://happycoding.io/tutorials/java/processing-in-java

	public static void main(String[] args) {
		String[] processingArgs = {"Rectangles"};
		Rectangles sketch = new Rectangles(args[0].toLowerCase() == "server");
		PApplet.runSketch(processingArgs, sketch);
	}
}
