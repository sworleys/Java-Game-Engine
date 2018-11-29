package engine;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import engine.events.Event;
import engine.scripting.ScriptManager;
import processing.core.PApplet;
import processing.core.PVector;

public class Physics extends PApplet implements Shape {
	private static final float GRAV = (float) 0.8;

	// Vector code inspired by processing tutorial 'acceleration with vectors'
	private PVector location;
	private PVector velocity;
	private PVector acceleration;
	private float topSpeed;
	private float objWidth;
	private float objHeight;
	private float mass;
	private boolean isGrav;

	public Physics(float x, float y, float objWidth, float objHeight, float mass, float topSpeed, boolean isGrav) {
		this.isGrav = isGrav;
		this.location = new PVector(x, y);
		this.velocity = new PVector(0,0);
		if (this.isGrav) {
			this.acceleration = new PVector(0, GRAV);
		} else {
			this.acceleration = new PVector(0, 0);
		}
		this.topSpeed = topSpeed;
		this.objWidth = objWidth;
		this.objHeight = objHeight;
		this.mass = mass;
	}

	public void update(GameObj caller) {
		//this.acceleration.setMag(0.2);

		GameObj collidedWith = null;
		
		for (GameObj obj : Rectangles.objects) {
			if (this.intersects(obj.getPy().getBounds2D()) && !obj.getUUID().equals(caller.getUUID()) && !obj.getType().equals("player")) {
				collidedWith = obj;
				// TODO: Need break here?
				break;
			}
		}

		/**
		 * TODO: Implement here so that if collision occurs, no movement below, let collision event handle
		 * raising a new movement event
		 * 
		 * else, just raise movement event here if object moved
		 * 
		 * In replays, just worry about movement events! :D
		 */
		
		if (collidedWith != null) {
			HashMap<String, Object> data = new HashMap<>();
			data.put("caller", caller.getUUID());
			data.put("collidedWith", collidedWith.getUUID());
			// TODO: Could just make the collision event also be movement?
			Event e = new Event(Event.EVENT_COLLISION, Rectangles.globalTimeline.getCurrentTime(), data);
			Rectangles.eventManager.raiseEvent(e);
		} else {
			this.velocity.add(this.acceleration);
			this.velocity.limit(this.topSpeed);
			if (this.velocity.mag() > 0) {
				PVector newLoc = new PVector(this.location.x, this.location.y);
				newLoc.add(this.velocity);
				HashMap<String, Object> data = new HashMap<>();
				data.put("caller", caller.getUUID());
				data.put("x", newLoc.x);
				data.put("y", newLoc.y);
				Event e = new Event(Event.EVENT_MOVEMENT, Rectangles.globalTimeline.getCurrentTime(), data);
				Rectangles.eventManager.raiseEvent(e);
				// Actually move
				//this.location.add(this.velocity);
			}
		}

		// Reset acceleration?
		if (this.isGrav) {
			this.acceleration = new PVector(0, GRAV);
		}
	}
	
	// From processing tutorial forces with vectors
	public void applyForce(PVector force) {
		PVector f = PVector.div(force, this.mass);
		acceleration.add(f);
	}
	
	public float getMass() {
		return this.mass;
	}

	public PVector getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(PVector acceleration) {
		this.acceleration = acceleration;
	}

	public void setAccelerationX(float x) {
		this.acceleration.x = x;
	}
	
	public void setAccelerationY(float y) {
		this.acceleration.y = (y + GRAV);
	}

	public PVector getLocation() {
		return location;
	}

	public void setLocation(PVector location) {
		this.location = location;
	}

	public PVector getVelocity() {
		return velocity;
	}

	public void setVelocity(PVector velocity) {
		this.velocity = velocity;
	}
	public void setVelocityX(float velocity) {
		this.velocity.x = velocity;
	}

	public float getTopSpeed() {
		return topSpeed;
	}

	public void setTopSpeed(float topSpeed) {
		this.topSpeed = topSpeed;
	}

	@Override
	public boolean contains(Point2D p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Rectangle2D r) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double x, double y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Float(this.location.x, this.location.y, this.objWidth, this.objHeight);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return this.getBounds2D().intersects(r.getBounds2D());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * Helper function for copying location in script
	 */
	public PVector copyLoc() {
		return new PVector(this.getLocation().x, this.getLocation().y);
	}
	/*
	 * Helper function for getting new location in script
	 */
	public PVector newLoc(float x, float y) {
		return new PVector(x, y);
	}
}
