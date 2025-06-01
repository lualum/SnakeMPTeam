# Project: Extended APCS Project from singleplayer to multiplayer Snake (using P2P)

# Instructions for Download:
Download from latest releases and unzip .app file. When running on mac, it will say that the .app is damaged, although it isn't. Go to settings and allow it to run, then open the application. 

# Creating Game:
When running the program: a popup will appear asking if you wanted to host or be a client. Note that in its current state, all players must be on the same network. If you aren't, use the help of something like Tailscale (download [here](https://tailscale.com/download)).
### Host:
When hosting, there will be a popup for how many players per team you want. After entering, the display will close and wait for other clients to join. Give the clients your IPv4, found in System Settings.
### Client:
When joining a game, there will be a popup for the IP of the host. Ask the host for their IP. If the display does not pop up, it is likely that there is still more people who need to join/someone has not connected properly.

# Gameplay:
Use your WASD or arrow keys to move the head of your snake, you die by running into the sides of other snakes (including your own)
When eating fruit, 
- more blue ones will cause all of the opposing snakes to slow down more
- more red ones will cause all of the opposing snakes to speed up more

# Configuration:
If you want to host a custom game, there is many different configuration options found in `Main.java` (note that only the host needs the changes).
```java
public static final int BOARDROWS = 20;
public static final int BOARDCOLS = 20;
public static final double FRUITSPACERATIO = 0.05;
public static final int BLOCKSIZE = 10;
public static final boolean FULLWIDTH = false;
public static final double INITIALSPEED = 2;

public static final boolean LOOPBOUNDS = false;
public static final double FRUITPOWERSPREAD = 2.0;
public static final double MAXFRUITSTRENGTH = 0.5;
```

## TODO
- fix error of being damaged on mac
- add some sort of "Players Joined (X/X)" screen
