package engine.time;

public interface Timeline {
	
	/* Start Timeline */
	public void start();
	
	/* Pause Timeline */
	public void pause();
	
	/* Unpause Timeline */
	public void unpause();
	
	/* Get Current time */
	public long getCurrentTime();

	/* Get tick size */
	public int getTickSize();
	
	/* Set tick size */
	public void setTickSize(int tickSize);
	
}
