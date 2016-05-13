package features;

import island.Island;

import org.bukkit.Location;

import aero.Schematic;

public class PostInstance {

	protected Location location;
	protected PostFeature feature;
	
	public PostInstance(PostFeature feature, Location location) {
		this.feature = feature;
		this.location = location;
	}
	
	public PostInstance(PostFeature feature, Schematic schematic, int x, int y, int z) {
		this.feature = feature;
		this.location = new Location(schematic.getWorld(), schematic.getX()+x, schematic.getY()+y, schematic.getZ()+z);
	}
	
	public PostInstance(PostFeature feature) {
		this.feature = feature;
	}
	
	public void setLocation(Island island, int x, int y, int z) {
		this.location = new Location(island.getWorld(), island.getX()+x, island.getY()+y, island.getZ()+z);
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}
	
	public void placeIntoWorld() {
		this.feature.placeIntoWorld(location);
	}
	
}
