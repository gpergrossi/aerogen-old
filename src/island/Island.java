package island;

import java.util.ArrayList;
import java.util.Random;

import noise.FractalNoise2D;
import noise.NoiseMap;
import noise.RangeMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;


import features.CaveSystem;
import features.OreVein;
import features.FeaturePlacement;
import features.Pond;
import features.PostShrub;
import features.PostTree;

import aero.AeroGenerator;
import aero.Int3D;
import aero.Schematic;

/**
 * Island is where all the magic happens, here and SchematicSpawnHandler. 
 * This Island class is responsible for generating each island you see in 
 * the world. The class is kind of size.xy, but it's totally awesome. 
 * (I think the professional levels of my comments are degrading).
 * 
 * @author MortusNegati
 */
public class Island extends Schematic {
	
	/**
	 * Creates an island at the given chunk position
	 * @param gen - plugin to report all output to 
	 * @param world - world to generate in 
	 * @param random - random generator for generation 
	 * @param chunkX - chunk position
	 * @param chunkZ - chunk position
	 * @return
	 */
	public static Island createIsland(AeroGenerator gen, World world, Random random, int chunkX, int chunkZ) {
		
		Biome mcBiome = world.getBiome(chunkX*16+8, chunkZ*16+8);
		BiomeDescription biome = BiomeDescription.getDescription(mcBiome);
		
		if(!biome.rollIsland(random) && !(chunkX == 0 && chunkZ == 0)) return null;
		Int3D size = null;
		Location position = null;
		if(chunkX == 0 && chunkZ == 0) {
			biome = BiomeDescription.getDescription(Biome.FOREST);
			size = new Int3D(128, 64, 128);
			position = new Location(world, -56, 48, -56);
			gen.setSpawn(new Int3D(0, (int) (position.getY()+size.y-biome.roughness), 0));
		} else {
			size = biome.getDimensions(random);
			position = biome.getPosition(world, random, chunkX, chunkZ, size);
		}
		
		return new Island("Island("+chunkX+","+chunkZ+")", position, size, random, biome);
		
	}
		
	private Int3D center;
	private Random random;
	private boolean[][] isLand;
	private int[][] distanceFromEdge;
	private double[][] heightCoefficients;
	private int[][] heightMap;
	private int[][] bottomMap;
	private int[][] cliffMap;
	private BiomeDescription biome;
	private int totalVolume = 0;
	
	/**
	 * Constructs and therefore generates an island. These parameters are hard to fill so
	 * use the Island.createIsland() method instead.
	 * @param biome - BiomeDescription returned from BiomeDescription.getDescription()
	 * @param random - random object used for generation (dependent on the world seed and chunk position)
	 * @param position - position of the island's center block
	 * @param size - size of the island  
	 */
	private Island(String name, Location position, Int3D size, Random random, BiomeDescription biome) {
		super(name);
		
//		System.out.println("Generating " + name + "...");
		
		this.position = position;
		
		this.biome = biome;
		this.random = random;

		Topograph topograph = new Topograph(size.x, size.z);
		this.isLand = topograph.getTopograph();
		this.distanceFromEdge = topograph.getDistancesFromEdge();
		this.heightCoefficients = topograph.getHeightCoefficients();
		
		this.size = new Int3D();
		this.size.x = topograph.getWidth();
		this.size.z = topograph.getHeight();
		this.size.y = size.y;

		this.center = new Int3D();
		this.center.x = this.size.x / 2;
		this.center.z = this.size.z / 2;
		this.center.y = this.size.y - biome.getTerrainSpace();

//		System.out.println("Generating terrain...");
		generateIslandTerrain();		//Main terrain generation
		erode();						//Roughens up the edges of things and makes them look nice

//		System.out.println("Generating features...");
		addPonds();						//Water and lava ponds
		addCaves();						//Caves
		supportFallingMaterials();		//Supports any flowing or falling materials
		
		populatePlants();				//Trees and shrubs
		addExtras();					//Lava falls, water falls, and ores
		
	}

