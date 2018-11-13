package engine;


import java.util.HashMap;
import java.util.UUID;

import engine.events.Event;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

public class Player extends GameObj {
	private float dim;
	
	public Player(PApplet inst, float dim, float x, float y) {
		super(dim, dim, 0, x, y, false, true);
		this.dim = dim;
		int[] color = {(int) random(255), (int) random(255), (int) random(255)};
		this.rend = new Renderable(inst, color, PShape.RECT, dim, dim);
	}

	@Override
	public String getType() {
		return "player";
	}
	
	public float getDim() {
		return this.dim;
	}

	@Override
	public String toSerial() {
		String serial = "{"
				+ "dim:" + this.dim + ","
				+ "x:" + this.getPy().getLocation().x + ","
				+ "y:" + this.getPy().getLocation().y + ","
				+ "color:" + this.rend.getColorToString()
				+ "}";
		return serial;
	}
	
	
	public static Player deSerial(PApplet inst, String serial) {
		float dim = 0;
		float x = 0;
		float y = 0;
		int[] color = new int[3];

		serial = serial.replace("{", "").replace("}", "");
		String[] data = serial.split(",");

		for (String d : data) {
			String[] subData = d.split(":");
			String key = subData[0];
			String value = subData[1];

			switch(key) {
			case("dim"):
				dim = Float.parseFloat(value);
				break;
			case("x"):
				x = Float.parseFloat(value);
				break;
			case("y"):
				y = Float.parseFloat(value);
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
		Player res = new Player(inst, dim, x, y);
		res.rend.setColor(color);
		return res;
	}

	@Override
	public void handleEvent(Event e) {
		
		switch(e.getType()) {
		case(Event.EVENT_COLLISON):
			if (((UUID) e.getData().get("caller")).equals(this.getUUID())) {
				GameObj collidedWith = Rectangles.objectMap.get((UUID) e.getData().get("collidedWith"));
				if (collidedWith.isFloor()) {
					this.getPy().getVelocity().y = (float) -1;
				} else {
					switch (collidedWith.getType()) {
					case ("player"):
						// Do nothing, clip-less
						break;
					case ("platform"):
						this.getPy().getAcceleration().mult((float) -1);
						if (this.getPy().getVelocity().y > 0) {
							this.getPy().getVelocity().y = (float) 0;
						} else {
							this.getPy().getVelocity().y = (float) -1;
							this.getPy().getVelocity().x = (float) 0;
						}
						break;
					case ("death-zone"):
						HashMap<String, Object> data = new HashMap<>();
						data.put("caller", this.getUUID());
						Event death = new Event(Event.EVENT_DEATH, e.getTime(), data);
						Rectangles.eventManager.raiseEvent(death);

					default:
						this.getPy().getVelocity().mult((float) -1);
						break;
					}
				}
				if (this.getPy().getVelocity().mag() > 0) {
					PVector newLoc = new PVector(this.getPy().getLocation().x, this.getPy().getLocation().y);
					newLoc.add(this.getPy().getVelocity());
					HashMap<String, Object> data = new HashMap<>();
					data.put("caller", this.getUUID());
					data.put("x", newLoc.x);
					data.put("y", newLoc.y);
					Event mov = new Event(Event.EVENT_MOVEMENT, e.getTime(), data);
					Rectangles.eventManager.raiseEvent(mov);
				}
			}
			break;
		case(Event.EVENT_MOVEMENT):
			if (((UUID) e.getData().get("caller")).equals(this.getUUID())) {
				PVector newLoc = new PVector((float) (e.getData().get("x")), (float) e.getData().get("y"));
				this.getPy().setLocation(newLoc);
			}
			break;
		case(Event.EVENT_DEATH):
			if (((UUID) e.getData().get("caller")).equals(this.getUUID())) {
				Rectangles.deathPoints++;
				HashMap<String, Object> data = new HashMap<>();
				data.put("caller", this.getUUID());
				Event spawn = new Event(Event.EVENT_SPAWN, e.getTime(), data);
				Rectangles.eventManager.raiseEvent(spawn);
			}
		case(Event.EVENT_SPAWN):
			if (((UUID) e.getData().get("caller")).equals(this.getUUID())) {
				Spawn s = Rectangles.spawnPoints[Rectangles.generator.nextInt(2)];
				HashMap<String, Object> data = new HashMap<>();
				data.put("caller", this.getUUID());
				data.put("x", s.getPy().getLocation().x);
				data.put("y", s.getPy().getLocation().y);
				Event mov = new Event(Event.EVENT_MOVEMENT, e.getTime(), data);
				Rectangles.eventManager.raiseEvent(mov);
			}
			break;
		case(Event.EVENT_INPUT):
			if (((UUID) e.getData().get("caller")).equals(this.getUUID())) {
				switch((int) e.getData().get("keyCode")) {
				case(PConstants.LEFT):
					this.getPy().setAccelerationX(-5);
					break;
				case(PConstants.RIGHT):
					this.getPy().setAccelerationX(5);
					break;
				case(' '):
					this.getPy().setAccelerationY(-20);
					break;
				}
			}
			break;
		default:
			break;
		}
		
	}

}
