package engine;

import engine.events.Event;
import processing.core.PApplet;
import processing.core.PShape;

public class Spawn extends GameObj {

	public Spawn(PApplet inst, float x, float y) {
		super(0, 0, 0, x, y, false, false);
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "spawn";
	}

	@Override
	public String toSerial() {
		String serial = "{"
				+ "x:" + this.getPy().getLocation().x + ","
				+ "y:" + this.getPy().getLocation().y + ","
				+ "}";
		return serial;
	}

	public static Spawn deSerial(PApplet inst, String serial) {
		float x = 0;
		float y = 0;

		serial = serial.replace("{", "").replace("}", "");
		String[] data = serial.split(",");

		for (String d : data) {
			String[] subData = d.split(":");
			String key = subData[0];
			String value = subData[1];

			switch(key) {
			case("x"):
				x = Float.parseFloat(value);
				break;
			case("y"):
				y = Float.parseFloat(value);
				break;
			default:
				break;
			}
		}
		Spawn res = new Spawn(inst, x, y);
		return res;
	}

	@Override
	public void handleEvent(Event e) {
		// Do nothing		
	}
}
