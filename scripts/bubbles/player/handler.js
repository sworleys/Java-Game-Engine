/**
 * Player event handler
 */


var EVENT = Java.type("engine.events.Event");
var PApplet = Java.type("processing.core.PVector");
var Platform = Java.type("engine.Platform");
var Packet = Java.type("networking.Packet");

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
			self.rotate(-5);
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
			self.rotate(5);
			break;
		}
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