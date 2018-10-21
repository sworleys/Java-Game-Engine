package engine;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
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
	public static CopyOnWriteArrayList<GameObj> objects = new CopyOnWriteArrayList<GameObj>();
	public static CopyOnWriteArrayList<GameObj> movObjects = new CopyOnWriteArrayList<GameObj>();
	public static ConcurrentHashMap<UUID, GameObj> objectMap = new ConcurrentHashMap<UUID, GameObj>();
	public static Spawn[] spawnPoints = new Spawn[2];
	public static Random generator = new Random();
	public static int deathPoints = 0;
	public static int perfIter = 0;
	public static final int MOV_OBJECTS = 1000;

	
	public static Player player;
	
	private boolean isServer;
	private Server server;
	private Client localClient;
	private GameObj floor;
	private GameObj ceiling;
	private GameObj leftWall;
	private GameObj rightWall;

	private ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);

	
	public Rectangles(boolean isServer) {
		this.isServer = isServer;
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
			
			float sqrDim = 50;

			// Spawn Points
			spawnPoints[0] =  new Spawn(this, width - sqrDim, 0 + sqrDim);
			spawnPoints[1] =  new Spawn(this, width - sqrDim, height - sqrDim);

			for (Spawn s : spawnPoints) {
				objects.add(s);
				objectMap.put(s.getUUID(), s);	
			}
			
			// Death Zone
			DeathZone dz_1 = new DeathZone(this, 0, 0);
			objects.add(dz_1);
			objectMap.put(dz_1.getUUID(), dz_1);
			
			// Player
			Spawn rand = spawnPoints[generator.nextInt(2)];
			player = new Player(this, sqrDim, rand.getPy().getLocation().x,
					rand.getPy().getLocation().y);

			this.server = new Server(this, 9200, this.threadPool, player);
			this.localClient = this.server.getLocalClient();
			new Thread(this.server).start();
			
			this.server.newPacket(Packet.PACKET_CREATE, player);


			// Add screen boundaries
			this.floor = new Boundary(width, (float) 100, 0, height, true);
			this.ceiling = new Boundary(width, (float) 100, 0, -100, false);
			this.leftWall = new Boundary((float) 100, height, -100, 0, false);
			this.rightWall = new Boundary((float) 100, height, width, 0, false);

			objects.add(this.floor);
			objectMap.put(this.floor.getUUID(), this.floor);
			this.server.newPacket(Packet.PACKET_CREATE, this.floor);
			
			objects.add(this.ceiling);
			objectMap.put(this.ceiling.getUUID(), this.ceiling);
			this.server.newPacket(Packet.PACKET_CREATE, this.ceiling);

			objects.add(this.leftWall);
			objectMap.put(this.leftWall.getUUID(), this.leftWall);
			this.server.newPacket(Packet.PACKET_CREATE, this.leftWall);

			objects.add(this.rightWall);
			objectMap.put(this.rightWall.getUUID(), this.rightWall);
			this.server.newPacket(Packet.PACKET_CREATE, this.rightWall);




			// Platforms
			float pWidth = width / 5;
			float pHeight = 25;

			ArrayList<Platform> staticPlatforms = new ArrayList<Platform>();
			ArrayList<Platform> movPlatforms = new ArrayList<Platform>();

			//Platform static_1 = new Platform(this, pWidth, pHeight, width - 4*pWidth, 500, false);
			Platform static_1 = new Platform(this, pWidth, pHeight, width - pWidth, 100, false);

			
			staticPlatforms.add(static_1);

			for (int i=1; i <= MOV_OBJECTS; i++) {
				Platform mov = new Platform(this, pWidth, pHeight, width - 3*pWidth, generator.nextInt(320), false);
				movPlatforms.add(mov);
			}

			//Platform mov_1 = new Platform(this, pWidth, pHeight, width - 3*pWidth, 150, false);
			//Platform mov_2 = new Platform(this, pWidth, pHeight, width - 5*pWidth, 250, false);


			//movPlatforms.add(mov_1);
			//movPlatforms.add(mov_2);


			for (Platform p : movPlatforms) {
				p.getPy().setTopSpeed(10);
				p.getPy().setVelocity(new PVector(generator.nextInt(11), 0));
			}


			for (Platform p : staticPlatforms) {
				objects.add(p);
				objectMap.put(p.getUUID(), p);
				this.server.newPacket(Packet.PACKET_CREATE, p);

			}
			
			for (Platform p : movPlatforms) {
				objects.add(p);
				objectMap.put(p.getUUID(), p);
				movObjects.add(p);
				this.server.newPacket(Packet.PACKET_CREATE, p);
			}

	
		} else {
			try {
				this.localClient = new Client(this, new Socket("127.0.0.1", 9200), this.threadPool, null);
			} catch (IOException e) {
				System.out.println("Error opening local client socket");
				e.printStackTrace();
			}
			new Thread(this.localClient).start();
		}

	}

	public void draw() {
		background(0);
		if (this.isServer) {
			text("Deaths: " + deathPoints , 110, 40);
		}
		
		this.renderAll(objects);


		// Dummy Renderer?
		if (isServer) {
			// Update physics
			for (GameObj obj : movObjects) {
				obj.getPy().update(obj, objects);
			}
		}
		
		// Only run update to clients 30fps?
		if (frameCount % 1 == 0) {
			if (this.isServer) {
				this.server.updateClients();
			}
		}
		
		if (this.isServer && this.server.perfStarted) {
			perfIter++;
			if (perfIter >= 1000) {
				exit();
			}
		}

	}

	private void renderAll(CopyOnWriteArrayList<GameObj> objects) {
		for (GameObj obj : objects) {
			this.render(obj);;
		}
	}
	
	public void render(GameObj obj) {
		if (obj.getRend() != null) {
			shape(obj.getRend().getShape(), obj.getPy().getLocation().x, obj.getPy().getLocation().y);
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
				if (this.isServer) {
					player.getPy().setAccelerationX(-5);
				} else {
					Packet p = new Packet(keyCode, player.getUUID());
					this.localClient.write(p);
				}
			}
			if (keyCode == RIGHT) {
				if (this.isServer) {
					player.getPy().setAccelerationX(5);
				} else {
					Packet p = new Packet(keyCode, player.getUUID());
					this.localClient.write(p);
				}
			}
		}
		if (key == ' ') {
			if (this.isServer) {
				player.getPy().setAccelerationY(-20);
			} else {
				Packet p = new Packet(keyCode, player.getUUID());
				this.localClient.write(p);
			}
		}
	}
	
	public static void setPlayer(Player p) {
		player = p;
		objectMap.put(player.getUUID(), player);
		objects.add(player);
		movObjects.add(player);
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
