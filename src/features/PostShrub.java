package features;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import aero.Schematic;

public class PostShrub extends PostFeature {

	FeaturePlacement placement;
	byte type;
	
	public PostShrub(byte type, double chance) {
		this.type = type;
		if(type == (byte) 0) {
			this.placement = new FeaturePlacement(-0.1, 0.0, Schematic.sand, chance);
		} else {
			this.placement = new FeaturePlacement(-0.1, 0.0, Schematic.grass, chance);
		}
	}

	public FeaturePlacement getPlacement() {
		return placement;
	}

	public void placeIntoWorld(Location location) {
		Block block = location.getBlock();
		Integer id = Integer.valueOf(block.getTypeId());
		if(!placement.replaces.contains(id)) return;
		block.setType(Material.LONG_GRASS);
		block.setData(type);
	}
	
}
