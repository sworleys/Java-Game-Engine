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


var player_width = 100;
var player_height = 20;
var sqrDim = 20;

var margin = 2
var num_rows = 6

// Colors
var red = [180, 0, 0];
var green = [0, 180, 0];
var blue = [0, 0, 180];
var white = [230, 230, 230];
var player_color = [247, 126, 4];


// Player
var player = new Player(self, player_width, player_height, width / 2, height - player_height - margin);
player.getRend().setColor(player_color);
player.getPy().setTopSpeed(20);
objects.add(player);
objectMap.put(player.getUUID(), player);
movObjects.add(player);
self.setPlayer(player);


// Bricks
var pWidth = sqrDim;
var pHeight = sqrDim;


// Spawnpoint for the queue bubble
spawnPoints[0] =  new Spawn(self, width / 2, height - 3 * player_height);

// Ball
var ball = new Player(self, pWidth, pHeight, spawnPoints[0].getPy().getLocation().x, 
		spawnPoints[0].getPy().getLocation().y);
var random_int = Math.floor(Math.random() * 4);
ball.getRend().setColor(white);
ball.getPy().setTopSpeed(1.5);
ball.getPy().setVelocityY(-0.3);
ball.getPy().setVelocityX(-0.2);
ball.setIsBall(true);
objects.add(ball);
objectMap.put(ball.getUUID(), ball);
movObjects.add(ball);



var bricks = [];

var j;
var i;
var row_length = Math.floor(width / (pWidth));
var above_space = 50;

for (j = 0; j < num_rows; j++) {
	for (i = 0; i < row_length; i++) {
		var b = new Platform(self, pWidth, pHeight, ((pWidth) * i), 
				(pHeight) * j + above_space, false);
		b.registerDeath();
		bricks.push(b);
	}
}

for each (var p in bricks) {
	objects.add(p);
	objectMap.put(p.getUUID(), p);
}
















