package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import engine.events.Event;
import engine.time.LocalTimeline;
import processing.core.PConstants;
import processing.core.PVector;

public class Replay extends EngineObject {
	
	private CopyOnWriteArrayList<Event> history = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<Event> startState = new CopyOnWriteArrayList<>();
	private LocalTimeline replayTimeline = new LocalTimeline(Rectangles.globalTimeline, 1);
	private boolean recording = false;
	private boolean playing = false;
	private Object lock = new Object();


	public CopyOnWriteArrayList<Event> getHistory() {
		return history;
	}

	public void setHistory(CopyOnWriteArrayList<Event> history) {
		this.history = history;
	}

	public LocalTimeline getReplayTimeline() {
		return replayTimeline;
	}

	public void setReplayTimeline(LocalTimeline replayTimeline) {
		this.replayTimeline = replayTimeline;
	}

	public boolean isRecording() {
		return recording;
	}

	public void setRecording(boolean recording) {
		this.recording = recording;
	}

	@Override
	public void handleEvent(Event e) {
		switch(e.getType()) {
		case(Event.EVENT_INPUT):
			switch((int) e.getData().get("keyCode")) {
			case('R'):
				synchronized (this.lock) {
					if (!this.recording && !this.playing) {
						this.recording = true;
						this.replayTimeline.start();
						this.startState.clear();
						this.history.clear();
						System.out.println("Started Recording");
					}
					
				}
				break;
			case('S'):
				synchronized (this.lock) {
					if (!this.playing) {
						this.recording = false;
						this.playing = true;
						Rectangles.physicsTimeline.pause();
						// Make start events time after history events, then queue them
						for (GameObj obj : Rectangles.movObjects) {
							HashMap<String, Object> data = new HashMap<>();
							// Setting time to 0 here, change when stop recording
							Event s = new Event(Event.EVENT_MOVEMENT, this.replayTimeline.getCurrentTime(), data);
							data.put("x", obj.getPy().getLocation().x);
							data.put("y", obj.getPy().getLocation().y);
							data.put("caller", obj.getUUID());
							this.history.add(s);
						}
						HashMap<String, Object> data = new HashMap<>();
						// Setting time to 0 here, change when stop recording
						Event end = new Event(Event.EVENT_END_REPLAY, this.replayTimeline.getCurrentTime(), data);
						data.put("caller", Rectangles.player.getUUID());
						this.history.add(end);
						Rectangles.eventTimeline = new LocalTimeline(Rectangles.globalTimeline, 8);
						Rectangles.eventTimeline.start();
						Rectangles.eventManager.getEventQueue().addAll(this.history);
						System.out.println("Started Replay");
					}
				}
				break;
			case('1'):
				Rectangles.eventTimeline = new LocalTimeline(Rectangles.globalTimeline, 8);
				Rectangles.eventTimeline.start();
				break;
			case('2'):
				Rectangles.eventTimeline = new LocalTimeline(Rectangles.globalTimeline, 2);
				Rectangles.eventTimeline.start();
				break;
			case('3'):
				Rectangles.eventTimeline = new LocalTimeline(Rectangles.globalTimeline, 1);
				Rectangles.eventTimeline.start();
				break;
			default:
				break;
			}
		case (Event.EVENT_MOVEMENT):
			synchronized (this.lock) {
				if (this.recording && !this.playing) {
					Event copy = new Event(e);
					copy.setTime(this.replayTimeline.getCurrentTime());
					copy.getData().put("isReplay", true);
					this.history.add(copy);
				}
			}
			break;
		case(Event.EVENT_END_REPLAY):
			synchronized (this.lock) {
				this.playing = false;
				Rectangles.eventTimeline = new LocalTimeline(Rectangles.globalTimeline, 2);
				Rectangles.eventTimeline.start();
				Rectangles.physicsTimeline.unpause();
			}
			break;
		default:
			break;
		}
	}

	public boolean isPlaying() {
		return this.playing;
	}
}
