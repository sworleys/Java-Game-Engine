/**
 * Player event handler
 */


var EVENT = Java.type("engine.events.Event");
var PApplet = Java.type("processing.core.PVector");
var Platform = Java.type("engine.Platform");
var Packet = Java.type("networking.Packet");


function event_collision(self, e, object_map) {
	if (e.getData().get("caller").equals(self.getUUID())) {
		var collidedWith = object_map.get(e.getData().get("collidedWith"));
		if (self.isBall()) {
			if (collidedWith.isFloor()) {
				self.gameEnd("You lose");
			} else {
				self.getPy().getVelocity().mult(-2);
				switch (collidedWith.getType()) {
				case "platform":
					var collider = new (java.util.HashMap)();
					collider["caller"] = collidedWith.getUUID();
					self.raiseEvent(EVENT.EVENT_DEATH, e.getTime(), collider);
					break;
				}
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


function event_spawn(self, e) {
	// Queued Bubble
	scope = new Platform(self.getRend().getInst(), pWidth, pHeight, spawnPoints[0].getPy()
			.getLocation().x, spawnPoints[0].getPy().getLocation().y, false);
	var random_int = Math.floor(Math.random() * 4);
	scope.getRend().setColor(bubble_colors[random_int]);
	scope.registerInput();
	scope.setQueued(true);
	scope.getPy().setTopSpeed(2);
	objects.add(scope);
	objectMap.put(scope.getUUID(), scope);
	movObjects.add(scope);
	server.newPacket(Packet.PACKET_CREATE, scope);
}

function event_input(self, e) {
	if (e.getData().get("caller").equals(self.getUUID())) {
		switch(e.getData().get("keyCode")) {
		case 37:
			self.getPy().setVelocityX(-20);
			break;
		case 39:
			self.getPy().setVelocityX(20);
			break;
		}
	}
}

function event_movement(self, e) {
	if (e.getData().get("caller").equals(self.getUUID())) {
		var newLoc = self.getPy().newLoc(e.getData().get("x"), 
				e.getData().get("y"));
		self.getPy().setLocation(newLoc);
		if (!self.isBall()) {
			self.getPy().getVelocity().mult(0);
		}
	}
}

function event_physics(self, e) {
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