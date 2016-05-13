package aero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

/**
 * AeroGenerator is the class that interfaces with Bukkit to provide block populators
 * and chunk data for newly generated chunks. Most of the work done by AeroGen is passed
 * off to the IslandSpawner, which randomly rolls schematics (islands) and makes 
 * sure that they are added to the world.
 * 
 * @author MortusNegati
 */
public class AeroGenerator extends ChunkGenerator {

	private AeroGen plugin;
	private String worldName = "";
	private HashMap<World, SchematicGenerator> generators = new HashMap<World, SchematicGenerator>();
	private Int3D spawn;
	
	/**
	 * Creates a new AeroGenerator for the world given.
	 * @param plugin
	 * @param worldName
	 */
	public AeroGenerator(AeroGen plugin, String worldName) {
		this.plugin = plugin;
		this.worldName = worldName;
	}
	
	/**
	 * Returns the spawn location. This is a bit buggy right now, I use multiverse, which fixes this for me.
	 */
	public Location getFixedSpawnLocation(World world, Random random) {
		if(spawn == null) {
			if(!generators.containsKey(world)) {
				generators.put(world, new SchematicGenerator(this, world));
			}
			generators.get(world).prepareChunk(world, 0, 0);
			if(spawn == null) spawn = new Int3D(8, 90, 8);
		}
		int x = spawn.x+random.nextInt(32)-16;
		int z = spawn.z+random.nextInt(32)-16;
		return new Location(world, x, world.getHighestBlockYAt(x, z), z);
	}
	
	/**
	 * List of populators including the all important ReplacementPopulator
	 */
	public List<BlockPopulator> getDefaultPopulators(World world) {
		ArrayList<BlockPopulator> list = new ArrayList<BlockPopulator>();
		list.add(new PostPopulator(plugin));
		return list;
	}
	
	/**
	 * Generate the chunks block data by passing it off on the SchematicSpawnHandler
	 */
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
		if(!generators.containsKey(world)) {
			generators.put(world, new SchematicGenerator(this, world));
		}
		short[][] blocks = generators.get(world).generate(world, random, chunkX, chunkZ, biomes);
		byte[][] bytes = new byte[blocks.length][4096];
		for(int i = 0; i < blocks.length; i++) {
			if(blocks[i] != null) {
				bytes[i] = new byte[4096];
				for(int j = 0; j < 4096; j++) {
					bytes[i][j] = (byte) blocks[i][j];
				}
			}
		}
		return bytes;
	}
	
	/**
	 * Generate the chunks block data by passing it off on the SchematicSpawnHandler
	 */
	public short[][] generateExtBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
		if(!generators.containsKey(world)) {
			generators.put(world, new SchematicGenerator(this, world));
		}
		return generators.get(world).generate(world, random, chunkX, chunkZ, biomes);
	}

	/**
	 * Provides the spawn location so that an island will be guaranteed to spawn there.
	 * @param location
	 */
	public void setSpawn(Int3D location) {
		this.spawn = location;
	}

	/**
	 * Returns the name of the world that this generator object was created for.
	 * @return
	 */
	public String getWorldName() {
		return this.worldName;
	}
	
}
