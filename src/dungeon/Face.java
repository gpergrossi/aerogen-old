package dungeon;

public class Face {

	boolean exterior;
	Room[] rooms;
	Facing facing;
	int x1, y1, z1;
	int x2, y2, z2;
	
	public Face(Face face, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.exterior = face.exterior;
		this.rooms = face.rooms;
		this.facing = face.facing;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	public void attachRoom(Room room) {
		if(this.rooms[1] != null) {
			System.out.println("Cannot attach more than 2 rooms to a wall.");
		}
		this.rooms[1] = room;
		if(this.facing == Facing.west || this.facing == Facing.east) {
			this.facing = Facing.doubleZY;
		}
		if(this.facing == Facing.north || this.facing == Facing.south) {
			this.facing = Facing.doubleXY;
		}
		if(this.facing == Facing.top || this.facing == Facing.bottom) {
			this.facing = Facing.doubleXZ;
		}
	}
	
	public Face(Room room, Facing facing) {
		this.rooms = new Room[] {room, null};
		this.facing = facing;
		int floorThickness = room.structure.floorThickness;
		int wallThickness = room.structure.wallThickness;
		switch(facing) {
			case top:
				this.x1 = room.x1;
				this.y1 = room.y2+1;
				this.z1 = room.z1;
				this.x2 = room.x2;
				this.y2 = room.y2+floorThickness;
				this.z2 = room.z2;
				this.exterior = (room.floor < room.structure.stories);
			break;
			case west:
				this.x1 = room.x1-wallThickness;
				this.y1 = room.y1;
				this.z1 = room.z1;
				this.x2 = room.x1-1;
				this.y2 = room.y2;
				this.z2 = room.z2;
				this.exterior = true;
			break;
			case east:
				this.x1 = room.x2+1;
				this.y1 = room.y1;
				this.z1 = room.z1;
				this.x2 = room.x2+wallThickness;
				this.y2 = room.y2;
				this.z2 = room.z2;
				this.exterior = true;
			break;
			case north:
				this.x1 = room.x1;
				this.y1 = room.y1;
				this.z1 = room.z1-wallThickness;
				this.x2 = room.x2;
				this.y2 = room.y2;
				this.z2 = room.z1-1;
				this.exterior = true;
			break;
			case south:
				this.x1 = room.x1;
				this.y1 = room.y1;
				this.z1 = room.z2+1;
				this.x2 = room.x2;
				this.y2 = room.y2;
				this.z2 = room.z2+wallThickness;
				this.exterior = true;
			break;
			case bottom:
				this.x1 = room.x1;
				this.y1 = room.y1-floorThickness;
				this.z1 = room.z1;
				this.x2 = room.x2;
				this.y2 = room.y1-1;
				this.z2 = room.z2;
				this.exterior = (room.floor > 1);
			break;
		}
	}
	
	public int getSigX1() {
		switch(facing) {
			case top:	 return x1;
			case west:	 return z1;
			case east:	 return z1;
			case north:	 return x1;
			case south:	 return x1;
			case bottom: return x1;
		}
		return 0;
	}
	
	public int getSigY1() {
		switch(facing) {
			case top:	 return z1;
			case west:	 return y1;
			case east:	 return y1;
			case north:	 return y1;
			case south:	 return y1;
			case bottom: return z1;
		}
		return 0;
	}
	
	public int getSigX2() {
		switch(facing) {
			case top:	 return x2;
			case west:	 return z2;
			case east:	 return z2;
			case north:	 return x2;
			case south:	 return x2;
			case bottom: return x2;
		}
		return 0;
	}
	
	public int getSigY2() {
		switch(facing) {
			case top:	 return z2;
			case west:	 return y2;
			case east:	 return y2;
			case north:	 return y2;
			case south:	 return y2;
			case bottom: return z2;
		}
		return 0;
	}
	
	public int getSigP1() {
		switch(facing) {
			case top:	 return y1;
			case west:	 return x1;
			case east:	 return x1;
			case north:	 return z1;
			case south:	 return z1;
			case bottom: return y1;
		}
		return 0;
	}
	
	public int getSigP2() {
		switch(facing) {
			case top:	 return y2;
			case west:	 return x2;
			case east:	 return x2;
			case north:	 return z2;
			case south:	 return z2;
			case bottom: return y2;
		}
		return 0;
	}
	
	public Face createOnPlane(int sx1, int sy1, int sx2, int sy2) {
		switch(facing) {
			case top:	 return new Face(this, this.x1, sy1, sx1, sx2, this.y2, sy2);
			case west:	 return new Face(this, this.x1, sy1, sx1, this.x2, sy2, sx2);
			case east:	 return new Face(this, this.x1, sy1, sx1, this.x2, sy2, sx2);
			case north:	 return new Face(this, sx1, sy1, this.z1, sx2, sy2, this.z2);
			case south:	 return new Face(this, sx1, sy1, this.z1, sx2, sy2, this.z2);
			case bottom: return new Face(this, sx1, this.y1, sy1, sx2, this.y2, sy2);
		}
		return null;
	}

	public int getCenterX() {
		return (x1+x2)/2;
	}

	public int getCenterY() {
		return (x1+x2)/2;
	}
	
	public int getCenterZ() {
		return (x1+x2)/2;
	}
	
	
	
}
