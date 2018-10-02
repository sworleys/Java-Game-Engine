package networking;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

public class UpdateClients implements Runnable {

	protected CopyOnWriteArrayList<Client> clientArray;
	protected ExecutorService threadPool;
	
	UpdateClients(CopyOnWriteArrayList<Client> clientArray, ExecutorService threadPool) {
		this.clientArray = clientArray;
		this.threadPool = threadPool;
	}
	
	@Override
	public void run() {
		synchronized (clientArray) {
			for (Client client : this.clientArray) {
				
			}
		}
	}

}
