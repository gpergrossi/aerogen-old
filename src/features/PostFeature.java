package features;

import island.Island;

import java.util.Random;

import org.bukkit.Location;

public abstract class PostFeature implements Feature {
	
	public abstract void placeIntoWorld(Location location);
	
	public void place(Island island, Random random, int x, int y, int z) {
		island.addFeature(this, x, y, z);
	}
	
}
