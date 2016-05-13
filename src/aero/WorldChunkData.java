package aero;

import java.util.ArrayList;

import features.PostInstance;


public class WorldChunkData {
	
	private WorldChunkCache parent;
	private int maxWorldHeight;
	private int chunkX;
	private int chunkZ;
	private short[][] blocks;
	private boolean inFile;
	public int savePosition;
	private boolean complete;
	private ArrayList<PostInstance> features;
	
	public WorldChunkData(WorldChunkCache parent, int chunkX, int chunkZ) {
		this.parent = parent;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.maxWorldHeight = this.parent.getWorld().getMaxHeight();
		
		//This fixes a chunk hole problem cause by minecraft marking a chunk as complete before AeroGen finishes it.
		if(chunkX == parent.curChunkX && chunkZ == parent.curChunkZ) {
			this.complete = false;
		} else {
			this.complete = parent.getWorld().isChunkLoaded(chunkX, chunkZ);
		}
		
		if(!this.complete) this.blocks = new short[this.maxWorldHeight >> 4][];
		
		this.inFile = false;
		this.savePosition = -1;
		this.features = new ArrayList<PostInstance>();
	}

	public void addSchematic(Schematic scheme) {
		
		if(this.complete) {
			AeroGen.log(scheme.getName()+" attempted to spawn into an already loaded chunk at ("+chunkX+", "+chunkZ+")");
			return;
		}
		
		//Calculate world position of chunk
		int chunkPosX = (chunkX << 4);
		int chunkPosZ = (chunkZ << 4);
		
		//Calculate the minimum and maximum cells in the chunk column array
		int minChunkY = Schematic.floor(scheme.getY() / 16.0);
		if(minChunkY < 0) minChunkY = 0;
		int maxChunkY = Schematic.floor((scheme.getY()+scheme.getHeight()) / 16.0)+1;
		if(maxChunkY > this.blocks.length) maxChunkY = this.blocks.length;
		
		//Calculate the minimum and maximum x values used within the schematic
		int minX = scheme.getX() - chunkPosX;
		if(minX < 0) minX = 0;
		int maxX = (scheme.getX() + scheme.getLength()) - chunkPosX;
		if(maxX > 16) maxX = 16;

		//Calculate the minimum and maximum z values used within the schematic
		int minZ = scheme.getZ() - chunkPosZ;
		if(minZ < 0) minZ = 0;
		int maxZ = (scheme.getZ() + scheme.getBreadth()) - chunkPosZ;
		if(maxZ > 16) maxZ = 16;

		//Calculate the offset x and z values for the schematic block positions
		int initX = chunkPosX - scheme.getX();
		int initZ = chunkPosZ - scheme.getZ();

		//For each section in the chunk column array
		for(int section = minChunkY; section < maxChunkY; section++) {

			//Calculate the minimum and maximum y values used within the schematic
			int minY = scheme.getY() - (section << 4);
			if(minY < 0) minY = 0;
			int maxY = (scheme.getY() + scheme.getHeight()) - (section << 4);
			if(maxY > 16) maxY = 16;

			//Calculate the offset y value for the schematic block positions
			int initY = (section << 4) - scheme.getY();

			//For each slice in the x direction
			for(int i = minX; i < maxX; i++) {
				int x = initX + i;
				
				//For each row in the z direction
				for(int k = minZ; k < maxZ; k++) {
					int z = initZ + k;
					
					//The array offset for the schematic block position
					int schemeBlockOffset = (x * scheme.getBreadth() + z) * scheme.getHeight() + initY;
					
					//For each block in the y direction
					for(int j = minY; j < maxY; j++) {
						int schemeBlockIndex = schemeBlockOffset + j;
						
						//If the block in the schematic is air skip to the next one
						if(scheme.blocks[schemeBlockIndex] == 0) continue;
							
						//If this cell of the chunk column array is null, define it
						if(blocks[section] == null) {
							blocks[section] = new short[4096];
							parent.memoryUsage += 8192;
						}
						
						//Set the block in the chunk to the value of the block in the schematic
						int chunkBlockIndex = ((j & 0xF) << 8) | (k << 4) | i;
						
						//If the block to be placed isn't caveSpace, waterP, or lavaP and the chunk already has a block other than air in this position, skip to the next block
						if(blocks[section][chunkBlockIndex] != 0) continue; 
						
						blocks[section][chunkBlockIndex] = scheme.blocks[schemeBlockIndex];
						
					}
				}
			}
		}
		
		for(PostInstance feature : scheme.getFeatures()) {
			int chunkX = floor(feature.getLocation().getX()/16);
			int chunkZ = floor(feature.getLocation().getZ()/16);
			if(chunkX == this.chunkX && chunkZ == this.chunkZ) {
				this.features.add(feature);
			}
		}
		
	}
	
	private int floor(double d) {
		if(d < 0) return (int) d - 1;
		if(d > 0) return (int) d;
		return 0;
	}

	public boolean isInFile() {
		return this.inFile;
	}

	public short[][] getFinalBlockData() {
		short[][] blocks = this.blocks;
		this.complete = true;
		if(this.blocks != null) {
			for(int j = 0; j < this.blocks.length; j++) {
				if(this.blocks[j] != null) {
					parent.memoryUsage -= 8192;
				}
			}
		}
		this.blocks = null;
		return blocks;
	}

	public ArrayList<PostInstance> getFeatures() {
		return features;
	}

	public int getSavePosition() {
		return this.savePosition;
	}
	
	public void load() {
		this.blocks = parent.loadFromFile(this.savePosition);
		this.savePosition = -1;
		this.inFile = false;
	}

	public void save() {
		this.savePosition = parent.saveToFile(this.blocks);
		this.blocks = null;
		this.inFile = true;
	}
	
}
