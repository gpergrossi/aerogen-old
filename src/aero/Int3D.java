package aero;

/**
 * A Simple data structure for saving a 3D point with integer precision.
 * 
 * @author MortusNegati
 */
public class Int3D {

	public int x, y, z;
	
	public Int3D() {}
	
	public Int3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Int3D)) return false;
		Int3D c = (Int3D) o;
		if(this.x == c.x && this.y == c.y && this.z == c.z) return true;
		return false;
	}
	
}
