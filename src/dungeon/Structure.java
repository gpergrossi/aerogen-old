package dungeon;

import java.util.ArrayList;
import java.util.Random;

public class Structure {
	
	//Room
	int floorThickness = 1;	//Thickness of the top and bottom faces of a room
	int wallThickness = 1;	//Thickness of the walls of a room
	int hallWidthMin = 1;	//Minimum width of a hallway
	int hallWidthMax = 3;	//Minimum height of a hallway
	int minRoomSizeX = 5;	//Minimum internal room space in the X direction
	int maxRoomSizeX = 12;  //Maximum internal room space in the X direction
	int minRoomSizeZ = 5;	//Minimum internal room space in the Z direction
	int maxRoomSizeZ = 12;	//Maximum internal room space in the Z direction
	int doorWidthMin = 1;	//The amount of wall space that a wall must have to support an attached room
	
	//Structure
	Random random;					//Generator for this structure (same seed will produce the same building)
	int stories = 1;				//Number of stories
	int storyHeight = 4;			//Height of a story
	int structureTargetArea = 60;	//The generator will stop when this target is exceeded
	int structureSizeX = 60;		//Rooms will attempt to fit in this space or fail
	int structureSizeZ = 60;		//Rooms will attempt to fit in this space or fail
	ArrayList<Room> rooms;			//List of the current rooms
	ArrayList<Face> faces;			//List of the current valid faces
	
	//Generation
	int tries = 0;		//Number of rooms that have failed in a row, the generator will stop after 10 failed attempts
	int totalArea = 0;	//Total area of the rooms added together
	int currentMinX = Integer.MAX_VALUE;
	int currentMaxX = Integer.MIN_VALUE;
	int currentMinZ = Integer.MAX_VALUE;
	int currentMaxZ = Integer.MIN_VALUE;
	
	public Structure(long seed) {
		random = new Random(seed);
		rooms = new ArrayList<Room>();
		faces = new ArrayList<Face>();
		totalArea = 0;
		int width = random.nextInt(maxRoomSizeX-minRoomSizeX+1)+minRoomSizeX+2;
		int height = random.nextInt(maxRoomSizeZ-minRoomSizeZ+1)+minRoomSizeZ+2;
		int minX = 0-width/2;
		int maxX = minX+width-1;
		int minZ = 0-height/2;
		int maxZ = minZ+height-1;
		Room room = new Room(this, 1, minX, 0, minZ, maxX, storyHeight-1, maxZ);
		addRoom(room);
		generate();
	}

	private void addRoom(Room room) {
		rooms.add(room);
		totalArea += room.getArea();
		ArrayList<Face> roomFaces = room.getAllFaces();
		for(Face face : roomFaces) {
			if(!faces.contains(face) && face.exterior) faces.add(face);
		}
		if(room.x1 < currentMinX) currentMinX = room.x1;
		if(room.z1 < currentMinZ) currentMinZ = room.z1;
		if(room.x2 > currentMaxX) currentMaxX = room.x2;
		if(room.z2 > currentMaxZ) currentMaxZ = room.z2;
	}
	
	private void generate() {
		tries = 0;
		while(tries < 10 && totalArea < structureTargetArea && faces.size() > 0) {
			Face face = faces.get(random.nextInt(faces.size()));
			faces.remove(face);
			boolean success = addRoomToFace(face);
			if(success) {
				tries = 0;
			} else {
				tries++;
			}
		}
	}

	private boolean addRoomToFace(Face face) {
		if(!face.exterior) return false;
		switch(face.facing) {
			case top:
				if(face.rooms[0].hall) return false;
				return addRoomToTop(face);
			case west:
				return addRoomToWest(face);
			case east:
				return addRoomToEast(face);
			case north:
				return addRoomToNorth(face);
			case south:
				return addRoomToSouth(face);
			case bottom:
				if(face.rooms[0].hall) return false;
				return addRoomToBottom(face);
		}
		return false;
	}
	
	int posX, minX, maxX;
	int posY, minY, maxY;
	int posZ, minZ, maxZ;
	
	boolean roomToEast;
	boolean roomToWest;
	boolean roomToNorth;
	boolean roomToSouth;
	
	boolean roomPlaced;
	boolean roomHall;
	int roomX1, roomX2, roomZ1, roomZ2;
	
