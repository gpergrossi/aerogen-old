package features;

import island.Island;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Pond implements Feature {
	
	private short material = 0;
	private int size = 0;
	private FeaturePlacement placement;
	
	public Pond(int size, short material, FeaturePlacement place) {
		this.material = material;
		this.size = size;
		this.placement = place;
	}
	
	public void place(Island island, Random random, int x, int y, int z) {
		
		ArrayList<Point> deep = new ArrayList<Point>();
		ArrayList<Point> shallow = new ArrayList<Point>();
		
		deep.add(new Point(0, 0));
		shallow.add(new Point(1, 0));
		shallow.add(new Point(-1, 0));
		shallow.add(new Point(0, 1));
		shallow.add(new Point(0, -1));
		int size = 5;
		
		while(size < this.size) {
			int index = random.nextInt(shallow.size());
			Point point = shallow.get(index);
			
			while(shallow.contains(point)) {
				shallow.remove(point);
			}
			deep.add(point);
			
			Point neighbor = new Point(point.x+1, point.y);
			if(!deep.contains(neighbor)) {
				if(!shallow.contains(neighbor)) size++;
				shallow.add(neighbor);
			}
			neighbor = new Point(point.x-1, point.y);
			if(!deep.contains(neighbor)) {
				if(!shallow.contains(neighbor)) size++;
				shallow.add(neighbor);
			}
			neighbor = new Point(point.x, point.y+1);
			if(!deep.contains(neighbor)) {
				if(!shallow.contains(neighbor)) size++;
				shallow.add(neighbor);
			}
			neighbor = new Point(point.x, point.y-1);
			if(!deep.contains(neighbor)) {
				if(!shallow.contains(neighbor)) size++;
				shallow.add(neighbor);
			}
		}
		
		for(Point p : deep) {
			island.setBlockSafe(x+p.x, y, z+p.y, material);
			island.setBlockSafe(x+p.x, y-1, z+p.y, material);
			island.setBlockSafe(x+p.x, y+1, z+p.y, (short) 0);
		}
		for(Point p : shallow) {
			island.setBlockSafe(x+p.x, y, z+p.y, material);
			island.setBlockSafe(x+p.x, y+1, z+p.y, (short) 0);
		}
		
	}

	public FeaturePlacement getPlacement() {
		return placement;
	}

}
