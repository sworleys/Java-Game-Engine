# Java-Game-Engine
Game Engine Implementation in Java

How to run:

1) Setup running processing in eclipse using this tutorial: https://processing.org/tutorials/eclipse/

2) Import the source code for this project. It is raw source code with no eclipse project information so you will have to import it as such. Eclipse should automagically fix everything the proper way.

2.5) The "Main" part of the code you want to be running is in Rectangles.Java. This is where your run configurations will go.

3) Add two run configurations called "Client" and "Server". The "Server" one needs the command line argument 'server' passed to it.

4) Run the project, selecting "Server" run configuration.

5) Run as many clients as you want with the "Client" run configuration.

6) Now you can play with it like so:

Arrow Keys: move
Space: Jump (Notice the cool gravity and bounce affects? :D)
'q': Iterate the number printed on the screen. This is just a simple way to demonstrate world state between clients.



Note (Untested): If not using eclipse, you will have to compile everything in the terminal, being sure to connect processing. I am not entirely sure how to do that but it should work. Then just manually enter the command line arguments.
