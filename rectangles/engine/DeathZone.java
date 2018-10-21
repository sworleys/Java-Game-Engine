package engine;

import java.util.HashMap;

import processing.core.PApplet;

public class DeathZone extends GameObj {

	public DeathZone(PApplet inst, float x, float y) {
		super(100, 100, 0, x, y, false, false);
	}

	public DeathZone(HashMap<String, Object> data) {
		super(100, 100, 0, (float) data.get("x"), (float) data.get("y"), false, false);
	}

	@Override
	public String getType() {
		return "death-zone";
	}

	@Override
	public String toSerial() {
		String serial = "{"
				+ "x:" + this.getPy().getLocation().x + ","
				+ "y:" + this.getPy().getLocation().y + ","
				+ "}";
		return serial;
	}

	public static DeathZone deSerial(PApplet inst, String serial) {
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
		DeathZone res = new DeathZone(inst, x, y);
		return res;
	}
}
