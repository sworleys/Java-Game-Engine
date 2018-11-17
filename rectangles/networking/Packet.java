package networking;

import java.util.HashMap;
import java.util.UUID;

import engine.Boundary;
import engine.DeathZone;
import engine.GameObj;
import engine.Platform;
import engine.Player;
import engine.Rectangles;
import engine.Spawn;
import engine.events.Event;
import processing.core.PApplet;
import processing.core.PConstants;

public class Packet {
	public static final int PACKET_REGISTER = 0;
	public static final int PACKET_CREATE = 1;
	public static final int PACKET_UPDATE = 2;
	public static final int PACKET_DESTROY = 3;
	public static final int PACKET_KEY_PRESS = 4;

	private int type = -1;
	private UUID uuid;
	private HashMap<String, Object> data;
	private GameObj obj;
	private float[] location;
	private PApplet inst;
	private int keyPress;

	public Packet(int type, GameObj obj) {
		this.inst = inst;
		this.obj = obj;
		this.type = type;
		this.uuid = obj.getUUID();
		this.data = new HashMap<>();
		
		switch (this.type) {
		case (PACKET_REGISTER):
			this.create(obj);
			break;
		case (PACKET_CREATE):
			// Here to send arguments or whole object?
			// For now, choosing arguments; test with objects later
			this.create(obj);
			break;
		case (PACKET_DESTROY):
			// Do nothing
			// Just need type and UUID
			break;
		case (PACKET_UPDATE):
			this.update(obj);
			break;
		default:
			break;
		}
	}
	
	public Packet(int key, UUID uuid) {
		this.type = PACKET_KEY_PRESS;
		this.keyPress = key;
		this.uuid = uuid;
	}
	
	public Packet(HashMap<String, Object> data) {
		this.data = data;
		this.type = (int) data.get("type");
		this.uuid = (UUID) data.get("uuid");

		/*
		switch (this.type) {
		case (PACKET_CREATE):
			this.unpackCreate();
			break;
		case (PACKET_DESTROY):
			// Do nothing
			// Just need type and UUID
			break;
		case (PACKET_UPDATE):
			this.unpackUpdate();
			break;
		default:
			break;
		}
		*/
	}

	private void create(GameObj obj) {
		this.data.put("object_type", obj.getType());
		this.update(obj);
		
		switch(obj.getType()) {
		case("player"):
			this.data.put("shape", obj.getRend().getShape());
			break;
		case("platform"):
			this.data.put("shape", obj.getRend().getShape());
			this.data.put("movable", ((Platform) obj).isMovable());
			break;
		case("boundary"):
			break;
		default:
			break;
		}
	}
	
	private void update(GameObj obj) {
		this.data.put("x", obj.getPy().getLocation().x);
		this.data.put("y", obj.getPy().getLocation().y);
	}
	
	public HashMap<String, Object> getData() {
		this.data.put("type", this.type);
		this.data.put("uuid", this.uuid);
		return this.data;
	}
	
	public String getSerialData() {
		String serial = 
				"type:" + this.type + ","
				+ "uuid:" + this.uuid.toString() + "|";
		
		switch (this.type) {
		case (PACKET_REGISTER):
		case (PACKET_CREATE):
			serial = serial + "object_type:" + this.data.get("object_type") + "|"
					+ obj.toSerial();
			break;
		case (PACKET_DESTROY):
			// Do nothing
			// Just need type and UUID
			break;
		case (PACKET_UPDATE):
			serial = serial + "x:" + this.obj.getPy().getLocation().x + ","
					+ "y:" + this.obj.getPy().getLocation().y;
			break;
		case(PACKET_KEY_PRESS):
			serial = serial + "key:" + this.keyPress;
			break;
		default:
			break;
		}
		
		return serial;
	}
	
