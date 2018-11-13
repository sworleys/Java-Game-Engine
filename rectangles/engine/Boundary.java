package engine;

import engine.events.Event;

public class Boundary extends GameObj{

	public Boundary(float objWidth, float objHeight, float x, float y, boolean isFloor) {
		super(objWidth, objHeight, 0, x, y, isFloor, false);
	}

	@Override
	public String getType() {
		return "boundary";
	}
	
	@Override
	public String toSerial() {
		String serial = "{"
				+ "width:" + this.getObjWidth() + ","
				+ "height:" + this.getObjHeight() + ","
				+ "x:" + this.getPy().getLocation().x + ","
				+ "y:" + this.getPy().getLocation().y + ","
				+ "isFloor:" + this.isFloor()
				+ "}";
		return serial;
	}

	public static Boundary deSerial(String serial) {
		float width = 0;
		float height = 0;
		float x = 0;
		float y = 0;
		boolean isFloor = false;

		serial = serial.replace("{", "").replace("}", "");
		String[] data = serial.split(",");
		
		for (String d : data) {
			String[] subData = d.split(":");
			String key = subData[0];
			String value = subData[1];
			
			switch(key) {
			case("width"):
				width = Float.parseFloat(value);
				break;
			case("height"):
				height = Float.parseFloat(value);
				break;
			case("x"):
				x = Float.parseFloat(value);
				break;
			case("y"):
				y = Float.parseFloat(value);
				break;
			case("isFloor"):
				isFloor = Boolean.parseBoolean(value);
				break;
			default:
				break;
			}
		}
		Boundary res = new Boundary(width, height, x, y, isFloor);
		return res;
	}
	
	public void handleEvent(Event e) {
		// Do nothing
	}
}
