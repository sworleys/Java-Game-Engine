/**
 * Player event handler
 */

var EVENT = Java.type("engine.events.Event");


function event_collision(self, e, object_map) {
	if (e.getData().get("caller").equals(self.getUUID())) {
		var collidedWith = object_map.get(e.getData().get("collidedWith"));
		if (collidedWith.isFloor()) {
			self.getPy().getVelocity().y = -1;
		} else {
			switch (collidedWith.getType()) {
			case "player":
				// Do nothing, clip-less
				break;
			case "platform":
				self.getPy().getAcceleration().mult(-1);
				if (self.getPy().getVelocity().y > 0) {
					self.getPy().getVelocity().y = 0;
				} else {
					self.getPy().getVelocity().y = -1;
					self.getPy().getVelocity().x = 0;
				}
				break;
			case "death-zone":
				var data = new (java.util.HashMap)();
				data["caller"] = self.getUUID();
				self.raiseEvent(EVENT.EVENT_DEATH, e.getTime(), data);
				break;
			default:
				self.getPy().getVelocity().mult(-1);
				break;
			}
		}
		if (self.getPy().getVelocity().mag() > 0) {
			var newLoc = self.getPy().copyLoc();
			newLoc.add(self.getPy().getVelocity());
			var data = new (java.util.HashMap)();
			data["caller"] = self.getUUID();
			data["x"] = newLoc.x;
			data["y"] = newLoc.y;
			self.raiseEvent(EVENT.EVENT_MOVEMENT, e.getTime(), data);
		}
	}
}

function event_death(self, e) {
	if (e.getData().get("caller").equals(self.getUUID())) {
		//Rectangles.deathPoints++;
		var data = new (java.util.HashMap)();
		data["caller"] = self.getUUID();
		self.raiseEvent(EVENT.EVENT_SPAWN, e.getTime(), data);
	}
}

function event_spawn(self, e) {
	if (e.getData().get("caller").equals(self.getUUID())) {
		var s = self.getRandomSpawn();
		var data = new (java.util.HashMap)();
		data["caller"] = self.getUUID();
		data["x"] = s.getPy().getLocation().x;
		data["y"] = s.getPy().getLocation().y;
		self.raiseEvent(EVENT.EVENT_MOVEMENT, e.getTime(), data);
	}
}

function event_input(self, e) {
	if (e.getData().get("caller").equals(self.getUUID())) {
		switch(e.getData().get("keyCode")) {
		case 37:
			break;
		case 39:
			print("right");
			var inst = self.getRend().getInst();
			inst.translate(width/2, height/2);
			inst.rotate(25);
//			self.getPy().getVelocity().rotate(25);
//			self.getRend().getShape().translate(self.getObjWidth()/2,
//						self.getObjHeight()/2);
//			self.getRend().getShape().rotate(25);
			break;
		case 32:
			self.getPy().setAccelerationY(-20);
			break;
		}
	}
}

function event_movement(self, e) {
	if (e.getData().get("caller").equals(self.getUUID())) {
		var newLoc = self.getPy().newLoc(e.getData().get("x"), 
				e.getData().get("y"));
		self.getPy().setLocation(newLoc);
	}
}

function event_physics(self, e) {
	return
	if (e.getData().get("caller").equals(self.getUUID())) {
		self.getPy().update(self);
		var data = new (java.util.HashMap)();
		data["caller"] = self.getUUID();
		self.raiseEvent(EVENT.EVENT_PHYSICS, e.getTime() + 1, data);
	}
}

function handle_event(self, e, object_map) {
	switch(e.getType()) {
	case EVENT.EVENT_COLLISION:
		event_collision(self, e, object_map);
		break;
	case EVENT.EVENT_DEATH:
		event_death(self, e);
		break;
	case EVENT.EVENT_SPAWN:
		event_spawn(self, e);
		break;
	case EVENT.EVENT_INPUT:
		event_input(self, e);
		break;
	case EVENT.EVENT_MOVEMENT:
		event_movement(self, e);
		break;
	case EVENT.EVENT_PHYSICS:
		event_physics(self, e);
		break;
	default:
		break;
	}
}