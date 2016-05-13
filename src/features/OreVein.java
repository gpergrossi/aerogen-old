package features;

import island.Island;

import java.util.ArrayList;
import java.util.Random;

import aero.Int3D;

public class OreVein implements Feature {
	
	private String name = "";
	private short material = 0;
	private int minVeinSize = 3;
	private int maxVeinSize = 9;
	private FeaturePlacement placement;
	
	public OreVein(String name) {
		this.name = name;
		this.placement = new FeaturePlacement((short) 1, 0.0, 1.0, 0.0);
	}
	
	public String getName() {
		return this.name;
	}

	public short getMaterial() {
		return material;
	}

	public void setMaterial(short material) {
		this.material = material;
	}

	public short getReplaces() {
		return this.placement.replaces.get(0);
	}

	public void setReplaces(short replaces) {
		this.placement.replaces.set(0, replaces);
	}

	public double getChance() {
		return this.placement.chance;
	}

	public void setChance(double chance) {
		this.placement.chance = chance;
	}

	public double getMinDepth() {
		return this.placement.minDepth;
	}

	public void setMinDepth(double minDepth) {
		this.placement.minDepth = minDepth;
	}

	public double getMaxDepth() {
		return this.placement.maxDepth;
	}

	public void setMaxDepth(double maxDepth) {
		this.placement.maxDepth = maxDepth;
	}

	public int getMinVeinSize() {
		return minVeinSize;
	}

	public void setMinVeinSize(int minVeinSize) {
		this.minVeinSize = minVeinSize;
	}

	public int getMaxVeinSize() {
		return maxVeinSize;
	}

	public void setMaxVeinSize(int maxVeinSize) {
		this.maxVeinSize = maxVeinSize;
	}
	
	public int rollAmount(Random random) {
		return random.nextInt(maxVeinSize-minVeinSize+1)+minVeinSize;
	}
	
	public FeaturePlacement getPlacement() {
		return placement;
	}
	
	public void place(Island island, Random random, int i, int j, int k) {
		//Create a list of nodes and a temporary list of neighbors
		ArrayList<Int3D> nodes = new ArrayList<Int3D>();
		ArrayList<Int3D> neighbors = new ArrayList<Int3D>();
		
		//Record starting material
		short startingMaterial = island.getBlock(i, j, k);
		
		//Add an initial node
		Int3D node = new Int3D(i, j, k);
		nodes.add(node);
		
		neighbors.add(new Int3D(node.x+1, node.y, node.z));
		neighbors.add(new Int3D(node.x-1, node.y, node.z));
		neighbors.add(new Int3D(node.x, node.y+1, node.z));
		neighbors.add(new Int3D(node.x, node.y-1, node.z));
		neighbors.add(new Int3D(node.x, node.y, node.z+1));
		neighbors.add(new Int3D(node.x, node.y, node.z-1));
		
		int amount = rollAmount(random);
		
		while(nodes.size() < amount && nodes.size() > 0) {
			
			//Choose a node at random from the valid neighbor nodes
			node = nodes.get(random.nextInt(nodes.size()));
			neighbors.remove(node);
			nodes.add(node);
				
			//Add new neighbors
			Int3D neighbor; 
			neighbor = new Int3D(node.x+1, node.y, node.z);
			if(!neighbors.contains(neighbor) && !nodes.contains(neighbor) && island.getBlockSafe(node.x+1, node.y, node.z) == startingMaterial) neighbors.add(neighbor);
			neighbor = new Int3D(node.x-1, node.y, node.z);
			if(!neighbors.contains(neighbor) && !nodes.contains(neighbor) && island.getBlockSafe(node.x-1, node.y, node.z) == startingMaterial) neighbors.add(neighbor);
			neighbor = new Int3D(node.x, node.y+1, node.z);
			if(!neighbors.contains(neighbor) && !nodes.contains(neighbor) && island.getBlockSafe(node.x, node.y+1, node.z) == startingMaterial) neighbors.add(neighbor);
			neighbor = new Int3D(node.x, node.y-1, node.z);
			if(!neighbors.contains(neighbor) && !nodes.contains(neighbor) && island.getBlockSafe(node.x, node.y-1, node.z) == startingMaterial) neighbors.add(neighbor);
			neighbor = new Int3D(node.x, node.y, node.z+1);
			if(!neighbors.contains(neighbor) && !nodes.contains(neighbor) && island.getBlockSafe(node.x, node.y, node.z+1) == startingMaterial) neighbors.add(neighbor);
			neighbor = new Int3D(node.x, node.y, node.z-1);
			if(!neighbors.contains(neighbor) && !nodes.contains(neighbor) && island.getBlockSafe(node.x, node.y, node.z-1) == startingMaterial) neighbors.add(neighbor);
			
		}
		
		//Place ore in each of the nodes' positions
		for(Int3D n : nodes) {
			island.setBlock(n.x, n.y, n.z, material);
		}
		
	}
	
}
