package island;

import java.awt.Point;
import java.util.ArrayList;

public class Topograph {
	
	private int width, height;
	private boolean[][] topograph;
	private int[][] distanceFromEdge;
	private double[][] heightCoefficients;
	
	public static void main(String[] args) {
		Topograph topograph = new Topograph(20, 20);
		
		StringBuilder str = new StringBuilder();
		for(int y = 0; y < topograph.height; y++) {
			for(int x = 0; x < topograph.width; x++) {
				switch(topograph.distanceFromEdge[x][y]) {
					case 0: str.append(' '); break;
					case 1: str.append('o'); break;
					case 2: str.append('I'); break;
					case 3: str.append('L'); break;
					case 4: str.append('U'); break;
					case 5: str.append('O'); break;
					case 6: str.append('D'); break;
					case 7: str.append('Z'); break;
					case 8: str.append('X'); break;
					case 9: str.append('W'); break;
					default: str.append(' ');
				}
			}
			str.append("\n");
		}
		System.out.println(str.toString());
	}
	
	public Topograph(int width, int height) {
		this.width = width;
		this.height = height;
		this.genTopograph();
		this.genEdgeDistances();
	}

	private void genTopograph() {

		// Array to describe the shape of the island when looking down on it
		boolean[][] map = new boolean[width][height];
		boolean[][] edge = new boolean[width][height];
		int midX = width / 2;
		int midY = height / 2;
		
		//Keep track of boundaries
		int minX = width-1;
		int maxX = 0;
		int minY = height-1;
		int maxY = 0;
		
		// Initialize a few points around the center of the island
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(midX, midY));
		map[midX][midY] = true;
		edge[midX][midY] = true;

		// The number of times to iterate
		int num = (int) (width * height * 0.785);

		//Odds of the island growing in the specified direction
		double xOdds = (double) width / (double) (width + height);
		double zOdds = 1.0 - xOdds;

		for(int i = 0; i < num && points.size() > 0; i++) {

			//Choose a random point
			Point p = points.get((int)(Math.random()*points.size()));

			int x = p.x;
			int y = p.y;

			//If the roll is successful, add the neighbor in the x-1 direction to the list of points
			if(x > 0 && !map[x-1][y] && !edge[x-1][y] && Math.random() < xOdds) {
				if(x > 1) points.add(new Point(x-1, y));
				map[x-1][y] = true;
				edge[x-1][y] = true;
				if(x-1 < minX) minX = x-1;
			}

			//If the roll is successful, add the neighbor in the x+1 direction to the list of points
			if(x < width-1 && !map[x+1][y] && !edge[x+1][y] && Math.random() < xOdds) {
				if(x < width-2) points.add(new Point(x+1, y));
				map[x+1][y] = true;
				edge[x+1][y] = true;
				if(x+1 > maxX) maxX = x+1;
			}

			//If the roll is successful, add the neighbor in the z-1 direction to the list of points
			if(y > 0 && !map[x][y-1] && !edge[x][y-1] && Math.random() < zOdds) {
				if(y > 1) points.add(new Point(x, y-1));
				map[x][y-1] = true;
				if(y-1 < minY) minY = y-1;
				edge[x][y-1] = true;
			}

			//If the roll is successful, add the neighbor in the z+1 direction to the list of points
			if(y < height-1 && !map[x][y+1] && !edge[x][y+1] && Math.random() < zOdds) {
				if(y < height - 2) points.add(new Point(x, y+1));
				map[x][y+1] = true;
				if(y+1 > maxY) maxY = y+1;
				edge[x][y+1] = true;
			}

			//If the point chosen originally has no more empty neighbors, throw it out
			if(map[x-1][y] && map[x+1][y] && map[x][y-1] && map[x][y+1]) {
				points.remove(p);
				edge[x][y+1] = false;
			}

		}

		points.clear();

		// Add the position of adjacent air blocks
		for(int x = 1; x < width - 1; x++) {
			for(int y = 1; y < height - 1; y++) {
				if(map[x][y]) continue;
				int n = 0;
				if(map[x - 1][y]) n++;
				if(map[x + 1][y]) n++;
				if(map[x][y - 1]) n++;
				if(map[x][y + 1]) n++;
				if(n > 1) points.add(new Point(x, y));
			}
		}

		// Fill the adjacent air blocks so as to fill any small holes
		for(Point p : points) {
			int x = (int) p.getX();
			int y = (int) p.getY();
			if(x >= 0 && x < width && y >= 0 && y < height) {
				map[x][y] = true;
				if(x > maxX) maxX = x;
				if(x < minX) minX = x;
				if(y > maxY) maxY = y;
				if(y < minY) minY = y;
			}
		}
		
//		System.out.println("[0, 0, "+(width-1)+", "+(height-1)+"] -> ["+minX+", "+minY+", "+maxX+", "+maxY+"]");
		this.width = maxX - minX + 1;
		this.height = maxY - minY + 1;
		this.topograph = new boolean[width][height];
		for(int x = 0; x < this.width; x++) {
			System.arraycopy(map[x+minX], minY, this.topograph[x], 0, this.height);
		}

	}
	
	private void genEdgeDistances() {
		
		this.distanceFromEdge = new int[width][height];
		this.heightCoefficients = new double[width][height];
		int unknownDist = 10000;
		int max = 0;
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(topograph[x][y]) {
					if(x == 0 || x == width-1 || y == 0 || y == height-1) {
						this.distanceFromEdge[x][y] = 1;
						continue;
					}
					this.distanceFromEdge[x][y] = unknownDist;
				} else {
					this.distanceFromEdge[x][y] = 0;
				}
			}
		}
		
		for(int i = 0; i < (width+height)/2; i++) {
			for(int x = 1; x < width-1; x++) {
				for(int y = 1; y < height-1; y++) {
					if(this.distanceFromEdge[x+1][y]+1 < this.distanceFromEdge[x][y]) {
						this.distanceFromEdge[x][y] = this.distanceFromEdge[x+1][y]+1;
					}
					if(this.distanceFromEdge[x-1][y]+1 < this.distanceFromEdge[x][y]) {
						this.distanceFromEdge[x][y] = this.distanceFromEdge[x-1][y]+1;
					}
					if(this.distanceFromEdge[x][y+1]+1 < this.distanceFromEdge[x][y]) {
						this.distanceFromEdge[x][y] = this.distanceFromEdge[x][y+1]+1;
					}
					if(this.distanceFromEdge[x][y-1]+1 < this.distanceFromEdge[x][y]) {
						this.distanceFromEdge[x][y] = this.distanceFromEdge[x][y-1]+1;
					}
					if(distanceFromEdge[x][y] == 10000) continue;
					if(this.distanceFromEdge[x][y] > max) max = this.distanceFromEdge[x][y];
				}
			}
		}
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				heightCoefficients[x][y] = 1.0 - Math.pow(1.0 - ((double) distanceFromEdge[x][y] / max), 2.5); //Power adjustment makes it look nicer
			}
		}
		
	}
	
	public boolean[][] getTopograph() {
		return this.topograph;
	}
	
	public int[][] getDistancesFromEdge() {
		return this.distanceFromEdge;
	}

	public double[][] getHeightCoefficients() {
		return this.heightCoefficients;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
}
