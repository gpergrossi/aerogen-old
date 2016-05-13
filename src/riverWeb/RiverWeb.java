package riverWeb;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;

import aero.AeroGenerator;
import aero.Int3D;
import aero.Schematic;
import noise.Noise2D;
import noise.NoiseMap;
import noise.SimplexNoise2D;

public class RiverWeb extends Schematic {
	
	private static HashMap<World, SimplexNoise2D> worldNoise = new HashMap<World, SimplexNoise2D>();

	public static RiverWeb createRiverWeb(AeroGenerator gen, World world, Random random, int chunkX, int chunkZ) {
		return new RiverWeb("RiverWeb("+chunkX+","+chunkZ+")", new Location(world, chunkX*16+8.5, 56, chunkZ*16+8.5), new Int3D(16,12,16));
	}
	
	public RiverWeb(String name, Location location, Int3D size) {
		super(name, location, size);
		
		Noise2D noise = getNoise(location.getWorld());
		
		for(int x = 0; x < 16; x++) {
			for(int z = 0; z < 16; z++) {
				int land = (int) (noise.getValue(x+location.getBlockX(), z+location.getBlockZ())*8);
				for(int y = 0; y < land; y++) {
					setBlock(x, 5+y, z, air);
					if(y < (land-2)) {
						setBlock(x, 5-y, z, water);
					} else {
						if(y <= 2) {
							if(y == 0) {
								setBlock(x, 5-y, z, grass);
							} else {
								setBlock(x, 5-y, z, dirt);
							}
						} else {
							setBlock(x, 5-y, z, stone);
						}
					}
				}
			}
		}
		
	}
	
	private static SimplexNoise2D getNoise(World world) {
		if(!worldNoise.containsKey(world)) {
			SimplexNoise2D noise = new SimplexNoise2D(world.getSeed(), 1.0/128.0);
			noise.setOutputMap(new NoiseMap() {
				public double map(double value) {
					value = NoiseMap.SharpCurve.map(value);
					if(value > 0.6) return (value-0.6)*2.5;
					return 0.0;
				}
			});
			worldNoise.put(world, noise);
		}
		return worldNoise.get(world);
	}
	
}
