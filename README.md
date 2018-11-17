# Java-Game-Engine
Game Engine Implementation in Java

How to run:

1) Setup running processing in eclipse using this tutorial: https://processing.org/tutorials/eclipse/

2) Import the source code for this project.

2.5) The "Main" part of the code you want to be running is in Rectangles.Java. This is where your run configurations will go.

3) Add two run configurations called "Client" and "Server". The "Server" one needs the command line argument 'server' passed to it.

4) Run the project, selecting "Server" run configuration.

5) Run as many clients as you want with the "Client" run configuration.

6) Now you can play with it like so:

Arrow Keys: move
Space: Jump

Start Recording: r
Stop and Play Recording: s  (This defaults to 1/2 speed)
Change speed: 1-3  (1 = 1/2 speed, 2 = normal speed, 3 = double speed)

Pause: p
Un-Pause: u



Note (Untested): If not using eclipse, you will have to compile everything in the terminal, being sure to connect processing. I am not entirely sure how to do that but it should work. Then just manually enter the command line arguments.
