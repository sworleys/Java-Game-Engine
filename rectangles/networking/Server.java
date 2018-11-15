package networking;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;


import engine.GameObj;
import engine.Player;
import engine.Rectangles;
import engine.Spawn;
import engine.events.Event;
import processing.core.PApplet;

// From http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html

public class Server extends PApplet implements Runnable {

	protected ServerSocket serverSocket;
	protected Thread runningThread;
	protected CopyOnWriteArrayList<Client> clients;
	protected ConcurrentLinkedQueue<Packet> packetQueue;
	private Client localClient;
	private PApplet inst;

	protected int serverPort = 9000;
	protected boolean isStopped = false;
	private ExecutorService threadPool;

	public Server(PApplet inst, int port, ExecutorService threadPool, Player player) {
		this.inst = inst;
		this.serverPort = port;
		this.threadPool = threadPool;
		this.clients = new CopyOnWriteArrayList<Client>();
		this.packetQueue = new ConcurrentLinkedQueue<Packet>();
		this.localClient = new Client(this.inst, threadPool, player);
	}

	@Override
	public void run() {
		synchronized (this) {
			this.runningThread = Thread.currentThread();
		}
		this.openServerSocket();
		while (!this.isStopped()) {
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
			} catch (IOException e) {
				if (this.isStopped()) {
					break;
				}
				throw new RuntimeException("Error accepting client connection" + e);
			}
			Random r = Rectangles.generator;
			
			Spawn s = Rectangles.spawnPoints[r.nextInt(2)];

			Client client = new Client(this.inst, clientSocket, this.threadPool, new Player(inst, Rectangles.player.getDim(),
					s.getPy().getLocation().x, s.getPy().getLocation().y));

			for (GameObj obj : Rectangles.objects) {
				Packet p = new Packet(Packet.PACKET_CREATE, obj);
				client.write(p);
			}
			Packet p = new Packet(Packet.PACKET_CREATE, client.getPlayer());
			for (Client c : this.clients) {
				c.write(p);
			}
			Rectangles.objectMap.put(client.getPlayer().getUUID(), client.getPlayer());
			Rectangles.objects.add(client.getPlayer());
			Rectangles.movObjects.add(client.getPlayer());
			
			// Register player for events
			Rectangles.eventManager.registerHandler(client.getPlayer(), Event.EVENT_INPUT);
			Rectangles.eventManager.registerHandler(client.getPlayer(), Event.EVENT_DEATH);
			Rectangles.eventManager.registerHandler(client.getPlayer(), Event.EVENT_SPAWN);
			
			p = new Packet(Packet.PACKET_REGISTER, client.getPlayer());
			client.write(p);
			synchronized (this.clients) {
				this.clients.add(client);
			}
			new Thread(client).start();
		}
		System.out.println("Server Stopped");
	}

	public synchronized void stop() {
		this.isStopped = true;
		for (Client client : clients) {
			client.stop();
		}
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}

	public void newPacket(int type, GameObj obj) {
		this.packetQueue.add(new Packet(type, obj));
	}


	public Client getLocalClient() {
		return this.localClient;
	}

	public void updateClients() {
		this.threadPool.execute(new UpdateClients(this.clients));
	}

	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port " + this.serverPort + ":" + e);
		}
	}
	

	private class UpdateClients implements Runnable {

		private CopyOnWriteArrayList<Client> clients;

		public UpdateClients(CopyOnWriteArrayList<Client> clients) {
			this.clients = clients;
		}

		@Override
		public void run() {
			for (GameObj obj : Rectangles.movObjects) {
				Packet p = new Packet(Packet.PACKET_UPDATE, obj);
				for (Client client : this.clients) {
					client.write(p);
				}
			}
		}

	}

}
