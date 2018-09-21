import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class Physics extends PApplet{
	private static final float GRAV = (float) -9.8;
	
	// Vector code inspired by processing tutorial 'acceleration with vectors'
	private PVector location;
	private PVector velocity;
	private PVector acceleration;
	private float topSpeed;
	private float radius;

	// Collision based on circle around center of object
	// inspired from https://happycoding.io/tutorials/processing/collision-detection

	public Physics(float x, float y, float radius, float topSpeed) {
		this.location = new PVector(x, y);
		this.velocity = new PVector(0,0);
		this.acceleration = new PVector(0, GRAV);
		this.topSpeed = topSpeed;
		this.radius = radius;

	}

	public PVector update(ArrayList<GameObj> objects) {
		//this.acceleration.setMag(0.2);
		
		for (GameObj obj : objects) {
			if (dist(this.location.x, this.location.y, obj.getPy().getLocation().x,
					obj.getPy().getLocation().y) < (this.radius + obj.getPy().getRadius())) {
				this.velocity.mult(0);
				this.acceleration.mult(0);
			}
		}
		
		this.velocity.add(this.acceleration);
		this.velocity.limit(this.topSpeed);
		this.location.add(this.velocity);
		
		// Reset acceleration?
		this.acceleration = new PVector(0, GRAV);
		
		return this.location;
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
		this.acceleration.x = (y + GRAV);
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

	public float getTopSpeed() {
		return topSpeed;
	}

	public void setTopSpeed(float topSpeed) {
		this.topSpeed = topSpeed;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
}
