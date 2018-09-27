import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

// From http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html

public class ThreadPoolServer implements Runnable {

	protected ServerSocket serverSocket;
	protected Thread runningThread;
	protected ExecutorService threadPool;


	protected int serverPort = 9000;
	protected boolean isStopped = false;
	
	public ThreadPoolServer(int port, ExecutorService threadPool) {
		this.serverPort = port;
		this.threadPool = threadPool;
	}

	@Override
	public void run() {
		synchronized(this) {
			this.runningThread = Thread.currentThread();
		}
		this.openServerSocket();
		while (! this.isStopped()) {
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
			} catch (IOException e) {
				if (this.isStopped()) {
					break;
				}
				throw new RuntimeException("Error accepting client connection" + e);
			}
			this.threadPool.execute(new WorkerRunnable(clientSocket, "Thread Pooled Server"));
		}
		this.threadPool.shutdown();
		System.out.println("Server Stopped");
	}

	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
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

}