	public Packet(PApplet inst, String serial) {
		this.inst = inst;
		String[] serialData = serial.split("\\|");
		String[] pData = serialData[0].split(",");
		for (String d : pData) {
			String[] subData = d.split(":");
			String key = subData[0];
			String value = subData[1];
			
			switch(key) {
			case("type"):
				this.type = Integer.parseInt(value);
				break;
			case("uuid"):
				this.uuid = UUID.fromString(value);
				break;
			default:
				break;
			}
		}
		
		if (this.type == -1) {
			System.out.println("Type not found in serial packet");
			return;
		}
		
		switch (this.type) {
		// Same as create
		case (PACKET_REGISTER):
		case (PACKET_CREATE):
			String objectType = serialData[1].split(":")[1];
			String objectSerial = serialData[2];

			switch (objectType) {
			case ("player"):
				this.obj = Player.deSerial(this.inst, objectSerial);
				Rectangles.movObjects.add(this.obj);
				//System.out.println(this.uuid + ": " + this.obj.getRend().getColorToString());
				if(this.type == PACKET_REGISTER) {
					Rectangles.player = (Player) this.obj;
					Rectangles.registered = true;
				}
				break;
			case ("platform"):
				this.obj = Platform.deSerial(this.inst, objectSerial);
				if (((Platform)this.obj).isMovable()) {
					Rectangles.movObjects.add(this.obj);
				}
				break;
			case ("boundary"):
				this.obj = Boundary.deSerial(objectSerial);
				break;
			case("spawn"):
				this.obj = Spawn.deSerial(inst, objectSerial);
			case("death-zone"):
				this.obj = DeathZone.deSerial(inst, objectSerial);
			default:
				break;
			}
			this.obj.setUUID(this.uuid);
			Rectangles.objectMap.put(this.obj.getUUID(), this.obj);
			Rectangles.objects.add(this.obj);
			Rectangles.eventManager.registerHandler(this.obj, Event.EVENT_MOVEMENT);
			Rectangles.eventManager.registerHandler(this.obj, Event.EVENT_INPUT);


			break;
		case (PACKET_DESTROY):
			Rectangles.objectMap.remove(this.uuid);
			for (GameObj obj : Rectangles.objects) {
				if (obj.getUUID().equals(this.uuid)) {
					Rectangles.objects.remove(obj);
					break;
				}
			}
			for (GameObj obj : Rectangles.movObjects) {
				if (obj.getUUID().equals(this.uuid)) {
					Rectangles.movObjects.remove(obj);
					break;
				}
			}
			break;
		case (PACKET_UPDATE):
			String locationData = serialData[1];
			this.location = new float[2];
			for (String d : locationData.split(",")) {
				String[] subData = d.split(":");
				String key = subData[0];
				String value = subData[1];
				switch (key) {
				case ("x"):
					this.location[0] = Float.parseFloat(value);
					break;
				case ("y"):
					this.location[1] = Float.parseFloat(value);
					break;
				default:
					break;
				}
			}
			
			// Perf testing
			try {
				if (this.uuid.equals(Rectangles.player.getUUID())) {
					synchronized (Rectangles.lock) {
						if (++Rectangles.inputCounter == Rectangles.inputTotal) {
							//System.out.println("Counter: " + Rectangles.inputCounter);
//							System.out.println("End: " + Rectangles.globalTimeline.getCurrentTime());
							System.out.println("Time: " + (Rectangles.globalTimeline.getCurrentTime() - Rectangles.startTime));
							System.exit(0);
						}
					}
				}
			} catch (NullPointerException e) {
				// ignore
			}
			
			try {
				//System.out.println("Update recv");
				HashMap<String, Object> data = new HashMap<>();
				data.put("caller", this.uuid);
				data.put("x", this.location[0]);
				data.put("y", this.location[1]);
				Event e = new Event(Event.EVENT_MOVEMENT, Rectangles.globalTimeline.getCurrentTime(), data);
				Rectangles.eventManager.raiseEvent(e);
				
				//Rectangles.objectMap.get(this.uuid).getPy().getLocation()
				//	.set(this.location[0], this.location[1]);

			} catch (NullPointerException e) {
				// Do nothing, update for object not yet created most likely
			}
			break;
		case (PACKET_KEY_PRESS):
			this.keyPress = Integer.parseInt(serialData[1].split(":")[1]);
			HashMap<String, Object> data = new HashMap<>();
			Event e = new Event(Event.EVENT_INPUT, Rectangles.globalTimeline.getCurrentTime(), data);
			data.put("keyCode", this.keyPress);
			data.put("caller", this.uuid);
			Rectangles.eventManager.raiseEvent(e);			
			break;
		default:
			break;
		}
		
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public void setData(HashMap<String, Object> data) {
		this.data = data;
	}
}
