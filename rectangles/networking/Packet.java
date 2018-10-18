package networking;

import java.util.HashMap;
import java.util.UUID;

import engine.GameObj;
import engine.Platform;

public class Packet {
	public static final int PACKET_REGISTER = 0;
	public static final int PACKET_CREATE = 1;
	public static final int PACKET_UPDATE = 2;
	public static final int PACKET_DESTROY = 3;

	private int type;
	private UUID uuid;
	private HashMap<String, Object> data;

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
	
	private void unpackUpdate() {
		// TODO Auto-generated method stub
		
	}

	private void unpackCreate() {
		
		
	}

	private void create(GameObj obj) {
		this.data.put("object_type", obj.getType());
		this.update(obj);
		
		switch(obj.getType()) {
		case("player"):
			this.data.put("shape", obj.getShape());
			break;
		case("platform"):
			this.data.put("shape", obj.getShape());
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
