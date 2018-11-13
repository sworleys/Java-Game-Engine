package engine.time;

public class GlobalTimeline implements Timeline {

	private int tickSize;
	private long startTime;


	/* Is it paused? */
	private boolean isPaused = false;
	
	/* Accumulates paused time */
	private long pausedTotalTime = 0;

	/* Anchor for when timeline was paused */
	private long pausedTime = 0;

	/* Last time delta was calculated */
	private long lastTime = 0;

	/* Lock for pausing */
	private Object lock = new Object();


	public GlobalTimeline(int tickSize) {
		this.tickSize = tickSize;
	}

	@Override
	public void start() {
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void pause() {
		synchronized (this.lock) {
			if (!this.isPaused) {
				this.pausedTime = System.currentTimeMillis();
				this.isPaused = true;
			}
		}
	}

	@Override
	public void unpause() {
		synchronized (this.lock) {
			this.pausedTotalTime += (System.currentTimeMillis() - this.pausedTime);
			this.pausedTime = 0;
			this.isPaused = false;
		}
	}

	@Override
	public long getCurrentTime() {
		if (this.isPaused ) {
			return this.pausedTime;
		} else {
			long elapsedTime = (System.currentTimeMillis() - this.startTime) - this.pausedTotalTime;
			return elapsedTime / this.tickSize;
		}
	}

	@Override
	public int getTickSize() {
		return this.tickSize;
	}

	@Override
	public void setTickSize(int tickSize) {
		this.tickSize = tickSize;
	}

	@Override
	public long getAndResetDelta() {
		long currentTime = this.getCurrentTime();
		long delta = currentTime - this.lastTime;
		this.lastTime = currentTime;
		return delta;
	}
}
