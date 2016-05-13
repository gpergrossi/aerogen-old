package island;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;


import features.OreVein;
import features.PostShrub;
import features.PostTree;

import aero.AeroGen;
import aero.Int3D;

/**
 * BiomeDescription is a class that converts Mojang's strange biome definitions, which I have no clue how to use, into
 * something compatible with my generator. Biomes for islands are still generated using the default Minecraft code, but
 * how the generated biomes look (i.e. how many islands, what the islands look like) is controlled by this class and the
 * config.yml file.
 *  
 * @author MortusNegati
 */
public class BiomeDescription {

	private static HashMap<String, BiomeDescription> biomes = new HashMap<String, BiomeDescription>();
	private static HashMap<Biome, BiomeDescription> biomeDescription = new HashMap<Biome, BiomeDescription>();
	private static BiomeDescription defaultBiome = new BiomeDescription();
	
	/**
	 * Gets the appropriate BiomeDescription for the minecraft biome object provided. 
	 * @param mcBiome - minecraft biome object
	 * @return BiomeDescription - description for the minecraft biome provided
	 */
	public static BiomeDescription getDescription(Biome mcBiome) {
		if(!biomeDescription.containsKey(mcBiome)) {
			String name = "";
			String[] nameParts = mcBiome.name().split("_");
			for(int i = 0; i < nameParts.length; i++) {
				if(i == 0) {
					name += nameParts[i].toLowerCase();
				} else {
					name += nameParts[i].substring(0, 1).toUpperCase()+nameParts[i].substring(1).toLowerCase();
				}
			}
			if(!biomes.containsKey(name)) {
				biomes.put(name, new BiomeDescription(name));
			}
			biomeDescription.put(mcBiome, biomes.get(name));
		}
		return biomeDescription.get(mcBiome);
	}
	
	/**
	 * Holds a list of all the fields in this class
	 */
	private static final Field[] allFields = BiomeDescription.class.getFields();
	
