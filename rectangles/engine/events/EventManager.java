package engine.events;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.w3c.dom.css.Rect;

import engine.EngineObject;
import engine.GameObj;
import engine.Rectangles;

public class EventManager implements Runnable {

	// Event Comparator implementation. Time is weighted highest, then event type
	private static Comparator<Event> eventCompare = new Comparator<Event>() {

		@Override
		public int compare(Event e1, Event e2) {
			if (e1.getType() < e2.getType()) {
				return -1;
			} else if (e1.getType() == e2.getType()) {
				return (int) (e1.getTime() - e2.getTime());
			} else {
				return 1;
			}
		}
	};
	
	private HashMap<Integer, ArrayList<EngineObject>> registrar = new HashMap<>();
	private PriorityBlockingQueue<Event> eventQueue = new PriorityBlockingQueue<>(11, eventCompare);



	public void registerHandler(EngineObject handler, int type) {
		if (!this.registrar.containsKey(type)) {
			this.registrar.put(type, new ArrayList<EngineObject>());
		}
		this.registrar.get(type).add(handler);
	}
	
	public void raiseEvent(Event e) {
		//System.out.println(Rectangles.eventManager.getEventQueue().size());
		//System.out.println(e.getType() + " : " + Rectangles.objectMap.get(e.getData().get("caller")).getType());
		if (e.getType() == Event.EVENT_INPUT || !Rectangles.replay.isPlaying()) {
			this.eventQueue.add(e);
		}
	}

	@Override
	public void run() {
		Event next = this.eventQueue.poll();
		// System.out.println(next.getType() + " : " +
		// Rectangles.objectMap.get(next.getData().get("caller")).getType());
		// System.out.println(next.getData().getOrDefault("isReplay", false));
		if (next != null) {
			for (EngineObject handler : this.registrar.get(next.getType())) {
				handler.handleEvent(next);
				// Rectangles.threadPool.execute(handler.getReader(next));
			}
		}
	}

	public HashMap<Integer, ArrayList<EngineObject>> getRegistrar() {
		return registrar;
	}

	public void setRegistrar(HashMap<Integer, ArrayList<EngineObject>> registrar) {
		this.registrar = registrar;
	}

	public PriorityBlockingQueue<Event> getEventQueue() {
		return eventQueue;
	}

	public void setEventQueue(PriorityBlockingQueue<Event> eventQueue) {
		this.eventQueue = eventQueue;
	}
}
