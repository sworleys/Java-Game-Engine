import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

// Code used for testing my thread pool
// From https://www.programcreek.com/java-api-examples/index.php?source_dir=Android_H264Stream-master/src/tw/jwzhuang/ipcam/server/WorkerRunnable.java
public class WorkerRunnable implements Runnable {

	 protected Socket clientSocket = null; 
	 protected String serverText = null; 
	 
	 public WorkerRunnable(Socket clientSocket, String serverText) { 
	  this.clientSocket = clientSocket; 
	  this.serverText = serverText; 
	 } 
	 
	 @Override 
	 public void run() { 
	  try { 
	   InputStream input = clientSocket.getInputStream(); 
	   OutputStream output = clientSocket.getOutputStream(); 
	   long time = System.currentTimeMillis(); 
	   output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " 
	     + this.serverText + " - " + time + "").getBytes()); 
	   output.close(); 
	   input.close(); 
	   System.out.println("Request processed: " + time); 
	  } catch (IOException e) { 
	   // report exception somewhere. 
	   e.printStackTrace(); 
	  } 
	 }
}