	/**
	 * Creates a new biome description using the name provided and loading the defaults
	 * from the defaultBiome.
	 * @param name - name of the biome
	 */
	public BiomeDescription(String name) {
		for(Field field : allFields) {
			try {
				field.set(this, field.get(defaultBiome));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.name = name;
		this.treeType = new PostTree[defaultBiome.treeType.length];
		System.arraycopy(defaultBiome.treeType, 0, this.treeType, 0, this.treeType.length);
	}
	
	/**
	 * Creates a new biome named "default", using the coded default values.
	 */
	public BiomeDescription() {
		this.name = "default";
	}
	
	/********************************************************************************************************\
	 ********************** ALL OF THE ATTRIBUTES ARE BASED PER BIOME, BELOW ARE DEFAULTS *******************
	\********************************************************************************************************/
	
	public String name = "Nameless";
	
	//General
	public double chance = 0.075; 			//Chance of this type of island to appear centered in any given chunk
	public int minSize = 32;			//Minimum lateral size of the island
	public int maxSize = 256;			//Maximum lateral size of the island
	public double sizeWeightExponent = 6.0;	//Exponent applied to the random roll of sizes
	public double squareRegularity = 0.5;	//Coefficient of squareness (totally made this up but it works)
	public int minDepth = 28;			//Minimum vertical size of the island
	public int maxDepth = 128;			//Maximum vertical size of the island
	public double depthRatio = 0.4;			//Ratio of height attributed to size 
	public double depthRegularity = 0.8;	//Coefficient of height according to the calculated ratio
	public double minAltitude = 0;			//Minimum spawn altitude (adjusted by height of the island)
	public double maxAltitude = 1.0;		//Maximum spawn altitude (adjusted by height of the island)
	
	//Composition
	public byte rockMaterial = (byte) Material.STONE.getId();		//Material to fill the bottom of the island with (stone)
	public byte soilMaterial = (byte) Material.DIRT.getId();		//Material to fill the top layers of the island with (dirt)
	public byte surfaceMaterial = (byte) Material.GRASS.getId(); 	//Material to put on the surface of the island (grass)
	public byte lakeBedMaterial = (byte) Material.SAND.getId();		//Material to use as the beach (sand)
	public byte lakeBedMudMaterial = (byte) Material.SAND.getId();	//Material to mix into the beach material in swamps mostly (dirt)
	public byte fluidMaterial = (byte) Material.WATER.getId();		//Material to use for lakes
	public byte snowMaterial = (byte) Material.SNOW.getId();		//Material to use for snow (always snow?)
	
	//Terrain
	public double roughness = 8.0;			//Roughness, double amplitude of surface noise
	public double frequency = 0.5;			//Frequency of surface noise
	public boolean cliffs = false;			//Cliffs?
	public int minCliffHeight = 4;		//Minimum cliff height
	public int maxCliffHeight = 8;		//Maximum cliff height
	public double soilDepth = 0.3;			//Top percentage of the island to be made of soil
	public double lakeChance = 0.1;			//Chance of a lake occurring
	public double waterPondChance = 0.0002;	//Water pond chance, rolled for each block on the surface of the island
	public double lavaPondChance = 0.0001;	//Lava pond chance, rolled for each stone block with a caveSpace material above it
	public boolean snow = false;			//Snow?
	public double caveChance = 1.0;
	public double waterFallDensity = 0.0005;//Chance of a lava or water fall, rolled per underground stone with exactly one air adjacent
	public double lavaFallDensity = 0.0005;	//Chance that the liquid fall is lava, rolled after liquid fall is guaranteed
	
	//Plant life
	public double treeDensity = 0.005;		//Chance of a tree, rolled for each surface grass
	public PostTree[] treeType = new PostTree[0];	//Type of tree
	
	public double reedDensity = 0.0;		//Reed density, rolled only for sand or grass blocks near water
	public int readClusterMin = 2;			//Minimum cluster size for reeds
	public int readClusterMax = 5;			//Maximum cluster size for reeds
	
	public double cactusDensity = 0.0;		//Cactus density, rolled for any sand block on the surface
	
	public double shrubDensity = 0.02;			//Chance of a shrub, rolled for each surface grass or sand block
	public PostShrub[] shrubType = new PostShrub[0];	//Type of shrub (0 is dried bush, 1 is grass, 2 is fern)
	
	public double flowerDensity = 0.0;			//Flower cluster density, roll per surface grass block
	public double flowerClusterRed = 0.5;		//Chance for cluster to be red, rolled after cluster is guaranteed
	public int flowerClusterMin = 3;			//Flower cluster minimum size
	public int flowerClusterMax = 7;			//Flower cluster maximum size
	
	public double mushroomDensity = 0.0;		//Mushroom cluster density, roll per surface grass block
	public double mushroomClusterBrown = 0.5;	//Chance for cluster to be red, rolled after cluster is guaranteed
	public int mushroomClusterMin = 3;			//Mushroom cluster minimum size
	public int mushroomClusterMax = 5;			//Mushroom cluster maximum size
	
	public double watermelonDensity = 0.0;		//Water melon cluster density, rolled per surface grass block
	public int watermelonClusterMin = 3;		//Cluster minimum size
	public int watermelonClusterMax = 5;		//Cluster maximum size
	
	public double pumpkinDensity = 0.0;			//Pumpkin cluster density, rolled per surface grass block
	public int pumpkinClusterMin = 3;			//Cluster minimum size
	public int pumpkinClusterMax = 5;			//Cluster maximum size
	
	//Caves
	public double cavePercentage = 0.1;			//Percentage of the total island volume that can become cave
	
	public double maxCaveRadius = 4.0;			//Maximum node radius
	public double startCaveRadius = 2.0;		//Starting node radius
	public double minCaveRadius = 1.8;			//Minimum node radius
	public double maxCaveRadiusChange = 0.3;	//Maximum change in radius from one node to the next
	
	public int maxSplit = 2;				//Maximum number of tunnels a single tunnel can split into when a split occurs 
	public double splitChance = 0.3;		//Chance for the cave to form an intersection per node
	
	public double angleDX = Math.PI/8;		//angle change maximum magnitude for x direction
	public double angleDY = Math.PI/24;		//angle change maximum magnitude for y direction
	public double maxAngleY = Math.PI/2; 	//Maximum steepness of cave tunnels
	
	//Ores	(in order: {gravel, coal, iron, gold, lapis, redstone, diamond}
	public ArrayList<OreVein> oreVeins = new ArrayList<OreVein>();
	public ArrayList<ArrayList<OreVein>> orePasses = new ArrayList<ArrayList<OreVein>>();
	
	/**
	 * Decides whether an island should spawn for a particular chunk. Since this
	 * method is called once for each chunk, the percentage spawn chance should
	 * be fairly low, islands usually take up 6x6 chunks. By default, islands 
	 * spawn in a chunk 7.5% percent of the time.
	 * @param random - random generator object
	 * @return boolean - true if an island should be made, else false
	 */
	public boolean rollIsland(Random random) {
		return random.nextDouble() < chance;
	}
	
	/**
	 * Rolls the position of the island, taking into account the island's size and making sure
	 * not to place any part of the island outside the provided minimum and maximum altitudes.
	 * Since the maximum altitude changes per world, minimumAltitude and maximumAltitude are
	 * provided as a percentage which scales to the world.
	 * @param world - world to be placed in (used to get max world height)
	 * @param random - a random number generator
	 * @param chunkX - chunk position x (output position will be within this chunk)
	 * @param chunkZ - chunk position y (output position will be within this chunk)
	 * @param size - size of the island
	 * @return Int3D - location of the island within the given chunk
	 */
	public Location getPosition(World world, Random random, int chunkX, int chunkZ, Int3D size) {
		int x = chunkX*16 + random.nextInt(16)-size.x/2;
		int maxHeight = world.getMaxHeight();
		int yMin = (int) (minAltitude*maxHeight);
		int yMax = (int) (maxAltitude*maxHeight-size.y-getAirSpace());
		int y = yMin + random.nextInt(yMax-yMin+1);
		int z = chunkZ*16 + random.nextInt(16)-size.z/2;
		return new Location(world, x, y, z);
	}
	
	/**
	 * Rolls the dimensions of the island using a weighted number generator. The generator
	 * rolls a uniform double from 0.0 to 1.0 and then takes it to the power of sizeWeightExponent,
	 * this number is then multiplied by the range and added to the minimum for each size 
	 * dimension. Dimensions are correlated by the factors squareRegularity and depthRegularity.
	 * @param random - a random number generator
	 * @return Int3D - dimensions of the island
	 */
	public Int3D getDimensions(Random random) {
		int length = minSize + weightedRand(random, maxSize-minSize+1, this.sizeWeightExponent);
		int bredth = minSize + weightedRand(random, maxSize-minSize+1, this.sizeWeightExponent);
		bredth = (int) (squareRegularity*(double)length + (1.0-squareRegularity)*(double)bredth);
		int height = minDepth + weightedRand(random, maxDepth-minDepth+1, this.sizeWeightExponent);
		if(height < minDepth) height = minDepth;
		height = (int) (depthRegularity*(double)(length+bredth)/2.0*depthRatio + (1.0-depthRegularity)*(double)height);
		height += getTerrainSpace();
		return new Int3D(length, height, bredth);
	}

	/**
	 * Calculates the require land space that needs to be available above the centerY coordinate in
	 * the Island schematic.
	 * @return int - number of blocks that need to be open above the centerY coordinate in the schematic
	 */
	public int getTerrainSpace() {
		return (int)roughness + maxCliffHeight + 1;
	}
	
	/**
	 * Calculates the airspace of the island, which is the number of blocks below the maximum island altitude
	 * that the top of the island schematic has to be.
	 * @return int - blocks of free space needed above the island for trees and other such objects
	 */
	public int getAirSpace() {
		int airSpace = 0;
		if(treeDensity > 0) {
			int maxTreeHeight = 0;
			for(int i = 0; i < treeType.length; i++) {
				int height = getHeightOfTreeType(treeType[i].getType());
				if(height > maxTreeHeight) {
					maxTreeHeight = height;
					if(height == 24) break;
				}
			}
			airSpace += maxTreeHeight;
		}
		return airSpace;
	}

	/**
	 * Returns a guessed maximum height for each type of tree
	 * @param type - tree type
	 * @return int - number of blocks it could potentially take up in the y direction
	 */
	private int getHeightOfTreeType(TreeType type) {
		//These are all educated guesses
		if(type.equals(TreeType.BIG_TREE)) return 14;
		if(type.equals(TreeType.BIRCH)) return 6;
		if(type.equals(TreeType.BROWN_MUSHROOM)) return 7;
		if(type.equals(TreeType.JUNGLE)) return 24;
		if(type.equals(TreeType.JUNGLE_BUSH)) return 5;
		if(type.equals(TreeType.RED_MUSHROOM)) return 7;
		if(type.equals(TreeType.REDWOOD)) return 7;
		if(type.equals(TreeType.SMALL_JUNGLE)) return 6;
		if(type.equals(TreeType.SWAMP)) return 7;
		if(type.equals(TreeType.TALL_REDWOOD)) return 9;
		if(type.equals(TreeType.TREE)) return 7;
		return 24; //Should be safe for new tree types until I update
	}

	/**
	 * Non linear random makes lots of small islands and some rare, really big ones.
	 * This number generator behaves the same as a normal one with a power of 1,
	 * higher powers result in a lower median score. output = random()^power*range
	 * @param random - a random number generator
	 * @param range - range of output
	 * @param power - power used for weighting curve
	 * @return int - integer from 0 (incluse) to range (exclusive), high powers result in overall lower rolls
	 */
	private int weightedRand(Random random, int range, double power) {
		return (int) (Math.pow(random.nextDouble(), power)*(range+1));
	}
	
	/**
	 * Rolls to see if this island should place water at lower parts of the terrain.
	 * @param random - a random number generator
	 * @return boolean - true if a lake should form, else false
	 */
	public boolean rollLake(Random random) {
		return random.nextDouble() < lakeChance;
	}

	/**
	 * Rolls the height of the cliffs on the current island. All cliffs are just an offset of the normal
	 * terrain height map.
	 * @param random - a random number generator
	 * @return int - cliff height between minCliffHieght and maxCliffHeight
	 */
	public int rollCliffHeight(Random random) {
		return random.nextInt(maxCliffHeight-minCliffHeight+1)+minCliffHeight;
	}

	public boolean rollReed(Random random) {
		return random.nextDouble() < reedDensity;
	}

	public boolean rollCactus(Random random) {
		return random.nextDouble() < cactusDensity;
	}

	public boolean rollFlower(Random random) {
		return random.nextDouble() < flowerDensity;
	}
	
	public boolean rollFlowerType(Random random) {
		return random.nextDouble() < flowerClusterRed;
	}
	
	public int rollFlowerClusterSize(Random random) {
		return random.nextInt(flowerClusterMax-flowerClusterMin+1) + flowerClusterMin;
	}
	
	public boolean rollMushroom(Random random) {
		return random.nextDouble() < mushroomDensity;
	}
	
	public boolean rollMushroomType(Random random) {
		return random.nextDouble() < mushroomClusterBrown;
	}
	
	public int rollMushroomClusterSize(Random random) {
		return random.nextInt(mushroomClusterMax-mushroomClusterMin+1) + mushroomClusterMin;
	}

	public boolean rollPumpkin(Random random) {
		return random.nextDouble() < pumpkinDensity;
	}
	
	public int rollPumpkinClusterSize(Random random) {
		return random.nextInt(pumpkinClusterMax-pumpkinClusterMin+1) + pumpkinClusterMin;
	}
	
	public boolean rollWatermelon(Random random) {
		return random.nextDouble() < watermelonDensity;
	}
	
	public int rollWatermelonClusterSize(Random random) {
		return random.nextInt(watermelonClusterMax-watermelonClusterMin+1) + watermelonClusterMin;
	}

	public int getMaximumCaveSpace(int islandMass) {
		return (int) (this.cavePercentage * islandMass);
	}

	public boolean rollWaterPond(Random random) {
		return random.nextDouble() < waterPondChance;
	}
	
	public boolean rollLavaPond(Random random) {
		return random.nextDouble() < lavaPondChance;
	}

	public boolean rollCave(Random random) {
		return random.nextDouble() < caveChance;
	}
	
	public boolean rollWaterFall(Random random) {
		return random.nextDouble() < waterFallDensity;
	}

	public boolean rollLavaFall(Random random) {
		return random.nextDouble() < lavaFallDensity;
	}
	
	/**
	 * Loads the appropriate configuration data from the biomes section in the Config.yml file.
	 * As this method discovers biome groups it passes their part of the config to the method loadBiome().
	 * @param config
	 */
	public static void loadBiomes(ConfigurationSection config) {
		Set<String> biomes = config.getKeys(false);
		for(String biomeName : biomes) {
			ConfigurationSection biomeConfig = config.getConfigurationSection(biomeName);
			loadBiome(biomeName, biomeConfig);
		}
		AeroGen.log("Loaded "+biomes.size()+" biomes from config file.");
	}
	
	/**
	 * Loads the appropriate data from the Config.yml as it applies to one biome in the biomes section.
	 * The ores section is passed to loadOres, the rest are processed in this method.
	 * @param config
	 */
	private static void loadBiome(String name, ConfigurationSection config) {
		BiomeDescription biome = new BiomeDescription(name);
		HashMap<Byte, Double> shrubs = new HashMap<Byte, Double>();
		HashMap<TreeType, Double> trees = new HashMap<TreeType, Double>();
		Set<String> biomeKeys = config.getKeys(false);
		for(String key : biomeKeys) {
			ConfigurationSection section = config.getConfigurationSection(key);
			if(key.equalsIgnoreCase("ores")) {
				loadOres(section, biome);
				continue;
			}
			Set<String> items = section.getKeys(false);
			for(String item : items) {
				if(item.equalsIgnoreCase("treeTypes")) {
					ConfigurationSection treeTypesConfig = section.getConfigurationSection(item);
					Set<String> treeTypes = treeTypesConfig.getKeys(false);
					for(String treeType : treeTypes) {
						TreeType type = getTreeType(treeType);
						if(type != null) {
							trees.put(type, treeTypesConfig.getDouble(treeType));
						} else {
							AeroGen.log("Tree type in config.yml, \""+section.getCurrentPath()+"."+treeType+"\", does not exist. Try one of the types defined in the default biome in config.yml.");
						}
					}
				} else if(item.equalsIgnoreCase("shrubTypes")) {
					ConfigurationSection shrubTypesConfig = section.getConfigurationSection(item);
					Set<String> shrubTypes = shrubTypesConfig.getKeys(false);
					for(String shrubType : shrubTypes) {
		                if(shrubType.equalsIgnoreCase("driedBush")) {
		                	shrubs.put((byte) 0, shrubTypesConfig.getDouble(shrubType));
		                } else if(shrubType.equalsIgnoreCase("tallGrass")) {
		                	shrubs.put((byte) 1, shrubTypesConfig.getDouble(shrubType));
		                } else if(shrubType.equalsIgnoreCase("fern")) {
		                	shrubs.put((byte) 2, shrubTypesConfig.getDouble(shrubType));
		                } else {
		                	AeroGen.log("Shrub type in config.yml, \""+section.getCurrentPath()+"."+shrubType+"\", does not exist. Try \"driedBush\", \"tallGrass\", or \"fern\".");
		                }
					}
				} else if(key.equalsIgnoreCase("composition")) {
					Field field = null;
					try {
						field = biome.getClass().getField(item);
						field.set(biome, Byte.valueOf((byte) section.getInt(item)));
					} catch (Exception e) {
						AeroGen.log("Value in config.yml, \""+section.getCurrentPath()+item+"\", is not supported.");
					}
				} else {
					try {
						Field field = biome.getClass().getField(item);
						field.set(biome, section.get(item));
					} catch (Exception e) {
						AeroGen.log("Value in config.yml, \""+section.getCurrentPath()+item+"\", is not supported.");
					}
				}
			}
		}
		
		//Compile shrub HashMap into arrays
		Set<Byte> shrubSet = shrubs.keySet();
		biome.shrubType = new PostShrub[shrubSet.size()];
		int index = 0;
		for(Byte shrub : shrubSet) {
			biome.shrubType[index] = new PostShrub(shrub, shrubs.get(shrub)*biome.shrubDensity);
			index++;
		}
		
		//Compile tree HashMap into arrays
		Set<TreeType> treeSet = trees.keySet();
		biome.treeType = new PostTree[treeSet.size()];
		index = 0;
		for(TreeType tree : treeSet) {
			biome.treeType[index] = new PostTree(tree, trees.get(tree)*biome.treeDensity);
			index++;
		}
		
		if(name.equals("default")) {
			defaultBiome = biome;
		} else {
			BiomeDescription.biomes.put(name, biome);
		}
	}
	
	private static TreeType getTreeType(String type) {
		if(type.equalsIgnoreCase("oak")) return TreeType.TREE;
		if(type.equalsIgnoreCase("tallOak")) return TreeType.BIG_TREE;
		if(type.equalsIgnoreCase("birch")) return TreeType.BIRCH;
		if(type.equalsIgnoreCase("redwood")) return TreeType.REDWOOD;
		if(type.equalsIgnoreCase("tallRedwood")) return TreeType.TALL_REDWOOD;
		if(type.equalsIgnoreCase("jungleTree")) return TreeType.JUNGLE;
		if(type.equalsIgnoreCase("jungleBush")) return TreeType.JUNGLE_BUSH;
		if(type.equalsIgnoreCase("smallJungleTree")) return TreeType.SMALL_JUNGLE;
		if(type.equalsIgnoreCase("swampTree")) return TreeType.SWAMP;
		if(type.equalsIgnoreCase("bigBrownMushroom")) return TreeType.BROWN_MUSHROOM;
		if(type.equalsIgnoreCase("bigRedMushroom")) return TreeType.RED_MUSHROOM;
		return null;
	}

	private static void loadOres(ConfigurationSection config, BiomeDescription biome) {
		//Load a list of all the ores
		biome.oreVeins = new ArrayList<OreVein>();
		Set<String> ores = config.getKeys(false);
		for(String ore : ores) {
			ConfigurationSection section = config.getConfigurationSection(ore);
			OreVein vein = new OreVein(ore);
			Set<String> properties = section.getKeys(false);
			for(String property : properties) {
				if(property.equalsIgnoreCase("blockID")) {
					vein.setMaterial((byte) section.getInt(property));
				} else if(property.equalsIgnoreCase("replaces")) {
					vein.setReplaces((byte) section.getInt(property));
				} else if(property.equalsIgnoreCase("chance")) {
					vein.setChance(section.getDouble(property));
				} else if(property.equalsIgnoreCase("minDepth")) {
					vein.setMinDepth(section.getDouble(property));
				} else if(property.equalsIgnoreCase("maxDepth")) {
					vein.setMaxDepth(section.getDouble(property));
				} else if(property.equalsIgnoreCase("minVeinSize")) {
					vein.setMinVeinSize(section.getInt(property));
				} else if(property.equalsIgnoreCase("maxVeinSize")) {
					vein.setMaxVeinSize(section.getInt(property));
				} else {
					AeroGen.log(property+" is not a valid ore property in "+section.getCurrentPath());
				}
			}
			if(vein.getChance() > 0.0) biome.oreVeins.add(vein);
		}
		
		//Compile the ores into an efficient list of passes 
		//(an ore that generates a material that a later ore replaces must be in an earlier pass to work properly)
		biome.orePasses = new ArrayList<ArrayList<OreVein>>();
		ArrayList<OreVein> currentPass = new ArrayList<OreVein>();
		for(OreVein vein : biome.oreVeins) {
			boolean compatible = true;
			for(OreVein other : currentPass) {
				if(other.getMaterial() == vein.getReplaces()) {
					compatible = false;
					break;
				}
			}
			if(compatible) {
				currentPass.add(vein);
			} else {
				biome.orePasses.add(currentPass);
				currentPass = new ArrayList<OreVein>();
			}
		}
		biome.orePasses.add(currentPass);
		
	}
	
}