	/**
	 * Generates the main island blocks. I've rewritten this so many times I'm not quite sure how
	 * it works any more and it doesn't help that I don't know the meaning of the scaling on the 
	 * Simplex noise generator.
	 */
	private void generateIslandTerrain() {
		
		//Does this island have lakes?
		boolean lake = biome.rollLake(random);
		
		//Roll the island's cliff size.y
		int cliffHeight = 0;
		if(biome.cliffs) {
			cliffHeight = biome.rollCliffHeight(random);
		}
		
		//Initialize the island size.y maps
		int maxY = 0;
		int minY = size.y-1;
		heightMap = new int[size.x][size.z];
		bottomMap = new int[size.x][size.z];
		cliffMap = new int[size.x][size.z];
		totalVolume = 0;
		
		//Create a noise generator
		FractalNoise2D gen1 = new FractalNoise2D(random.nextLong(), 4, biome.frequency/64.0);
		FractalNoise2D gen2 = new FractalNoise2D(random.nextLong(), 1, biome.frequency/16.0);
		FractalNoise2D gen3 = new FractalNoise2D(random.nextLong(), 4, biome.frequency/64.0);
		FractalNoise2D gen4 = new FractalNoise2D(random.nextLong(), 2, 0.125);
		NoiseMap map = new RangeMap(0.0, 1.0);
		gen2.setOutputMap(map);
		gen3.setOutputMap(map);
		gen4.setOutputMap(map);
		
		for(int x = 0; x < size.x; x++) {
			for(int z = 0; z < size.z; z++) {
				if(!isLand[x][z]) continue;
				double top = gen1.getValue(x, z)*biome.roughness;
				if(lake) {
					top = top*heightCoefficients[x][z];
					if(biome.lakeChance < 1.0) {
						top *= -1.0;
						top = Math.pow(Math.abs(top), 0.75)*Math.signum(top);
					}
				}
				top += center.y;
				double bottom = heightCoefficients[x][z]*gen2.getValue(x, z)*top;
				double cliff = (gen3.getValue(x, z) > 0.7 ? cliffHeight : 0.0);
				cliffMap[x][z] = (int)cliff;
				heightMap[x][z] = (int)top;
				bottomMap[x][z] = (int)top - (int)bottom;
				if(bottomMap[x][z] < minY) minY = bottomMap[x][z];
				if(cliffMap[x][z]+heightMap[x][z] > maxY) maxY = cliffMap[x][z]+heightMap[x][z];
			}
		}
		
		this.size.y = maxY-minY+1;
		this.blocks = new short[size.x*size.y*size.z];
		this.center.y -= minY;
		
		for(int x = 0; x < size.x; x++) {
			for(int z = 0; z < size.z; z++) {
				if(!isLand[x][z]) continue;
				bottomMap[x][z] = bottomMap[x][z] - minY;
				heightMap[x][z] = heightMap[x][z] - minY;
				maxY = heightMap[x][z]+cliffMap[x][z];
				int rangeY = maxY-bottomMap[x][z];
				if(lake && maxY < center.y) maxY = center.y;
				for(int y = bottomMap[x][z]; y <= maxY; y++) {
					double percent = 1.0 - ((double)(y - bottomMap[x][z]) / (double)rangeY);
					if(lake && y <= center.y) {
						if(cliffMap[x][z] > 0) {
							if(percent == 0.0 || y == heightMap[x][z]+cliffMap[x][z]) {
								setBlock(x, y, z, biome.lakeBedMaterial);
								totalVolume++;
								continue;
							}
							setBlock(x, y, z, biome.rockMaterial);
							totalVolume++;
							continue;
						}
						if(percent <= biome.soilDepth) {
							if(percent < 0.0 || y > heightMap[x][z]+cliffMap[x][z]) {
								setBlock(x, y, z, biome.fluidMaterial);
								totalVolume++;
								continue;
							}
							double soil2 = gen4.getValue(x, z);
							if(soil2 <= 0.1 || soil2 >= 0.9) {
								setBlock(x, y, z, biome.lakeBedMudMaterial);
								totalVolume++;
								continue;
							}
							setBlock(x, y, z, biome.lakeBedMaterial);
							totalVolume++;
							continue;
						}
						setBlock(x, y, z, biome.rockMaterial);
						totalVolume++;
						continue;
					}
					if(cliffMap[x][z] > 0) {
						if(percent == 0.0 || y == heightMap[x][z]+cliffMap[x][z]) {
							setBlock(x, y, z, biome.surfaceMaterial);
							totalVolume++;
							continue;
						}
						setBlock(x, y, z, biome.rockMaterial);
						totalVolume++;
						continue;
					}
					if(percent <= biome.soilDepth) {
						if(percent == 0.0 || y == heightMap[x][z]+cliffMap[x][z]) {
							setBlock(x, y, z, biome.surfaceMaterial);
							totalVolume++;
							continue;
						}
						setBlock(x, y, z, biome.soilMaterial);
						totalVolume++;
						continue;
					}
					setBlock(x, y, z, biome.rockMaterial);
					totalVolume++;
				}
			}
		}
		
	}
	
