package noise;

public class RangeMap extends NoiseMap {

	double magnitude = 1.0;
	double offset = 0.0;
	
	public RangeMap(double min, double max) {
		this.setRange(min, max);
	}
	
	public RangeMap(double scale) {
		this.setScale(scale);
	}
	
	public double map(double value) {
		return value*magnitude+offset;
	}
	
	/**
	 * Gets the scale of this simplex noise generator.
	 * @return double - this generator's scaling factor
	 */
	public double getScale() {
		return this.magnitude;
	}
	
	/**
	 * Sets the scale of this simplex noise generator.
	 * @param scale - the desired scaling factor
	 */
	public void setScale(double scale) {
		this.magnitude = scale;
	}
	
	/**
	 * Multiplies the scale of this simplex noise generator. The result of this method 
	 * will be that the scaling factor will be equal to the old scaling factor times scale.
	 * @param multiplier - the multiplier by which the old scaling factor will be multiplied
	 */
	public void multiplyScale(double multiplier) {
		this.magnitude *= multiplier;
	}
	
	/**
	 * Gets the offset value of this simplex noise generator.
	 * @return double - the offset value of this generator
	 */
	public double getOffset() {
		return this.offset;
	}
	
	/**
	 * Sets the offset value of this simplex noise generator.
	 * @param offset - the desired offset value
	 */
	public void setOffset(double offset) {
		this.offset = offset;
	}
	
	/**
	 * Adds to the offset value of this simplex noise generator. The result of this
	 * method will be that the offset will be equal to the old offset plus the value given.
	 * @param offset - amount to offset the current offset by 
	 */
	public void addOffset(double offset) {
		this.offset += offset;
	}
	
	/**
	 * Returns the lowest number that this generator will return.
	 * @return double - lowest value this generator can return 
	 */
	public double getMinimum() {
		return (-1.0 * magnitude) + offset;
	}
	
	/**
	 * Returns the highest number that this generator will return.
	 * @return double - highest value this generator can return 
	 */
	public double getMaximum() {
		return (1.0 * magnitude) + offset;
	}
	
	/**
	 * Sets this simplex noise generator's scale factor and offset such that the
	 * range of returnable numbers is [min, max]. 
	 * @param min - the desired minimum return value
	 * @param max - the desired maximum return value
	 */
	public void setRange(double min, double max) {
		double scale = (max-min)/2;
		double offset = min+scale;
		this.magnitude = scale;
		this.offset = offset;
	}

}
