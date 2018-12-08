package engine;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.w3c.dom.css.Rect;

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
	
	private PVector last_velocity = new PVector(0, 0);

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
		String file;
		FileReader script = null;
		try {
			file = new File("scripts/" + Rectangles.game + "/" + caller.getType() + "/physics.js").getAbsolutePath();
			script = new FileReader(file);
		} catch (FileNotFoundException e1) {
			file = new File("scripts/" + Rectangles.game + "/physics.js").getAbsolutePath();
			try {
				script = new FileReader(file);
			} catch (FileNotFoundException e2) {
				e1.printStackTrace();
			}
		}
		ScriptManager.bindArgument("objects", Rectangles.objects);
		ScriptManager.bindArgument("GRAV", GRAV);
		ScriptManager.bindArgument("globalTimeline", Rectangles.globalTimeline);
		ScriptManager.loadScript(script);
		ScriptManager.executeScript("update", this, caller);
	}
	
	public void updateLastVelocity() {
		this.last_velocity.set(this.velocity);
	}
	
	public PVector getLastVelocity() {
		return this.last_velocity;
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

	public void resetAcceleration() {
		this.acceleration = new PVector(0, GRAV);
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
	
	public void setVelocityY(float velocity) {
		this.velocity.y = velocity;
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

	public boolean isGrav() {
		return isGrav;
	}
}
