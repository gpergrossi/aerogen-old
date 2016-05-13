package features;

import island.BiomeDescription;
import island.Island;

import java.util.ArrayList;
import java.util.Random;

/**
 * CaveSystem generates and describes a cave system within a floating island.
 * The shape of the cave system is defined as an ArrayList of CaveNodes.
 * Until the CaveSystem.carve() method is called, no changes are made to the island.
 * 
 * @author MortusNegati
 */
public class CaveSystem implements Feature {
	
	private Island island;
	private int length, height, breadth;
	private BiomeDescription description;
	private int stayBelow;
	private Random random;
	private ArrayList<CaveNode> currentEnds;
	private double splits = 1;
	private int targetVolume;
	private int currentVolume;
	private short[] blocks;
	private CavePlacement placement = new CavePlacement();
	
	public void place(Island island, Random random, int x, int y, int z) {
		
		this.island = island;		
		this.length = island.getLength();
		this.breadth = island.getBreadth();
		this.height = island.getHeight();
		this.description = island.getBiome();
		this.stayBelow = (int) (island.getCenterY()+description.roughness/2);
		this.random = random;
		this.blocks = island.getBlocks();

		this.currentEnds = new ArrayList<CaveNode>();
		double angle = random.nextDouble()*Math.PI*2.0;
		CaveNode nodeLeft = new CaveNode(x, y, z, angle, -0.2, description.startCaveRadius);
		this.currentEnds.add(nodeLeft);
		CaveNode nodeRight = new CaveNode(x, y, z, angle-Math.PI, -0.2, description.startCaveRadius);
		this.currentEnds.add(nodeRight);

		this.splits = 1;
		this.targetVolume = description.getMaximumCaveSpace(island.getTotalVolume());
		
		int loops = 0;
		int lastVolume = 0;
		while(currentVolume < targetVolume && currentEnds.size() > 0) {
			expand();
			if(currentVolume != lastVolume) {
				lastVolume = currentVolume;
				loops = 0;
			} else {
				loops++;
				if(loops > 10) break;
			}
		}
		island.setBlocks(blocks);
		
	}
	
	/**
	 * Expands the cave by one node by randomly selecting a node from the list of
	 * current ends and attaching another node to the end of it. This method, when 
	 * called repeatedly, will iteratively build a tree-like cave system.
	 */
	private void expand() {
		
		//Pick a node from the current list of ends
		int selectedIndex = random.nextInt(currentEnds.size());
		CaveNode selectedEnd = currentEnds.get(selectedIndex);
		currentEnds.remove(selectedIndex);
		
		//Carve the selected node into the block array
		int xmin = (int) (selectedEnd.x - selectedEnd.radius);
		int ymin = (int) (selectedEnd.y - selectedEnd.radius);
		int zmin = (int) (selectedEnd.z - selectedEnd.radius);
		int xmax = (int) (selectedEnd.x + selectedEnd.radius);
		int ymax = (int) (selectedEnd.y + selectedEnd.radius);
		int zmax = (int) (selectedEnd.z + selectedEnd.radius);
		if(xmin < 0) xmin = 0;
		if(ymin < 0) ymin = 0;
		if(zmin < 0) zmin = 0;
		if(xmax > length) xmax = length;
		if(ymax > height) ymax = height;
		if(zmax > breadth) zmax = breadth;
		for(int i = xmin; i < xmax; i++) {
			for(int k = zmin; k < zmax; k++) {
				for(int j = ymin; j < ymax; j++) {
					if(isInRange(i, j, k, selectedEnd.x, selectedEnd.y, selectedEnd.z, selectedEnd.radius)) {
						int index = (i*breadth + k)*height + j;
						if(blocks[index] != Island.caveSpace) {
							blocks[index] = Island.caveSpace;
							this.currentVolume++;
						}
					}
				}
			}
		}
		
		//Attach additional nodes to the end of the selected node
		int numSplits = 1;
		while(numSplits < description.maxSplit) {
			if(random.nextDouble() < description.splitChance/splits) {
				numSplits++;
				splits++;
			} else {
				break;
			}
		}
		double swayDX = description.angleDX*numSplits*numSplits;
		double swayDY = description.angleDY*numSplits*numSplits;
		 
		for(int i = 0; i < numSplits; i++) {
			
			double x = selectedEnd.x + Math.cos(selectedEnd.angleX)*Math.cos(selectedEnd.angleY)*2.0;
			double z = selectedEnd.z + Math.sin(selectedEnd.angleX)*Math.cos(selectedEnd.angleY)*2.0;
			double y = selectedEnd.y + Math.sin(selectedEnd.angleY)*2.0;
			
			if(y < stayBelow && island.getBlockSafe((int)x, (int)y, (int)z) != 0) {
				
				double radius = selectedEnd.radius + (random.nextDouble()*2.0-1.0)*description.maxCaveRadiusChange;
				if(radius < description.minCaveRadius) radius = description.minCaveRadius;
				if(radius > description.maxCaveRadius) radius = description.maxCaveRadius;

				double angleX;
				double angleY;
				if(numSplits > 1) {
					double a = random.nextDouble()*Math.PI*2.0;
					angleX = selectedEnd.angleX + Math.cos(a)*swayDX;
					angleY = selectedEnd.angleY + Math.sin(a)*swayDY;
				} else {
					angleX = selectedEnd.angleX + (random.nextDouble()*2.0-1.0)*swayDX;
					angleY = selectedEnd.angleY + (random.nextDouble()*2.0-1.0)*swayDY;
				}
				
				if(angleY < -description.maxAngleY) angleY = -description.maxAngleY;
				if(angleY > description.maxAngleY) angleY = description.maxAngleY;
				
				CaveNode node = new CaveNode(x, y, z, angleX, angleY, radius);
				this.currentEnds.add(node);
				
			}
		}
		
	}
	
	/**
	 * Fast distance comparison. Checks if the given node is in a bounding box, then does euclidian distance.
	 */
	public boolean isInRange(double node1X, double node1Y, double node1Z, double node2X, double node2Y, double node2Z, double radius) {
		double distX = Math.abs(node1X-node2X);
		double distY = Math.abs(node1Y-node2Y);
		double distZ = Math.abs(node1Z-node2Z);
		//This line is pointless because of the type input coming from carve()
		//if(distX > radius || distY > radius || distZ > radius) return false;
		if(distX+distY+distZ < radius) return true;
		if(distX*distX + distY*distY + distZ*distZ < radius*radius) return true;
		return false;
	}
	
	/**
	 * A cave node is a part of a cave system. One node represents one sphere of air
	 * to be taken out of the island. Caves generated by AeroGen are made up of many
	 * closely space bubbles of varying sizes.
	 * 
	 * @author MortusNegati
	 */
	private class CaveNode {
		
		protected double x, y, z;			//Position
		protected double angleX, angleY;	//Pointing, used for next node
		protected double radius;	//Radius and Radius squared

		/**
		 * Creates a cave node at the location, radius, and pointing that are given.
		 */
		public CaveNode(double x, double y, double z, double angleX, double angleY, double radius) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
			this.angleX = angleX;
			this.angleY = angleY;
			this.radius = radius;
		}
		
	}

	public Placement getPlacement() {
		return placement;
	}
	
	public static class CavePlacement implements Placement {

		public boolean canPlace(Island island, Random random, int x, int y, int z) {
			if(island.getCenterX() != x) return false;
			if(island.getCenterY() != y) return false;
			if(island.getCenterZ() != z) return false;
			return true;
		}

		public int quickPlace(Island island, Random random) {
			
			return 1;
		}
		
	}
	
}