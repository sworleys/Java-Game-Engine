package networking;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class Client implements Runnable {
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private boolean isStopped;
	private ExecutorService threadPool;
	private Client trackClient;
	
	private int numIter = 0;

	public Client(ExecutorService threadPool) {
		this.threadPool = threadPool;
		this.trackClient = this;
	}
	
	public Client(Socket s, Client trackClient, ExecutorService threadPool) {
		this.socket = s;
		if (trackClient != null) {
			this.trackClient = trackClient;
		} else {
			this.trackClient = this;
		}
		this.threadPool = threadPool;
		try {
			this.input = new DataInputStream(this.getSocket().getInputStream());
		} catch (IOException e) {
			System.out.println("Error opening input stream for socket: " + this.socket.toString());
			e.printStackTrace();
		}
		try {
			this.output = new DataOutputStream(this.getSocket().getOutputStream());
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
	
	public synchronized void setNumIter(int n) {
		this.numIter = n;
	}

	public synchronized void iterNumIter() {
		this.numIter++;
	}

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
					this.threadPool.execute(new ClientRead(this.input.readInt(), this.trackClient));
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

	public void write(int data) {
		this.threadPool.execute(new ClientWrite(data));
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
		private int data;
		private Client client;

		public ClientRead(int data, Client client) {
			this.data = data;
			this.client = client;
		}

		@Override
		public void run() {
			this.client.setNumIter(this.data);
		}

	}

	private class ClientWrite implements Runnable {
		private int data;

		public ClientWrite(int data) {
			this.data = data;
		}

		@Override
		public void run() {
			synchronized (this.getClient().output) {
				try {
					this.getClient().output.writeInt(this.data);
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
