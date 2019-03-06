/**
 * Init for rectangles platformer game
 */


/**
 * Types
 */
var Spawn = Java.type("engine.Spawn");
var DeathZone = Java.type("engine.DeathZone");
var Player = Java.type("engine.Player");
var Platform = Java.type("engine.Platform");
var PVector = Java.type("processing.core.PVector");

var sqrDim = 50;

spawnPoints[0] =  new Spawn(self, width - sqrDim, 0 + sqrDim);
spawnPoints[1] =  new Spawn(self, width - sqrDim, height - sqrDim);

for each (var s in spawnPoints) {
	objects.add(s);
	objectMap.put(s.getUUID(), s);
}


// Death Zone
var dz_1 = new DeathZone(self, 0, 0);
objects.add(dz_1);
objectMap.put(dz_1.getUUID(), dz_1);

// Player
var rand = spawnPoints[generator.nextInt(2)];
player = new Player(self, sqrDim, rand.getPy().getLocation().x,
		rand.getPy().getLocation().y);
	objects.add(player);
	objectMap.put(player.getUUID(), player);
	movObjects.add(player);

self.setPlayer(player);

// Platforms
var pWidth = width / 5;
var pHeight = 25;

var staticPlatforms = [];
var movPlatforms = [];

var static_1 = new Platform(self, pWidth, pHeight, width - pWidth, 100, false);

staticPlatforms.push(static_1);

var mov_1 = new Platform(self, pWidth, pHeight, width - 3*pWidth, 150, false);
var mov_2 = new Platform(self, pWidth, pHeight, width - 5*pWidth, 250, false);

movPlatforms.push(mov_1);
movPlatforms.push(mov_2);

for each (var p in movPlatforms) {
	p.getPy().setTopSpeed(2);
	p.getPy().setVelocity(new PVector(2, 0));
}

var mov_3 = new Platform(self, pWidth, pHeight, 0, 100, false);
mov_3.getPy().setTopSpeed(2);
mov_3.getPy().setVelocity(new PVector(0, 2));
movPlatforms.push(mov_3);

for each (var p in staticPlatforms) {
	objects.add(p);
	objectMap.put(p.getUUID(), p);

}
for each (var p in movPlatforms) {
	objects.add(p);
	objectMap.put(p.getUUID(), p);
	movObjects.add(p);
}
















