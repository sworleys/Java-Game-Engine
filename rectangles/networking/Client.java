package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import engine.GameObj;
import engine.Player;
import engine.Rectangles;
import processing.core.PApplet;

public class Client implements Runnable {
	public AtomicBoolean isUpdate = new AtomicBoolean(false);
	
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private boolean isStopped;
	private ExecutorService threadPool;
	private ConcurrentHashMap<UUID, GameObj> state;
	private Player player;
	private PApplet inst;
	
	private int numIter = 0;

	public Client(PApplet inst, ExecutorService threadPool, Player player) {
		this.inst = inst;
		this.threadPool = threadPool;
		this.player = player;
		Rectangles.objectMap.put(player.getUUID(), player);
		Rectangles.objects.add(player);
		Rectangles.movObjects.add(player);
	}
	
	public Client(PApplet inst, Socket s, ExecutorService threadPool, Player player) {
		this.inst = inst;
		if (player != null) {
			this.player = player;
		}
		this.socket = s;
		this.state = new ConcurrentHashMap<>();
		this.threadPool = threadPool;
		try {
			this.output = new ObjectOutputStream(this.getSocket().getOutputStream());
		} catch (IOException e) {
			System.out.println("Error opening output stream for socket: " + this.socket.toString());
			e.printStackTrace();
		}
		try {
			this.input = new ObjectInputStream(this.getSocket().getInputStream());
		} catch (IOException e) {
			System.out.println("Error opening input stream for socket: " + this.socket.toString());
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return this.socket;
	}

	public int getNumIter() {
		return this.numIter;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public synchronized void setNumIter(int n) {
		this.numIter = n;
	}

	public synchronized void iterNumIter() {
		this.numIter++;
	}

	public synchronized ConcurrentHashMap<UUID, GameObj> getState() {
		return this.state;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/*
	 * Listening socket thread for each client (non-Javadoc) ExecutorService
	 * threadPool
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (!this.isStopped()) {
			try {
				// TODO: Just keeping track of a number for now
				synchronized (this.input) {
					try {
						Object recv = this.input.readObject();
						//System.out.println("Received: " + recv);
						this.threadPool.execute(new ClientRead(recv, this));
					} catch (EOFException | SocketException e) {
						// Ignore, client has just disconnected 
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				System.out.print("Error reading from socket: " + this.socket.toString() + ": " + e.toString());
				e.printStackTrace();
			}
		}
	}

	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.socket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing client socket", e);
		}
	}

	public void write(Packet p) {
		this.threadPool.execute(new ClientWrite(p));
	}

	private synchronized boolean isStopped() {
		return this.isStopped;
	}
	
	/**
	 * Handles data from server to local client
	 * 
	 * @author sworley
	 *
	 */
	private class ClientRead implements Runnable {
		private Packet p;
		private Client client;
		private Object recv;

		public ClientRead(Object recv, Client client) {
			this.recv = recv;
			this.client = client;
		}

		@Override
		public void run() {
			// Just force processing
			this.p = (Packet) this.recv;
			this.p.handlePacket(this.client.inst);
		}
	}

	private class ClientWrite implements Runnable, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Packet p;

		public ClientWrite(Packet p) {
			this.p = p;
		}

		@Override
		public void run() {
			synchronized (this.getClient().output) {
				try {
					//System.out.println("Sent: " + p.getSerialData());
					this.getClient().output.writeObject(p);
					this.getClient().output.flush();
				} catch (SocketException e) {
					// Ignore client has just disconnected
				} catch (IOException e) {
					System.out.println("Error writing to socket: " + this.getClient().socket.toString());
					e.printStackTrace();
				}
			}
		}

		public Client getClient() {
			return Client.this;
		}

	}

	public void updateServer() {
		if (isUpdate.compareAndSet(true, false)) {
			Packet p = new Packet(Packet.PACKET_UPDATE, Rectangles.player);
			this.threadPool.execute(new ClientWrite(p));
		}
	}
}
