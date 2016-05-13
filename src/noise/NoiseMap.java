package noise;

public abstract class NoiseMap {

	/**
	 * This function must accept a double value from -1.0 to 1.0 and
	 * re-map it to a new value also on the range of -1.0 to 1.0;
	 * @param value - a value from -1.0 to 1.0 inclusive
	 * @return double - a value from -1.0 to 1.0 inclusive
	 */
	public abstract double map(double value);
	
	public static NoiseMap SharpCurve = new NoiseMap() {
		public double map(double value) {
			return 1.0-Math.abs(value)*2.0;
		}
	};
	
}
