package engine.events;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import engine.EngineObject;
import engine.GameObj;

public class EventManager implements Runnable {

	// Event Comparator implementation. Time is weighted highest, then event type
	private static Comparator<Event> eventCompare = new Comparator<Event>() {

		@Override
		public int compare(Event e1, Event e2) {
			if (e1.getTime() < e2.getTime()) {
				return -1;
			} else if (e1.getTime() == e2.getTime()) {
				return (e1.getType() - e2.getType());
			} else {
				return 1;
			}
		}
	};
	
	private HashMap<Integer, ArrayList<EngineObject>> registrar = new HashMap<>();
	private PriorityBlockingQueue<Event> eventQueue = new PriorityBlockingQueue<>(11, eventCompare);



	public void registerHandler(GameObj handler, int type) {
		this.registrar.get(type).add(handler);
	}
	
	public void raiseEvent(Event e) {
		this.eventQueue.add(e);
	}

	@Override
	public void run() {
		try {
			Event next = this.eventQueue.take();
			for (EngineObject handler : this.registrar.get(next.getType())) {
				handler.handleEvent(next);
			}
		} catch (InterruptedException e) {
			System.out.println("Event removal Interrupted");
			e.printStackTrace();
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