	/**
	 * Removes some of the blocks around the edges of the island and cliffs for a more natural look.
	 */
	private void erode() {
		for(int i = 1; i < size.x-1; i++) {
			for(int k = 1; k < size.z-1; k++) {
				
				//If the block is on the edge of the island...
				if(distanceFromEdge[i][k] == 1) {
					int range = heightMap[i][k] - bottomMap[i][k] + 1;
					int jMax = bottomMap[i][k];
					if(range > 1) {
						jMax = random.nextInt(range) + bottomMap[i][k];
					}
					
					//...randomly remove the bottom blocks from that column 
					for(int j = bottomMap[i][k]; j < jMax; j++) {
						short block = getBlock(i, j, k);
						if(block == biome.rockMaterial || block == biome.soilMaterial) {
							setBlock(i, j, k, air);
						}
					}
				}
				
				//If the block is at the edge of a cliff...
				if(cliffMap[i][k] > 0) {
					if(cliffMap[i-1][k] == 0 || cliffMap[i+1][k] == 0 || cliffMap[i][k-1] == 0 || cliffMap[i][k+1] == 0) {
						int jm = random.nextInt(cliffMap[i][k]*3/4) + heightMap[i][k];
						
						//...randomly remove the bottom blocks from a column between the island surface and the cliff top
						for(int j = heightMap[i][k]+1; j < jm; j++) {
							short block = getBlock(i, j, k);
							
							//only the island's rock material is affected
							if(block == biome.rockMaterial) setBlock(i, j, k, air);
						}
					}
				}
				
			}
		}
	}
	
	/**
	 * Adds lava and water ponds
	 */
	private void addPonds() {
		
		//Randomly add some ponds to the underground and the surface. Lava ponds spawn underground, water on the surface.
		Pond waterPond = new Pond(8+random.nextInt(8), water, new FeaturePlacement(grass, air, biome.waterPondChance));
		Pond lavaPond = new Pond(8+random.nextInt(8), lava, new FeaturePlacement(stone, caveSpace, biome.lavaPondChance));
		Pond netherLavaPond = new Pond(12+random.nextInt(16), lava, new FeaturePlacement(netherrack, air, biome.lavaPondChance));
		for(int i = 0; i < size.x; i++) {
			for(int j = 0; j < size.y; j++) {
				for(int k = 0; k < size.z; k++) {
					if(waterPond.getPlacement().canPlace(this, random, i, j, k)) waterPond.place(this, random, i, j, k);
					if(lavaPond.getPlacement().canPlace(this, random, i, j, k)) lavaPond.place(this, random, i, j, k);
					if(netherLavaPond.getPlacement().canPlace(this, random, i, j, k)) netherLavaPond.place(this, random, i, j, k);
				}
			}
		}
		
	}
	
