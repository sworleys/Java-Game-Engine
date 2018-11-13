package engine;

import engine.events.Event;
import processing.core.PApplet;

public abstract class EngineObject extends PApplet {
	
	public abstract void handleEvent(Event e);
}
