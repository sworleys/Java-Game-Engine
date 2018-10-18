package engine;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import networking.Client;
import networking.Packet;
import networking.Server;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;



public class Rectangles extends PApplet {

	public static final int NUM_THREADS = 5;

	private boolean isServer;
	private Server server;
	private Client localClient;
	private GameObj floor;
	private GameObj ceiling;
	private GameObj leftWall;
	private GameObj rightWall;
	private Player player;

	private ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
	private CopyOnWriteArrayList<GameObj> objects = new CopyOnWriteArrayList<GameObj>();
	private CopyOnWriteArrayList<GameObj> movObjects = new CopyOnWriteArrayList<GameObj>();
	private ConcurrentHashMap<UUID, GameObj> objectMap = new ConcurrentHashMap<UUID, GameObj>();
	
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

		
		// Setup Server
		if (this.isServer) {
			this.server = new Server(9200, this.threadPool, this.player);
			this.localClient = this.server.getLocalClient();
			new Thread(this.server).start();


			// Add screen boundaries
			this.floor = new Boundary(width, (float) 100, 0, 0, height, null, true, false);
			this.ceiling = new Boundary(width, (float) 100, 0, 0, -100, null, false, false);
			this.leftWall = new Boundary((float) 100, height, 0, -100, 0, null, false, false);
			this.rightWall = new Boundary((float) 100, height, width, width, 0, null, false, false);

			this.objects.add(this.floor);
			this.objectMap.put(this.floor.getUUID(), this.floor);
			this.server.newPacket(Packet.PACKET_CREATE, this.floor);
			
			this.objects.add(this.ceiling);
			this.objectMap.put(this.ceiling.getUUID(), this.ceiling);
			this.server.newPacket(Packet.PACKET_CREATE, this.ceiling);

			this.objects.add(this.leftWall);
			this.objectMap.put(this.leftWall.getUUID(), this.leftWall);
			this.server.newPacket(Packet.PACKET_CREATE, this.leftWall);

			this.objects.add(this.rightWall);
			this.objectMap.put(this.rightWall.getUUID(), this.rightWall);
			this.server.newPacket(Packet.PACKET_CREATE, this.rightWall);




			// Platforms
			PShape platformStatic = createShape(RECT, 0, 0, width / 5, 25);
			platformStatic.setFill(color(random(255), random(255), random(255)));
			platformStatic.setStroke(false);

			PShape platformMov = createShape(RECT, 0, 0, width / 5, 25);
			platformMov.setFill(color(random(255), random(255), random(255)));
			platformMov.setStroke(false);

			ArrayList<Platform> staticPlatforms = new ArrayList<Platform>();
			ArrayList<Platform> movPlatforms = new ArrayList<Platform>();

			for (Platform p : movPlatforms) {
				p.getPy().setTopSpeed(5);
				p.getPy().setVelocity(new PVector(5, 0));
			}

			Platform static_1 = new Platform(platformStatic, false, width - platformStatic.getWidth(), 100);
			Platform static_2 = new Platform(platformStatic, false, platformStatic.getWidth(), 100);

			staticPlatforms.add(static_1);
			staticPlatforms.add(static_2);

			Platform mov_1 = new Platform(platformStatic, false, width - platformStatic.getWidth(), 300);
			Platform mov_2 = new Platform(platformStatic, false, platformStatic.getWidth(), 300);

			movPlatforms.add(mov_1);
			movPlatforms.add(mov_2);

			for (Platform p : staticPlatforms) {
				this.objects.add(p);
				this.objectMap.put(p.getUUID(), p);
				this.server.newPacket(Packet.PACKET_CREATE, p);

			}
			
			for (Platform p : movPlatforms) {
				this.objects.add(p);
				this.objectMap.put(p.getUUID(), p);
				this.movObjects.add(p);
				this.server.newPacket(Packet.PACKET_CREATE, p);
			}

			// Player
			float sqrDim = 50;
			PShape sqr = createShape(RECT, 0, 0, sqrDim, sqrDim);
			sqr.setFill(color(random(255), random(255), random(255)));
			sqr.setStroke(false);
			this.player = new Player(sqr, height - sqrDim  - 2, 1);
			this.objectMap.put(this.player.getUUID(), this.player);
			this.movObjects.add(this.player);
			this.server.newPacket(Packet.PACKET_CREATE, this.player);
	
		} else {
			try {
				this.localClient = new Client(new Socket("127.0.0.1", 9200), this.threadPool, null);
			} catch (IOException e) {
				System.out.println("Error opening local client socket");
				e.printStackTrace();
			}
			new Thread(this.localClient).start();
		}

	}

	public void draw() {
		background(0);
		
		this.renderAll(objects);
		// Walls
		rect((float) this.floor.getPy().getBounds2D().getX(), (float) this.floor.getPy().getBounds2D().getY(),
				(float) this.floor.getPy().getBounds2D().getWidth(), (float) this.floor.getPy().getBounds2D().getHeight());
		rect((float) this.ceiling.getPy().getBounds2D().getX(), (float) this.ceiling.getPy().getBounds2D().getY(),
				(float) this.ceiling.getPy().getBounds2D().getWidth(), (float) this.ceiling.getPy().getBounds2D().getHeight());
		rect((float) this.leftWall.getPy().getBounds2D().getX(), (float) this.leftWall.getPy().getBounds2D().getY(),
				(float) this.leftWall.getPy().getBounds2D().getWidth(), (float) this.leftWall.getPy().getBounds2D().getHeight());
		rect((float) this.rightWall.getPy().getBounds2D().getX(), (float) this.rightWall.getPy().getBounds2D().getY(),
				(float) this.rightWall.getPy().getBounds2D().getWidth(), (float) this.rightWall.getPy().getBounds2D().getHeight());

		// Dummy Renderer?
		if (isServer) {
			// Update physics
			for (GameObj obj : movObjects) {
				obj.getPy().update(obj, this.objects);
			}
		}
		// Render
		shape(this.localClient.getPlayer().getShape(), this.localClient.getPlayer().getPy().getLocation().x,
				this.localClient.getPlayer().getPy().getLocation().y);

		
		// Only run update to clients 3fps?
		if (this.isServer && (frameCount % 20 == 0)) {
			this.server.updateClients();
		}

	}

	private void renderAll(CopyOnWriteArrayList<GameObj> objects) {
		for (GameObj obj : objects) {
			this.render(obj);;
		}
	}
	
	public void render(GameObj obj) {
		if (obj.getType().equals("boundary")) {
			rect((float) obj.getPy().getBounds2D().getX(), (float) obj.getPy().getBounds2D().getY(),
					(float) obj.getPy().getBounds2D().getWidth(), (float) obj.getPy().getBounds2D().getHeight());
		} else {
			shape(obj.getShape(), obj.getPy().getLocation().x, obj.getPy().getLocation().y);
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
				this.localClient.getPlayer().getPy().setAccelerationX(-5);
			}
			if (keyCode == RIGHT) {
				this.localClient.getPlayer().getPy().setAccelerationX(5);
			}
		}
		if (key == ' ') {
			this.localClient.getPlayer().getPy().setAccelerationY(-20);
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