	/**
	 * Places stable blocks under all sand and gravel and around the bottom and edge blocks of lava and water.
	 * Secretly also replaces any dirt that has air above it with grass.
	 */
	private void supportFallingMaterials() {
		for(int i = 1; i < size.x-1; i++) {
			for(int k = 1; k < size.z-1; k++) {
				for(int j = 0; j < size.y; j++) {
					
					//Liquids
					if(getBlock(i, j, k) == water) {
						if(j > 0) {
							if(getBlock(i, j-1, k) == air) setBlock(i, j-1, k, biome.rockMaterial);
							if(getBlock(i-1, j, k) == air) setBlock(i-1, j, k, biome.soilMaterial);
							if(getBlock(i+1, j, k) == air) setBlock(i+1, j, k, biome.soilMaterial);
							if(getBlock(i, j, k-1) == air) setBlock(i, j, k-1, biome.soilMaterial);
							if(getBlock(i, j, k+1) == air) setBlock(i, j, k+1, biome.soilMaterial);
							if(getBlock(i, j-1, k) == caveSpace) setBlock(i, j-1, k, biome.rockMaterial);
							if(getBlock(i-1, j, k) == caveSpace) setBlock(i-1, j, k, biome.soilMaterial);
							if(getBlock(i+1, j, k) == caveSpace) setBlock(i+1, j, k, biome.soilMaterial);
							if(getBlock(i, j, k-1) == caveSpace) setBlock(i, j, k-1, biome.soilMaterial);
							if(getBlock(i, j, k+1) == caveSpace) setBlock(i, j, k+1, biome.soilMaterial);
						} else {
							setBlock(i, j, k, biome.rockMaterial);
						}
						continue;
					}
					
					if(getBlock(i, j, k) == lava) {
						if(j > 0) {
							if(getBlock(i, j-1, k) == air) setBlock(i, j-1, k, biome.rockMaterial);
							if(getBlock(i-1, j, k) == air) setBlock(i-1, j, k, biome.rockMaterial);
							if(getBlock(i+1, j, k) == air) setBlock(i+1, j, k, biome.rockMaterial);
							if(getBlock(i, j, k-1) == air) setBlock(i, j, k-1, biome.rockMaterial);
							if(getBlock(i, j, k+1) == air) setBlock(i, j, k+1, biome.rockMaterial);
							if(getBlock(i, j-1, k) == caveSpace) setBlock(i, j-1, k, biome.rockMaterial);
							if(getBlock(i-1, j, k) == caveSpace) setBlock(i-1, j, k, biome.rockMaterial);
							if(getBlock(i+1, j, k) == caveSpace) setBlock(i+1, j, k, biome.rockMaterial);
							if(getBlock(i, j, k-1) == caveSpace) setBlock(i, j, k-1, biome.rockMaterial);
							if(getBlock(i, j, k+1) == caveSpace) setBlock(i, j, k+1, biome.rockMaterial);
						} else {
							setBlock(i, j, k, biome.rockMaterial);
						}
						continue;
					}
					
					//Sand or gravel
					if(getBlock(i, j, k) == sand || getBlock(i, j, k) == gravel) {
						if(j > 0) {
							if(getBlock(i, j-1, k) == air) setBlock(i, j-1, k, biome.rockMaterial);
							if(getBlock(i, j-1, k) == caveSpace) setBlock(i, j-1, k, biome.rockMaterial);
						} else {
							setBlock(i, j, k, biome.rockMaterial);
						}
						continue;
					}
					
					//Add grass to any dirt that has air above it
					if(j < size.y-1 && getBlock(i, j, k) == biome.soilMaterial && (getBlock(i, j+1, k) == air || getBlock(i, j+1, k) == caveSpace)) {
						setBlock(i, j, k, biome.surfaceMaterial);
						continue;
					}
				}
			}
		}
		
		for(int j = 0; j < size.y-1; j++) {
			for(int i = 0; i < size.x; i++) {
				int k = 0;
				if(getBlock(i, j, k) == water || getBlock(i, j, k) == lava) setBlock(i, j, k, biome.soilMaterial);
				if(j < size.y-1 && getBlock(i, j, k) == biome.soilMaterial && (getBlock(i, j+1, k) == air || getBlock(i, j+1, k) == caveSpace)) {
					setBlock(i, j, k, biome.surfaceMaterial);
				}
				
				k = size.z-1;
				if(getBlock(i, j, k) == water || getBlock(i, j, k) == lava) setBlock(i, j, k, biome.soilMaterial);
				if(j < size.y-1 && getBlock(i, j, k) == biome.soilMaterial && (getBlock(i, j+1, k) == air || getBlock(i, j+1, k) == caveSpace)) {
					setBlock(i, j, k, biome.surfaceMaterial);
				}
			}
			
			for(int k = 1; k < size.z-1; k++) {
				int i = 0;
				if(getBlock(i, j, k) == water || getBlock(i, j, k) == lava) setBlock(i, j, k, biome.soilMaterial);
				if(j < size.y-1 && getBlock(i, j, k) == biome.soilMaterial && (getBlock(i, j+1, k) == air || getBlock(i, j+1, k) == caveSpace)) {
					setBlock(i, j, k, biome.surfaceMaterial);
				}
				
				i = size.x-1;
				if(getBlock(i, j, k) == water || getBlock(i, j, k) == lava) setBlock(i, j, k, biome.soilMaterial);
				if(j < size.y-1 && getBlock(i, j, k) == biome.soilMaterial && (getBlock(i, j+1, k) == air || getBlock(i, j+1, k) == caveSpace)) {
					setBlock(i, j, k, biome.surfaceMaterial);
				}
			}
		}
		
	}
	
