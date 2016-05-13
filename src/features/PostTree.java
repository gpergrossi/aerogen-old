package features;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.Block;

import aero.Schematic;

public class PostTree extends PostFeature {

	FeaturePlacement placement;
	TreeType type;
	
	public PostTree(TreeType type, double chance) {
		this.type = type;
		this.placement = new FeaturePlacement(-0.1, 0.0, Schematic.grass, chance);
	}
	
	public FeaturePlacement getPlacement() {
		return placement;
	}

	public void placeIntoWorld(Location location) {
		Block block = location.getBlock();
		Integer id = Integer.valueOf(block.getTypeId());
		if(!placement.replaces.contains(id)) return;
		location.getWorld().generateTree(location, type);
	}

	public TreeType getType() {
		return type;
	}
	
}
