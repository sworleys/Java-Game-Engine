package engine;

import engine.events.Event;
import processing.core.PApplet;

public abstract class EngineObject extends PApplet {
	
	public abstract void handleEvent(Event e);
	
	public EventRead getReader (Event e) {
		return new EventRead(e);
	}
	
	private class EventRead implements Runnable {
		private Event e;
		
		public EventRead(Event e) {
			this.e = e;
		}

		@Override
		public void run() {
			handleEvent(e);
		}
	}
}
