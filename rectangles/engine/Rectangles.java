package engine;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import engine.events.Event;
import engine.events.EventManager;
import engine.time.GlobalTimeline;
import engine.time.LocalTimeline;
import engine.time.Timeline;
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
	public static Timeline globalTimeline = new GlobalTimeline(1);
	public static Timeline eventTimeline = new LocalTimeline(globalTimeline, 2);
	public static Timeline physicsTimeline = new LocalTimeline(globalTimeline, 2);
	public static Timeline networkTimeline = new LocalTimeline(globalTimeline, 12);
	public static Timeline renderTimeline = new LocalTimeline(globalTimeline, 12);
	public static EventManager eventManager = new EventManager();
	public static ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
	public static Replay replay = new Replay();


	public static Player player;
	
	private boolean isServer;
	private Server server;
	private Client localClient;
	private GameObj floor;
	private GameObj ceiling;
	private GameObj leftWall;
	private GameObj rightWall;

	private boolean setup = false;

	
	public Rectangles(boolean isServer) {
		this.isServer = isServer;
		System.out.println("Server: " + this.isServer);
		
		// Start timelines
		globalTimeline.start();
		physicsTimeline.start();
		networkTimeline.start();
		renderTimeline.start();
	}
	
	/**
	 * Just runs the game loop infinitely
	 */
	public void runLoop() {
		if (this.isServer) {
			// Register moving objects
			for (GameObj obj : movObjects) {
				eventManager.registerHandler(obj, Event.EVENT_COLLISION);
				eventManager.registerHandler(obj, Event.EVENT_MOVEMENT);
				eventManager.registerHandler(obj, Event.EVENT_PHYSICS);
				if (obj.getType() == "player") {
					eventManager.registerHandler(obj, Event.EVENT_INPUT);
					eventManager.registerHandler(obj, Event.EVENT_DEATH);
					eventManager.registerHandler(obj, Event.EVENT_SPAWN);
				}
				HashMap<String, Object> data = new HashMap<>();
				data.put("caller", obj.getUUID());
				Event e = new Event(Event.EVENT_PHYSICS,
						globalTimeline.getCurrentTime() + (long) physicsTimeline.getTickSize(), data);
				eventManager.raiseEvent(e);
			}
			
			// Register replays
			eventManager.registerHandler(replay, Event.EVENT_INPUT);
			eventManager.registerHandler(replay, Event.EVENT_MOVEMENT);
			eventManager.registerHandler(replay, Event.EVENT_END_REPLAY);

		}
		while(true) {
			this.gameLoop(globalTimeline.resetDelta());
		}
	}
	
	/**
	 * Single iteration of the game loop
	 * @param delta local time since last iteration
	 */
	private void gameLoop(boolean delta) {
		if (delta) {
			if (this.isServer) {
				this.updateEvent(eventTimeline.resetDelta());
				// this.updatePhysics(physicsTimeline.getAndResetDelta());
				this.updateNetwork(networkTimeline.resetDelta());
			}
			this.updateRender(renderTimeline.resetDelta());
		}
	}

	/* Update methods with deltas */
	// TODO: Determine if this is event the best way to do it...

	private void updateEvent(boolean delta) {
		if (delta) {
			threadPool.execute(eventManager);
		}
	}	

	private void updateRender(boolean delta) {
		if (delta) {
			this.redraw();	
		}
	}

	private void updateNetwork(boolean delta) {
		if (delta && this.isServer) {
			this.server.updateClients();
		}
	}

	private void updatePhysics(boolean delta) {
		// Dummy Renderer?
		if (delta && this.isServer) {
			// Update physics
			for (GameObj obj : movObjects) {
				obj.getPy().update(obj, objects);
			}
		}		
	}

	public void settings() {
		size(640, 360);
	}

	public void setup() {
		background(0);
		// Just set to be unreasonably high
		frameRate(1000);
		textSize(32);
		noLoop();


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

			Platform mov_1 = new Platform(this, pWidth, pHeight, width - 3*pWidth, 150, false);
			Platform mov_2 = new Platform(this, pWidth, pHeight, width - 5*pWidth, 250, false);


			movPlatforms.add(mov_1);
			movPlatforms.add(mov_2);

			for (Platform p : movPlatforms) {
				p.getPy().setTopSpeed(2);
				p.getPy().setVelocity(new PVector(2, 0));
			}
			
			Platform mov_3 = new Platform(this, pWidth, pHeight, 0, 100, false);
			mov_3.getPy().setTopSpeed(2);
			mov_3.getPy().setVelocity(new PVector(0, 2));
			movPlatforms.add(mov_3);


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
				this.localClient = new Client(this, new Socket("127.0.0.1", 9200), threadPool, null);
			} catch (IOException e) {
				System.out.println("Error opening local client socket");
				e.printStackTrace();
			}
			new Thread(this.localClient).start();
		}
		
		this.setup = true;
	}

	public void draw() {
		background(0);
		if (this.isServer) {
			text("Server", 110, 40);
		}

		this.renderAll(objects);
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
		if (keyCode == LEFT || keyCode == RIGHT || key == ' ' || key == 'r'
				|| key == 's' || key == '1' || key == '2' || key == '3') {
			if (this.isServer) {
				HashMap<String, Object> data = new HashMap<>();
				Event e = new Event(Event.EVENT_INPUT, globalTimeline.getCurrentTime(), data);
				data.put("keyCode", keyCode);
				data.put("caller", player.getUUID());
				eventManager.raiseEvent(e);
			} else {
				Packet p = new Packet(keyCode, player.getUUID());
				this.localClient.write(p);
			}
		}

		if (key == 'p') {
			globalTimeline.pause();
			System.out.println("Paused");
		}

		if (key == 'u') {
			globalTimeline.unpause();
			System.out.println("Un-Paused");
		}
	}

	public boolean isSetup() {
		return this.setup;
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
		while (!sketch.isSetup()) {
			System.out.println("Waiting...");
		}
		sketch.runLoop();
	}

}