	private void clearWorkVariables() {
		posX = Integer.MAX_VALUE;
		minX = Integer.MAX_VALUE;
		maxX = Integer.MAX_VALUE;
		posY = Integer.MAX_VALUE;
		minY = Integer.MAX_VALUE;
		maxY = Integer.MAX_VALUE;
		posZ = Integer.MAX_VALUE;
		minZ = Integer.MAX_VALUE;
		maxZ = Integer.MAX_VALUE;
		roomToEast = false;
		roomToWest = false;
		roomToNorth = false;
		roomToSouth = false;

		roomPlaced = false;
		roomHall = false;
	}
	
	private void findConstraints(int floor) {
		posY = floor*(storyHeight+floorThickness);
		minY = posY;
		maxY = posY+storyHeight-1;
		if(minX < currentMaxX-structureSizeX+1) minX = currentMaxX-structureSizeX+1;
		if(maxX > currentMinX+structureSizeX-1) maxX = currentMinX+structureSizeX-1;
		if(minZ < currentMaxZ-structureSizeZ+1) minZ = currentMaxZ-structureSizeZ+1;
		if(maxZ > currentMinZ+structureSizeZ-1) maxZ = currentMinZ+structureSizeZ-1;
		if(random.nextBoolean()) {
			for(Room room : rooms) {
				if(room.floor != floor) continue;
				if(!room.intersectsYZ(posY, posZ)) continue;
				if(room.x2+wallThickness+1 < posX && room.x2+wallThickness+1 > minX) {
					roomToWest = true;
					minX = room.x2+wallThickness+1;
				}
				if(room.x1-wallThickness-1 > posX && room.x1-wallThickness-1 < maxX) {
					roomToEast = true;
					maxX = room.x1-wallThickness-1;
				}
			}
			for(Room room : rooms) {
				if(room.floor != floor) continue;
				if(!room.intersectsXrangeY(minX, maxX, posY)) continue;
				if(room.z2+wallThickness+1 < posZ && room.z2+wallThickness+1 > minZ) {
					roomToNorth = true;
					minZ = room.z2+wallThickness+1;
				}
				if(room.z1-wallThickness-1 > posZ && room.z1-wallThickness-1 < maxZ) {
					roomToSouth = true;
					maxZ = room.z1-wallThickness-1;
				}
			}
		} else {
			for(Room room : rooms) {
				if(room.floor != floor) continue;
				if(!room.intersectsXY(posX, posY)) continue;
				if(room.z2+wallThickness+1 < posZ && room.z2+wallThickness+1 > minZ) {
					roomToNorth = true;
					minZ = room.z2+wallThickness+1;
				}
				if(room.z1-wallThickness-1 > posZ && room.z1-wallThickness-1 < maxZ) {
					roomToSouth = true;
					maxZ = room.z1-wallThickness-1;
				}
			}
			for(Room room : rooms) {
				if(room.floor != floor) continue;
				if(!room.intersectsYZrange(posY, minZ, maxZ)) continue;
				if(room.x2+wallThickness+1 < posX && room.x2+wallThickness+1 > minX) {
					roomToWest = true;
					minX = room.x2+wallThickness+1;
				}
				if(room.x1-wallThickness-1 > posX && room.x1-wallThickness-1 < maxX) {
					roomToEast = true;
					maxX = room.x1-wallThickness-1;
				}
			}
		}
	}
	
