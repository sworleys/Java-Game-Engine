import processing.core.PApplet;

public class Physics extends PApplet{
	private static final float GRAV = (float) -9.8;
	private float xPos;
	private float yPos;
	private float objWidth;
	private float objHeight;
	private float xV = 0;
	private float yV = 0;
	private float xA = 0;
	private float yA = 0;

	public Physics(float xPos, float yPos, float objWidth, float objHeight) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.objWidth = objWidth;
		this.objHeight = objHeight;
	}

	public float updateY(float t) {
		float accl = this.yA + GRAV;
		this.yPos = (this.yPos + this.yV * t + (1/2) * accl * (t * t));
		this.yV = this.yV + accl * t;

		// Did I hit the floor/ceiling
		if ((this.yPos + this.objHeight) > height) {
			this.yPos = height - this.objHeight;
		}
		if (this.yPos > height) {
			this.yPos = height;
		}

		return this.yPos;
	}

	public float updateX(float t) {
		// TODO: Friction?
		this.xPos = (this.xPos + this.xV * t + (1/2) * this.xA * (t * t));
		this.xV = this.xV + this.xA * t;

		// Did I hit the walls
		if (this.xPos < 0) {
			this.xPos = 0;
			this.xA = this.xA * (-1);
		}
		if ((this.xPos + this.objWidth) > width) {
			this.xPos = (width - this.objWidth);
			this.yA = this.yA * (-1);
		}

		return this.xPos;
	}

	public float getxPos() {
		return xPos;
	}

	public void setxPos(float xPos) {
		this.xPos = xPos;
	}

	public float getyPos() {
		return yPos;
	}

	public void setyPos(float yPos) {
		this.yPos = yPos;
	}

	public float getxV() {
		return xV;
	}

	public void setxV(float xV) {
		this.xV = xV;
	}

	public float getyV() {
		return yV;
	}

	public void setyV(float yV) {
		this.yV = yV;
	}

	public float getxA() {
		return xA;
	}

	public void setxA(float xA) {
		this.xA = xA;
	}

	public float getyA() {
		return yA;
	}

	public void setyA(float yA) {
		this.yA = yA;
	}

}
