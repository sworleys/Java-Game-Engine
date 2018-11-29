/**
 * this bound as player object
 * e bound as event object
 * object_map bound as hashmap of objects from rectangles
 */

function event_collision(this, e, object_map) {
	if (e.getData().get("caller") == this.getUUID()) {
		var collidedWith = object_map.get(e.getData().get("collidedWith"));
		if (collidedWith.isFloor()) {
			this.getPy().getVelocity().y = -1;
		} else {
			switch (collidedWith.getType()) {
			case "player":
				// Do nothing, clip-less
				break;
			case "platform":
				this.getPy().getAcceleration().mult((float) -1);
				if (this.getPy().getVelocity().y > 0) {
					this.getPy().getVelocity().y = (float) 0;
				} else {
					this.getPy().getVelocity().y = (float) -1;
					this.getPy().getVelocity().x = (float) 0;
				}
				break;
			case "death-zone":
				var data = {};
				data["caller"] = this.getUUID();
				this.raiseEvent(e.EVENT_DEATH, e.getTime(), data);
				break;
			default:
				this.getPy().getVelocity().mult((float) -1);
				break;
			}
		}
		if (this.getPy().getVelocity().mag() > 0) {
			var newLoc = this.getPy().copyLoc();
			newLoc.add(this.getPy().getVelocity());
			var data = {};
			data["caller"] = this.getUUID();
			data["x"] = newLoc.x;
			data["y"] = newLoc.y;
			this.raiseEvent(e.EVENT_MOVEMENT, e.getTime(), data);
		}
	}
}

function event_death(this, e) {
	if (e.getData().get("caller") == this.getUUID()) {
		//Rectangles.deathPoints++;
		var data = {};
		data["caller"] = this.getUUID();
		this.raiseEvent(e.EVENT_SPAWN, e.getTime(), data);
	}
}

function event_spawn(this, e) {
	if (e.getData().get("caller") == this.getUUID()) {
		var s = this.getRandomSpawn();
		var data = {};
		data["caller"] = this.getUUID();
		data["x"] = s.getPy().getLocation().x;
		data["y"] = s.getPy().getLocation().y;
		this.raiseEvent(e.EVENT_MOVEMENT, -1, data);
	}
}

function event_input(this, e) {
	if (e.getData().get("caller") == this.getUUID()) {
		switch(e.getData().get("keyCode")) {
		case 37:
			this.getPy().setAccelerationX(-5);
			break;
		case 39:
			this.getPy().setAccelerationX(5);
			break;
		case ' ':
			this.getPy().setAccelerationY(-20);
			break;
		}
	}
}

function event_movement(this, e) {
	if (e.getData().get("caller") == this.getUUID()) {
		var newLoc = this.getPy().newLoc(e.getData().get("x"), 
				e.getData().get("y"));
		this.getPy().seLocation(newLoc);
	}
}

function event_physics(this, e) {
	if (e.getData().get("caller") == this.getUUID()) {
		this.getPy().update(this);
		var data = {};
		data["caller"] = this.getUUID();
		this.raiseEvent(e.EVENT_PHYSICS, e.getTime(), data);
	}
}

function handle_event(this, e, object_map) {
	switch(e.getType()) {
	case e.EVENT_COLLISION:
		event_collision(this, e, object_map);
		break;
	case e.EVENT_DEATH:
		event_death(this, e);
		break;
	case e.EVENT_SPAWN:
		event_spawn(this, e);
		break;
	case e.EVENT_INPUT:
		event_input(this, e);
		break;
	case e.EVENT_MOVEMENT:
		event_movement();
		break;
	case e.EVENT_PHYSICS:
		event_physics();
		break;
	default:
		break;
	}
}