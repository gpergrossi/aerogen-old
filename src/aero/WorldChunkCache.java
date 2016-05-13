package aero;

import java.util.HashMap;

import org.bukkit.World;

public class WorldChunkCache {

	private static HashMap<World, WorldChunkCache> hashmap = new HashMap<World, WorldChunkCache>();
	
	public static WorldChunkCache forWorld(World world) {
		return hashmap.get(world);
	}
	
	private WorldDataFile worldDataFile;
	private WorldChunkData[][] chunkData;
	private int chunkMinX, chunkMaxX;
	private int chunkMinZ, chunkMaxZ;
	private World world;
	protected int memoryUsage;
	public int curChunkX;
	public int curChunkZ;
	
	public WorldChunkCache(World world) {
		this.world = world;
		hashmap.put(world, this);
		worldDataFile = new WorldDataFile(world);
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public void addSchematic(Schematic scheme) {
		int minChunkX = Schematic.floor(scheme.getX() / 16.0);
		int minChunkZ = Schematic.floor(scheme.getZ() / 16.0);
		int maxChunkX = Schematic.floor((scheme.getX()+scheme.getLength()) / 16.0);
		int maxChunkZ = Schematic.floor((scheme.getZ()+scheme.getBreadth()) / 16.0);
		for(int i = minChunkX; i <= maxChunkX; i++) {
			for(int k = minChunkZ; k <= maxChunkZ; k++) {
				WorldChunkData chunk = getChunkData(i, k);
				chunk.addSchematic(scheme);
			}
		}
	}
	
	public boolean chunkExists(int chunkX, int chunkZ) {
		if(chunkData == null) return false;
		if(chunkX < chunkMinX || chunkX > chunkMaxX || chunkZ < chunkMinZ || chunkZ > chunkMaxZ) return false;
		if(this.chunkData[chunkX-this.chunkMinX][chunkZ-this.chunkMinZ] == null) return false;
		return true;
	}
	
	public short[][] loadChunk(int chunkX, int chunkZ) {
		WorldChunkData chunk = this.getChunkData(chunkX, chunkZ);
		return chunk.getFinalBlockData();
	}
	
	public WorldChunkData getChunkData(int chunkX, int chunkZ) {
		if(chunkData == null) createChunkData(chunkX, chunkZ);
		while(chunkX < chunkMinX || chunkX > chunkMaxX || chunkZ < chunkMinZ || chunkZ > chunkMaxZ) {
			expandChunkData(chunkX, chunkZ);
		}
		if(this.chunkData[chunkX-this.chunkMinX][chunkZ-this.chunkMinZ] == null) {
			this.chunkData[chunkX-this.chunkMinX][chunkZ-this.chunkMinZ] = new WorldChunkData(this, chunkX, chunkZ);
		}
		return this.chunkData[chunkX-this.chunkMinX][chunkZ-this.chunkMinZ];
	}

	private void createChunkData(int chunkX, int chunkZ) {
		this.chunkMinX = chunkX;
		this.chunkMinZ = chunkZ;
		this.chunkMaxX = chunkX;
		this.chunkMaxZ = chunkZ;
		this.chunkData = new WorldChunkData[1][1];
	}

	private void expandChunkData(int chunkX, int chunkZ) {
		int xOffset = 0;
		int	zOffset = 0;
		int xSize = this.chunkMaxX - this.chunkMinX + 1;
		int	zSize = this.chunkMaxZ - this.chunkMinZ + 1;
		int newMinX = this.chunkMinX, newMaxX = this.chunkMaxX;
		int	newMinZ = this.chunkMinZ, newMaxZ = this.chunkMaxZ;
		
		if(chunkX < this.chunkMinX) { xOffset = xSize;	newMinX -= xSize; }
		if(chunkZ < this.chunkMinZ) { zOffset = zSize; 	newMinZ -= zSize; }
		if(chunkX > this.chunkMaxX) newMaxX += xSize; 
		if(chunkZ > this.chunkMaxZ) newMaxZ += zSize; 
		
		WorldChunkData[][] newChunkData = new WorldChunkData[newMaxX - newMinX + 1][newMaxZ - newMinZ + 1];
		for(int copyX = this.chunkMinX; copyX <= this.chunkMaxX; copyX++) {
			System.arraycopy(this.chunkData[copyX - this.chunkMinX], 0, newChunkData[copyX - this.chunkMinX + xOffset], zOffset, zSize);
		}
		this.chunkData = newChunkData;
		this.chunkMinX = newMinX;
		this.chunkMinZ = newMinZ;
		this.chunkMaxX = newMaxX;
		this.chunkMaxZ = newMaxZ;
	}
	
	public int getMemoryUsage() {
		return this.memoryUsage;
	}

	public short[][] loadFromFile(int savePosition) {
		return this.worldDataFile.load(savePosition);
	}

	public int saveToFile(short[][] blocks) {
		return this.worldDataFile.save(blocks);
	}
	
}
