/**
 * Player event handler
 */


var EVENT = Java.type("engine.events.Event");
var PApplet = Java.type("processing.core.PVector");
var Platform = Java.type("engine.Platform");

//Colors
var red = [180, 0, 0];
var green = [0, 180, 0];
var blue = [0, 0, 180];
var white = [230, 230, 230];
var player_color = [247, 126, 4];
var bubble_colors = [];
bubble_colors.push(red);
bubble_colors.push(green);
bubble_colors.push(blue);
bubble_colors.push(white);

var sqrDim = 20;
var pWidth = sqrDim;
var pHeight = sqrDim;


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

function event_spawn(self, e) {
	// Queued Bubble
	print("here");
	scope = new Platform(self.getRend().getInst(), pWidth, pHeight, spawnPoints[0].getPy()
			.getLocation().x, spawnPoints[0].getPy().getLocation().y, false);
	var random_int = Math.floor(Math.random() * 4);
	scope.getRend().setColor(bubble_colors[random_int]);
	scope.setQueued(true);
	scope.getPy().setTopSpeed(5);
	objects.add(scope);
	objectMap.put(scope.getUUID(), scope);
	movObjects.add(scope);
}

function event_input(self, e) {
	if (e.getData().get("caller").equals(self.getUUID())) {
		switch(e.getData().get("keyCode")) {
		case 37:
			self.rotate(-10);
			break;
		case 39:
//			var inst = self.getRend().getInst();
//			inst.pushMatrix();
//			inst.translate(self.getPy().getLocation().x + self.getObjWidth(),
//					self.getPy().getLocation().y + self.getObjHeight())
//			inst.rotate(inst.radians(25));
			
//			inst.popMatrix();
			//self.getRend().rotate(25);
//			self.rotate(25);
			//self.getRend().getShape().translate((-1)*self.getObjWidth()/2, (-1)*self.getObjHeight()/2);			
			//self.getRend().getShape().rotate(0.43);
			//self.getRend().getShape().translate((-1)*self.getObjWidth()/2, (-1)*self.getObjHeight()/2);			
//			self.getPy().getVelocity().rotate(25);
//			self.getRend().getShape().translate(self.getObjWidth()/2,
//						self.getObjHeight()/2);
//			self.getRend().getShape().rotate(25);
			self.rotate(10);
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
	case EVENT.EVENT_INPUT:
		event_input(self, e);
		break;
	case EVENT.EVENT_SPAWN:
		event_spawn(self, e);
		break;
	default:
		break;
	}
}