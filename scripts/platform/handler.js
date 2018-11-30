/**
 * Player event handler
 */

var EVENT = Java.type("engine.events.Event");


function event_collision(self, e, object_map) {
	if (e.getData().get("caller") == self.getUUID()) {
		var collidedWith = object_map.get(e.getData().get("collidedWith"));
		if (collidedWith.getType().equals("boundary")) {
			self.getPy().getVelocity().mult(-1);
		}
		if (self.getPy().getVelocity().mag() > 0) {
			var newLoc = self.getPy().copyLoc();
			newLoc.add(self.getPy().getVelocity());
			var data = new (java.util.HashMap)();
			data["caller"] = self.getUUID();
			data["x"] = newLoc.x;
			data["y"] = newLoc.y;
			self.raiseEvent(EVENT.EVENT_MOVEMENT, -1, data);
		}
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
		var data = new (java.util.HashMap)();
		data["caller"] = self.getUUID();
		self.raiseEvent(EVENT.EVENT_PHYSICS, e.getTime(), data);
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
	default:
		break;
	}
}