package engine;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.UUID;

import engine.events.Event;
import engine.scripting.ScriptManager;
import networking.Packet;
import processing.core.PApplet;

public abstract class GameObj extends EngineObject {
	protected Physics py;
	protected Renderable rend;
	private float objWidth;
	private float objHeight;
	private boolean isFloor;
	private int rotated = 0;
	protected String type;

	private UUID uuid = UUID.randomUUID();

	public GameObj(float objWidth, float objHeight, float mass, float x, float y, 
			boolean isFloor, boolean isGrav) {
		this.isFloor = isFloor;
		this.objHeight = objHeight;
		this.objWidth = objWidth;
		this.py = new Physics(x, y, objWidth, objHeight, mass, 20, isGrav);

	}
	
	public abstract String toSerial();
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Renderable getRend() {
		return this.rend;
	}

	public void rotate(int angle) {
		this.rotated += angle;
	}
	
	public void setRotation(int angle) {
		this.rotated = angle;
	}
	public int getRotation() {
		return this.rotated;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public void setUUID(UUID id) {
		this.uuid = id;
	}
	
	public Physics getPy() {
		return py;
	}

	public void setPy(Physics py) {
		this.py = py;
	}

	public float getObjWidth() {
		return objWidth;
	}

	public void setObjWidth(float objWidth) {
		this.objWidth = objWidth;
	}

	public float getObjHeight() {
		return objHeight;
	}

	public void setObjHeight(float objHeight) {
		this.objHeight = objHeight;
	}

	public boolean isFloor() {
		return this.isFloor;
	}
	
	public void draw() {
		this.rend.getInst().shape(this.getRend().getShape(),
				this.getPy().getLocation().x, this.getPy().getLocation().y);
	}

	public void gameEnd(String s) {
		Packet p = new Packet(Packet.PACKET_GAME_OVER, this);
		System.out.println("Sending game over");
		Rectangles.server.sendPacketNow(p);
		//System.out.println(s);
		rend.getInst().exit();
	}
	
	public void handleEvent(Event e) {
		String file;
		FileReader script = null;
		try {
			file = new File("scripts/" + Rectangles.game + "/" + this.getType() + "/handler.js").getAbsolutePath();
			script = new FileReader(file);
		} catch (FileNotFoundException e1) {
			file = new File("scripts/"  + Rectangles.game + "/handler.js").getAbsolutePath();
			try {
				script = new FileReader(file);
			} catch (FileNotFoundException e2) {
				e1.printStackTrace();
			}
		}
		ScriptManager.bindArgument("spawnPoints", Rectangles.spawnPoints);
		ScriptManager.bindArgument("objectMap", Rectangles.objectMap);
		ScriptManager.bindArgument("objects", Rectangles.objects);
		ScriptManager.bindArgument("movObjects", Rectangles.movObjects);
		ScriptManager.bindArgument("server", Rectangles.server);
		ScriptManager.loadScript(script);
		ScriptManager.executeScript("handle_event", this, e, Rectangles.objectMap);
	}
	
	public void raiseEvent(int type, long time, HashMap<String, Object> data) {
//		if (time < 0) {
//			time = Rectangles.globalTimeline.getCurrentTime()
//					+ (long) Rectangles.eventTimeline.getTickSize();
//		}
//		if (type == Event.EVENT_PHYSICS) {
//			time += (long) Rectangles.physicsTimeline.getTickSize();
//		}

//		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
//		    System.out.println(ste);
//		}
		Event e = new Event(type, time, data);
		if (!data.get("caller").equals(Rectangles.player.getUUID()) && e.getType() == Event.EVENT_PHYSICS) {
			//System.out.println("platform" + e.getTime());
		}
		if (data.get("caller").equals(Rectangles.player.getUUID()) && e.getType() == Event.EVENT_PHYSICS) {
			//System.out.println(e.getTime());
		}
		
		Rectangles.eventManager.raiseEvent(e);
		//System.out.println(Rectangles.player.getUUID());
		//System.out.println(e.getData().get("caller") + " : " + e.getType() + " : " + e.getTime());
	}
}
