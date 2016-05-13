package features;

import island.Island;

import java.util.ArrayList;
import java.util.Random;

import aero.Schematic;

public class FeaturePlacement implements Placement {
	
	protected double chance = 0.0;					//Chance of it spawning if all other conditions are met
	protected double minDepth = 0.0;				//Minimum depth it is allowed to spawn at (anything negative will start checking at the schematics max height, not the surface)
	protected double maxDepth = 1.0;				//Maximum depth it is allowed to spawn at (anything greater than 1.0 will check until the bottom of the schematic, not the island)
	protected ArrayList<Short> replaces = null; 	//List of blocks it can spawn on top of
	
	//Above and below are +y and -y, respectively
	protected ArrayList<Short> above = null;		//List of blocks it can spawn above
	protected ArrayList<Short> below = null;		//List of blocks it can spawn below
	
	//All neighbors are calculated in the horizontal plain
	protected ArrayList<Short> neighbors = null;	//List of blocks that are considered neighbors for the neighbor calculation
	protected int minNeighbors = 1;					//Minimum number of neighbors required to spawn
	protected int maxNeighbors = 4;					//Maximum number of neighbors required to spawn
		
	public FeaturePlacement() {}
	
	/**
	 * This creates a placement that is useful for describing ore positions.
	 * @param replaces - material to replace
	 * @param minDepth - minimum spawn depth
	 * @param maxDepth - maximum spawn depth
	 * @param chance - chance of spawning
	 */
	public FeaturePlacement(short replaces, double minDepth, double maxDepth, double chance) {
		this.chance = chance;
		this.replaces = new ArrayList<Short>();
		this.replaces.add(replaces);
		this.minDepth = minDepth;
		this.maxDepth = maxDepth;
	}
	
	/**
	 * This creates a placement that is useful for describing pond positions.
	 * @param replaces - material to replace
	 * @param minDepth - minimum spawn depth
	 * @param maxDepth - maximum spawn depth
	 * @param chance - chance of spawning
	 */
	public FeaturePlacement(short replaces, short below, double chance) {
		this.chance = chance;
		this.replaces = new ArrayList<Short>();
		this.replaces.add(replaces);
		this.below = new ArrayList<Short>();
		this.below.add(below);
		if(below == (short) 0) {
			this.minDepth = 0.0;
			this.maxDepth = 0.0;
		}
		if(below == Schematic.caveSpace) {
			this.minDepth = 0.0;
			this.maxDepth = 0.0;
		}
	}
	
	public FeaturePlacement(double minDepth, double maxDepth, short above, double chance) {
		this.minDepth = minDepth;
		this.maxDepth = maxDepth;
		this.above = new ArrayList<Short>();
		this.above.add(above);
		this.chance = chance;
	}
	
	/**
	 * Checks if this placement description applies to the location in the given island.
	 * @param island - island to check in
	 * @param random - a random number generator 
	 * @param x - position x
	 * @param y - position y
	 * @param z - position z
	 * @return boolean - true if all conditions of this placement are satisfied by the position given.
	 */
	public boolean canPlace(Island island, Random random, int x, int y, int z) {
		if(x < 0 || x >= island.getLength() || y < 0 || y >= island.getHeight() || z < 0 || z >= island.getBreadth()) return false;
		if(minDepth >= 0.0 || maxDepth <= 1.0) {
			double depth = island.getPercentDepth(x, y, z);
			if(minDepth > depth) {
				if(minDepth >= 0.0) return false;
			}
			if(maxDepth < depth) {
				if(maxDepth <= 1.0) return false;
			}
		}
		if(replaces != null) {
			if(!replaces.contains(island.getBlock(x, y, z))) return false;
		}
		if(above != null) {
			if(!above.contains(island.getBlockSafe(x, y-1, z))) return false;
		}
		if(below != null) {
			if(!below.contains(island.getBlockSafe(x, y+1, z))) return false;
		}
		if(neighbors != null) {
			int n = 0;
			if(neighbors.contains(island.getBlockSafe(x+1, y, z))) n++;
			if(neighbors.contains(island.getBlockSafe(x-1, y, z))) n++;
			if(neighbors.contains(island.getBlockSafe(x, y, z+1))) n++;
			if(neighbors.contains(island.getBlockSafe(x, y, z-1))) n++;
			if(n < minNeighbors || n > maxNeighbors) return false;
		}
		if(random.nextDouble() > chance) return false;
		return true;
	}
	
}
