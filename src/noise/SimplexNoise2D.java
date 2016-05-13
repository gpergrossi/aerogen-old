package noise;

import java.util.Random;

public class SimplexNoise2D extends SimplexNoise implements Noise2D {
	
	protected Random random;
	protected double frequency = 1.0;
	protected double offsetX = 0.0;
	protected double offsetY = 0.0;
	private NoiseMap output = null;
	
	/**
	 * Constructs a 2D simplex noise generator with the given frequency.
	 * @param seed - a seed value for this generator
	 * @param frequency - The frequency of the generator. Noise repeats with a wavelength of 1/frequency
	 */
	public SimplexNoise2D(long seed, double frequency) {
		random = new Random(seed);
		offsetX = random.nextDouble()*8192.0-4096.0;
		offsetY = random.nextDouble()*8192.0-4096.0;
		this.frequency = frequency;
	}
	
	/**
	 * Constructs a 2D simplex noise generator with the given frequency.
	 * @param frequency - The frequency of the generator. Noise repeats with a wavelength of 1/frequency
	 */
	public SimplexNoise2D(double frequency) {
		random = new Random();
		offsetX = random.nextDouble()*8192.0-4096.0;
		offsetY = random.nextDouble()*8192.0-4096.0;
		this.frequency = frequency;
	}
	
	/**
	 * Returns a value in the range [-1.0, 1.0] that reflects the value 
	 * of a 2D simplex noise function at a point (x, y). 
	 * @param x - a double
	 * @param y - a double
	 * @return double - a value from -1.0 to 1.0 inclusive
	 */
	public double getValue(double x, double y) {
		x = x * frequency + offsetX;
		y = y * frequency + offsetY;
		double value = SimplexNoise.noise(x, y);
		if(output != null) value = output.map(value);
		return value;
	}
	
	/**
	 * Gets the frequency of this simplex noise generator.
	 * @return double - this generator's frequency
	 */
	public double getFrequency() {
		return this.frequency;
	}
	
	/**
	 * Sets the frequency of this simplex noise generator.
	 * @param frequency - the desired frequency
	 */
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	
	/**
	 * Multiplies the frequency of this simplex noise generator. The result of this method 
	 * will be that the frequency will be equal to the old frequency times the multiplier given.
	 * @param multiplier - the multiplier by which the old frequency will be multiplied
	 */
	public void multiplyFrequency(double multiplier) {
		this.frequency *= multiplier;
	}

	/**
	 * Sets the noise map that is applied to the final output of this noise
	 * @param map - a noise map
	 */
	public void setOutputMap(NoiseMap map) {
		this.output = map;
	}
	
	/**
	 * Gets the noise map that is applied to the final output of this noise
	 * @return NoiseMap - a noise map
	 */
	public NoiseMap getOutputMap() {
		return this.output;
	}
	
}
