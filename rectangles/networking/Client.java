package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import engine.GameObj;
import engine.Player;

public class Client implements Runnable {
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private boolean isStopped;
	private ExecutorService threadPool;
	private ConcurrentHashMap<UUID, GameObj> state;
	private Player player;

	private int numIter = 0;

	public Client(ExecutorService threadPool, Player player) {
		this.threadPool = threadPool;
		this.player = player;
		this.state = new ConcurrentHashMap<UUID, GameObj>();
		this.state.put(player.getUUID(), player);
	}
	
	public Client(Socket s, ExecutorService threadPool, Player player) {
		if (player != null) {
			this.player = player;
		}
		this.socket = s;
		this.state = new ConcurrentHashMap<>();
		this.threadPool = threadPool;
		try {
			this.input = new ObjectInputStream(this.getSocket().getInputStream());
		} catch (IOException e) {
			System.out.println("Error opening input stream for socket: " + this.socket.toString());
			e.printStackTrace();
		}
		try {
			this.output = new ObjectOutputStream(this.getSocket().getOutputStream());
		} catch (IOException e) {
			System.out.println("Error opening output stream for socket: " + this.socket.toString());
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return this.socket;
	}

	public int getNumIter() {
		return this.numIter;
	}
	
	public GameObj getPlayer() {
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
						this.threadPool.execute(new ClientRead(new Packet((HashMap<String, Object>) this.input.readObject()), this));
					} catch (EOFException | SocketException e) {
						// Ignore, client has just disconnected 
					} catch (ClassNotFoundException e) {
						System.out.println("Error on readObject:");
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

		public ClientRead(Packet p, Client client) {
			this.p = p;
			this.client = client;
		}

		@Override
		public void run() {
			switch (p.getType()) {
			case (Packet.PACKET_REGISTER):
				this.handleRegister();
				break;
			case (Packet.PACKET_CREATE):
				this.handleCreate();
				break;
			case (Packet.PACKET_DESTROY):
				this.handleDestroy();
				break;
			case (Packet.PACKET_UPDATE):
				this.handleUpdate();
				break;
			default:
				break;
			}
		}

		private void handleUpdate() {
			
		}

		private void handleDestroy() {
			// TODO Auto-generated method stub
			
		}

		private void handleCreate() {
			// TODO Auto-generated method stub
			
		}

		private void handleRegister() {
			// TODO Auto-generated method stub
			
		}

	}

	private class ClientWrite implements Runnable {
		private Packet p;

		public ClientWrite(Packet p) {
			this.p = p;
		}

		@Override
		public void run() {
			synchronized (this.getClient().output) {
				try {
					this.getClient().output.writeObject(p.getData());
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
}
