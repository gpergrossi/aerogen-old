package dungeon;

import java.util.ArrayList;

public class Room {
	
	Structure structure;
	int floor = 0;
	
	boolean hall;
	
	ArrayList<Face> topFaces;
	ArrayList<Face> westFaces;
	ArrayList<Face> eastFaces;
	ArrayList<Face> northFaces;
	ArrayList<Face> southFaces;
	ArrayList<Face> bottomFaces;

	int x1, y1, z1;
	int x2, y2, z2;
	
	public Room(Structure structure, int floor, int x1, int y1, int z1, int x2, int y2, int z2, ArrayList<Room> rooms) {
		this(structure, floor, x1, y1, z1, x2, y2, z2);
		for(Room room : rooms) {
			if(room.floor == floor+1) for(Face face : room.bottomFaces) mergeUp(face);
			if(room.floor == floor-1) for(Face face : room.topFaces) mergeDown(face);
		}		
	}
	
	private void mergeUp(Face that) {
		
	}
	
	private void mergeDown(Face that) {
		
	}

	public Room(Structure structure, int floor, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.structure = structure;
		this.floor = floor;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		topFaces = new ArrayList<Face>();
		westFaces = new ArrayList<Face>();
		eastFaces = new ArrayList<Face>();
		northFaces = new ArrayList<Face>();
		southFaces = new ArrayList<Face>();
		bottomFaces = new ArrayList<Face>();
		topFaces.add(new Face(this, Facing.top));
		westFaces.add(new Face(this, Facing.west));
		eastFaces.add(new Face(this, Facing.east));
		northFaces.add(new Face(this, Facing.north));
		southFaces.add(new Face(this, Facing.south));
		bottomFaces.add(new Face(this, Facing.bottom));
	}

	public int getArea() {
		return (x2-x1+1)*(y2-y1+1);
	}

	public ArrayList<Face> getAllFaces() {
		ArrayList<Face> faces = new ArrayList<Face>();
		for(Face face : topFaces) faces.add(face);
		for(Face face : westFaces) faces.add(face);
		for(Face face : eastFaces) faces.add(face);
		for(Face face : northFaces) faces.add(face);
		for(Face face : southFaces) faces.add(face);
		for(Face face : bottomFaces) faces.add(face);
		return faces;
	}
	
	public boolean intersectsXY(int x, int y) {
		if(x1 <= x && x2 >= x && y1 <= y && y2 >= y) return true;
		return false;
	}
	
	public boolean intersectsXrangeY(int minX, int maxX, int y) {
		if(x1 <= maxX && x2 >= minX && y1 <= y && y2 >= y) return true;
		return false;
	}
	
	public boolean intersectsXYrange(int x, int minY, int maxY) {
		if(x1 <= x && x2 >= x && y1 <= maxY && y2 >= minY) return true;
		return false;
	}
	
	public boolean intersectsXZ(int x, int z) {
		if(x1 <= x && x2 >= x && z1 <= z && z2 >= z) return true;
		return false;
	}
	
	public boolean intersectsXrangeZ(int minX, int maxX, int z) {
		if(x1 <= maxX && x2 >= minX && z1 <= z && z2 >= z) return true;
		return false;
	}
	
	public boolean intersectsXZrange(int x, int minZ, int maxZ) {
		if(x1 <= x && x2 >= x && z1 <= maxZ && z2 >= minZ) return true;
		return false;
	}
	
	public boolean intersectsYZ(int y, int z) {
		if(y1 <= y && y2 >= y && z1 <= z && z2 >= z) return true;
		return false;
	}
	
	public boolean intersectsYZrange(int y, int minZ, int maxZ) {
		if(y1 <= y && y2 >= y && z1 <= maxZ && z2 >= minZ) return true;
		return false;
	}
	

}
