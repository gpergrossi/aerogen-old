package noise;

public class SimplexNoise3D extends SimplexNoise2D {

	protected double offsetZ = 0.0;

	/**
	 * Constructs a 3D simplex noise generator with the given frequency.
	 * @param seed - a seed value for this generator
	 * @param frequency - The frequency of the generator. Noise repeats with a wavelength of 1/frequency.
	 */
	public SimplexNoise3D(long seed, double frequency) {
		super(seed, frequency);
		
		offsetZ = random.nextDouble() * 8192.0 - 4096.0;
	}

	/**
	 * Constructs a 3D simplex noise generator with the given frequency.
	 * @param frequency - The frequency of the generator. Noise repeats with a wavelength of 1/frequency.
	 */
	public SimplexNoise3D(double frequency) {
		super(frequency);
		
		offsetZ = random.nextDouble() * 8192.0 - 4096.0;
	}

	/**
	 * Returns a value in the range [-1, 1] that reflects the value 
	 * of a 3D simplex noise function at a point (x, y, 0). 
	 * @param x - a double
	 * @param y - a double
	 * @param z - a double
	 * @return double - a value from -1 to 1
	 */
	public double getValue(double x, double y) {
		return super.getValue(x, y);
	}
	
	/**
	 * Returns a value in the range [-1, 1] that reflects the value 
	 * of a 3D simplex noise function at a point (x, y, z). 
	 * @param x - a double
	 * @param y - a double
	 * @param z - a double
	 * @return double - a value from -1 to 1
	 */
	public double getValue(double x, double y, double z) {
		x = x * frequency + offsetX;
		y = y * frequency + offsetY;
		z = z * frequency + offsetZ;
		double value = SimplexNoise.noise(x, y, z);
		return value;
	}

}
