/**
 * Platform event handler
 */

var EVENT = Java.type("engine.events.Event");


var moving = false;

function event_collision(self, e, object_map) {
	if (e.getData().get("caller") == self.getUUID()) {
		var collidedWith = object_map.get(e.getData().get("collidedWith"));
		if (collidedWith.isFloor()) {
			self.gameEnd("You lose");
		}else if (!collidedWith.getType().equals("player")) {
			if (self.isQueued()) {
				self.getPy().getVelocity().mult(0);
				self.setQueued(false);
				var data = new (java.util.HashMap)();
				data["caller"] = self.getUUID();
				self.raiseEvent(EVENT.EVENT_SPAWN, e.getTime(), data);
				if (collidedWith.getType().equals("platform")) {
					if (compare_colors(collidedWith.getRend().getColor(),
							self.getRend().getColor())) {
						var destroy = new (java.util.HashMap)();
						destroy["caller"] = self.getUUID();
						self.raiseEvent(EVENT.EVENT_DEATH, e.getTime(), destroy);
						var collider = new (java.util.HashMap)();
						collider["caller"] = collidedWith.getUUID();
						self.raiseEvent(EVENT.EVENT_DEATH, e.getTime(), collider);
					}
				}
			}
		}
	}
}

function compare_colors(a, b) {
	var i = 0;
	for each (var v in a) {
		if (v != b[i]) {
			return false;
		}
		i += 1;
	}
	return true;
}

function event_input(self, e) {
	switch(e.getData().get("keyCode")) {
	case 32:
		if (self.isQueued()) {
			self.registerPhysics();
			self.getPy().getVelocity().set(self.getAim()[0], self.getAim()[1]);
			self.getPy().getVelocity().setMag(2);
			var data = new (java.util.HashMap)();
			data["caller"] = self.getUUID();
			self.raiseEvent(EVENT.EVENT_PHYSICS, e.getTime() + 1, data);
		}
		break;
	}
}

function event_movement(self, e) {
	if (e.getData().get("caller") == self.getUUID()) {
		var newLoc = self.getPy().newLoc(e.getData().get("x"), 
				e.getData().get("y"));
		self.getPy().setLocation(newLoc);
	}
}

function event_physics(self, e) {
	if (e.getData().get("caller") == self.getUUID()) {
			self.getPy().update(self);
		if (self.isQueued()) {
			var data = new (java.util.HashMap)();
			data["caller"] = self.getUUID();
			self.raiseEvent(EVENT.EVENT_PHYSICS, e.getTime() + 1, data);
		}
	}
}

function event_death(self, e) {
	if (e.getData().get("caller").equals(self.getUUID())) {
		// Just push it off the screen
		self.getPy().getLocation().set(self.getRend().getInst().width * 2,
				self.getRend().getInst().height*2);
	}
}


function handle_event(self, e, object_map) {
	switch(e.getType()) {
	case EVENT.EVENT_COLLISION:
		event_collision(self, e, object_map);
		break;
	case EVENT.EVENT_MOVEMENT:
		event_movement(self, e);
		break;
	case EVENT.EVENT_PHYSICS:
		event_physics(self, e);
		break;
	case EVENT.EVENT_INPUT:
		event_input(self, e);
		break;
	case EVENT.EVENT_DEATH:
		event_death(self, e);
		break;
	default:
		break;
	}
}