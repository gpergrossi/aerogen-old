package aero;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.bukkit.World;

public class WorldDataFile {
	
	private static int sectionSize = 16*16*16*2;
	
	private RandomAccessFile file;
	
	private int chunkSize = 0;
	private int sections = 0;
	private boolean[] locations;

	public WorldDataFile(World world) {
		String filename = AeroGen.pluginFolder+world.getName()+".part";
		File file = new File(filename);
		try {
			this.file = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e) {
			AeroGen.log("Failed to open save file: "+filename);
		}
		this.locations = new boolean[4];
		this.sections = (world.getMaxHeight() >> 4);
		this.chunkSize = sectionSize*sections;
	}
	
	public int getEmptyLocation() {
		for(int i = 0; i < locations.length; i++) {
			if(locations[i] == false) return i;
		}
		int index = locations.length;
		boolean[] newLocations = new boolean[locations.length*2];
		System.arraycopy(locations, 0, newLocations, 0, locations.length);
		locations = newLocations;
		return index;
	}

	private void trackTo(int position) {
		try {
			file.seek(position*chunkSize);
		} catch (IOException e) {
			AeroGen.log("Failed to seek to location: ("+position+":"+(position*chunkSize)+")");
		}
	}
	
	public int save(short[][] blocks) {
		int position = getEmptyLocation();
		trackTo(position);
		for(int i = 0; i < sections; i++) {
			try {
				if(blocks[i] == null) {
						file.writeChars("null");
						int remaining = sectionSize-8;
						remaining -= file.skipBytes(remaining);
						byte[] bytes = new byte[remaining];
						file.write(bytes);
					continue;
				}
				for(int j = 0; j < 4096; j++) {
					file.writeShort(blocks[i][j]);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return position;
	}
	
	public short[][] load(int position) {
		trackTo(position);
		short[][] blocks = new short[sections][];
		for(int i = 0; i < sections; i++) {
			try {
				blocks[i] = new short[16*16*16];
				blocks[i][0] = file.readShort();
				blocks[i][1] = file.readShort();
				blocks[i][2] = file.readShort();
				blocks[i][3] = file.readShort();
				if((char) blocks[i][0] == 'n' & (char) blocks[i][1] == 'u' && (char) blocks[i][2] == 'l' && (char) blocks[i][3] == ';') {
					blocks[i] = null;
				} else {
					for(int j = 4; j < 4096; j++) {
						blocks[i][j] = file.readShort();
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return blocks;
	}
	
}
