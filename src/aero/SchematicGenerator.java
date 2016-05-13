package aero;


import island.Island;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;

import riverWeb.RiverWeb;


public class SchematicGenerator {
	
	private int memory = 0;
	
	private Random random = new Random();
	public int chunkMinX, chunkMaxX;
	public int chunkMinZ, chunkMaxZ;
	public boolean[][] chunkPrepared = null;
	
	private AeroGenerator gen;
	private World world;
	private WorldChunkCache worldCache;
	
	public SchematicGenerator(AeroGenerator gen, World world) {
		AeroGen.log("Generator created for "+world.getName());
		this.gen = gen;
		this.world = world;
		this.worldCache = new WorldChunkCache(world);
	}

	public short[][] generate(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
		loadChunk(world, chunkX, chunkZ);
		short[][] blockSections = worldCache.loadChunk(chunkX, chunkZ);
		return blockSections;
	}
	
	private void loadChunk(World world, int chunkX, int chunkZ) {
		worldCache.curChunkX = chunkX;
		worldCache.curChunkZ = chunkZ;
		int maxSchematicRange = ((Schematic.maxSchematicChunkSize >> 1)+1);
		for(int i = chunkX-maxSchematicRange; i <= chunkX+maxSchematicRange; i++) {
			for(int k = chunkZ-maxSchematicRange; k <= chunkZ+maxSchematicRange; k++) {
				prepareChunk(world, i, k);
			}
		}
	}

	public String getMemoryUsage() {
		if(memory < 1024) {
			return memory + " bytes";
		} else if(memory < 1048576) {
			double mem = (double)memory / 1024.0;
			String amount = String.valueOf(mem);
			amount = amount.substring(0, amount.indexOf(".")+2);
			return amount + " KB";
		} else if(memory < 1073741824){
			double mem = (double)memory / 1048576.0;
			String amount = String.valueOf(mem);
			amount = amount.substring(0, amount.indexOf(".")+2);
			return amount + " MB";
		} else {
			double mem = (double)memory / 1073741824.0;
			String amount = String.valueOf(mem);
			amount = amount.substring(0, amount.indexOf(".")+2);
			return amount + " GB";
		}
	}

	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return world.isChunkLoaded(chunkX, chunkZ);
	}
	
	public void prepareChunk(World world, int chunkX, int chunkZ) {
		
		if(chunkPrepared == null) createChunkArray(chunkX, chunkZ);
		while(chunkX < chunkMinX || chunkX > chunkMaxX || chunkZ < chunkMinZ || chunkZ > chunkMaxZ) {
			expandChunkArray(chunkX, chunkZ);
		}
		if(chunkPrepared[chunkX-chunkMinX][chunkZ-chunkMinZ]) return;
		
		long seed = (long) (chunkX*world.getSeed()) % (Long.MAX_VALUE/2);
		seed += (long) (chunkZ*(world.getSeed() % 65536)*512);
		random.setSeed(seed);

		Island island = Island.createIsland(gen, world, random, chunkX, chunkZ);
		if(island != null) {
			worldCache.addSchematic(island.asSchematic());
		}

//		RiverWeb rivers = RiverWeb.createRiverWeb(gen, world, random, chunkX, chunkZ);
//		if(rivers != null) {
//			worldCache.addSchematic(rivers.asSchematic());
//		}
		
		chunkPrepared[chunkX-chunkMinX][chunkZ-chunkMinZ] = true;
	}

	private void createChunkArray(int chunkX, int chunkZ) {
		chunkMinX = chunkX;
		chunkMinZ = chunkZ;
		chunkMaxX = chunkX;
		chunkMaxZ = chunkZ;
		chunkPrepared = new boolean[1][1];
	}

	private void expandChunkArray(int x, int y) {
		int xOffset = 0;
		int	zOffset = 0;
		int xSize = chunkMaxX - chunkMinX + 1;
		int	zSize = chunkMaxZ - chunkMinZ + 1;
		int newMinX = chunkMinX, newMaxX = chunkMaxX;
		int	newMinZ = chunkMinZ, newMaxZ = chunkMaxZ;
		
		if(x < chunkMinX) { xOffset = xSize;	newMinX -= xSize; }
		if(y < chunkMinZ) { zOffset = zSize; 	newMinZ -= zSize; }
		if(x > chunkMaxX) newMaxX += xSize; 
		if(y > chunkMaxZ) newMaxZ += zSize; 
		
		//Copy all the array data
		boolean[][] newChunkArray = new boolean[newMaxX - newMinX + 1][newMaxZ - newMinZ + 1];
		for(int copyX = chunkMinX; copyX <= chunkMaxX; copyX++) {
			System.arraycopy(chunkPrepared[copyX - chunkMinX], 0, newChunkArray[copyX - chunkMinX + xOffset], zOffset, zSize);
		}
		chunkPrepared = newChunkArray;
		chunkMinX = newMinX;
		chunkMinZ = newMinZ;
		chunkMaxX = newMaxX;
		chunkMaxZ = newMaxZ;
	}

	public World getWorld() {
		return this.world;
	}

	public WorldChunkCache getWorldCache() {
		return this.worldCache;
	}
	
}
