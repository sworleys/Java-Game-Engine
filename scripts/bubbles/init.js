/**
 * Init for bubbles platformer game
 */


/**
 * Types
 */
var Player = Java.type("engine.Player");
var PVector = Java.type("processing.core.PVector");
var Platform = Java.type("engine.Platform");


var sqrDim = 20;
var scope_dist = 80
var margin = 2
var num_rows = 2




// Player
player = new Player(self, sqrDim, width / 2, height - 100);
objects.add(player);
objectMap.put(player.getUUID(), player);
movObjects.add(player);

self.setPlayer(player);

// Scope
scope = new Player(self, sqrDim, width / 2, height - scope_dist - sqrDim);
objects.add(scope);
objectMap.put(scope.getUUID(), scope);
movObjects.add(scope);

// Bubbles
var pWidth = sqrDim;
var pHeight = sqrDim;

var bubbles = [];

var j;
var i;
var row_length = Math.floor(width / (pWidth + margin))
for (j = 0; j < num_rows; j++) {
	for (i = 0; i < row_length; i++) {
		bubbles.push(new Platform(self, pWidth, pHeight, ((pWidth + margin) * i), (pHeight + margin) * j, false))
	}
}

for each (var p in bubbles) {
	objects.add(p);
	objectMap.put(p.getUUID(), p);
	movObjects.add(p);
}
















