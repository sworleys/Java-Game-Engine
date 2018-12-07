/**
 * Init for bubbles game
 */


/**
 * Types
 */
var Player = Java.type("engine.Player");
var Spawn = Java.type("engine.Spawn");
var PVector = Java.type("processing.core.PVector");
var Platform = Java.type("engine.Platform");


var sqrDim = 20;
var scope_dist = 100;
var margin = 2
var num_rows = 2

// Colors
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


// Player
player = new Player(self, sqrDim, width / 2, height - scope_dist);
player.rotate(270);
player.getRend().setColor(player_color);
objects.add(player);
objectMap.put(player.getUUID(), player);
movObjects.add(player);
self.setPlayer(player);


// Bubbles
var pWidth = sqrDim;
var pHeight = sqrDim;


// Spawnpoint for the queue bubble
spawnPoints[0] =  new Spawn(self, width / 2, height - sqrDim);

// Queued Bubble
scope = new Platform(self, pWidth, pHeight, spawnPoints[0].getPy().getLocation().x, 
		spawnPoints[0].getPy().getLocation().y, true);
var random_int = Math.floor(Math.random() * 4);
scope.getRend().setColor(bubble_colors[random_int]);
scope.setQueued(true);
scope.getPy().setTopSpeed(2);
objects.add(scope);
objectMap.put(scope.getUUID(), scope);
movObjects.add(scope);


var bubbles = [];

var j;
var i;
var row_length = Math.floor(width / (pWidth))

var above_colors = new Array(row_length);
for each (var c in above_colors) {
	c = -1;
}

for (j = 0; j < num_rows; j++) {
	var last_color = -1;
	for (i = 0; i < row_length; i++) {
		var b = new Platform(self, pWidth, pHeight, ((pWidth) * i), 
				(pHeight) * j, false);
		var random_int = last_color;
		while (random_int == last_color || random_int == above_colors[i]) {
			random_int = Math.floor(Math.random() * 4);
		}
		last_color = random_int;
		b.getRend().setColor(bubble_colors[random_int]);
		if (j == 0 ) {
			above_colors[i] = random_int;
		}
		bubbles.push(b);
	}
}

for each (var p in bubbles) {
	objects.add(p);
	objectMap.put(p.getUUID(), p);
	movObjects.add(p);
}
