	/**
	 * Adds the cave system. Caves were a very complicated task, so the code for them is in a different class.
	 * Because it was hard to get caves to look good, at all, their shape is not affected by biome.
	 */
	private void addCaves() {
		if(biome.rollCave(random)) {
			CaveSystem system = new CaveSystem();
			system.place(this, random, this.center.x, this.center.y, this.center.z);			
		}
	}
	
	/**
	 * Generates some colorful plant-life including flowers, shrubs, cactuses, sugar cane, and trees.
	 * All statistics are controlled by the BiomeDescription.
	 */
	private void populatePlants() {
		for(int i = 1; i < size.x-1; i++) {
			for(int k = 1; k < size.z-1; k++) {
				int j = heightMap[i][k]+cliffMap[i][k];
				if(j < size.y-1 && getBlock(i, j+1, k) == air || getBlock(i, j+1, k) == snow || getBlock(i, j+1, k) == caveSpace) {
					
					for(PostTree tree : biome.treeType) {
						if(tree.getPlacement().canPlace(this, random, i, j+1, k)) {
							tree.place(this, random, i, j+1, k);
						}
					}
					
					for(PostShrub shrub : biome.shrubType) {
						if(shrub.getPlacement().canPlace(this, random, i, j+1, k)) {
							shrub.place(this, random, i, j+1, k);
						}
					}
					
					//If the block is grass... (If any of the following succeed, the ones after them are ignored)
					if(getBlock(i, j, k) == grass) {
						
						//Roll for a reed
						if(getBlock(i+1, j, k) == water || getBlock(i-1, j, k) == water || getBlock(i, j, k+1) == water || getBlock(i, j, k-1) == water) {
							if(biome.rollReed(random)) {
								placeReed(i, j+1, k, random.nextInt(3)+1);
								continue;
							}
						}
						
						//Roll for a flower cluster
						if(biome.rollFlower(random)) {
							if(biome.rollFlowerType(random)) {
								placePlantCluster(flowerRed, i, k, biome.rollFlowerClusterSize(random), 5);
								continue;
							} else {
								placePlantCluster(flowerYellow, i, k, biome.rollFlowerClusterSize(random), 5);		
								continue;						
							}
						}
						
						//Roll for a mushroom cluster (These should really be underground)
						if(biome.rollMushroom(random)) {
							if(biome.rollMushroomType(random)) {
								placePlantCluster(mushroomRed, i, k, biome.rollMushroomClusterSize(random), 5);
								continue;
							} else {
								placePlantCluster(mushroomBrown, i, k, biome.rollMushroomClusterSize(random), 5);			
								continue;					
							}
						}
						
						//Roll for a pumpkin cluster
						if(biome.rollPumpkin(random)) {
							placePlantCluster(pumpkin, i, k, biome.rollPumpkinClusterSize(random), 5);
							continue;
						}
						
						//Roll for a watermelon cluster
						if(biome.rollWatermelon(random)) {
							placePlantCluster(watermelon, i, k, biome.rollWatermelonClusterSize(random), 5);
							continue;
						}
						
					}
					
					//If the block is sand... (again successes skip the laters)
					if(getBlock(i, j, k) == sand) {
						
						//Roll for a reed
						if(getBlock(i+1, j, k) == water || getBlock(i-1, j, k) == water || getBlock(i, j, k+1) == water || getBlock(i, j, k-1) == water) {
							if(biome.rollReed(random)) {
								placeReed(i, j+1, k, random.nextInt(3)+1);
								continue;
							}
						}
						
						//Roll for a cactus
						if(getBlock(i+1, j+1, k) == air || getBlock(i-1, j+1, k) == air || getBlock(i, j+1, k+1) == air || getBlock(i, j+1, k-1) == air) {
							if(biome.rollCactus(random)) {
								placeCactus(i, j+1, k, random.nextInt(3)+1);
								continue;
							}
						}

					}
					
					if(getBlock(i, j, k) == stone || getBlock(i, j, k) == netherrack) {
						//Roll for a mushroom cluster
						if(biome.rollMushroom(random)) {
							if(biome.rollMushroomType(random)) {
								placePlantCluster(mushroomRed, i, k, biome.rollMushroomClusterSize(random), 5);
								continue;
							} else {
								placePlantCluster(mushroomBrown, i, k, biome.rollMushroomClusterSize(random), 5);			
								continue;					
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Creates random water and lava falls like the ones you will find in caves in the normal generator
	 * and also adds ore to the ground. Odds for ores and liquid falls are in the BiomeDescription class.
	 */
	private void addExtras() {
		int passNumber = 1;
		for(ArrayList<OreVein> pass : biome.orePasses) {
			for(int i = 1; i < size.x-1; i++) {
				for(int k = 1; k < size.z-1; k++) {
					int jMin = bottomMap[i][k];
					int jMax = heightMap[i][k]+cliffMap[i][k];
					for(int j = jMin; j <= jMax; j++) {
						
						short block = getBlock(i, j, k);
						
						//Check for adding water and lava falls to appropriate cave locations
						if(passNumber == 1 && block == biome.rockMaterial || block == biome.soilMaterial) {
							//count how many adjacent spaces are or will be air 
							// - caveSpace is currently end stone and is replaced with air by a block populator, 
							//   this makes it so that caves can not be overwritten by other islands. While a little
							//   slower than just using air blocks, it makes cool situations.
							int n = 0;
							if(getBlockSafe(i, j-1, k) != caveSpace && getBlockSafe(i, j+1, k) != caveSpace) { 
								if(getBlock(i+1, j, k) == caveSpace) n++;
								if(getBlock(i-1, j, k) == caveSpace) n++;
								if(n < 2 && getBlock(i, j, k+1) == caveSpace) n++;
								if(n < 2 && getBlock(i, j, k-1) == caveSpace) n++;
								
								//If there is only one adjacent air block and the top and bottom are covered, roll for a liquid fall.
								if(n != 1) continue;
								if(biome.rollWaterFall(random)) {
									setBlock(i, j, k, water);
									continue;
								}
								if(biome.rollLavaFall(random)) {
									setBlock(i, j, k, lava);
									continue;
								}
							}
						}
												
						for(OreVein vein : pass) {
							if(vein.getPlacement().canPlace(this, random, i, j, k)) {
								vein.place(this, random, i, j, k);
							}
						}
						
					}
				}
			}
			passNumber++;
		}
	}
	
	/**
	 * Places a sugar cane plant on the block given if it has room to grow. Does not verify that the block given is grass or sand.
	 * @param x - position of the block that the sugar cane should be planted on
	 * @param y - position of the block that the sugar cane should be planted on
	 * @param z - position of the block that the sugar cane should be planted on
	 * @param size.y - size.y of the sugar cane
	 */
	private void placeReed(int x, int y, int z, int height) {
		for(int j = 0; j < height; j++) {
			setBlockSafe(x, y+j, z, reed);
		}
	}
	
	/**
	 * Places a cactus on the block given if it has room to grow. Does not verify that the block given is sand.
	 * @param x - position of the sand block that the cactus should be planted on
	 * @param y - position of the sand block that the cactus should be planted on
	 * @param z - position of the sand block that the cactus should be planted on
	 * @param size.y - size.y of the cactus
	 */
	private void placeCactus(int x, int y, int z, int height) {
		for(int j = 0; j < height; j++) {
			if(getBlockSafe(x-1, y, z) == air && getBlockSafe(x+1, y, z) == air && getBlockSafe(x, y, z-1) == air && getBlockSafe(x, y, z+1) == air) {
				setBlockSafe(x, y+j, z, cactus);
			} else {
				break;
			}
		}
	}
	
	/**
	 * Rolls a random cluster blocks of the given material centered roughly on the (x, z) location
	 * provided. There will be a number of plants equal to numPlants.
	 * @param type - material of the plant (one of the strange blocks used as place holders for the block populator)
	 * @param x - position on the surface to place the plant cluster
	 * @param z - position on the surface to place the plant cluster
	 * @param numPlants - number of plants in the cluster
	 * @param radius - max radius of the cluster (technically the cluster is a square)
	 */
	private void placePlantCluster(short type, int x, int z, int numPlants, int radius) {
		placePlant(type, x, z);
		for(int i = 1; i < numPlants; i++) {
			placePlant(type, x+random.nextInt(radius*2)-radius, z+random.nextInt(radius*2)-radius);
		}
	}

	/**
	 * Places a single plant on top of the given block if it is a grass block.
	 * The position is indexed as an (x, z) location and will automatically be put on the
	 * top layer of the island. If either block is out of bounds, nothing happens.
	 * @param type - material of the plant (one of the strange blocks used as place holders for the block populator)
	 * @param x - position on the surface to place the plant
	 * @param z - position on the surface to place the plant
	 */
	private void placePlant(short type, int x, int z) {
		if(x < 0 || x >= size.x || z < 0 || z >= size.z) return;
		int y = heightMap[x][z]+cliffMap[x][z];
		if(y < 0 || y >= size.y-1) return;
		if(getBlock(x, y, z) == grass) setBlock(x, y+1, z, type);
	}

	public BiomeDescription getBiome() {
		return this.biome;
	}

	public int getTotalVolume() {
		return this.totalVolume;
	}

	public double getCenterX() {
		return this.center.x;
	}
	
	public double getCenterY() {
		return this.center.y;
	}
	
	public double getCenterZ() {
		return this.center.z;
	}
	
	public Int3D getCenter() {
		return this.center;
	}

	public double getPercentDepth(int x, int y, int z) {
		int jMin = bottomMap[x][z];
		int jMax = heightMap[x][z]+cliffMap[x][z];
		double jRange = jMax-jMin;
		return 1.0 - ((double)(y-jMin) / jRange);
	}
	
}
