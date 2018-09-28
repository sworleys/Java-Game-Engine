
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import processing.core.PApplet;
import processing.core.PShape;

public class Rectangles extends PApplet {

	private boolean isServer;
	private Server server;
	private Client localClient;
	private GameObj floor;
	private GameObj ceiling;
	private GameObj leftWall;
	private GameObj rightWall;
	private GameObj square;
	private GameObj rectangle;

	private ExecutorService threadPool = Executors.newFixedThreadPool(5);
	private ArrayList<GameObj> objects = new ArrayList<GameObj>();

	public Rectangles(boolean isServer) {
		this.isServer = isServer;
		System.out.println("Server: " + this.isServer);
	}
	
	public void settings() {
		size(640, 360);
	}

	public void setup() {
		background(0);
		frameRate(60);
		textSize(32);
		

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
		if (this.isServer) {
			this.server = new Server(9200, this.threadPool);
			this.localClient = this.server.getLocalClient();
			new Thread(this.server).start();
		} else {
			try {
				this.localClient = new Client(new Socket("127.0.0.1", 9200), null, threadPool);
			} catch (IOException e) {
				System.out.println("Error opening local client socket");
				e.printStackTrace();
			}
			new Thread(this.localClient).start();
		}

	}

	public void draw() {
		background(0);
		text(this.localClient.getNumIter(), 10, 40);
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
		
		// Only run update to clients 3fps?
		if (this.isServer && (frameCount % 20 == 0)) {
			this.server.updateClients();
		}

	}

	public void dispose() {
		if (this.isServer) {
			System.out.println("Stopping Server");
			this.server.stop();
		} else {
			this.localClient.stop();
		}
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
		if (key == 'q') {
			System.out.println("Q Pressed");
			this.localClient.iterNumIter();
			if (!this.isServer) {
				this.localClient.write(this.localClient.getNumIter());
			}
		}
	}

	// API stuff from https://happycoding.io/tutorials/java/processing-in-java

	public static void main(String[] args) {
		String[] processingArgs = {"Rectangles"};
		Rectangles sketch;
		if (args.length > 0) {
			sketch = new Rectangles(args[0].toLowerCase().equals("server"));
		} else {
			sketch = new Rectangles(false);
		}
		PApplet.runSketch(processingArgs, sketch);
	}
}
