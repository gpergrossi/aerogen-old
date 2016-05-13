package noise;

import java.util.Random;

public class FractalNoise2D implements Noise2D {

	SimplexNoise2D[] octaves;
	double[] magnitudeScales;
	int numOctaves = 1;
	double frequency;
	double lacunarity;
	double gain;
	NoiseMap inputMap;
	NoiseMap outputMap;
	
	public FractalNoise2D() {
		this(0, 4, 1.0/128.0, 0.5, 2.0);
	}
	
	public FractalNoise2D(double frequency) {
		this(0, 4, frequency, 0.5, 2.0);
	}
	
	public FractalNoise2D(long seed, int numOctaves, double frequency) {
		this((seed == 0 ? 1 : seed), numOctaves, frequency, 0.5, 2.0);
	}
	
	public FractalNoise2D(int numOctaves, double frequency) {
		this(0, numOctaves, frequency, 0.5, 2.0);
	}
	
	public FractalNoise2D(long seed, int numOctaves, double frequency, double gain, double lacunarity) {
		this.numOctaves = numOctaves;
		this.gain = gain;
		this.magnitudeScales = new double[numOctaves];
		double scale = 1.0;
		double scaleTotal = 0.0;
		for(int i = 0; i < this.numOctaves; i++) {
			this.magnitudeScales[i] = scale;
			scaleTotal += scale;
			scale *= this.gain;
		}
		this.frequency = frequency;
		this.lacunarity = lacunarity;
		this.octaves = new SimplexNoise2D[numOctaves];
		Random random = new Random(seed);
		for(int i = 0; i < this.numOctaves; i++) {
			this.magnitudeScales[i] /= scaleTotal;
			this.octaves[i] = new SimplexNoise2D(random.nextLong(), frequency);
			frequency *= this.lacunarity;
		}
	}
	
	/**
	 * Returns a value in the range [-1, 1] that reflects the value 
	 * of a 2D simplex noise function at a point (x, y). 
	 * @param x - a double
	 * @param y - a double
	 * @return double - a value from -1 to 1
	 */
	public double getValue(double x, double y) {
		double value = 0.0;
		if(inputMap != null) {
			for(int i = 0; i < numOctaves; i++) {
				value += inputMap.map(octaves[i].getValue(x, y)*this.magnitudeScales[i]);
			}
		} else {
			for(int i = 0; i < numOctaves; i++) {
				value += (octaves[i].getValue(x, y)*this.magnitudeScales[i]);
			}
		}
		if(outputMap != null) {
			value = outputMap.map(value);
		}
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
	 * Sets the noise map that is applied to each subnoise function of the fractal noise
	 * @param map - a noise map
	 */
	public void setInputMap(NoiseMap map) {
		this.inputMap = map;
	}
	
	/**
	 * Gets the noise map that is applied to each subnoise function of the fractal noise
	 * @return NoiseMap - a noise map
	 */
	public NoiseMap getInputMap() {
		return this.inputMap;
	}
	
	/**
	 * Sets the noise map that is applied to the final output of this fractal noise
	 * @param map - a noise map
	 */
	public void setOutputMap(NoiseMap map) {
		this.outputMap = map;
	}
	
	/**
	 * Gets the noise map that is applied to the final output of this fractal noise
	 * @return NoiseMap - a noise map
	 */
	public NoiseMap getOutputMap() {
		return this.outputMap;
	}
	
}