	private void chooseConstraints(Face face) {
		Facing facing = face.facing;
		ArrayList<Integer> westConstraints = new ArrayList<Integer>();
		ArrayList<Integer> eastConstraints = new ArrayList<Integer>();
		ArrayList<Integer> northConstraints = new ArrayList<Integer>();
		ArrayList<Integer> southConstraints = new ArrayList<Integer>();
		westConstraints.add(minX);
		eastConstraints.add(maxX);
		northConstraints.add(minZ);
		southConstraints.add(maxZ);
		for(int i = hallWidthMin; i <= hallWidthMax; i++) {
			if(facing != Facing.east) westConstraints.add(minX+wallThickness+i);
			if(facing != Facing.west) eastConstraints.add(maxX-wallThickness-i);
			if(facing != Facing.south) northConstraints.add(minZ+wallThickness+i);
			if(facing != Facing.north) southConstraints.add(maxZ-wallThickness-i);
		}
		for(int i = Math.max(minRoomSizeX, hallWidthMax+1); i <= maxRoomSizeX; i++) {
			if(facing != Facing.east) westConstraints.add(minX+wallThickness+i);
			if(facing != Facing.west) eastConstraints.add(maxX-wallThickness-i);
			if(facing != Facing.south) northConstraints.add(minZ+wallThickness+i);
			if(facing != Facing.north) southConstraints.add(maxZ-wallThickness-i);
		}
		
		ArrayList<PossibleConstraint> xConstraints = new ArrayList<PossibleConstraint>();
		for(int i : westConstraints) {
			for(int j : eastConstraints) {
				int size = j-i+1;
				if(size >= minRoomSizeX && size <= maxRoomSizeX) {
					if(facing == Facing.top || facing == Facing.bottom || facing == Facing.north || facing == Facing.south) {
						if(j-face.x1+1 < doorWidthMin || face.x2-i+1 < doorWidthMin) continue;
					}
					xConstraints.add(new PossibleConstraint(i, j, false));
				}
				if(size >= hallWidthMin && size <= hallWidthMax) {
					if(facing == Facing.top || facing == Facing.bottom || facing == Facing.north || facing == Facing.south) {
						if(j-face.x1+1 < doorWidthMin || face.x2-i+1 < doorWidthMin) continue;
					}
					xConstraints.add(new PossibleConstraint(i, j, true));
				}
			}
		}
		if(xConstraints.size() == 0) return;
		
		ArrayList<PossibleConstraint> yConstraints = new ArrayList<PossibleConstraint>();
		for(int i : northConstraints) {
			for(int j : southConstraints) {
				int size = j-i+1;
				if(size >= minRoomSizeZ && size <= maxRoomSizeZ) {
					if(facing == Facing.top || facing == Facing.bottom || facing == Facing.north || facing == Facing.south) {
						if(j-face.z1+1 < doorWidthMin || face.z2-i+1 < doorWidthMin) continue;
					}
					yConstraints.add(new PossibleConstraint(i, j, false));
				}
				if(size >= hallWidthMin && size <= hallWidthMax) {
					if(facing == Facing.top || facing == Facing.bottom || facing == Facing.north || facing == Facing.south) {
						if(j-face.z1+1 < doorWidthMin || face.z2-i+1 < doorWidthMin) continue;
					}
					yConstraints.add(new PossibleConstraint(i, j, true));
				}
			}
		}
		if(yConstraints.size() == 0) return; 
		
		ArrayList<PossibleRoom> possibleRooms = new ArrayList<PossibleRoom>();
		for(PossibleConstraint i : xConstraints) {
			for(PossibleConstraint j : yConstraints) {
				if(!i.hall || !j.hall) {
					possibleRooms.add(new PossibleRoom(i.v1, i.v2, j.v1, j.v2, i.hall || j.hall));
				}
			}
		}
		if(possibleRooms.size() == 0) return;
		
		PossibleRoom choice = possibleRooms.get(random.nextInt(possibleRooms.size()));
		roomPlaced = true;
		roomHall = choice.hall;
		roomX1 = choice.x1;
		roomZ1 = choice.z1;
		roomX2 = choice.x2;
		roomZ2 = choice.z2;
	}
	
	private static class PossibleRoom {
		int x1, z1, x2, z2;
		boolean hall;
		public PossibleRoom(int x1, int z1, int x2, int z2, boolean hall) {
			this.x1 = x1;
			this.z1 = z1;
			this.x2 = x2;
			this.z2 = z2;
			this.hall = hall;
		}
	}

	private static class PossibleConstraint {
		int v1, v2;
		boolean hall;
		public PossibleConstraint(int v1, int v2, boolean hall) {
			this.v1 = v1;
			this.v2 = v2;
			this.hall = hall;
		}
	}
	
	private boolean addRoomToTop(Face face) {
		
		clearWorkVariables();
		
		posX = face.getCenterX();
		posZ = face.getCenterZ();
		minX = face.x1+wallThickness+doorWidthMin-maxRoomSizeX;
		maxX = face.x2-wallThickness-doorWidthMin+maxRoomSizeX;
		minZ = face.z1+wallThickness+doorWidthMin-maxRoomSizeZ;
		maxZ = face.z2-wallThickness-doorWidthMin+maxRoomSizeZ;
		
		int floor = face.rooms[0].floor+1;
		findConstraints(floor);
		
		chooseConstraints(face);
		
		if(!roomPlaced) return false;
		Room newRoom = new Room(this, floor, roomX1, minY, roomZ1, roomX2, maxY, roomZ2, rooms);		
		
		addRoom(newRoom);
		
		return true;
		
	}
        
	private boolean addRoomToWest(Face face) {
		return false;
	}	

	private boolean addRoomToEast(Face face) {
		return false;
	}

	private boolean addRoomToNorth(Face face) {
		return false;
	}

	private boolean addRoomToSouth(Face face) {
		return false;
	}
	
	private boolean addRoomToBottom(Face face) {
		return false;
	}
	
}
