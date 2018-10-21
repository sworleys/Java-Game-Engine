package networking;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

import engine.Boundary;
import engine.DeathZone;
import engine.GameObj;
import engine.Platform;
import engine.Player;
import engine.Rectangles;
import engine.Spawn;
import processing.core.PApplet;
import processing.core.PConstants;

public class Packet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int PACKET_REGISTER = 0;
	public static final int PACKET_CREATE = 1;
	public static final int PACKET_UPDATE = 2;
	public static final int PACKET_DESTROY = 3;
	public static final int PACKET_KEY_PRESS = 4;

	private int type = -1;
	private UUID uuid;
	private HashMap<String, Object> data;
	private float[] location;
	private int keyPress;

	public Packet(int type, GameObj obj) {
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
			this.data.put("dim", ((Player) obj).getDim());
			break;
		case("platform"):
			this.data.put("movable", ((Platform) obj).isMovable());
			this.data.put("width", ((Platform) obj).getObjWidth());
			this.data.put("height", ((Platform) obj).getObjHeight());
			break;
		case("boundary"):
			this.data.put("width", ((Boundary) obj).getObjWidth());
			this.data.put("height", ((Boundary) obj).getObjHeight());
			this.data.put("isFloor", ((Boundary) obj).isFloor());
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


	public void handlePacket(PApplet inst) {
		if (this.type == -1) {
			System.out.println("Type not found in serial packet");
			return;
		}
		
		GameObj obj = null;

		switch (this.type) {
		// Same as create
		case (PACKET_REGISTER):
		case (PACKET_CREATE):
			String objectType = (String) this.data.get("object_type");

			switch (objectType) {
			case ("player"):
				obj = new Player(inst, this.data);
				Rectangles.movObjects.add(obj);
				if(this.type == PACKET_REGISTER) {
					Rectangles.player = (Player) obj;
				}
				break;
			case ("platform"):
				obj = new Platform(inst, this.data);
				if (((Platform) obj).isMovable()) {
					Rectangles.movObjects.add(obj);
				}
				break;
			case ("boundary"):
				obj = new Boundary(this.data);
				break;
			case("spawn"):
				obj = new Spawn(this.data);
			case("death-zone"):
				obj = new DeathZone(this.data);
			default:
				break;
			}
			if (obj != null) {
				obj.setUUID(this.uuid);
				Rectangles.objectMap.put(obj.getUUID(), obj);
				Rectangles.objects.add(obj);
			}


			break;
		case (PACKET_DESTROY):
			Rectangles.objectMap.remove(this.uuid);
			for (GameObj o : Rectangles.objects) {
				if (o.getUUID().equals(this.uuid)) {
					Rectangles.objects.remove(obj);
					break;
				}
			}
			for (GameObj o : Rectangles.movObjects) {
				if (o.getUUID().equals(this.uuid)) {
					Rectangles.movObjects.remove(obj);
					break;
				}
			}
			break;
		case (PACKET_UPDATE):
			Rectangles.objectMap.get(this.uuid).getPy().getLocation().set((float) data.get("x"), (float) data.get("y"));
			break;
		case (PACKET_KEY_PRESS):
			if (this.keyPress == PConstants.LEFT) {
				Rectangles.objectMap.get(this.uuid).getPy().setAccelerationX(-5);
			}
			if (this.keyPress == PConstants.RIGHT) {
				Rectangles.objectMap.get(this.uuid).getPy().setAccelerationX(5);
			}
			if (this.keyPress == ' ') {
				Rectangles.objectMap.get(this.uuid).getPy().setAccelerationY(-20);
			}
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
