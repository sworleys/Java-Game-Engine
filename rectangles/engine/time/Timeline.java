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
	
	/* Get delta time since last time serviced */
	public long getAndResetDelta();

	/* Get tick size */
	public int getTickSize();
	
	/* Set tick size */
	public void setTickSize(int tickSize);
	
}
