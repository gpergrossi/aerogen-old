package noise;

public class SimplexNoise4D extends SimplexNoise3D {

	private double offsetW = 0.0;

	/**
	 * Constructs a 4D simplex noise generator with the given frequency.
	 * @param seed - a seed value for this generator
	 * @param frequency - The frequency of the generator. Noise repeats with a wavelength of 1/frequency.
	 */
	public SimplexNoise4D(long seed, double frequency) {
		super(seed, frequency);

		offsetW = random.nextDouble() * 8192.0 - 4096.0;
	}

	/**
	 * Constructs a 4D simplex noise generator with the given frequency.
	 * @param frequency - The frequency of the generator. Noise repeats with a wavelength of 1/frequency.
	 */
	public SimplexNoise4D(double frequency) {
		super(frequency);

		offsetW = random.nextDouble() * 8192.0 - 4096.0;
	}

	/**
	 * Returns a value in the range [-1, 1] that reflects the value 
	 * of a 4D simplex noise function at a point (x, y, 0, 0). 
	 * @param x - a double
	 * @param y - a double
	 * @return double - a value from -1 to 1
	 */
	public double getValue(double x, double y) {
		return super.getValue(x, y);
	}
	
	/**
	 * Returns a value in the range [-1, 1] that reflects the value 
	 * of a 4D simplex noise function at a point (x, y, z, 0). 
	 * @param x - a double
	 * @param y - a double
	 * @param z - a double
	 * @return double - a value from -1 to 1
	 */
	public double getValue(double x, double y, double z) {
		return super.getValue(x, y, z);
	}
	
	/**
	 * Returns a value in the range [-1, 1] that reflects the value 
	 * of a 4D simplex noise function at a point (x, y, z, w). 
	 * @param x - a double
	 * @param y - a double
	 * @param z - a double
	 * @param w - a double
	 * @return double - a value from -1 to 1
	 */
	public double getValue(double x, double y, double z, double w) {
		x = x * frequency + offsetX;
		y = y * frequency + offsetY;
		z = z * frequency + offsetZ;
		w = w * frequency + offsetW;
		double value = SimplexNoise.noise(x, y, z, w);
		return value;
	}

}
