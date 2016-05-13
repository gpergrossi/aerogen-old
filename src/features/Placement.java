package features;

import island.Island;

import java.util.Random;

public interface Placement {

	/**
	 * Returns true if this (x, y, z) coordinate in the island is a valid placement.
	 * @param island - an island
	 * @param random - a random number generator
	 * @param x - x coordinate 
	 * @param y - y coordinate
	 * @param z - z coordinate
	 * @return boolean - is this a valid location?
	 */
	public boolean canPlace(Island island, Random random, int x, int y, int z);
	
	/**
	 * Uses heuristics of the placement info to quickly populate all valid placements
	 * in the given island.
	 * @param island - an island
	 * @param random - a random number generator
	 * @return int - number of successful placements
	 */
//	public int quickPlace(Island island, Random random);
	
}
