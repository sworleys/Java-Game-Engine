package engine.time;

public class LocalTimeline implements Timeline {

	private int tickSize;
	public Timeline anchor;
	private long startTime;


	/* Is it paused? */
	private boolean isPaused = false;
	
	/* Accumulates paused time */
	private long pauseTotalTime = 0;

	/* Anchor for when timeline was paused */
	private long pausedTime = 0;


	public LocalTimeline(Timeline anchor, int tickSize) {
		this.anchor = anchor;
		this.tickSize = tickSize;
	}

	@Override
	public void start() {
		this.startTime = anchor.getCurrentTime();
	}

	@Override
	public void pause() {
		synchronized (this) {
			if (!this.isPaused) {
				this.pausedTime = anchor.getCurrentTime();
				this.isPaused = true;
			}
		}

	}

	@Override
	public void unpause() {
		this.pausedTime += (anchor.getCurrentTime() - this.pausedTime);
		this.pausedTime = 0;
		this.isPaused = true;
	}

	@Override
	public long getCurrentTime() {
		long elapsedTime = (this.anchor.getCurrentTime() - this.startTime) - this.pauseTotalTime;
		return elapsedTime / this.tickSize;
	}

	@Override
	public int getTickSize() {
		return this.tickSize;
	}

	@Override
	public void setTickSize(int tickSize) {
		this.tickSize = tickSize;
	}

}
