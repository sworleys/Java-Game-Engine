package networking;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import engine.GameObj;
import processing.core.PApplet;

// From http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html

public class Server extends PApplet implements Runnable {

	protected ServerSocket serverSocket;
	protected Thread runningThread;
	protected CopyOnWriteArrayList<Client> clients;
	private Client localClient;

	protected int serverPort = 9000;
	protected boolean isStopped = false;
	private ExecutorService threadPool;

	public Server(int port, ExecutorService threadPool, GameObj player) {
		this.serverPort = port;
		this.threadPool = threadPool;
		this.clients = new CopyOnWriteArrayList<Client>();
		this.localClient = new Client(threadPool, player);
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
			GameObj playerCopy = this.localClient.getPlayer();
			Random r = new Random();
			playerCopy.getShape().setFill(color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
			
			Client client = new Client(clientSocket, this.threadPool, new GameObj(playerCopy.getObjWidth(),
					playerCopy.getObjHeight(), playerCopy.getPy().getMass(), playerCopy.getPy().getLocation().x,
					playerCopy.getPy().getLocation().y, playerCopy.getShape(), false, false));

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

	public Client getLocalClient() {
		return this.localClient;
	}

	public void updateClients() {
		this.threadPool.execute(new UpdateClients(this.clients, this.localClient));
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
	
	/*
	 * TODO: For now just updating all clients with numIter
	 */
	private class UpdateClients implements Runnable {

		private CopyOnWriteArrayList<Client> clients;
		private Client localState;

		public UpdateClients(CopyOnWriteArrayList<Client> clients, Client localState) {
			this.clients = clients;
			this.localState = localState;
		}

		@Override
		public void run() {
			for (Client client : this.clients) {
				client.write(this.localState.getNumIter());
			}
		}

	}

}
