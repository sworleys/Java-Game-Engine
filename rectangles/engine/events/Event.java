package engine.events;

import java.util.HashMap;
import java.util.UUID;

public class Event {
	public static final int EVENT_INPUT = 0;
	public static final int EVENT_DEATH = 1;
	public static final int EVENT_SPAWN = 2;
	public static final int EVENT_MOVEMENT = 3;
	public static final int EVENT_COLLISION = 4;
	public static final int EVENT_PHYSICS = 5;

	private int type = -1;
	
	private HashMap<String, Object> data;
	private long time;
	
	public Event(int type, long time, HashMap<String, Object> data) {
		this.type = type;
		this.time = time;
		this.data = data;
	}
	
	public int getType() {
		return this.type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public HashMap<String, Object> getData() {
		return data;
	}

	public void setData(HashMap<String, Object> data) {
		this.data = data;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
