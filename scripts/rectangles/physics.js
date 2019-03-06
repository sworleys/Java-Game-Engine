/**
 * Physics for player
 */


function update(self, caller) {
	var collidedWith = null;
	
	for each (var obj in objects) {
		if (self.intersects(obj.getPy().getBounds2D()) && !(obj.getUUID() == caller.getUUID())
				&& !(obj.getType() == "player")) {
			collidedWith = obj;
			// TODO: Need break here?
			break;
		}
	}

	if (collidedWith != null) {
		var data = new (java.util.HashMap)();
		data["caller"] = caller.getUUID();
		data["collidedWith"] = collidedWith.getUUID();
		caller.raiseEvent(EVENT.EVENT_COLLISION, globalTimeline.getCurrentTime(), data);
	} else {
		self.velocity.add(self.acceleration);
		self.velocity.limit(self.topSpeed);
		if (self.velocity.mag() > 0) {
			var newLoc = self.copyLoc();
			newLoc.add(self.velocity);
			var data = new (java.util.HashMap)();
			data["caller"] = caller.getUUID();
			data["x"] = newLoc.x;
			data["y"] = newLoc.y;
			caller.raiseEvent(EVENT.EVENT_MOVEMENT, globalTimeline.getCurrentTime(), data);
		}
	}

	// Reset acceleration?
	if (self.isGrav()) {
		self.resetAcceleration();
		self.setVelocityX(0);
	}
}