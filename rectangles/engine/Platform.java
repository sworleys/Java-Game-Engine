package engine;

import java.util.HashMap;
import java.util.UUID;

import engine.events.Event;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Platform extends GameObj {
	private boolean movable;
	private PApplet inst;

	public Platform(PApplet inst, float width, float height, float x, float y, boolean movable) {
		super(width, height, 0, x, y, false, false);
		this.movable = movable;

		int[] color = {(int) random(255), (int) random(255), (int) random(255)};
		this.rend = new Renderable(inst, color, PShape.RECT, width, height);
	}

	@Override
	public String getType() {
		return "platform";
	}
	
	public boolean isMovable() {
		return this.movable;
	}
	
	@Override
	public String toSerial() {
		String serial = "{"
				+ "width:" + this.getObjWidth() + ","
				+ "height:" + this.getObjHeight() + ","
				+ "x:" + this.getPy().getLocation().x + ","
				+ "y:" + this.getPy().getLocation().y + ","
				+ "movable:" + this.movable + ","
				+ "color:" + this.rend.getColorToString()
				+ "}";

		return serial;
	}

	public static Platform deSerial(PApplet inst, String serial) {
		float width = 0;
		float height = 0;
		float x = 0;
		float y = 0;
		boolean movable = false;
		int[] color = new int[3];

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
			case("movable"):
				movable = Boolean.parseBoolean(value);
				break;
			case("color"):
				value = value.replace("[", "").replace("]", "");
				int i = 0;
				for(String c : value.split(" ")) {
					color[i] = Integer.parseInt(c);
					i++;
				}
				break;
			default:
				break;
			}
		}
		Platform res = new Platform(inst, width, height, x, y, movable);
		res.rend.setColor(color);
		return res;
	}

	@Override
	public void handleEvent(Event e) {
		switch(e.getType()) {
		case (Event.EVENT_COLLISON):
			if (((UUID) e.getData().get("caller")).equals(this.getUUID())) {
				GameObj collidedWith = Rectangles.objectMap.get((UUID) e.getData().get("collidedWith"));
				if (collidedWith.getType().equals("boundary")) {
					this.getPy().getVelocity().mult(-1);
					if (this.getPy().getVelocity().mag() > 0) {
						PVector newLoc = new PVector(this.getPy().getLocation().x, this.getPy().getLocation().y);
						newLoc.add(this.getPy().getVelocity());
						HashMap<String, Object> data = new HashMap<>();
						data.put("caller", this.getUUID());
						data.put("x", newLoc.x);
						data.put("y", newLoc.y);
						Event mov = new Event(Event.EVENT_MOVEMENT, Rectangles.globalTimeline.getCurrentTime(), data);
						Rectangles.eventManager.raiseEvent(mov);
					}
				}
			}
			break;
		case(Event.EVENT_MOVEMENT):
			if (((UUID) e.getData().get("caller")).equals(this.getUUID())) {
				PVector newLoc = new PVector((float) (e.getData().get("x")), (float) e.getData().get("y"));
				this.getPy().setLocation(newLoc);
			}
			break;
		default:
			break;
		}		
	}
}
