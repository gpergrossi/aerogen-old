package features;

import island.Island;

import java.util.Random;

public interface Feature {
	
	public void place(Island island, Random random, int x, int y, int z);
	
	public Placement getPlacement();
	
}
