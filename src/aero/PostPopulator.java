package aero;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import features.PostInstance;

public class PostPopulator extends BlockPopulator {

	AeroGen plugin;
	
	public PostPopulator(AeroGen plugin) {
		this.plugin = plugin;
	}
	
	public void populate(World world, Random random, Chunk chunk) {
		WorldChunkCache cache = WorldChunkCache.forWorld(world);
		WorldChunkData data = cache.getChunkData(chunk.getX(), chunk.getZ());
		ArrayList<PostInstance> features = data.getFeatures();
		for(PostInstance instance : features) {
			instance.placeIntoWorld();
		}
	}

}
